# AI 使用情况报告

## 一、基本信息

| 项目 | 内容 |
|---|---|
| 姓名 | ____________ |
| 学号 | ____________ |
| 班级 | ____________ |
| 提交日期 | ____________ |
| 开发的系统 | 课堂学情监控管理系统 |
| 开发模式 | ☐ Spec驱动　☐ Vibe Coding　☑ **混合模式**（Spec 驱动拆解需求 → 按模块迭代实现，与两份驱动手册对应） |

---

## 二、AI 工具使用清单

| 工具名称 | 产地（国产/海外） | 工具类型 | 主要使用场景 | 使用次数 / 大致时长 |
|---|---|---|---|---|
| 豆包（豆包办公 · 工作模式） | 国产 | 通用大模型类 | 需求拆解、系统设计、代码生成、调试排错、文档起草 | 30+ 次 / 跨 7 个开发任务 |
| 豆包内置浏览器自动化 | 国产 | 其他辅助工具 | 多角色登录、逐页运行截图、导出文件验证 | 10+ 次 |

> 说明：代码编辑、编译运行、接口联调均在 IDEA + 本地 MySQL 环境完成；AI 未直接操作本地开发环境，所有修改均由本人落盘。

---

## 三、各阶段 AI 使用整体情况

### 3.1 需求理解与功能拆解阶段

- **AI 完成的工作**：阅读《课堂学情监控管理系统产品要求文档》全文，按学生端 / 教师端 / 管理员端整理出结构化功能清单初稿（学生 3 项、教师 5 项、管理员 2 项及公共约束）。
- **AI 的作用（辅助 / 主要完成）**：**辅助**
- **自行调整的内容**：将 AI 清单与 PRD 原文逐条对照核对，确认无遗漏、无扩展；补充了"越权访问拦截""账号禁用后不可登录"等约束性要求；确定基础功能与自主拓展模块的边界。
- **对应关键提示词编号**：第 1 条

### 3.2 系统设计阶段

- **AI 完成的工作**：给出 Controller / Service / Mapper / Entity 分层架构、8 个功能模块划分、统一 JSON 返回格式与 Token 会话方案。
- **AI 的作用（辅助 / 主要完成）**：**辅助**
- **自行调整的内容**：本人拍板技术栈（Java 21 + Spring Boot 3.3 + MyBatis-Plus + MySQL 8）；设计"统计周期动态可配、修改后无需重启"的调度机制；决定按课程维度独立统计并保留历史快照；规划三端页面结构。
- **对应关键提示词编号**：第 2 条

### 3.3 代码生成阶段

- **AI 完成的工作**：按模块生成大部分代码初稿——认证（三种登录 + 拦截器）、学情反馈与定时统计、历史查询与 Excel 导出、在线讨论区、用户管理、课程与班级管理，以及三端前端页面。
- **AI 的作用（辅助 / 主要完成）**：**辅助**（初稿生成由 AI 完成，全部审查、修改、补齐由本人主导）
- **自行调整的内容**：密码改为 MD5 加盐存储；短信 / 第三方登录无真实服务资质，改为模拟实现；动态触发器改用 Spring Boot 3 的函数式 Trigger；讨论消息身份一律取自服务端登录态、前端做 HTML 转义防 XSS；导出文件名用 UTF-8 编码、数值用数值型单元格等。
- **对应关键提示词编号**：第 4~11 条

### 3.4 调试与排错阶段

- **AI 完成的工作**：对编译报错、接口异常提供定位线索与修复建议；协助复核统计口径。
- **AI 的作用（辅助 / 主要完成）**：**辅助**
- **自行调整的内容**：本人独立设计并执行全部测试用例（认证 16 项、用户管理 19 项、课程班级管理 15 项断言全部通过）；统计数值逐项手算核对；定位并修复"同秒重复提交导致人数虚增""快照同秒排序不确定"两个缺陷；修正讨论区列表 500（Mapper 列名错误）与初始密码哈希不匹配问题。
- **对应关键提示词编号**：第 6、9 条（修正过程详见第六节）

### 3.5 文档整理阶段

- **AI 完成的工作**：依据真实配置起草《系统安装调试手册》《新增业务模块说明文档》《用户使用手册》初稿。
- **AI 的作用（辅助 / 主要完成）**：**辅助**（初稿由 AI 生成，事实核验与修订由本人完成）
- **自行调整的内容**：本人逐一核验文档中的端口、账号、路径、验证记录等事实；设计并执行全部运行截图方案（统一比例、覆盖三端与新增模块）；为拓展模块文档补充需求提出理由、联动设计与开发分工表，明确本人独立完成部分。
- **对应关键提示词编号**：第 12、13 条

---

## 四、关键提示词输入输出记录

> 以下提示词均取自开发过程真实记录（详见 records/AI使用记录.md），提示词为本人发送原文，AI 输出摘要由本人提炼。

| 序号 | 使用阶段 | 提示词输入（完整内容） | AI 输出核心摘要 | 是否自行修正 | 修正说明 |
|---|---|---|---|---|---|
| 1 | 需求理解与功能拆解 | 向 AI 提供《产品要求文档》全文，要求"严格基于文档拆解成可实现的功能点清单，按学生端、教师端、管理员端分类，每个功能点一句话说明，禁止扩展文档外需求，不要技术实现内容" | 输出三端结构化功能清单（学生3项、教师5项、管理员2项、公共约束） | 否 | 逐条对照 PRD 核对无遗漏 |
| 2 | 系统架构与模块分解 | "技术栈用 Java SpringBoot + MySQL + 原生HTML/CSS/JS，给出分层架构、按功能模块拆分核心类/接口、说明前后端交互方式，不生成代码" | 给出分层架构、8 个功能模块划分、统一 JSON 返回 {code,message,data}、Token 会话方案 | 是 | 定时统计从固定 @Scheduled 改为 SchedulingConfigurer 动态读取配置周期 |
| 3 | 数据库设计 | "设计 MySQL 表结构，包含用户表（三类角色）、学情反馈表、统计表、讨论消息表，字段严格对应 PRD，标注主键外键类型注释，输出完整建表 SQL" | 生成 sys_user、feedback、statistics、discussion_message、sys_config 等表 SQL | 是 | 密码字段改为 MD5 加盐；统计表用 score1_count~score6_count 六列存分布；补充测试数据 |
| 4 | 前端页面生成 | "写登录页，三个标签页（密码/短信/第三方），登录成功按角色跳转对应首页；公共 JS 封装 fetch 自动带 token、401 跳登录；样式简洁" | 生成 login.html（三标签+验证码倒计时+模拟授权）、common.js、style.css、三端占位首页 | 是 | 补充测试账号提示、已登录自动跳转、角色不符自动跳回对应首页 |
| 5 | 后端接口生成 | "实现学生6档表情反馈提交（1-6分）；教师端按周期统计：每个学生每门课取最后一次反馈，计算提交人数、各分数段人数、平均分、总体方差，结果存快照表；统计周期5-10分钟管理员可配、改配置不用重启；提供教师手动立即统计接口" | 生成 Feedback/Statistics/SysConfig 实体与 Mapper、FeedbackService、StatsService（分组聚合+均值方差）、SchedulingConfigurer 动态触发器、相关控制器 | 是 | 动态触发器初版误用 Spring 旧版 API 编译报错，改为实现 Trigger 函数式接口、nextExecution 返回 Instant |
| 6 | 后端接口生成（讨论区） | "实现学生和教师共用的在线讨论区：可发文字和图片，消息显示发送人姓名和角色（学生/教师徽标）、内容、图片、时间；前端约3秒轮询实现实时刷新，自己的消息靠右；图片上传到本地 uploads 目录，限制10MB、仅允许常见图片格式、文件名用UUID；未登录不能访问" | 生成 DiscussionMessage 实体/VO/Mapper、DiscussionService、FileStorageService、DiscussionController、/uploads/** 静态映射、共享前端 discuss.js | 是 | 发送人身份一律取自服务端登录态（防伪造）；消息文本与姓名做 HTML 转义防 XSS；轮询在页面不可见时暂停 |
| 7 | 后端接口生成（用户管理） | "实现管理员对学生/教师/管理员三类账号的增删改查：可按角色筛选、按账号/姓名/手机号搜索；新增和编辑用弹窗，学生角色额外显示学号/年级/班级/专业字段；新增用户默认密码123456、账号唯一；可启用禁用账号；删除用户时级联清理其反馈、讨论消息和班级关联；管理员不能删除或禁用自己；接口仅管理员可访问" | 生成 UserDTO、UserManageService（角色化必填校验、账号唯一、密码加密、事务级联删除、自我保护）、UserManageController，重写 admin 用户管理页签 | 是 | 删除用户时统计快照作为历史数据保留；联系方式补充 11 位手机号格式校验；编辑时角色字段锁定不可变更 |
| 8 | **自主新增模块开发** | "实现自主拓展模块课程与班级管理：教师维护我的课程（增删改），每门课程下建班级（增删），每个班级维护学生名单（从全校学生中批量勾选加入、自动去重、可移除）；教师只能操作自己名下的课程班级；学生端可查看自己加入的课程与所在班级；删除课程时事务级联清理班级和名单，该课程已有反馈归入默认课堂；学生按课程提交的反馈要能按课程维度统计" | 生成 ClassInfo/ClassStudent 实体与 Mapper、CourseDTO/ClassDTO/AddStudentsDTO/StudentCourseVO、CourseManageService（三级操作+归属校验+事务级联）、教师三列钻取式管理界面、学生端我的课程卡片 | 是 | 删除课程时反馈不直接删除，而是把 course_id 置空归入默认课堂保留数据；班级与课程冗余 teacher_id 便于归属校验；添加学生逐个校验存在性与角色，重复加入跳过并返回实际加入人数 |

> 说明：UML 图由本人依据分层架构手工绘制于设计底稿中；调试排错阶段以第 5、8 条修正过程为代表（详见第六节），未另设独立提示词。

---

## 五、AI 贡献度评估

### 1. 整体 AI 贡献度：**50%**

### 2. 分阶段贡献度

| 阶段 | AI 贡献度 |
|---|---|
| 需求理解与功能拆解阶段 | 30% |
| 系统设计阶段 | 50% |
| 代码生成阶段 | 60% |
| 调试与排错阶段 | 50% |
| 文档整理阶段 | 60% |

### 3. 评估说明

AI 在本作业中的核心价值在于**快速产出可运行的初稿与文档骨架**，大幅压缩了重复性编码与排版耗时：需求清单、分层架构、数据库表结构、各模块接口与页面初稿均由 AI 生成，编译报错与接口异常能快速获得定位线索。

**本人独立完成的核心工作**包括：
1. **需求把控**：逐条核对 PRD 功能清单，划定边界，确定"基础功能 + 自主拓展模块"的交付范围；
2. **技术决策**：技术栈选型、动态统计周期机制、历史快照与课程维度统计口径均由本人确定；
3. **自主拓展模块（课程与班级管理）**：需求由本人独立提出，三级"课程→班级→学生名单"的数据模型与业务规则（归属隔离、级联删除、批量加入去重、删除课程反馈归入默认课堂）由本人设计，AI 仅按指示辅助编码；
4. **测试与排错**：全部测试用例由本人设计并执行（认证 16 项、用户管理 19 项、课程班级管理 15 项断言全部通过），统计数值逐项手算核对，两个统计缺陷与两处接口异常由本人定位并修复；
5. **交付核验**：运行截图方案由本人设计并逐页执行，四份文档全部事实经本人核验。

---

## 六、AI 生成内容问题与修正说明

**问题 1：动态统计触发器使用 Spring 旧版 API，编译报错**
AI 初版使用 `PeriodicTrigger` 的 `setPeriod` / `nextExecutionTimeInMillis`（Spring Boot 2 写法），在 Spring Boot 3 中已移除，编译失败。本人将实现改为实现 `Trigger` 函数式接口、`nextExecution` 返回 `Instant`，并验证周期动态修改后立即生效（默认 5 分钟，改 8/10 分钟生效，填 3/11 分钟被校验拒绝）。

**问题 2：统计人数虚增（同秒重复提交缺陷）**
联调时同一学生同一秒内两次提交，按 `MAX(create_time)` 关联会把同秒两条都计入，实测人数 4 而非正确的 3。本人定位后改为按自增主键 `MAX(id)` 判定最新反馈，修复后与手算一致（人数 3、均分 4.00、方差 0.6667）。

**问题 3：讨论区消息列表接口返回 500**
消息列表首次请求系统异常，本人定位为 Mapper 中 join sys_user 误用了不存在的列 `real_name`（实际列名为 `name`），修正后列表正常返回发送人姓名与角色徽标。

**问题 4：初始测试账号无法登录**
初始 schema.sql 中密码哈希为占位值，与 MD5+盐算法不一致，登录全部失败。本人重新计算 `MD5("classcheck"+"123456")` 并更新全部测试账号哈希后恢复正常。

---

## 七、原创性声明

本人郑重声明：本作业中 AI 仅作为开发辅助工具使用，所有核心业务逻辑、功能实现与调试均由本人独立完成；本人理解 AI 生成代码的逻辑，能独立解释和修改；自主新增业务模块由本人独立提出需求并完成设计；所有 AI 使用情况均已如实记录，无隐瞒、无抄袭。

签字：____________　　日期：____________

---

## 附录 A：对应第四节第 8 条提示词的完整输出

### 提示词输入

> 实现自主拓展模块课程与班级管理：教师维护我的课程（增删改），每门课程下建班级（增删），每个班级维护学生名单（从全校学生中批量勾选加入、自动去重、可移除）；教师只能操作自己名下的课程班级；学生端可查看自己加入的课程与所在班级；删除课程时事务级联清理班级和名单，该课程已有反馈归入默认课堂；学生按课程提交的反馈要能按课程维度统计。

### AI 完整输出

该条输出包含：`ClassInfo` / `ClassStudent` 实体与 Mapper、`CourseDTO` / `ClassDTO` / `AddStudentsDTO` / `StudentCourseVO`、`CourseManageService`（三级操作 + 归属校验 + 事务级联）、教师端三列钻取式管理界面与学生端"我的课程"页面。以下为 AI 输出的核心控制器类完整代码（经本人审查修改后的最终版本）：

```java
package com.classcheck.controller;

import com.classcheck.common.Result;
import com.classcheck.common.UserContext;
import com.classcheck.dto.AddStudentsDTO;
import com.classcheck.dto.ClassDTO;
import com.classcheck.dto.CourseDTO;
import com.classcheck.entity.ClassInfo;
import com.classcheck.entity.Course;
import com.classcheck.entity.SysUser;
import com.classcheck.service.CourseManageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 教师端：课程与班级管理（课程 → 班级 → 学生名单 三级）
 */
@RestController
public class CourseManageController {

    private final CourseManageService courseManageService;

    public CourseManageController(CourseManageService courseManageService) {
        this.courseManageService = courseManageService;
    }

    // ---- 课程 ----
    @GetMapping("/api/teacher/courses")
    public Result<List<Course>> myCourses() {
        return Result.success(courseManageService.listMyCourses(UserContext.getUserId()));
    }

    @PostMapping("/api/teacher/courses")
    public Result<Void> createCourse(@RequestBody CourseDTO dto) {
        courseManageService.createCourse(UserContext.getUserId(), dto);
        return Result.success("课程已创建", null);
    }

    @PutMapping("/api/teacher/courses/{id}")
    public Result<Void> updateCourse(@PathVariable Long id, @RequestBody CourseDTO dto) {
        courseManageService.updateCourse(UserContext.getUserId(), id, dto);
        return Result.success("课程已更新", null);
    }

    @DeleteMapping("/api/teacher/courses/{id}")
    public Result<Void> deleteCourse(@PathVariable Long id) {
        courseManageService.deleteCourse(UserContext.getUserId(), id);
        return Result.success("课程及其班级已删除", null);
    }

    @GetMapping("/api/teacher/courses/{id}/classes")
    public Result<List<ClassInfo>> courseClasses(@PathVariable Long id) {
        return Result.success(courseManageService.listClasses(UserContext.getUserId(), id));
    }

    // ---- 班级 ----
    @PostMapping("/api/teacher/classes")
    public Result<Void> createClass(@RequestBody ClassDTO dto) {
        courseManageService.createClass(UserContext.getUserId(), dto);
        return Result.success("班级已创建", null);
    }

    @DeleteMapping("/api/teacher/classes/{id}")
    public Result<Void> deleteClass(@PathVariable Long id) {
        courseManageService.deleteClass(UserContext.getUserId(), id);
        return Result.success("班级已删除", null);
    }

    // ---- 班级学生名单 ----
    @GetMapping("/api/teacher/classes/{id}/students")
    public Result<List<SysUser>> roster(@PathVariable Long id) {
        return Result.success(courseManageService.classRoster(UserContext.getUserId(), id));
    }

    @GetMapping("/api/teacher/students/all")
    public Result<List<SysUser>> allStudents() {
        return Result.success(courseManageService.allStudents());
    }

    @PostMapping("/api/teacher/classes/{id}/students")
    public Result<Void> addStudents(@PathVariable Long id, @RequestBody AddStudentsDTO dto) {
        int added = courseManageService.addStudents(UserContext.getUserId(), id, dto.getStudentIds());
        return Result.success("已添加 " + added + " 名学生（重复的自动跳过）", null);
    }

    @DeleteMapping("/api/teacher/classes/{id}/students/{studentId}")
    public Result<Void> removeStudent(@PathVariable Long id, @PathVariable Long studentId) {
        courseManageService.removeStudent(UserContext.getUserId(), id, studentId);
        return Result.success("已从班级移除该学生", null);
    }
}
```

> 说明：AI 首次输出后，本人审查并补充了"删除课程时反馈归入默认课堂而非直接删除""班级与课程冗余 teacher_id 做归属校验""批量添加学生逐个校验并返回实际加入人数"等业务约束，并完成 15 项接口断言验证。
