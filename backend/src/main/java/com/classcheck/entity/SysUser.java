package com.classcheck.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体（学生/教师/管理员 三类角色）
 */
@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录账号 */
    private String username;

    /** 密码（MD5加盐存储） */
    private String password;

    /** 姓名 */
    private String name;

    /** 角色：1学生 2教师 3管理员 */
    private Integer role;

    private Integer age;

    private String gender;

    /** 联系方式（兼作短信验证码接收号） */
    private String phone;

    /** 学号（学生专属） */
    private String studentNo;

    /** 年级（学生专属） */
    private String grade;

    /** 班级（学生专属） */
    private String className;

    /** 专业（学生专属） */
    private String major;

    /** 账号状态：1正常 0禁用 */
    private Integer status;

    private LocalDateTime createTime;
}
