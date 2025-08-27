# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述
这是一个基于Vue 3 + Spring Boot的扫码点餐系统，包含客户端、管理后台和后端API三个主要模块。

## 架构结构

### 后端架构 (backend/)
- **技术栈**: Spring Boot 2.6.15 + MyBatis Plus + MySQL/H2
- **主要依赖**: Druid连接池、Redis缓存、JWT认证、ZXing二维码、Knife4j文档
- **包结构**:
  - `controller/`: REST API控制器层
  - `service/`: 业务逻辑服务层  
  - `mapper/`: MyBatis数据访问层
  - `entity/`: 数据库实体类
  - `dto/`: 数据传输对象
  - `vo/`: 视图对象
  - `config/`: Spring配置类
  - `util/`: 工具类

### 前端架构 (frontend/)
- **技术栈**: Vue 3 + Composition API + Vant + Pinia + Vite
- **移动端优化**: 使用Vant UI组件库专为移动设备设计
- **状态管理**: Pinia存储购物车和用户信息

### 管理后台 (admin/)
- **技术栈**: Vue 3 + Element Plus + ECharts + Pinia
- **桌面端管理**: 管理员可管理菜品、订单、用户等

## 常用命令

### 开发环境启动
```bash
# 一键启动MySQL版本（推荐）
start-mysql.bat

# 一键启动H2内存数据库版本（快速体验）
start-h2.bat

# 手动启动后端
cd backend
mvn spring-boot:run

# H2版本
mvn spring-boot:run -Dspring.profiles.active=h2

# 前端客户端
cd frontend  
npm install
npm run dev

# 管理后台
cd admin
npm install
npm run dev
```

### 构建与测试
```bash
# 前端构建
cd frontend && npm run build
cd admin && npm run build

# 后端测试
cd backend && mvn test

# 后端构建
cd backend && mvn clean package
```

### 开发工具
- **API文档**: http://localhost:8080/doc.html (Knife4j)
- **H2数据库控制台**: http://localhost:8080/h2-console
- **访问地址**: 
  - 客户端: http://localhost:5173
  - 管理后台: http://localhost:5174
  - 后端API: http://localhost:8080

## 数据库配置

### MySQL配置 (生产环境)
- **数据库**: `qr_ordering` 
- **字符集**: utf8mb4
- **初始化脚本**: `database/init-mysql.sql`

### H2配置 (开发环境)
- **内存数据库**: 快速启动，数据不持久化
- **配置文件**: `application-h2.yml`

## 核心业务功能

### 用户端功能
1. 扫码进入餐厅菜单
2. 浏览菜品分类和详情
3. 添加商品到购物车
4. 提交订单并支付
5. 查看订单状态和历史

### 管理端功能  
1. 菜品分类管理
2. 菜品信息管理
3. 餐桌和二维码管理
4. 订单管理和状态更新
5. 用户管理
6. 数据统计分析

## 开发注意事项

### 代码规范
- 后端使用Lombok简化代码
- 前端使用Composition API + `<script setup>`
- 统一使用UTF-8编码
- MyBatis Plus自动驼峰转换

### 文件上传
- **上传路径**: `./uploads`
- **文件大小限制**: 5MB
- **支持类型**: 图片文件（菜品图片、头像等）

### 认证机制
- 使用JWT Token进行用户认证
- Token有效期24小时
- 用户信息存储在Redis缓存中

### 环境配置
- **开发环境**: 支持热重载（Spring DevTools + Vite HMR）
- **数据库**: 支持MySQL和H2双重配置
- **跨域**: 后端已配置CORS支持前端开发

## 待开发功能
- 微信支付和支付宝支付集成
- 实时订单状态推送（WebSocket）
- 二维码批量生成工具
- 更详细的数据统计报表
- 移动端管理功能