@echo off
chcp 65001 >nul
echo 启动Spring Boot开发模式（支持热重载）...
echo.
echo 注意：
echo 1. 修改Java代码后会自动重启
echo 2. 修改配置文件后会自动重启
echo 3. 按 Ctrl+C 停止服务
echo.

mvn spring-boot:run -Dspring-boot.run.fork=true

pause
