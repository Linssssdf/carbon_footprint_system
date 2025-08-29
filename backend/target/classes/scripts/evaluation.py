import pandas as pd
import numpy as np
import os
import sys
import json
import argparse
import logging
import re

# Custom JSON encoder to handle NumPy data types
class NumpyEncoder(json.JSONEncoder):
    def default(self, obj):
        if isinstance(obj, (np.integer, np.int64, np.int32, np.int16, np.int8)):
            return int(obj)
        elif isinstance(obj, (np.floating, np.float64, np.float32, np.float16)):
            return float(obj)
        elif isinstance(obj, np.ndarray):
            return obj.tolist()
        elif isinstance(obj, np.bool_):
            return bool(obj)
        else:
            return super(NumpyEncoder, self).default(obj)

def setup_logging():
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(levelname)s - %(message)s',
        stream=sys.stderr
    )
    return logging.getLogger(__name__)

logger = setup_logging()

def load_and_preprocess_data(file_path):
    """Load and preprocess trace.csv data with improved column detection"""
    try:
        logger.info(f"Attempting to read file: {file_path}")
        
        # 首先尝试检测文件是否有真实的表头
        with open(file_path, 'r') as f:
            first_line = f.readline().strip()
        
        # 检测可能的列分隔符
        delimiter = ',' if ',' in first_line else '\t'
        logger.info(f"Detected delimiter: '{delimiter}'")
        
        # 检查第一行是否包含Nextflow常见的列名
        nf_keywords = ['task_id', 'process', 'status', 'exit', 'duration', 'realtime', 'memory', '%cpu', 'cpus', 'hostname']
        has_nf_header = any(keyword in first_line.lower() for keyword in nf_keywords)
        
        # 尝试读取文件，优先使用检测到的表头
        if has_nf_header:
            logger.info("File appears to have Nextflow-compatible header")
            df = pd.read_csv(file_path, delimiter=delimiter, comment='#')
        else:
            # 尝试多种可能的列名格式
            logger.info("Trying to detect column structure...")
            
            # 尝试标准Nextflow列名
            try:
                expected_columns = [
                    'task_id', 'hostname', 'hash', 'native_id', 'process', 'tag', 'name', 'status', 'exit', 
                    'module', 'container', 'cpus', 'time', 'disk', 'memory', 'attempt', 'submit', 
                    'start', 'complete', 'duration', 'realtime', 'queue', '%cpu', '%mem', 'rss', 
                    'vmem', 'peak_rss', 'peak_vmem', 'rchar', 'wchar', 'syscr', 'syscw', 
                    'read_bytes', 'write_bytes', 'vol_ctxt', 'inv_ctxt', 'workdir', 'scratch', 
                    'error_action', 'create_bash_wrapper_time', 'create_request_time', 
                    'submit_to_scheduler_time', 'submit_to_k8s_time'
                ]
                
                # 读取文件并分配列名
                df = pd.read_csv(file_path, delimiter=delimiter, header=None, comment='#', 
                                names=expected_columns[:min(len(expected_columns), 50)])
                logger.info("Used standard Nextflow column names")
            except Exception as e:
                logger.warning(f"Standard column mapping failed: {e}")
                # 回退到通用列名
                df = pd.read_csv(file_path, delimiter=delimiter, header=None, comment='#')
                # 为列分配通用名称
                df.columns = [f'col_{i}' for i in range(len(df.columns))]
                logger.info("Used generic column names as fallback")
        
        # 添加调试信息
        logger.info(f"DataFrame columns: {list(df.columns)}")
        logger.info(f"First few rows:\n{df.head().to_string()}")
        
        # 确保进程列是字符串类型
        if 'process' in df.columns:
            df['process'] = df['process'].astype(str)
            logger.info(f"Process names: {df['process'].unique()}")
        
        # 定义需要处理的数值列
        numeric_cols = ['duration', 'realtime', 'memory', 'read_bytes', 'write_bytes', 'cpus', '%cpu']
        for col in numeric_cols:
            if col in df.columns:
                # Convert to numeric, coercing errors
                df[col] = pd.to_numeric(df[col], errors='coerce').fillna(0)
                logger.info(f"Column {col} statistics: min={df[col].min()}, max={df[col].max()}, mean={df[col].mean()}")

        # Unit conversions and new column creation
        # Convert duration and realtime from milliseconds to minutes for a better scale
        if 'duration' in df.columns:
            df['duration_min'] = df['duration'] / 60000
        if 'realtime' in df.columns:
            df['realtime_min'] = df['realtime'] / 60000
        
        # Convert memory and I/O bytes to GB
        if 'memory' in df.columns:
            df['memory_gb'] = df['memory'] / (1024**3)
        if 'read_bytes' in df.columns:
            df['read_gb'] = df['read_bytes'] / (1024**3)
        if 'write_bytes' in df.columns:
            df['write_gb'] = df['write_bytes'] / (1024**3)
        
        if 'read_gb' in df.columns and 'write_gb' in df.columns:
            df['total_io_gb'] = df['read_gb'] + df['write_gb']
        
        # Estimate energy consumption (simplified model)
        # Using a fixed CPU power draw (e.g., 200W per CPU) and I/O power (e.g., 5W per GB of I/O)
        # Assuming a base power draw for other components
        if all(col in df.columns for col in ['cpus', '%cpu']):
            # 检查%cpu值是否合理，如果过大则限制上限
            df['%cpu'] = df['%cpu'].apply(lambda x: min(x, 1000) if pd.notnull(x) else 0)
            df['cpu_power_watts'] = df['cpus'] * 200 * (df['%cpu'] / 100)
        
        if 'total_io_gb' in df.columns:
            df['io_power_watts'] = df['total_io_gb'] * 5 
        
        df['base_power_watts'] = 50  # Assume a 50W base power for other components
        
        # Calculate total power
        power_columns = [col for col in ['cpu_power_watts', 'io_power_watts', 'base_power_watts'] if col in df.columns]
        if power_columns:
            df['total_power_watts'] = df[power_columns].sum(axis=1)
        
        # Energy in Watt-hours (Wh) = Power in Watts * Duration in hours
        if 'total_power_watts' in df.columns and 'realtime' in df.columns:
            df['power_consumption_wh'] = df['total_power_watts'] * (df['realtime'] / 3600000)
        
        # Estimate carbon emissions (using a sample carbon intensity)
        # Average global carbon intensity of electricity is about 475 gCO2eq/kWh
        carbon_intensity_g_per_kwh = 475  
        df['carbon_intensity'] = carbon_intensity_g_per_kwh
        
        # Carbon emission in gCO2eq = Energy in kWh * Carbon Intensity in g/kWh
        if 'power_consumption_wh' in df.columns:
            df['carbon_emission'] = (df['power_consumption_wh'] / 1000) * df['carbon_intensity']
        
        logger.info(f"Data preprocessed successfully. DataFrame shape: {df.shape}")
        
        return df, None

    except FileNotFoundError:
        error_msg = f"Error: The file was not found at {file_path}"
        logger.error(error_msg)
        return None, error_msg
    except pd.errors.ParserError as e:
        error_msg = f"Error: Could not parse the CSV file. Please check the format. Details: {e}"
        logger.error(error_msg)
        return None, error_msg
    except Exception as e:
        error_msg = f"An unexpected error occurred during data loading: {e}"
        logger.error(error_msg, exc_info=True)
        return None, error_msg
        
def perform_analysis(df, hardware_config):
    """Perform performance analysis, including energy consumption and carbon emissions"""
    result = {
        'summary': {},
        'tasks': [],
        'hostData': {},
        'processData': []
    }
    
    # 1. Overall metrics - using safe column access
    total_runtime_min = df['duration_min'].sum() if 'duration_min' in df.columns else 0
    total_memory_gb = df['memory_gb'].sum() if 'memory_gb' in df.columns else 0
    total_io_gb = df['total_io_gb'].sum() if 'total_io_gb' in df.columns else 0
    
    # Calculate total energy consumption and carbon emissions
    total_energy_wh = df['power_consumption_wh'].sum() if 'power_consumption_wh' in df.columns else 0
    total_energy_kwh = total_energy_wh / 1000
    total_carbon = (total_energy_kwh * df['carbon_intensity'].mean()) / 1000 if 'carbon_intensity' in df.columns else 0  # kgCO2eq
    
    # Get CPU utilization column name
    cpu_col = next((col for col in ['%cpu', 'cpu_usage'] if col in df.columns), None)
    avg_cpu_utilization = df[cpu_col].mean() if cpu_col else 0
    
    result['summary'] = {
        'totalEnergy': total_energy_kwh,
        'totalCarbonFootprint': total_carbon,
        'totalRuntime': total_runtime_min,
        'totalMemory': total_memory_gb,
        'totalIo': total_io_gb,
        'totalTasks': len(df),
        'hardwareConfig': hardware_config,
        'avgCpuUtilization': avg_cpu_utilization
    }
    
    logger.info(f"Summary statistics - Total energy: {total_energy_kwh} kWh, Total carbon emissions: {total_carbon} kgCO2eq")
    
    # 2. Process-level detailed metrics (modified to use process instead of task_name)
    if 'process' in df.columns:
        # Group by process and calculate metrics
        agg_funcs = {}
        
        # Only add existing columns
        if 'duration_min' in df.columns:
            agg_funcs['tasks'] = ('duration_min', 'count')  # Count of tasks per process
        
        if 'duration_min' in df.columns:
            agg_funcs['runtime'] = ('duration_min', 'sum')  # Total runtime per process
        
        if cpu_col:
            agg_funcs['cpu_usage'] = (cpu_col, 'mean')  # Average CPU usage per process
        
        if 'memory_gb' in df.columns:
            agg_funcs['memory_allocated'] = ('memory_gb', 'mean')  # Average memory allocated per process
        
        if 'total_io_gb' in df.columns:
            agg_funcs['io_volume'] = ('total_io_gb', 'sum')  # Total I/O per process
        
        if 'power_consumption_wh' in df.columns:
            agg_funcs['energy_consumption'] = ('power_consumption_wh', 'sum')  # Total energy per process
        
        if 'carbon_emission' in df.columns:
            agg_funcs['carbon_footprint'] = ('carbon_emission', 'sum')  # Total carbon per process
        
        # Add I/O breakdown if available
        if 'read_bytes' in df.columns:
            agg_funcs['read_gb'] = ('read_bytes', lambda x: x.sum() / (1024 ** 3))  # Total read per process
        
        if 'write_bytes' in df.columns:
            agg_funcs['write_gb'] = ('write_bytes', lambda x: x.sum() / (1024 ** 3))  # Total write per process
        
        if agg_funcs:
            process_metrics = df.groupby('process').agg(**agg_funcs).reset_index()
        else:
            # If no columns to aggregate, create an empty DataFrame
            process_metrics = pd.DataFrame(columns=['process'])
        
        process_list = []
        for index, row in process_metrics.iterrows():
            process_data = {
                'process': row['process'],
                'tasks': row.get('tasks', 0),
                'runtime': row.get('runtime', 0),
                'cpu_usage': row.get('cpu_usage', 0),
                'memory_allocated': row.get('memory_allocated', 0),
                'io_volume': row.get('io_volume', 0),
                'energy_consumption': row.get('energy_consumption', 0),
                'carbon_footprint': row.get('carbon_footprint', 0),
                'read_gb': row.get('read_gb', 0),
                'write_gb': row.get('write_gb', 0),
                'hardware': hardware_config
            }
            process_list.append(process_data)
        
        # Modified line here:
        result['tasks'] = process_list
        logger.info(f"Processed {len(process_list)} processes")
    
    # 3. Host-level data
    if 'hostname' in df.columns:
        # Group by host and calculate metrics
        host_agg_funcs = {}
        
        if 'duration' in df.columns:
            host_agg_funcs['task_count'] = ('duration', 'count')
        
        if 'duration_min' in df.columns:
            host_agg_funcs['total_runtime'] = ('duration_min', 'sum')
        
        if cpu_col:
            host_agg_funcs['avg_cpu'] = (cpu_col, 'mean')
        
        if 'memory_gb' in df.columns:
            host_agg_funcs['total_memory'] = ('memory_gb', 'sum')
        
        if 'total_io_gb' in df.columns:
            host_agg_funcs['total_io'] = ('total_io_gb', 'sum')
        
        if 'power_consumption_wh' in df.columns:
            host_agg_funcs['total_energy'] = ('power_consumption_wh', 'sum')
        
        if 'carbon_emission' in df.columns:
            host_agg_funcs['total_carbon'] = ('carbon_emission', 'sum')
        
        if host_agg_funcs:
            host_metrics = df.groupby('hostname').agg(**host_agg_funcs).reset_index()
        else:
            host_metrics = pd.DataFrame(columns=['hostname'])
        
        # Get process list for each host (modified to show unique processes per host)
        processes_by_host = df.groupby('hostname')['process'].apply(lambda x: ', '.join(x.unique())).to_dict()
        
        host_data = {
            'hosts': host_metrics['hostname'].tolist(),
            'task_counts': host_metrics.get('task_count', [0] * len(host_metrics)).tolist(),
            'runtimes': host_metrics.get('total_runtime', [0] * len(host_metrics)).tolist(),
            'cpu_utilizations': host_metrics.get('avg_cpu', [0] * len(host_metrics)).tolist(),
            'memory_allocations': host_metrics.get('total_memory', [0] * len(host_metrics)).tolist(),
            'io_volumes': host_metrics.get('total_io', [0] * len(host_metrics)).tolist(),
            'energy_consumptions': host_metrics.get('total_energy', [0] * len(host_metrics)).tolist(),
            'carbon_emissions': host_metrics.get('total_carbon', [0] * len(host_metrics)).tolist(),
            'processes': processes_by_host  # Add processes running on each host
        }
        
        result['hostData'] = host_data
        logger.info(f"Processed {len(host_data['hosts'])} hosts")
    
    return result

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="""
        A Python script to analyze Nextflow trace data for performance and carbon footprint.
        It processes a trace.csv file, calculates key metrics, and outputs the results as a JSON object.
    """)
    parser.add_argument('file_path', type=str, help="Path to the trace CSV file.")
    parser.add_argument('--hardware', type=str, default='default', help="Hardware configuration used for the run.")
    
    args = parser.parse_args()
    logger.info(f"Command line arguments: file_path={args.file_path}, hardware={args.hardware}")

    try:
        # Check if file exists
        if not os.path.exists(args.file_path):
            error_msg = f"File does not exist: {args.file_path}"
            logger.error(error_msg)
            result = {'status': 'failed', 'message': error_msg}
        else:
            # Load data
            df, error = load_and_preprocess_data(args.file_path)
            
            if df is None:
                # If file loading failed, return error message
                result = {'status': 'failed', 'message': error}
            else:
                # Perform analysis
                result = perform_analysis(df, args.hardware)
                result['status'] = 'success'

    except Exception as e:
        # Catch any other unexpected errors
        error_message = f"An unexpected error occurred during analysis: {str(e)}"
        logger.error(error_message, exc_info=True)
        result = {'status': 'failed', 'message': error_message}
        
    # Output JSON result (ensure only JSON is printed to stdout)
    # Use custom encoder to handle NumPy data types
    print(json.dumps(result, indent=4, cls=NumpyEncoder))