# Windows 上安装 Redis 指南

## 方法 1：下载官方Redis for Windows (推荐)

1. 访问 Redis Windows 发布页面：
   https://github.com/tporadowski/redis/releases

2. 下载最新的 `Redis-x64-*.zip` 文件

3. 解压到任意文件夹，比如 `C:\Redis\`

4. 打开 PowerShell 管理员模式，进入 Redis 文件夹：
   ```powershell
   cd C:\Redis
   redis-server.exe redis.windows.conf
   ```

## 方法 2：使用 Docker Desktop (如果已安装)

```bash
# 拉取并运行Redis容器
docker run -d --name redis -p 6379:6379 redis:latest

# 查看运行状态
docker ps
```

## 方法 3：使用 WSL2 + Ubuntu (如果已启用)

```bash
# 在WSL中安装Redis
sudo apt update
sudo apt install redis-server -y

# 启动Redis服务
sudo service redis-server start

# 测试连接
redis-cli ping
```

## 验证安装

安装完成后，打开新的命令行窗口测试：

```bash
redis-cli ping
# 应该返回：PONG
```

## 配置Redis开机自启动 (Windows服务)

```powershell
# 以管理员身份运行 PowerShell
redis-server --service-install redis.windows.conf
redis-server --service-start
```
