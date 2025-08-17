// vite-custom-server.js
import { createServer } from 'vite';
import path from 'path';
import { fileURLToPath } from 'url';
import fs from 'fs';
import http from 'http';

// 获取当前目录路径
const __dirname = path.dirname(fileURLToPath(import.meta.url));

// 安全解码 URI 的辅助函数
function safeDecodeURI(uri) {
  try {
    return decodeURI(uri);
  } catch (e) {
    console.warn(`URI 解码失败: ${uri}`, e);
    // 移除非法字符（只保留安全的）
    return uri.replace(/[^a-zA-Z0-9\-_./?&=]/g, '');
  }
}

async function start() {
  const vite = await createServer({
    configFile: 'vite.config.js',
    server: {
      middlewareMode: true
    }
  });
  
  // 创建 HTTP 服务器
  const server = http.createServer((req, res) => {
    // 应用中间件
    vite.middlewares(req, res, () => {
      // 如果 Vite 没有处理请求，则处理 404
      res.statusCode = 404;
      res.end('Not found');
    });
  });
  
  // 修改中间件栈
  const originalStack = [...vite.middlewares.stack];
  vite.middlewares.stack = [];
  
  // 添加自定义 URI 处理中间件
  vite.middlewares.use((req, res, next) => {
    // 处理 URI
    req.url = safeDecodeURI(req.url);
    next();
  });
  
  // 添加 Vite 核心中间件
  vite.middlewares.use(vite.middlewares.handle);
  
  // 添加静态文件服务
  vite.middlewares.use((req, res, next) => {
    const filePath = path.join(__dirname, 'public', req.url);
    
    // 检查文件是否存在
    fs.stat(filePath, (err, stats) => {
      if (err || !stats.isFile()) {
        return next();
      }
      
      // 读取并发送文件
      fs.readFile(filePath, (err, data) => {
        if (err) {
          res.statusCode = 500;
          return res.end('Internal server error');
        }
        
        // 根据文件扩展名设置 Content-Type
        const ext = path.extname(filePath);
        const types = {
          '.html': 'text/html',
          '.css': 'text/css',
          '.js': 'text/javascript',
          '.json': 'application/json',
          '.png': 'image/png',
          '.jpg': 'image/jpeg',
          '.svg': 'image/svg+xml'
        };
        
        res.setHeader('Content-Type', types[ext] || 'text/plain');
        res.end(data);
      });
    });
  });
  
  // 添加其他原始中间件
  originalStack.forEach(middleware => {
    vite.middlewares.use(middleware.handle);
  });
  
  // 启动服务器
  const port = 5173;
  server.listen(port, () => {
    console.log(`Vite 服务器运行在 http://localhost:${port}`);
  });
  
  // 处理关闭事件
  process.on('SIGINT', () => {
    server.close();
    vite.close();
    process.exit();
  });
}

// 启动服务器
start().catch(err => {
  console.error('服务器启动失败:', err);
  process.exit(1);
});