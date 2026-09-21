package com.classcheck.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.classcheck.entity.SysConfig;
import com.classcheck.mapper.SysConfigMapper;
import org.springframework.stereotype.Service;

/**
 * 系统配置服务：统计周期（5-10分钟，管理员可配）
 */
@Service
public class SysConfigService {

    public static final String KEY_STATS_PERIOD = "stats_period_minutes";
    public static final int DEFAULT_PERIOD = 5;
    public static final int MIN_PERIOD = 5;
    public static final int MAX_PERIOD = 10;

    private final SysConfigMapper configMapper;

    public SysConfigService(SysConfigMapper configMapper) {
        this.configMapper = configMapper;
    }

    /** 读取统计周期（分钟），非法或缺失时返回默认值 5 */
    public int getStatsPeriodMinutes() {
        SysConfig config = configMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, KEY_STATS_PERIOD));
        if (config == null || config.getConfigValue() == null) {
            return DEFAULT_PERIOD;
        }
        try {
            int minutes = Integer.parseInt(config.getConfigValue().trim());
            if (minutes < MIN_PERIOD || minutes > MAX_PERIOD) {
                return DEFAULT_PERIOD;
            }
            return minutes;
        } catch (NumberFormatException e) {
            return DEFAULT_PERIOD;
        }
    }

    /** 修改统计周期，取值必须在 5-10 之间 */
    public void setStatsPeriodMinutes(int minutes) {
        if (minutes < MIN_PERIOD || minutes > MAX_PERIOD) {
            throw new IllegalArgumentException("统计周期必须在 " + MIN_PERIOD + "-" + MAX_PERIOD + " 分钟之间");
        }
        SysConfig config = configMapper.selectOne(new LambdaQueryWrapper<SysConfig>()
                .eq(SysConfig::getConfigKey, KEY_STATS_PERIOD));
        if (config == null) {
            config = new SysConfig();
            config.setConfigKey(KEY_STATS_PERIOD);
            config.setConfigValue(String.valueOf(minutes));
            configMapper.insert(config);
        } else {
            config.setConfigValue(String.valueOf(minutes));
            configMapper.updateById(config);
        }
    }
}
