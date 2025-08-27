@echo off
chcp 65001 >nul
echo ====================================
echo MySQL环境配置工具
echo ====================================
echo.

set MYSQL_BIN="D:\Program Files\MySQL\MySQL Server 9.4\bin\mysql.exe"

echo [1] MySQL版本信息:
%MYSQL_BIN% --version
echo.

echo [2] 测试MySQL连接...
echo 请输入MySQL root密码（通常在安装时设置）:
%MYSQL_BIN% -h localhost -u root -p -e "SELECT VERSION() AS MySQL版本, NOW() AS 当前时间;"

if %errorlevel% equ 0 (
    echo.
    echo ✓ MySQL连接成功！
    echo.
    echo [3] 检查qr_ordering数据库...
    %MYSQL_BIN% -h localhost -u root -p -e "SHOW DATABASES LIKE 'qr_ordering';"
    
    echo.
    echo [4] 是否需要创建数据库？
    echo 如果上面没有显示qr_ordering数据库，请选择Y创建
    set /p create_db="创建qr_ordering数据库? (Y/N): "
    
    if /i "%create_db%"=="Y" (
        echo 正在创建数据库...
        %MYSQL_BIN% -h localhost -u root -p < database\init-mysql.sql
        if %errorlevel% equ 0 (
            echo ✓ 数据库创建成功！
        ) else (
            echo × 数据库创建失败，请检查database\init-mysql.sql文件
        )
    )
    
    echo.
    echo [5] 添加MySQL到系统PATH（可选）
    echo 当前MySQL路径: D:\Program Files\MySQL\MySQL Server 9.4\bin
    echo.
    echo 您可以手动将此路径添加到系统环境变量PATH中，
    echo 这样就可以在任何地方直接使用mysql命令了。
    echo.
    echo 添加方法：
    echo 1. 右键"此电脑" → 属性 → 高级系统设置
    echo 2. 环境变量 → 系统变量 → 找到Path → 编辑
    echo 3. 新建 → 输入: D:\Program Files\MySQL\MySQL Server 9.4\bin
    echo 4. 确定保存，重启命令行
    
) else (
    echo × MySQL连接失败
    echo 可能的原因：
    echo 1. root密码错误
    echo 2. MySQL服务未启动
    echo 3. 端口被占用
)

echo.
echo 按任意键退出...
pause >nul













