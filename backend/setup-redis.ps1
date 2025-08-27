# Redis 自动安装脚本 for Windows
Write-Host "🔧 开始设置 Redis..." -ForegroundColor Green

# 创建Redis目录
$redisDir = "C:\Redis"
if (!(Test-Path $redisDir)) {
    New-Item -ItemType Directory -Path $redisDir -Force | Out-Null
    Write-Host "✅ 创建 Redis 目录: $redisDir" -ForegroundColor Green
}

# 检查是否已经安装
if (Test-Path "$redisDir\redis-server.exe") {
    Write-Host "✅ Redis 已存在，跳过下载" -ForegroundColor Green
} else {
    # 下载Redis for Windows
    $redisUrl = "https://github.com/tporadowski/redis/releases/download/v5.0.14.1/Redis-x64-5.0.14.1.zip"
    $zipFile = "$redisDir\Redis-x64-5.0.14.1.zip"
    
    Write-Host "📥 下载 Redis for Windows..." -ForegroundColor Yellow
    try {
        Invoke-WebRequest -Uri $redisUrl -OutFile $zipFile -UseBasicParsing
        Write-Host "✅ Redis 下载完成" -ForegroundColor Green
        
        # 解压文件
        Write-Host "📂 解压 Redis 文件..." -ForegroundColor Yellow
        Expand-Archive -Path $zipFile -DestinationPath $redisDir -Force
        
        # 删除zip文件
        Remove-Item $zipFile -Force
        Write-Host "✅ Redis 解压完成" -ForegroundColor Green
        
    } catch {
        Write-Host "❌ Redis 下载失败: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "💡 请手动下载并安装，参考 install-redis-windows.md" -ForegroundColor Yellow
        exit 1
    }
}

# 检查Redis进程是否已在运行
$redisProcess = Get-Process -Name "redis-server" -ErrorAction SilentlyContinue
if ($redisProcess) {
    Write-Host "⚠️ Redis 服务器已在运行" -ForegroundColor Yellow
} else {
    # 启动Redis服务器
    Write-Host "🚀 启动 Redis 服务器..." -ForegroundColor Yellow
    try {
        Start-Process -FilePath "$redisDir\redis-server.exe" -ArgumentList "$redisDir\redis.windows.conf" -WindowStyle Minimized
        Write-Host "✅ Redis 服务器启动成功" -ForegroundColor Green
        
        # 等待服务启动
        Start-Sleep -Seconds 3
    } catch {
        Write-Host "❌ Redis 服务器启动失败: $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 测试连接
Write-Host "🔍 测试 Redis 连接..." -ForegroundColor Yellow
try {
    $testResult = & "$redisDir\redis-cli.exe" ping 2>$null
    if ($testResult -eq "PONG") {
        Write-Host "✅ Redis 连接测试成功！" -ForegroundColor Green
        Write-Host "🎉 Redis 已成功启动并运行在 localhost:6379" -ForegroundColor Green
    } else {
        Write-Host "❌ Redis 连接测试失败" -ForegroundColor Red
    }
} catch {
    Write-Host "❌ Redis 连接测试出错: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host "`n📋 下一步操作：" -ForegroundColor Cyan
Write-Host "1. Redis 服务状态：Get-Process redis*" -ForegroundColor White
Write-Host "2. 停止 Redis：Stop-Process -Name redis-server" -ForegroundColor White
Write-Host "3. 现在可以配置应用连接 Redis" -ForegroundColor White