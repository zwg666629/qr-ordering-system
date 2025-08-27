@echo off
chcp 65001 >nul
echo ====================================
echo 扫码点餐系统 - MySQL版本启动脚本
echo ====================================
echo.

echo [检查环境]
echo 正在检查Java环境...
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo × Java环境未配置，请先安装Java 8+
    pause
    exit /b 1
)
echo ✓ Java环境正常

echo.
echo 正在检查Maven环境...
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo × Maven环境未配置，请先安装Maven
    pause
    exit /b 1
)
echo ✓ Maven环境正常

echo.
echo 正在检查MySQL连接...
mysql -h localhost -u root -p123456 -e "SELECT VERSION();" >nul 2>&1
if %errorlevel% neq 0 (
    echo × MySQL连接失败，请检查：
    echo   1. MySQL服务是否已启动
    echo   2. root密码是否为123456
    echo   3. 如果密码不同，请修改 backend/src/main/resources/application.yml
    pause
    exit /b 1
)
echo ✓ MySQL连接正常

echo.
echo [初始化数据库]
echo 正在创建数据库...
mysql -h localhost -u root -p123456 -e "CREATE DATABASE IF NOT EXISTS qr_ordering CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
if %errorlevel% equ 0 (
    echo ✓ 数据库创建成功
) else (
    echo ⚠ 数据库可能已存在，继续启动...
)

echo.
echo [启动后端服务]
echo 正在启动Spring Boot应用...
cd backend
start "QR-Ordering Backend" mvn spring-boot:run
echo ✓ 后端服务启动中... (http://localhost:8080)

echo.
echo [启动前端服务]
cd ..\frontend
if not exist node_modules (
    echo 正在安装前端依赖...
    npm install
)
start "QR-Ordering Frontend" npm run dev
echo ✓ 前端服务启动中... (http://localhost:5173)

echo.
echo [启动管理后台]
cd ..\admin
if not exist node_modules (
    echo 正在安装管理后台依赖...
    npm install
)
start "QR-Ordering Admin" npm run dev
echo ✓ 管理后台启动中... (http://localhost:5174)

echo.
echo ====================================
echo 🎉 系统启动完成！
echo ====================================
echo.
echo 访问地址：
echo - 客户端：  http://localhost:5173
echo - 管理后台：http://localhost:5174  
echo - API文档： http://localhost:8080/doc.html
echo.
echo 默认管理员账号：admin / admin123
echo.
echo 按任意键关闭此窗口...
pause >nul













