@echo off
chcp 65001 >nul
echo 正在启动扫码点餐系统后端服务...
echo 请确保已安装Java 8+和Maven
echo 请确保MySQL服务已启动

echo.
echo 检查Java环境...
java -version
if %errorlevel% neq 0 (
    echo Java环境未配置，请先安装Java 8+
    pause
    exit /b 1
)

echo.
echo 检查Maven环境...
mvn -version
if %errorlevel% neq 0 (
    echo Maven环境未配置，请先安装Maven
    pause
    exit /b 1
)

echo.
echo 正在启动Spring Boot应用...
mvn spring-boot:run

pause