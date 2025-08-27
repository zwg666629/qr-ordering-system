#!/bin/bash
# 重命名所有YAML文件以避免SnakeYAML冲突
mv src/main/resources/application.yml src/main/resources/application.yml.bak 2>/dev/null
mv src/main/resources/application-h2.yml src/main/resources/application-h2.yml.bak 2>/dev/null
mv src/main/resources/application-mysql.yml src/main/resources/application-mysql.yml.bak 2>/dev/null
mv src/main/resources/application-postgresql.yml src/main/resources/application-postgresql.yml.bak 2>/dev/null
mv src/main/resources/application-render.yml src/main/resources/application-render.yml.bak 2>/dev/null
echo "YAML files renamed to .bak"