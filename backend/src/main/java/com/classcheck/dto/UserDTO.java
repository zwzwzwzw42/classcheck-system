package com.classcheck.dto;

import lombok.Data;

/**
 * 用户新增/修改请求。
 * 学生角色额外使用 studentNo/grade/className/major；教师、管理员仅用基础字段。
 */
@Data
public class UserDTO {

    private Long id;

    private String username;

    /** 新增时可空（默认123456）；修改时为空表示不改密码 */
    private String password;

    private String name;

    /** 1学生 2教师 3管理员 */
    private Integer role;

    private Integer age;
    private String gender;
    private String phone;

    private String studentNo;
    private String grade;
    private String className;
    private String major;

    /** 1正常 0禁用 */
    private Integer status;
}
