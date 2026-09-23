package com.classcheck.controller;

import com.classcheck.common.Result;
import com.classcheck.common.UserContext;
import com.classcheck.dto.UserDTO;
import com.classcheck.entity.SysUser;
import com.classcheck.service.UserManageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员用户管理接口：三类账号增删改查、启用禁用
 */
@RestController
@RequestMapping("/api/admin/users")
public class UserManageController {

    private final UserManageService userManageService;

    public UserManageController(UserManageService userManageService) {
        this.userManageService = userManageService;
    }

    /** 查询用户：可按角色、关键字筛选 */
    @GetMapping
    public Result<List<SysUser>> list(
            @RequestParam(required = false) Integer role,
            @RequestParam(required = false) String keyword) {
        return Result.success(userManageService.list(role, keyword));
    }

    /** 新增用户 */
    @PostMapping
    public Result<Void> create(@RequestBody UserDTO dto) {
        userManageService.create(dto);
        return Result.success("用户新增成功，初始密码为 " + (dto.getPassword() == null || dto.getPassword().isBlank() ? "123456" : dto.getPassword()), null);
    }

    /** 修改用户 */
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody UserDTO dto) {
        dto.setId(id);
        userManageService.update(dto);
        return Result.success("用户信息已更新", null);
    }

    /** 删除用户（级联清理其反馈、讨论消息、班级关联） */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        userManageService.delete(id, UserContext.getUserId());
        return Result.success("用户已删除", null);
    }

    /** 启用/禁用账号 */
    @PutMapping("/{id}/status")
    public Result<Void> changeStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        userManageService.changeStatus(id, body.get("status"), UserContext.getUserId());
        return Result.success("账号状态已更新", null);
    }
}
