package com.classcheck.dto;

import lombok.Data;

/** 统计周期配置请求 */
@Data
public class StatsPeriodDTO {
    /** 统计刷新周期（分钟），取值 5-10 */
    private Integer minutes;
}
