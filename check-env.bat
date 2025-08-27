<<<<<<< HEAD
@echo off
chcp 65001 >nul
echo ====================================
echo 环境检查工具
echo ====================================
echo.

echo [1] 检查Java...
echo ----------------------------
java -version 2>&1 | findstr "version"
if %errorlevel% equ 0 (
    echo ✅ Java已安装
    where java
) else (
    echo ❌ Java未安装或未配置PATH
    echo    安装包位置: C:\Users\钟家伟\Downloads\OpenJDK17U-jdk_x64_windows_hotspot_17.0.15_6.msi
)

echo.
echo [2] 检查Maven...
echo ----------------------------
mvn -v 2>&1 | findstr "Apache Maven"
if %errorlevel% equ 0 (
    echo ✅ Maven已安装
    where mvn
) else (
    echo ❌ Maven未安装或未配置PATH
    echo    压缩包位置: C:\Users\钟家伟\Downloads\apache-maven-3.9.11-bin.zip
)

echo.
echo [3] 检查PostgreSQL...
echo ----------------------------
psql --version 2>&1 | findstr "psql"
if %errorlevel% equ 0 (
    echo ✅ PostgreSQL已安装
    where psql
) else (
    pg_ctl --version 2>&1 | findstr "pg_ctl"
    if %errorlevel% equ 0 (
        echo ✅ PostgreSQL已安装（服务端）
    ) else (
        echo ❌ PostgreSQL未安装
        echo    安装包位置: C:\Users\钟家伟\Downloads\postgresql-17.4-1-windows-x64.exe
    )
)

echo.
echo [4] 检查MySQL...
echo ----------------------------
mysql --version 2>&1 | findstr "mysql"
if %errorlevel% equ 0 (
    echo ✅ MySQL已安装
    where mysql
) else (
    echo ❌ MySQL未安装
)

echo.
echo [5] 检查Node.js...
echo ----------------------------
node -v 2>&1 | findstr "v"
if %errorlevel% equ 0 (
    echo ✅ Node.js已安装
    where node
) else (
    echo ❌ Node.js未安装
)

echo.
echo ====================================
echo 检查完成！
echo ====================================
echo.
=======
@echo off
chcp 65001 >nul
echo ====================================
echo 环境检查工具
echo ====================================
echo.

echo [1] 检查Java...
echo ----------------------------
java -version 2>&1 | findstr "version"
if %errorlevel% equ 0 (
    echo ✅ Java已安装
    where java
) else (
    echo ❌ Java未安装或未配置PATH
    echo    安装包位置: C:\Users\钟家伟\Downloads\OpenJDK17U-jdk_x64_windows_hotspot_17.0.15_6.msi
)

echo.
echo [2] 检查Maven...
echo ----------------------------
mvn -v 2>&1 | findstr "Apache Maven"
if %errorlevel% equ 0 (
    echo ✅ Maven已安装
    where mvn
) else (
    echo ❌ Maven未安装或未配置PATH
    echo    压缩包位置: C:\Users\钟家伟\Downloads\apache-maven-3.9.11-bin.zip
)

echo.
echo [3] 检查PostgreSQL...
echo ----------------------------
psql --version 2>&1 | findstr "psql"
if %errorlevel% equ 0 (
    echo ✅ PostgreSQL已安装
    where psql
) else (
    pg_ctl --version 2>&1 | findstr "pg_ctl"
    if %errorlevel% equ 0 (
        echo ✅ PostgreSQL已安装（服务端）
    ) else (
        echo ❌ PostgreSQL未安装
        echo    安装包位置: C:\Users\钟家伟\Downloads\postgresql-17.4-1-windows-x64.exe
    )
)

echo.
echo [4] 检查MySQL...
echo ----------------------------
mysql --version 2>&1 | findstr "mysql"
if %errorlevel% equ 0 (
    echo ✅ MySQL已安装
    where mysql
) else (
    echo ❌ MySQL未安装
)

echo.
echo [5] 检查Node.js...
echo ----------------------------
node -v 2>&1 | findstr "v"
if %errorlevel% equ 0 (
    echo ✅ Node.js已安装
    where node
) else (
    echo ❌ Node.js未安装
)

echo.
echo ====================================
echo 检查完成！
echo ====================================
echo.
>>>>>>> a98ace0d91d29ff6a48b77214d22d4a8c174ec8a
pause