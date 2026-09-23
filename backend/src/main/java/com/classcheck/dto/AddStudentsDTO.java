package com.classcheck.dto;

import lombok.Data;

import java.util.List;

/**
 * 批量添加学生到班级请求
 */
@Data
public class AddStudentsDTO {

    private List<Long> studentIds;
}
