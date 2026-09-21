# 课堂学情监控管理系统

课堂大作业：基于 SpringBoot 3 + MySQL 8 + 原生 HTML/CSS/JS 的课堂学情监控系统。
开发模式：混合模式（Spec 驱动设计 + Vibe Coding 小步迭代实现）。

## 项目结构

```
D:\classcheck_system\
├── backend\              # SpringBoot 后端项目
├── database\
│   └── schema.sql        # 建表 SQL + 初始化测试数据
├── docs\                 # 设计与交付文档
│   ├── 01_需求拆解与功能清单.md
│   ├── 02_系统架构设计.md
│   ├── 03_数据库设计.md
│   └── 04_拓展模块设计_课程与班级管理.md
├── tools\                # 本地工具（Maven，不入库）
├── .gitignore
└── README.md
```

## 本地环境

- JDK 21、Maven 3.9（tools 目录，已配置阿里云镜像）、MySQL 8.0
- 数据库：classcheck_system（root/123456），导入 `database/schema.sql` 后含测试数据
- 测试账号（密码 123456）：student01 / teacher01 / admin

## 快速启动

```bash
cd backend
..\tools\apache-maven-3.9.9\bin\mvn.cmd spring-boot:run
# 浏览器访问 http://localhost:8080/login.html
```
