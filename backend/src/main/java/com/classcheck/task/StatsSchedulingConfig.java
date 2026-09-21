package com.classcheck.task;

import com.classcheck.service.StatsService;
import com.classcheck.service.SysConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * 学情定时统计调度。
 * 周期由管理员在 sys_config 中配置（5-10分钟），每次调度前重新读取，修改周期无需重启。
 */
@Configuration
public class StatsSchedulingConfig implements SchedulingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(StatsSchedulingConfig.class);

    private final StatsService statsService;
    private final SysConfigService configService;

    public StatsSchedulingConfig(StatsService statsService, SysConfigService configService) {
        this.statsService = statsService;
        this.configService = configService;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        // 动态周期触发器：每次执行完后，按当前数据库中的配置决定下一次执行时间
        registrar.addTriggerTask(
                () -> {
                    try {
                        statsService.computeAndSave();
                    } catch (Exception e) {
                        log.error("学情定时统计执行失败", e);
                    }
                },
                triggerContext -> {
                    int minutes = configService.getStatsPeriodMinutes();
                    Instant last = triggerContext.lastScheduledExecution();
                    Instant base = (last != null) ? last : Instant.now();
                    log.info("下次学情统计将在 {} 分钟后执行", minutes);
                    return base.plus(minutes, ChronoUnit.MINUTES);
                }
        );
    }
}
