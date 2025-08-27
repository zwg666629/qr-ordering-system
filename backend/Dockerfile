# 构建阶段
FROM maven:3.8-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
# 重命名YAML文件以避免SnakeYAML冲突
RUN find src/main/resources -name "*.yml" -o -name "*.yaml" | while read f; do mv "$f" "$f.bak"; done
RUN mvn clean package -DskipTests -B

# 运行阶段
FROM openjdk:11-jre-slim
WORKDIR /app

# 安装必要工具
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 复制构建好的JAR文件
COPY --from=build /app/target/*.jar app.jar

# 创建必要的目录
RUN mkdir -p /app/uploads /app/logs

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Render使用的端口
EXPOSE 10000

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s \
  CMD curl -f http://localhost:10000/api/health || exit 1

# 设置环境变量
ENV SERVER_PORT=10000

# 启动应用 - 不再指定profile，因为现在使用properties文件
ENTRYPOINT ["java", "-Xmx512m", "-Xms256m", "-jar", "/app/app.jar"]