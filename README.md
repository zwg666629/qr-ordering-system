# 扫码点餐系统

## 项目简介
基于Vue + Spring Boot + MySQL的扫码点餐小程序

## 技术栈
- 前端：Vue 3 + Vant + Vite
- 后端：Spring Boot + MyBatis Plus + MySQL
- 支付：微信支付 + 支付宝支付

## 项目结构
```
qr-ordering-system/
├── backend/          # Spring Boot后端
├── frontend/         # Vue前端客户端
├── admin/           # Vue管理后台（待开发）
└── database/        # 数据库脚本
```

## 功能模块
1. 客户端：浏览菜单、购物车、下单、支付
2. 管理后台：菜品管理、订单管理、统计分析（待开发）

## 环境要求
- Java 8+
- Node.js 16+
- MySQL 8.0+
- Maven 3.6+

## 快速开始

### 🚀 一键启动（推荐）

#### Windows系统
```bash
# MySQL版本（推荐）
start-mysql.bat

# 或者H2内存数据库版本（快速体验）
start-h2.bat
```

### 📋 手动启动

#### 1. 数据库准备

**方式一：MySQL数据库（推荐生产环境）**
```bash
# 确保MySQL服务已启动
# 创建数据库
mysql -u root -p -e "CREATE DATABASE qr_ordering CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# 或执行初始化脚本
mysql -u root -p < database/init-mysql.sql
```

**方式二：H2内存数据库（快速体验）**
```bash
# 无需额外配置，使用 application-h2.yml 配置文件
```

#### 2. 后端启动
```bash
cd backend

# MySQL版本
mvn spring-boot:run

# H2版本
mvn spring-boot:run -Dspring.profiles.active=h2
```

#### 3. 前端启动
```bash
cd frontend
npm install
npm run dev
```

#### 4. 管理后台启动
```bash
cd admin
npm install  
npm run dev
```

### 🌐 访问地址
- **客户端**：http://localhost:5173
- **管理后台**：http://localhost:5174 （默认账号：admin/admin123）
- **API文档**：http://localhost:8080/doc.html
- **H2数据库控制台**：http://localhost:8080/h2-console （仅H2版本）

## 开发说明

### 后端开发
- 使用Spring Boot 2.7.14
- 集成MyBatis Plus进行数据库操作
- 使用Knife4j生成API文档
- 支持跨域访问

### 前端开发
- 使用Vue 3 + Composition API
- 使用Vant UI组件库（移动端优化）
- 使用Pinia进行状态管理
- 使用Vite构建工具

### 数据库设计
- 分类表（category）：菜品分类管理
- 菜品表（dish）：菜品信息管理  
- 餐桌表（dining_table）：餐桌和二维码管理
- 订单表（orders）：订单主表
- 订单详情表（order_detail）：订单商品详情
- 管理员表（admin）：后台管理员账号

## 待开发功能
- [ ] 管理后台界面
- [ ] 订单管理系统
- [ ] 支付功能集成
- [ ] 二维码生成
- [ ] 实时订单状态推送
- [ ] 数据统计分析