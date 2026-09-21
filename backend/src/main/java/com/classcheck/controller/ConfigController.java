package com.classcheck.controller;

import com.classcheck.common.Result;
import com.classcheck.dto.StatsPeriodDTO;
import com.classcheck.service.SysConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统配置接口（管理员）：统计周期 5-10 分钟
 */
@RestController
@RequestMapping("/api/admin/config")
public class ConfigController {

    private final SysConfigService configService;

    public ConfigController(SysConfigService configService) {
        this.configService = configService;
    }

    /** 查询统计周期 */
    @GetMapping("/stats-period")
    public Result<Map<String, Object>> getPeriod() {
        Map<String, Object> data = new HashMap<>();
        data.put("minutes", configService.getStatsPeriodMinutes());
        data.put("min", SysConfigService.MIN_PERIOD);
        data.put("max", SysConfigService.MAX_PERIOD);
        return Result.success(data);
    }

    /** 修改统计周期（5-10分钟） */
    @PutMapping("/stats-period")
    public Result<Void> setPeriod(@RequestBody StatsPeriodDTO dto) {
        if (dto.getMinutes() == null) {
            throw new IllegalArgumentException("周期不能为空");
        }
        configService.setStatsPeriodMinutes(dto.getMinutes());
        return Result.success("统计周期已更新为 " + dto.getMinutes() + " 分钟", null);
    }
}
