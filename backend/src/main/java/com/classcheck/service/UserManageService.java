package com.classcheck.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.classcheck.common.BusinessException;
import com.classcheck.dto.UserDTO;
import com.classcheck.entity.SysUser;
import com.classcheck.mapper.*;
import com.classcheck.util.PasswordUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 管理员用户管理服务：学生/教师/管理员三类账号的增删改查、启用禁用。
 */
@Service
public class UserManageService {

    private static final String DEFAULT_PASSWORD = "123456";

    private final SysUserMapper userMapper;
    private final FeedbackMapper feedbackMapper;
    private final DiscussionMessageMapper discussionMessageMapper;
    private final ClassStudentMapper classStudentMapper;

    public UserManageService(SysUserMapper userMapper, FeedbackMapper feedbackMapper,
                             DiscussionMessageMapper discussionMessageMapper,
                             ClassStudentMapper classStudentMapper) {
        this.userMapper = userMapper;
        this.feedbackMapper = feedbackMapper;
        this.discussionMessageMapper = discussionMessageMapper;
        this.classStudentMapper = classStudentMapper;
    }

    /** 按角色 + 关键字查询（关键字匹配账号、姓名、联系方式） */
    public List<SysUser> list(Integer role, String keyword) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .orderByAsc(SysUser::getRole)
                .orderByAsc(SysUser::getId);
        if (role != null) {
            wrapper.eq(SysUser::getRole, role);
        }
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();
            wrapper.and(w -> w.like(SysUser::getUsername, kw)
                    .or().like(SysUser::getName, kw)
                    .or().like(SysUser::getPhone, kw));
        }
        return userMapper.selectList(wrapper);
    }

    /** 新增用户 */
    public void create(UserDTO dto) {
        validateCommon(dto);
        if (dto.getRole() == 1) {
            validateStudent(dto);
        }
        // 账号唯一性
        Long exists = userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, dto.getUsername().trim()));
        if (exists != null && exists > 0) {
            throw new BusinessException("登录账号已存在，请更换");
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername().trim());
        String rawPassword = (dto.getPassword() == null || dto.getPassword().isBlank())
                ? DEFAULT_PASSWORD : dto.getPassword();
        user.setPassword(PasswordUtil.encrypt(rawPassword));
        fillFields(user, dto);
        user.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        userMapper.insert(user);
    }

    /** 修改用户（密码为空表示不改密码） */
    public void update(UserDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("缺少用户ID");
        }
        SysUser user = userMapper.selectById(dto.getId());
        if (user == null) {
            throw new BusinessException("用户不存在或已被删除");
        }
        validateCommon(dto);
        if (dto.getRole() == 1) {
            validateStudent(dto);
        }
        // 账号若改动需保证唯一
        if (!user.getUsername().equals(dto.getUsername().trim())) {
            Long exists = userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                    .eq(SysUser::getUsername, dto.getUsername().trim()));
            if (exists != null && exists > 0) {
                throw new BusinessException("登录账号已存在，请更换");
            }
            user.setUsername(dto.getUsername().trim());
        }
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(PasswordUtil.encrypt(dto.getPassword()));
        }
        fillFields(user, dto);
        userMapper.updateById(user);
    }

    /** 删除用户：级联清理反馈、讨论消息、班级关联（统计快照作为历史保留） */
    @Transactional
    public void delete(Long id, Long operatorId) {
        if (id.equals(operatorId)) {
            throw new BusinessException("不能删除当前登录的自己");
        }
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在或已被删除");
        }
        feedbackMapper.delete(new LambdaQueryWrapper<com.classcheck.entity.Feedback>()
                .eq(com.classcheck.entity.Feedback::getStudentId, id));
        discussionMessageMapper.delete(new LambdaQueryWrapper<com.classcheck.entity.DiscussionMessage>()
                .eq(com.classcheck.entity.DiscussionMessage::getSenderId, id));
        classStudentMapper.deleteByStudent(id);
        userMapper.deleteById(id);
    }

    /** 启用/禁用账号 */
    public void changeStatus(Long id, Integer status, Long operatorId) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态值非法");
        }
        if (id.equals(operatorId) && status == 0) {
            throw new BusinessException("不能禁用当前登录的自己");
        }
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException("用户不存在或已被删除");
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    /** 通用必填校验 */
    private void validateCommon(UserDTO dto) {
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new BusinessException("登录账号不能为空");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new BusinessException("姓名不能为空");
        }
        if (dto.getRole() == null || dto.getRole() < 1 || dto.getRole() > 3) {
            throw new BusinessException("请选择正确的角色");
        }
        if (dto.getPhone() != null && !dto.getPhone().isBlank()
                && !dto.getPhone().trim().matches("\\d{11}")) {
            throw new BusinessException("联系方式应为11位手机号");
        }
    }

    /** 学生专属字段校验 */
    private void validateStudent(UserDTO dto) {
        if (dto.getStudentNo() == null || dto.getStudentNo().isBlank()) {
            throw new BusinessException("学生账号必须填写学号");
        }
        if (dto.getClassName() == null || dto.getClassName().isBlank()) {
            throw new BusinessException("学生账号必须填写班级");
        }
    }

    /** 把 DTO 字段填入实体（角色不允许通过修改变更，保持原角色） */
    private void fillFields(SysUser user, UserDTO dto) {
        user.setName(dto.getName().trim());
        user.setRole(dto.getRole());
        user.setAge(dto.getAge());
        user.setGender(blankToNull(dto.getGender()));
        user.setPhone(blankToNull(dto.getPhone()));
        if (dto.getRole() == 1) {
            user.setStudentNo(trimOrNull(dto.getStudentNo()));
            user.setGrade(blankToNull(dto.getGrade()));
            user.setClassName(trimOrNull(dto.getClassName()));
            user.setMajor(blankToNull(dto.getMajor()));
        } else {
            // 非学生角色清空学生专属字段
            user.setStudentNo(null);
            user.setGrade(null);
            user.setClassName(null);
            user.setMajor(null);
        }
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s.trim();
    }

    private String trimOrNull(String s) {
        return blankToNull(s);
    }
}
