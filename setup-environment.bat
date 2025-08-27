@echo off
chcp 65001 >nul
echo ====================================
echo 扫码点餐系统 - 环境配置工具
echo ====================================
echo.

set CURRENT_DIR=%~dp0
set DOWNLOADS_DIR=C:\Users\钟家伟\Downloads

echo [步骤1] 检查Java环境...
java -version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Java已安装
    java -version
) else (
    echo × Java未安装
    echo.
    echo 发现Java安装包：OpenJDK17U-jdk_x64_windows_hotspot_17.0.15_6.msi
    echo 请按以下步骤安装：
    echo 1. 双击运行: %DOWNLOADS_DIR%\OpenJDK17U-jdk_x64_windows_hotspot_17.0.15_6.msi
    echo 2. 按照向导完成安装
    echo 3. 安装完成后重新运行此脚本
    echo.
    pause
    exit /b 1
)

echo.
echo [步骤2] 检查Maven环境...
mvn -version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ Maven已安装
    mvn -version | findstr "Apache Maven"
) else (
    echo × Maven未安装
    echo.
    echo 发现Maven压缩包：apache-maven-3.9.11-bin.zip
    echo 请按以下步骤安装：
    echo.
    echo 1. 解压文件：
    echo    - 将 %DOWNLOADS_DIR%\apache-maven-3.9.11-bin.zip 解压到 C:\Program Files\
    echo    - 确保解压后的文件夹名为：apache-maven-3.9.11
    echo.
    echo 2. 配置环境变量：
    echo    - 按 Win+X，选择"系统"
    echo    - 点击"高级系统设置"
    echo    - 点击"环境变量"
    echo    - 在系统变量中新建：
    echo      变量名：MAVEN_HOME
    echo      变量值：C:\Program Files\apache-maven-3.9.11
    echo    - 在系统变量Path中添加：%%MAVEN_HOME%%\bin
    echo.
    echo 3. 重启命令提示符并重新运行此脚本
    echo.
    pause
    exit /b 1
)

echo.
echo [步骤3] 检查MySQL环境...
mysql --version >nul 2>&1
if %errorlevel% equ 0 (
    echo ✓ MySQL已安装
    mysql --version
) else (
    echo × MySQL未安装
    echo.
    echo 检测到PostgreSQL安装包，但推荐使用MySQL
    echo 请选择以下选项：
    echo.
    echo [A] 下载并安装MySQL（推荐）
    echo [B] 使用PostgreSQL（需要修改配置）
    echo [C] 跳过数据库配置
    echo.
    choice /c ABC /n /m "请选择 [A/B/C]: "
    if errorlevel 3 goto skip_db
    if errorlevel 2 goto use_postgresql
    if errorlevel 1 goto install_mysql
)

:check_complete
echo.
echo ====================================
echo 环境检查完成！
echo ====================================
echo.
echo 下一步：
echo 1. 确保所有环境都已正确安装
echo 2. 运行 backend\start.bat 启动后端服务
echo.
pause
exit /b 0

:install_mysql
echo.
echo 请访问以下网址下载MySQL：
echo https://dev.mysql.com/downloads/installer/
echo.
echo 下载 mysql-installer-community-8.0.xx.msi
echo 选择"Developer Default"安装类型
echo 设置root密码为：123456（或自定义，需修改application.yml）
echo.
pause
exit /b 0

:use_postgresql
echo.
echo 使用PostgreSQL需要：
echo 1. 安装 %DOWNLOADS_DIR%\postgresql-17.4-1-windows-x64.exe
echo 2. 修改后端配置文件以支持PostgreSQL
echo.
pause
exit /b 0

:skip_db
echo 跳过数据库配置...
exit /b 0