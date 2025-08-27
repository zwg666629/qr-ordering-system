<<<<<<< HEAD
@echo off
chcp 65001 >nul
echo =====================================================
echo           启动扫码点餐系统所有服务
echo =====================================================
echo.

echo 正在检查并清理端口占用...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do taskkill /PID %%a /F >nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :3001') do taskkill /PID %%a /F >nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :3003') do taskkill /PID %%a /F >nul 2>&1
echo 端口清理完成
echo.

echo 1. 启动后端服务 (固定端口8080)...
start "后端服务-8080" cmd /k "cd /d "%~dp0backend" && echo 后端服务启动中... && mvn spring-boot:run"

echo.
echo 2. 启动管理后台 (固定端口3001)...
start "管理后台-3001" cmd /k "cd /d "%~dp0admin" && echo 管理后台启动中... && npm run dev"

echo.
echo 3. 启动客户端前端 (固定端口3003)...
start "客户端前端-3003" cmd /k "cd /d "%~dp0frontend" && echo 客户端前端启动中... && npm run dev"

echo.
echo =====================================================
echo 所有服务正在启动中，请等待约30秒...
echo.
echo 固定端口配置：
echo • 后端API:    http://localhost:8080 (Spring Boot)
echo • 管理后台:   http://localhost:3001 (Vue.js Admin)
echo • 客户端:     http://localhost:3003 (Vue.js Client)
echo • API文档:    http://localhost:8080/doc.html
echo.
echo 配置说明：
echo • strictPort: true - 端口被占用时会报错而不是自动换端口
echo • host: 0.0.0.0 - 允许外部访问
echo =====================================================
echo.
echo 按任意键退出此窗口...
pause >nul
=======
@echo off
chcp 65001 >nul
echo =====================================================
echo           启动扫码点餐系统所有服务
echo =====================================================
echo.

echo 正在检查并清理端口占用...
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :8080') do taskkill /PID %%a /F >nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :3001') do taskkill /PID %%a /F >nul 2>&1
for /f "tokens=5" %%a in ('netstat -ano ^| findstr :3003') do taskkill /PID %%a /F >nul 2>&1
echo 端口清理完成
echo.

echo 1. 启动后端服务 (固定端口8080)...
start "后端服务-8080" cmd /k "cd /d "%~dp0backend" && echo 后端服务启动中... && mvn spring-boot:run"

echo.
echo 2. 启动管理后台 (固定端口3001)...
start "管理后台-3001" cmd /k "cd /d "%~dp0admin" && echo 管理后台启动中... && npm run dev"

echo.
echo 3. 启动客户端前端 (固定端口3003)...
start "客户端前端-3003" cmd /k "cd /d "%~dp0frontend" && echo 客户端前端启动中... && npm run dev"

echo.
echo =====================================================
echo 所有服务正在启动中，请等待约30秒...
echo.
echo 固定端口配置：
echo • 后端API:    http://localhost:8080 (Spring Boot)
echo • 管理后台:   http://localhost:3001 (Vue.js Admin)
echo • 客户端:     http://localhost:3003 (Vue.js Client)
echo • API文档:    http://localhost:8080/doc.html
echo.
echo 配置说明：
echo • strictPort: true - 端口被占用时会报错而不是自动换端口
echo • host: 0.0.0.0 - 允许外部访问
echo =====================================================
echo.
echo 按任意键退出此窗口...
pause >nul
>>>>>>> a98ace0d91d29ff6a48b77214d22d4a8c174ec8a
