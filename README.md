# 在线考试系统
## 项目介绍
本项目是一个基于Spring Boot开发的在线考试系统，支持多角色（管理员、教师、学生）操作，提供完整的考试管理、题库管理、试卷组卷、在线考试等功能。系统采用前后端分离架构，后端使用Spring Boot 3.x构建RESTful API，前端可对接任意支持HTTP请求的客户端。

## 技术栈
### 后端技术
- 基础框架 ：Spring Boot 3.2.12
- 安全框架 ：Spring Security + JWT
- 持久层 ：MyBatis-Plus 3.5.11
- 数据库 ：MySQL
- API文档 ：Knife4j 4.4.0 + SpringDoc OpenAPI 2.3.0
- 文件处理 ：Apache POI 5.2.3
- 对象存储 ：阿里云OSS
### 开发环境
- JDK 17+
- Maven 3.8+
- MySQL 8.0+
## 系统功能
### 系统管理模块
- 用户管理：支持管理员、教师、学生三种角色
- 角色管理：基于RBAC的权限控制
- 权限管理：细粒度的功能权限控制
- 系统监控：系统运行状态监控
### 考试管理模块
- 科目管理：支持多级科目分类
- 题库管理：支持单选题、多选题、判断题、填空题四种题型
- 试卷管理：支持自动组卷和手动组卷
- 考试记录：记录学生考试情况和成绩
### 学生端模块
- 考试列表：展示可参加的考试
- 在线考试：支持限时考试和自动提交
- 我的考试：查看历史考试记录和成绩
- 错题本：自动收集错题，便于复习
## 数据库设计
系统主要包含以下核心表：

- sys_user ：用户表，存储所有用户信息
- exam_subject ：科目表，支持多级分类
- exam_question ：题库表，存储各类题目
- exam_paper ：试卷表，包含试卷元数据
- exam_record ：考试记录表，记录学生考试情况
- exam_wrong_book ：错题本表，记录学生错题
详细的数据库结构可查看 db/exam_system.sql 文件。

## 项目结构
plaintext

Open Folder

1

2

3

4

5

6

7

8

9

10

11

exam-system/

├── db/                  # 数据库脚本目录

│   ├── exam_system.sql  # 数据库初始化脚

本

│   ├── init.sql         # 初始数据脚本

│   └── update_tables.sql # 数据库更新脚本

├── doc/                 # 项目文档目录

│   └── 在线考试系统接口文档.md # API接口文

档

├── src/                 # 源代码目录

├── pom.xml              # Maven配置文件

├── mvnw                 # Maven包装器

(Unix/Linux)

└── mvnw.cmd             # Maven包装器

(Windows)

收起代码

## 快速开始
### 环境准备
1. 安装JDK 17或更高版本
2. 安装MySQL 8.0或更高版本
3. 准备Maven环境（或使用项目自带的mvnw）
### 数据库初始化
1. 创建数据库： exam_system
2. 执行SQL脚本：
   bash
   
   运行
   
   Open Folder
   
   1
   
   2
   
   mysql -u用户名 -p密码 exam_system <
   
   db/exam_system.sql
   
   mysql -u用户名 -p密码 exam_system <
   
   db/init.sql
### 配置修改
1. 修改 src/main/resources/application.yml 中的数据库连接信息
2. 根据需要调整其他配置参数
### 编译运行
bash

运行

Open Folder

1

2

3

4

5

6

7

8

# 使用Maven编译

mvn clean package -DskipTests

# 或使用Maven包装器

./mvnw clean package -DskipTests

# 运行应用

java -jar target/exam-system-0.0.

1-SNAPSHOT.jar

### 访问系统
- API文档地址： http://localhost:8080/doc.html
- 默认管理员账号：admin/123456
## 开发指南
### 代码规范
- 遵循阿里巴巴Java开发手册规范
- 使用Lombok简化代码
- 接口设计遵循RESTful风格
### 分支管理
- master：主分支，保持稳定可发布状态
- develop：开发分支，日常开发工作
- feature/*：功能分支，新功能开发
- hotfix/*：热修复分支，生产环境紧急修复
