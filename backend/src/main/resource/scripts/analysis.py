import pandas as pd
import numpy as np
import os
import sys
import json
import argparse
from datetime import datetime

# 碳强度数据源（实际应用中应替换为真实API）
def get_carbon_intensity(region, timestamp):
    """模拟获取特定时间和地区的碳强度"""
    hour = timestamp.hour
    if 7 <= hour < 10 or 17 <= hour < 20:  # 早晚高峰
        return 450  # gCO2eq/kWh
    elif 22 <= hour < 6:  # 夜间
        return 250  # gCO2eq/kWh
    else:  # 日间
        return 350  # gCO2eq/kWh

def load_and_preprocess_data(file_path):
    """加载并预处理数据，简化进程名称"""
    try:
        df = pd.read_csv(file_path)
    except Exception as e:
        print(f"Error reading file: {e}", file=sys.stderr)
        return None

    # 简化进程名称
    if 'process' in df.columns:
        df['process'] = df['process'].str.replace('NFCORE_CHIPSEQ:CHIPSEQ:', '', regex=False)
        df['process'] = df['process'].apply(lambda x: x.split(':')[-1])

    # 转换数值列
    numeric_cols = ['duration', 'memory', 'rss', '%cpu', 'cpus', 'mem_gb', 'total_io_gb', 'read_bytes', 'write_bytes', 'power_consumption_wh']
    for col in numeric_cols:
        if col in df.columns:
            df[col] = pd.to_numeric(df[col], errors='coerce').fillna(0)
    
    # 转换持续时间为分钟
    if 'duration' in df.columns:
        df['duration_min'] = df['duration'] / 60
    
    # 转换内存为GB
    if 'memory' in df.columns:
        df['memory_gb'] = df['memory'] / 1024
        
    return df

def perform_analysis(df, hardware_config):
    """进行碳足迹和性能分析"""
    result = {
        'summary': {},
        'tasks': [],
        'powerData': {},
        'processData': []
    }

    # 1. 总体指标
    total_energy_kwh = df['power_consumption_wh'].sum() / 1000
    total_runtime_min = df['duration'].sum() / 60
    
    # 模拟碳足迹计算
    carbon_intensity_g = get_carbon_intensity('US-EAST', datetime.now())
    total_carbon_footprint_g = total_energy_kwh * carbon_intensity_g
    
    result['summary'] = {
        'total_energy': total_energy_kwh,
        'total_carbon_footprint': total_carbon_footprint_g,
        'total_runtime': total_runtime_min,
        'hardware_config': hardware_config,
        'carbon_intensity_g_kwh': carbon_intensity_g
    }
    
    # 2. 任务级详细指标
    if 'task_name' in df.columns:
        task_metrics = df.groupby('task_name').agg({
            'power_consumption_wh': 'sum',
            'duration_min': 'sum',
            '%cpu': 'mean',
            'memory_gb': 'mean'
        }).reset_index()
        
        tasks_list = []
        for index, row in task_metrics.iterrows():
            tasks_list.append({
                'task_name': row['task_name'],
                'energy': row['power_consumption_wh'],
                'carbon_footprint': (row['power_consumption_wh'] / 1000) * carbon_intensity_g,
                'runtime': row['duration_min'],
                'cpu_usage': row['%cpu'],
                'memory_usage': row['memory_gb'],
                'hardware': hardware_config
            })
        result['tasks'] = tasks_list
        
    # 3. 主机能耗数据
    if 'hostname' in df.columns:
        power_analysis = df.groupby('hostname').agg(power_consumption_wh=('power_consumption_wh', 'sum')).reset_index()
        processes_by_host = df.groupby('hostname')['process'].apply(lambda x: ', '.join(x)).to_dict()
        
        result['powerData'] = {
            'hosts': power_analysis['hostname'].tolist(),
            'consumption': power_analysis['power_consumption_wh'].tolist(),
            'processes': processes_by_host
        }
    
    # 4. 进程性能指标
    if 'process' in df.columns:
        agg_funcs = {
            'task_count': ('duration', 'count'),
            'avgCpu': ('%cpu', 'mean'),
            'avgMem': ('memory_gb', 'mean'),
            'avgDuration': ('duration_min', 'mean'),
            'totalIo': ('total_io_gb', 'sum'),
            'readGb': ('read_bytes', lambda x: x.sum() / (1024 ** 3)),
            'writeGb': ('write_bytes', lambda x: x.sum() / (1024 ** 3))
        }
        
        # 只添加存在的列
        valid_agg = {}
        for key, (col, func) in agg_funcs.items():
            if col in df.columns:
                valid_agg[key] = (col, func)
        
        if valid_agg:
            process_metrics = df.groupby('process').agg(**valid_agg).reset_index()
            result['processData'] = process_metrics.to_dict('records')
    
    return result

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description="Analyze trace file for carbon footprint.")
    parser.add_argument('file_path', type=str, help="Path to the trace CSV file.")
    parser.add_argument('--hardware', type=str, default='default', help="Hardware configuration used for the run.")
    
    args = parser.parse_args()

    # 在 try-except 块中执行所有分析逻辑
    try:
        df = load_and_preprocess_data(args.file_path)
        
        if df is None or df.empty:
            # 如果文件为空或加载失败，返回一个明确的错误JSON
            error_message = f"Failed to load or process data from file: {args.file_path}"
            result = {'status': 'failed', 'message': error_message}
        else:
            result = perform_analysis(df, args.hardware)
            result['status'] = 'success'

    except Exception as e:
        # 捕获任何其他意外错误，并返回一个错误JSON
        error_message = f"An unexpected error occurred during analysis: {e}"
        result = {'status': 'failed', 'message': error_message}
        print(error_message, file=sys.stderr)
        
    # 无论成功或失败，都打印JSON输出
    print(json.dumps(result, indent=4))