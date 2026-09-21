-- ============================================================
-- 课堂学情监控管理系统 数据库脚本
-- 包含：全部建表 SQL + 初始化测试数据
-- 适用：MySQL 8.0，数据库 classcheck_system（utf8mb4）
-- 导入后系统可直接运行
-- ============================================================

-- 创建数据库（若未创建）
CREATE DATABASE IF NOT EXISTS classcheck_system DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE classcheck_system;

SET FOREIGN_KEY_CHECKS = 0;

-- 1. 用户表（学生/教师/管理员 三类角色）
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户编号',
    username    VARCHAR(50)  NOT NULL COMMENT '登录账号',
    password    VARCHAR(100) NOT NULL COMMENT '密码（MD5加盐存储）',
    name        VARCHAR(50)  NOT NULL COMMENT '姓名',
    role        TINYINT      NOT NULL COMMENT '角色：1学生 2教师 3管理员',
    age         INT                   COMMENT '年龄',
    gender      VARCHAR(10)           COMMENT '性别',
    phone       VARCHAR(20)           COMMENT '联系方式',
    student_no  VARCHAR(30)           COMMENT '学号（学生专属）',
    grade       VARCHAR(20)           COMMENT '年级（学生专属）',
    class_name  VARCHAR(30)           COMMENT '班级（学生专属）',
    major       VARCHAR(50)           COMMENT '专业（学生专属）',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '账号状态：1正常 0禁用',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB COMMENT='用户表';

-- 2. 课程表（拓展模块：课程与班级管理）
DROP TABLE IF EXISTS course;
CREATE TABLE course (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '课程ID',
    name        VARCHAR(100) NOT NULL COMMENT '课程名称',
    teacher_id  BIGINT       NOT NULL COMMENT '任课教师ID',
    description VARCHAR(255)          COMMENT '课程简介',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_teacher (teacher_id),
    CONSTRAINT fk_course_teacher FOREIGN KEY (teacher_id) REFERENCES sys_user (id)
) ENGINE=InnoDB COMMENT='课程表';

-- 3. 班级表（拓展模块）
DROP TABLE IF EXISTS class_info;
CREATE TABLE class_info (
    id          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '班级ID',
    name        VARCHAR(50) NOT NULL COMMENT '班级名称',
    teacher_id  BIGINT      NOT NULL COMMENT '所属教师ID',
    course_id   BIGINT      NOT NULL COMMENT '关联课程ID',
    create_time DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_teacher (teacher_id),
    KEY idx_course (course_id),
    CONSTRAINT fk_class_teacher FOREIGN KEY (teacher_id) REFERENCES sys_user (id),
    CONSTRAINT fk_class_course  FOREIGN KEY (course_id)  REFERENCES course (id)
) ENGINE=InnoDB COMMENT='班级表';

-- 4. 班级学生关联表（拓展模块）
DROP TABLE IF EXISTS class_student;
CREATE TABLE class_student (
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    class_id    BIGINT   NOT NULL COMMENT '班级ID',
    student_id  BIGINT   NOT NULL COMMENT '学生ID',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_class_student (class_id, student_id),
    CONSTRAINT fk_cs_class   FOREIGN KEY (class_id)   REFERENCES class_info (id),
    CONSTRAINT fk_cs_student FOREIGN KEY (student_id) REFERENCES sys_user (id)
) ENGINE=InnoDB COMMENT='班级学生关联表';

-- 5. 学情反馈表
DROP TABLE IF EXISTS feedback;
CREATE TABLE feedback (
    id          BIGINT   NOT NULL AUTO_INCREMENT COMMENT '反馈ID',
    student_id  BIGINT   NOT NULL COMMENT '学生ID',
    score       TINYINT  NOT NULL COMMENT '分值1-6（6完全理解 1完全没理解）',
    course_id   BIGINT            COMMENT '所属课程ID（可空=默认课堂）',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    PRIMARY KEY (id),
    KEY idx_student (student_id),
    KEY idx_course (course_id),
    KEY idx_create_time (create_time),
    CONSTRAINT fk_fb_student FOREIGN KEY (student_id) REFERENCES sys_user (id),
    CONSTRAINT fk_fb_course  FOREIGN KEY (course_id)  REFERENCES course (id),
    CONSTRAINT chk_score CHECK (score BETWEEN 1 AND 6)
) ENGINE=InnoDB COMMENT='学情反馈表';

-- 6. 学情统计快照表
DROP TABLE IF EXISTS statistics;
CREATE TABLE statistics (
    id            BIGINT        NOT NULL AUTO_INCREMENT COMMENT '统计ID',
    stat_time     DATETIME      NOT NULL COMMENT '统计时间（快照时刻）',
    course_id     BIGINT                 COMMENT '课程ID（可空=默认课堂）',
    course_name   VARCHAR(100)           COMMENT '课程名称冗余',
    total_count   INT           NOT NULL DEFAULT 0 COMMENT '提交表情的学生总人数（去重）',
    avg_score     DECIMAL(4,2)  NOT NULL DEFAULT 0 COMMENT '班级平均分',
    variance      DECIMAL(10,4) NOT NULL DEFAULT 0 COMMENT '分数方差',
    score1_count  INT           NOT NULL DEFAULT 0 COMMENT '1分（完全没理解）人数',
    score2_count  INT           NOT NULL DEFAULT 0 COMMENT '2分人数',
    score3_count  INT           NOT NULL DEFAULT 0 COMMENT '3分人数',
    score4_count  INT           NOT NULL DEFAULT 0 COMMENT '4分人数',
    score5_count  INT           NOT NULL DEFAULT 0 COMMENT '5分人数',
    score6_count  INT           NOT NULL DEFAULT 0 COMMENT '6分（完全理解）人数',
    create_time   DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库时间',
    PRIMARY KEY (id),
    KEY idx_stat_time (stat_time),
    KEY idx_course (course_id)
) ENGINE=InnoDB COMMENT='学情统计快照表';

-- 7. 讨论消息表
DROP TABLE IF EXISTS discussion_message;
CREATE TABLE discussion_message (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    sender_id   BIGINT       NOT NULL COMMENT '发送人ID',
    content     TEXT                  COMMENT '文字内容',
    image_url   VARCHAR(255)          COMMENT '图片地址（可空）',
    create_time DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    PRIMARY KEY (id),
    KEY idx_sender (sender_id),
    KEY idx_create_time (create_time),
    CONSTRAINT fk_msg_sender FOREIGN KEY (sender_id) REFERENCES sys_user (id)
) ENGINE=InnoDB COMMENT='讨论消息表';

-- 8. 系统参数表
DROP TABLE IF EXISTS sys_config;
CREATE TABLE sys_config (
    id           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '配置ID',
    config_key   VARCHAR(50)  NOT NULL COMMENT '配置键',
    config_value VARCHAR(100) NOT NULL COMMENT '配置值',
    update_time  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB COMMENT='系统参数表';

SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- 初始化测试数据
-- 测试账号（密码统一为 123456，MD5加盐后存储）：
--   学生：student01 / student02 / student03
--   教师：teacher01 / teacher02
--   管理员：admin
-- ============================================================

INSERT INTO sys_user (username, password, name, role, age, gender, phone, student_no, grade, class_name, major, status) VALUES
('student01', '9743c9b2810ecf639458c80eccbb213a', '张三', 1, 20, '男', '13800000001', '20220101', '2022级', '计科2201', '计算机科学与技术', 1),
('student02', '9743c9b2810ecf639458c80eccbb213a', '李四', 1, 21, '女', '13800000002', '20220102', '2022级', '计科2201', '计算机科学与技术', 1),
('student03', '9743c9b2810ecf639458c80eccbb213a', '王五', 1, 20, '男', '13800000003', '20220103', '2022级', '计科2202', '软件工程', 1),
('student04', '9743c9b2810ecf639458c80eccbb213a', '赵六', 1, 22, '女', '13800000004', '20220104', '2022级', '计科2202', '软件工程', 1),
('student05', '9743c9b2810ecf639458c80eccbb213a', '孙七', 1, 20, '男', '13800000005', '20220105', '2022级', '计科2203', '数据科学', 1),
('teacher01', '9743c9b2810ecf639458c80eccbb213a', '周老师', 2, 35, '男', '13900000001', NULL, NULL, NULL, NULL, 1),
('teacher02', '9743c9b2810ecf639458c80eccbb213a', '吴老师', 2, 32, '女', '13900000002', NULL, NULL, NULL, NULL, 1),
('admin',     '9743c9b2810ecf639458c80eccbb213a', '系统管理员', 3, 30, '男', '13700000000', NULL, NULL, NULL, NULL, 1);

-- 课程（拓展模块测试数据：教师1名下两门课，教师2名下一门课）
INSERT INTO course (name, teacher_id, description) VALUES
('Java程序设计', 6, '面向对象的Java基础与进阶课程'),
('数据库原理',   6, '关系数据库理论与SQL实践'),
('数据结构',     7, '常用数据结构与算法分析');

-- 班级（拓展模块测试数据）
INSERT INTO class_info (name, teacher_id, course_id) VALUES
('Java班01', 6, 1),
('Java班02', 6, 1),
('数据库班01', 6, 2),
('数据结构班01', 7, 3);

-- 班级学生关联（计科2201 学生1/2 在 Java班01；学生3 在 Java班02；学生4 在 数据库班01；学生5 在 数据结构班01）
INSERT INTO class_student (class_id, student_id) VALUES
(1, 1), (1, 2),
(2, 3),
(3, 4),
(4, 5);

-- 学情反馈示例数据（最近若干条，供统计演示；score 1-6）
INSERT INTO feedback (student_id, score, course_id) VALUES
(1, 6, 1), (2, 5, 1), (3, 4, 1),
(1, 5, 2), (4, 3, 2),
(5, 6, 3);

-- 学情统计快照示例（一条，供历史查询演示）
INSERT INTO statistics (stat_time, course_id, course_name, total_count, avg_score, variance, score1_count, score2_count, score3_count, score4_count, score5_count, score6_count) VALUES
(NOW() - INTERVAL 5 MINUTE, 1, 'Java程序设计', 3, 5.00, 0.6667, 0, 0, 0, 1, 1, 1);

-- 讨论消息示例
INSERT INTO discussion_message (sender_id, content, image_url) VALUES
(6, '同学们，今天的内容都理解了吗？有疑问可以在讨论区提出', NULL),
(1, '老师，第3章的例子能再讲一遍吗？', NULL);

-- 系统参数：统计周期默认 5 分钟（管理员可配 5-10）
INSERT INTO sys_config (config_key, config_value) VALUES
('stats_period_minutes', '5');
