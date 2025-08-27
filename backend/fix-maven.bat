@echo off
chcp 65001 >nul
echo ====================================
echo Maven依赖修复工具
echo ====================================
echo.

echo [1] 删除问题文件...
rd /s /q "C:\Users\钟家伟\.m2\repository\net\minidev\accessors-smart\2.4.11" 2>nul
echo ✓ 已清理问题文件

echo.
echo [2] 清理本地缓存...
call mvn dependency:purge-local-repository -DactTransitively=false -DreResolve=false 2>nul
echo ✓ 缓存清理完成

echo.
echo [3] 下载依赖（使用阿里云镜像）...
echo.
call mvn clean compile -Dmaven.repo.remote=https://maven.aliyun.com/repository/public

echo.
echo ====================================
echo 修复完成！
echo ====================================
echo.
echo 下一步：运行 mvn spring-boot:run
echo.
pause