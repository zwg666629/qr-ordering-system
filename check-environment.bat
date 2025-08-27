@echo off
chcp 65001 >nul
echo ====================================
echo 扫码点餐系统 - 环境检查工具
echo ====================================
echo.

set ERROR_COUNT=0

echo [1] 检查Java环境...
java -version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Java已安装
    for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr version') do (
        echo   版本: %%i
    )
) else (
    echo × Java未安装或未配置环境变量
    echo   请下载并安装Java 8或更高版本
    echo   下载地址: https://adoptium.net/
    set /a ERROR_COUNT+=1
)

echo.
echo [2] 检查Maven环境...
mvn -version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Maven已安装
    for /f "tokens=3" %%i in ('mvn -version 2^>^&1 ^| findstr "Apache Maven"') do (
        echo   版本: %%i
    )
) else (
    echo × Maven未安装或未配置环境变量
    echo   请下载并安装Apache Maven
    echo   下载地址: https://maven.apache.org/download.cgi
    set /a ERROR_COUNT+=1
)

echo.
echo [3] 检查Node.js环境...
node -v >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Node.js已安装
    for /f %%i in ('node -v') do echo   版本: %%i
    
    npm -v >nul 2>&1
    if %errorlevel% equ 0 (
        echo ✓ npm已安装
        for /f %%i in ('npm -v') do echo   版本: %%i
    ) else (
        echo × npm未安装
        set /a ERROR_COUNT+=1
    )
) else (
    echo × Node.js未安装
    echo   请下载并安装Node.js 16或更高版本
    echo   下载地址: https://nodejs.org/
    set /a ERROR_COUNT+=1
)

echo.
echo [4] 检查MySQL环境...
mysql --version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ MySQL客户端已安装
    for /f "tokens=1,2,3" %%i in ('mysql --version') do echo   版本: %%k
    
    echo   正在测试MySQL连接...
    mysql -h localhost -u root -p123456 -e "SELECT VERSION();" >nul 2>&1
    if %errorlevel% equ 0 (
        echo ✓ MySQL服务连接成功
        echo   正在检查数据库...
        mysql -h localhost -u root -p123456 -e "SHOW DATABASES LIKE 'qr_ordering';" 2>nul | findstr qr_ordering >nul
        if %errorlevel% equ 0 (
            echo ✓ qr_ordering数据库已存在
        ) else (
            echo ⚠ qr_ordering数据库不存在，将在启动时自动创建
        )
    ) else (
        echo × MySQL连接失败
        echo   可能的原因：
        echo   1. MySQL服务未启动
        echo   2. root密码不是123456
        echo   3. MySQL未安装或配置错误
        set /a ERROR_COUNT+=1
    )
) else (
    echo × MySQL未安装
    echo   请下载并安装MySQL 8.0或更高版本
    echo   下载地址: https://dev.mysql.com/downloads/mysql/
    set /a ERROR_COUNT+=1
)

echo.
echo [5] 检查项目文件...
if exist "backend\pom.xml" (
    echo ✓ 后端项目文件存在
) else (
    echo × 后端项目文件缺失
    set /a ERROR_COUNT+=1
)

if exist "frontend\package.json" (
    echo ✓ 前端项目文件存在
) else (
    echo × 前端项目文件缺失
    set /a ERROR_COUNT+=1
)

if exist "admin\package.json" (
    echo ✓ 管理后台项目文件存在
) else (
    echo × 管理后台项目文件缺失
    set /a ERROR_COUNT+=1
)

echo.
echo [6] 检查网络端口...
netstat -an | findstr ":8080" >nul
if %errorlevel% equ 0 (
    echo ⚠ 端口8080已被占用，可能影响后端启动
) else (
    echo ✓ 端口8080可用
)

netstat -an | findstr ":5173" >nul
if %errorlevel% equ 0 (
    echo ⚠ 端口5173已被占用，可能影响前端启动
) else (
    echo ✓ 端口5173可用
)

netstat -an | findstr ":5174" >nul
if %errorlevel% equ 0 (
    echo ⚠ 端口5174已被占用，可能影响管理后台启动
) else (
    echo ✓ 端口5174可用
)

echo.
echo ====================================
echo 环境检查结果
echo ====================================
if %ERROR_COUNT% equ 0 (
    echo 🎉 环境检查通过！所有必需组件都已正确安装
    echo.
    echo 您可以使用以下命令启动系统：
    echo   start-mysql.bat  （MySQL版本）
    echo   start-h2.bat     （H2快速体验版）
) else (
    echo ❌ 发现 %ERROR_COUNT% 个问题需要解决
    echo.
    echo 请根据上述提示安装缺失的组件后重新检查
)

echo.
echo [可选] 系统信息
echo 操作系统: 
systeminfo | findstr /B /C:"OS Name"
echo 处理器: 
systeminfo | findstr /B /C:"Processor"
echo 内存: 
systeminfo | findstr /B /C:"Total Physical Memory"

echo.
echo 按任意键退出...
pause >nul













