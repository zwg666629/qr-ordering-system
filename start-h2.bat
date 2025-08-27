@echo off
chcp 65001 >nul
echo ====================================
echo 扫码点餐系统 - H2版本启动脚本
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
echo [切换到H2配置]
cd backend
copy src\main\resources\application-h2.yml src\main\resources\application.yml >nul
copy pom-h2.xml pom.xml >nul
echo ✓ 已切换到H2内存数据库配置

echo.
echo [启动后端服务]
echo 正在启动Spring Boot应用...
start "QR-Ordering Backend (H2)" mvn spring-boot:run
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
echo 🎉 系统启动完成！（H2版本）
echo ====================================
echo.
echo 访问地址：
echo - 客户端：    http://localhost:5173
echo - 管理后台：  http://localhost:5174  
echo - API文档：   http://localhost:8080/doc.html
echo - H2控制台：  http://localhost:8080/h2-console
echo.
echo H2数据库连接信息：
echo - JDBC URL: jdbc:h2:mem:testdb
echo - 用户名: sa
echo - 密码: (留空)
echo.
echo 默认管理员账号：admin / admin123
echo.
echo 注意：H2是内存数据库，重启后数据会丢失
echo.
echo 按任意键关闭此窗口...
pause >nul













