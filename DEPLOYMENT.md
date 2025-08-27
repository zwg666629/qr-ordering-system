# 🚀 扫码点餐系统云部署指南

## 📋 部署架构

- **前端**: GitHub Pages（免费）
- **后端**: Render.com（免费套餐）
- **数据库**: Render PostgreSQL 或 Supabase（免费）

## 🛠️ 部署步骤

### 1️⃣ 部署前端到 GitHub Pages

#### 自动部署（已配置）
1. 进入 GitHub 仓库设置 → Pages
2. Source 选择 "GitHub Actions"
3. 每次推送到 main 分支会自动部署

#### 访问地址
```
https://zwg666629.github.io/qr-ordering-system/
```

### 2️⃣ 部署后端到 Render

#### 步骤：
1. 访问 [Render.com](https://render.com) 并注册
2. 点击 "New +" → "Web Service"
3. 连接 GitHub 仓库
4. 选择 `zwg666629/qr-ordering-system`
5. 配置：
   - Name: `qr-ordering-backend`
   - Root Directory: `backend`
   - Environment: `Java`
   - Build Command: `mvn clean package`
   - Start Command: `java -jar target/*.jar`
6. 添加环境变量：
   ```
   SERVER_PORT=10000
   SPRING_PROFILES_ACTIVE=production
   ```
7. 点击 "Create Web Service"

### 3️⃣ 配置数据库

#### 选项 A：Render PostgreSQL
1. 在 Render 创建 PostgreSQL 数据库（免费90天）
2. 复制连接字符串
3. 添加到后端环境变量

#### 选项 B：Supabase（推荐）
1. 访问 [Supabase.com](https://supabase.com)
2. 创建新项目（免费500MB）
3. 获取连接字符串
4. 配置到后端

### 4️⃣ 更新前端 API 地址

编辑 `frontend/src/api/index.js`：
```javascript
const API_URL = import.meta.env.VITE_API_URL || 
  'https://qr-ordering-backend.onrender.com';
```

## 🔄 自动部署配置

### GitHub Actions（已配置）
- 推送到 main 分支自动部署前端
- 配置文件：`.github/workflows/deploy-frontend.yml`

### Render 自动部署
- 连接 GitHub 后自动部署
- 配置文件：`render.yaml`

## 🌟 其他免费部署选项

### 前端替代方案
| 平台 | 特点 | 限制 |
|------|------|------|
| Vercel | 性能最佳 | 100GB带宽/月 |
| Netlify | 功能丰富 | 100GB带宽/月 |
| Cloudflare Pages | 无限带宽 | 500次构建/月 |

### 后端替代方案
| 平台 | 特点 | 限制 |
|------|------|------|
| Railway | 简单易用 | $5免费额度/月 |
| Fly.io | 全球部署 | 3个小实例免费 |
| Glitch | 即时部署 | 1000小时/月 |

### 数据库替代方案
| 平台 | 类型 | 免费额度 |
|------|------|----------|
| PlanetScale | MySQL | 5GB存储 |
| Neon | PostgreSQL | 3GB存储 |
| MongoDB Atlas | MongoDB | 512MB存储 |

## 📝 注意事项

1. **免费套餐限制**：
   - Render 免费实例15分钟无访问会休眠
   - 首次访问可能需要30秒启动时间
   
2. **生产环境优化**：
   - 配置 CDN 加速前端资源
   - 使用环境变量管理敏感信息
   - 配置 HTTPS 证书（自动提供）
   
3. **监控和日志**：
   - Render 提供基础监控
   - 可接入 Sentry 进行错误追踪

## 🔗 相关链接

- [GitHub Pages 文档](https://pages.github.com/)
- [Render 文档](https://render.com/docs)
- [Supabase 文档](https://supabase.com/docs)

## 💡 升级建议

当项目增长时，考虑：
1. 使用 CDN（Cloudflare 免费）
2. 添加缓存层（Redis）
3. 升级到付费套餐获得更好性能

---
🚀 祝部署顺利！如有问题，请创建 Issue。