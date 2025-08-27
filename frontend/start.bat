@echo off
chcp 65001 >nul
echo 正在启动扫码点餐系统前端服务...
echo 请确保已安装Node.js 16+

echo.
echo 检查Node.js环境...
node -v
if %errorlevel% neq 0 (
    echo Node.js环境未配置，请先安装Node.js 16+
    pause
    exit /b 1
)

echo.
echo 检查npm环境...
npm -v
if %errorlevel% neq 0 (
    echo npm环境异常
    pause
    exit /b 1
)

echo.
echo 检查依赖是否已安装...
if not exist node_modules (
    echo 正在安装依赖...
    npm install
)

echo.
echo 正在启动Vite开发服务器...
npm run dev

pause