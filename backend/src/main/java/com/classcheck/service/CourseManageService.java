package com.classcheck.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.classcheck.common.BusinessException;
import com.classcheck.dto.*;
import com.classcheck.entity.*;
import com.classcheck.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 拓展模块：课程与班级管理服务。
 * 教师维护“我的课程 → 课程下班级 → 班级学生名单”，全程做归属校验；
 * 学生查看自己加入的课程与班级。
 */
@Service
public class CourseManageService {

    private final CourseMapper courseMapper;
    private final ClassInfoMapper classInfoMapper;
    private final ClassStudentMapper classStudentMapper;
    private final SysUserMapper userMapper;
    private final FeedbackMapper feedbackMapper;

    public CourseManageService(CourseMapper courseMapper, ClassInfoMapper classInfoMapper,
                               ClassStudentMapper classStudentMapper, SysUserMapper userMapper,
                               FeedbackMapper feedbackMapper) {
        this.courseMapper = courseMapper;
        this.classInfoMapper = classInfoMapper;
        this.classStudentMapper = classStudentMapper;
        this.userMapper = userMapper;
        this.feedbackMapper = feedbackMapper;
    }

    // ============ 课程 ============

    public List<Course> listMyCourses(Long teacherId) {
        return courseMapper.selectList(new LambdaQueryWrapper<Course>()
                .eq(Course::getTeacherId, teacherId)
                .orderByAsc(Course::getId));
    }

    public void createCourse(Long teacherId, CourseDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BusinessException("课程名称不能为空");
        }
        Course course = new Course();
        course.setName(dto.getName().trim());
        course.setDescription(trimOrNull(dto.getDescription()));
        course.setTeacherId(teacherId);
        courseMapper.insert(course);
    }

    public void updateCourse(Long teacherId, Long id, CourseDTO dto) {
        Course course = requireOwnedCourse(teacherId, id);
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BusinessException("课程名称不能为空");
        }
        course.setName(dto.getName().trim());
        course.setDescription(trimOrNull(dto.getDescription()));
        courseMapper.updateById(course);
    }

    /** 删除课程：级联删除班级、班级名单，并把该课程反馈归入“默认课堂” */
    @Transactional
    public void deleteCourse(Long teacherId, Long id) {
        requireOwnedCourse(teacherId, id);
        List<ClassInfo> classes = classInfoMapper.selectList(new LambdaQueryWrapper<ClassInfo>()
                .eq(ClassInfo::getCourseId, id));
        for (ClassInfo c : classes) {
            classStudentMapper.delete(new LambdaQueryWrapper<ClassStudent>()
                    .eq(ClassStudent::getClassId, c.getId()));
        }
        classInfoMapper.delete(new LambdaQueryWrapper<ClassInfo>().eq(ClassInfo::getCourseId, id));
        // 保留反馈数据，解除课程归属（归入默认课堂），避免外键约束失败
        feedbackMapper.update(null, new LambdaUpdateWrapper<Feedback>()
                .eq(Feedback::getCourseId, id).set(Feedback::getCourseId, null));
        courseMapper.deleteById(id);
    }

    // ============ 班级 ============

    public List<ClassInfo> listClasses(Long teacherId, Long courseId) {
        requireOwnedCourse(teacherId, courseId);
        return classInfoMapper.selectList(new LambdaQueryWrapper<ClassInfo>()
                .eq(ClassInfo::getCourseId, courseId)
                .orderByAsc(ClassInfo::getId));
    }

    public void createClass(Long teacherId, ClassDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BusinessException("班级名称不能为空");
        }
        if (dto.getCourseId() == null) {
            throw new BusinessException("请先选择课程");
        }
        requireOwnedCourse(teacherId, dto.getCourseId());
        ClassInfo classInfo = new ClassInfo();
        classInfo.setName(dto.getName().trim());
        classInfo.setCourseId(dto.getCourseId());
        classInfo.setTeacherId(teacherId);
        classInfoMapper.insert(classInfo);
    }

    /** 删除班级：级联删除班级名单 */
    @Transactional
    public void deleteClass(Long teacherId, Long classId) {
        requireOwnedClass(teacherId, classId);
        classStudentMapper.delete(new LambdaQueryWrapper<ClassStudent>()
                .eq(ClassStudent::getClassId, classId));
        classInfoMapper.deleteById(classId);
    }

    // ============ 班级学生名单 ============

    public List<SysUser> classRoster(Long teacherId, Long classId) {
        requireOwnedClass(teacherId, classId);
        return classStudentMapper.selectRoster(classId);
    }

    /** 全校学生（供添加选择） */
    public List<SysUser> allStudents() {
        return userMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getRole, 1)
                .orderByAsc(SysUser::getId));
    }

    /** 批量添加学生到班级，自动去重，返回实际加入人数 */
    @Transactional
    public int addStudents(Long teacherId, Long classId, List<Long> studentIds) {
        requireOwnedClass(teacherId, classId);
        if (studentIds == null || studentIds.isEmpty()) {
            throw new BusinessException("请至少选择一名学生");
        }
        int added = 0;
        for (Long sid : studentIds) {
            SysUser student = userMapper.selectById(sid);
            if (student == null || student.getRole() != 1) {
                throw new BusinessException("学生不存在，已取消操作");
            }
            Long exists = classStudentMapper.selectCount(new LambdaQueryWrapper<ClassStudent>()
                    .eq(ClassStudent::getClassId, classId)
                    .eq(ClassStudent::getStudentId, sid));
            if (exists != null && exists > 0) {
                continue; // 已在班级，去重跳过
            }
            ClassStudent cs = new ClassStudent();
            cs.setClassId(classId);
            cs.setStudentId(sid);
            classStudentMapper.insert(cs);
            added++;
        }
        return added;
    }

    /** 从班级移除学生 */
    public void removeStudent(Long teacherId, Long classId, Long studentId) {
        requireOwnedClass(teacherId, classId);
        classStudentMapper.delete(new LambdaQueryWrapper<ClassStudent>()
                .eq(ClassStudent::getClassId, classId)
                .eq(ClassStudent::getStudentId, studentId));
    }

    // ============ 学生端 ============

    /** 学生“我的课程”：按课程聚合所在班级 */
    public List<StudentCourseVO> myCourses(Long studentId) {
        List<StudentCourseRowDTO> rows = classStudentMapper.selectStudentCourseRows(studentId);
        Map<Long, StudentCourseVO> map = new LinkedHashMap<>();
        for (StudentCourseRowDTO row : rows) {
            StudentCourseVO vo = map.computeIfAbsent(row.getCourseId(), k -> {
                StudentCourseVO v = new StudentCourseVO();
                v.setCourseId(row.getCourseId());
                v.setCourseName(row.getCourseName());
                v.setDescription(row.getDescription());
                v.setTeacherName(row.getTeacherName());
                v.setClasses(new ArrayList<>());
                return v;
            });
            ClassSimpleVO simple = new ClassSimpleVO();
            simple.setClassId(row.getClassId());
            simple.setClassName(row.getClassName());
            vo.getClasses().add(simple);
        }
        return new ArrayList<>(map.values());
    }

    // ============ 归属校验 ============

    private Course requireOwnedCourse(Long teacherId, Long courseId) {
        if (courseId == null) {
            throw new BusinessException("缺少课程ID");
        }
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException("课程不存在或已被删除");
        }
        if (!course.getTeacherId().equals(teacherId)) {
            throw new BusinessException("无权操作：这不是你名下的课程");
        }
        return course;
    }

    private ClassInfo requireOwnedClass(Long teacherId, Long classId) {
        if (classId == null) {
            throw new BusinessException("缺少班级ID");
        }
        ClassInfo classInfo = classInfoMapper.selectById(classId);
        if (classInfo == null) {
            throw new BusinessException("班级不存在或已被删除");
        }
        if (!classInfo.getTeacherId().equals(teacherId)) {
            throw new BusinessException("无权操作：这不是你名下的班级");
        }
        return classInfo;
    }

    private String trimOrNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }
}
