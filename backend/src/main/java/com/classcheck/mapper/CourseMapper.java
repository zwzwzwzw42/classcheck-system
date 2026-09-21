package com.classcheck.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.classcheck.entity.Course;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CourseMapper extends BaseMapper<Course> {
}
