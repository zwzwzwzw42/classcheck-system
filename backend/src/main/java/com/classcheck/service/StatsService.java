package com.classcheck.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.classcheck.entity.Course;
import com.classcheck.entity.Feedback;
import com.classcheck.entity.Statistics;
import com.classcheck.mapper.CourseMapper;
import com.classcheck.mapper.FeedbackMapper;
import com.classcheck.mapper.StatisticsMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 学情统计服务：
 * 每个统计周期，对每门课程，取每位学生的最新一次反馈，
 * 计算提交人数、各分数段人数、平均分、总体方差，并保存为统计快照。
 */
@Service
public class StatsService {

    private static final Logger log = LoggerFactory.getLogger(StatsService.class);
    private static final String DEFAULT_COURSE_NAME = "默认课堂";

    private final FeedbackMapper feedbackMapper;
    private final StatisticsMapper statisticsMapper;
    private final CourseMapper courseMapper;

    public StatsService(FeedbackMapper feedbackMapper, StatisticsMapper statisticsMapper,
                        CourseMapper courseMapper) {
        this.feedbackMapper = feedbackMapper;
        this.statisticsMapper = statisticsMapper;
        this.courseMapper = courseMapper;
    }

    /**
     * 执行一次全班学情统计并保存快照（定时任务调用，也可由教师手动触发）。
     * @return 本次生成的快照数量
     */
    public int computeAndSave() {
        // 1. 每位学生每门课程的最新反馈
        List<Feedback> latest = feedbackMapper.selectLatestPerStudent();
        // 2. 课程ID -> 课程名
        Map<Long, String> courseNames = courseMapper.selectList(null).stream()
                .collect(Collectors.toMap(Course::getId, Course::getName, (a, b) -> a));

        // 3. 按课程分组（null 归一为默认课堂，key=0）
        Map<Long, List<Feedback>> grouped = latest.stream()
                .collect(Collectors.groupingBy(f -> f.getCourseId() == null ? 0L : f.getCourseId()));

        LocalDateTime now = LocalDateTime.now();
        int saved = 0;
        for (Map.Entry<Long, List<Feedback>> entry : grouped.entrySet()) {
            Long courseKey = entry.getKey();
            List<Feedback> list = entry.getValue();
            Long courseId = courseKey == 0L ? null : courseKey;
            String courseName = courseId == null ? DEFAULT_COURSE_NAME
                    : courseNames.getOrDefault(courseId, "课程#" + courseId);

            Statistics stats = buildStatistics(now, courseId, courseName, list);
            statisticsMapper.insert(stats);
            saved++;
            log.info("学情统计完成：{}，提交人数={}，平均分={}，方差={}",
                    courseName, stats.getTotalCount(), stats.getAvgScore(), stats.getVariance());
        }
        log.info("本次学情统计共生成 {} 条快照", saved);
        return saved;
    }

    /** 根据去重后的反馈列表计算统计指标 */
    private Statistics buildStatistics(LocalDateTime statTime, Long courseId,
                                       String courseName, List<Feedback> list) {
        int[] scoreCounts = new int[7]; // 下标1-6
        int total = list.size();
        double sum = 0;
        for (Feedback f : list) {
            int s = f.getScore();
            scoreCounts[s]++;
            sum += s;
        }
        double avg = total == 0 ? 0 : sum / total;
        // 总体方差 = Σ(x-μ)² / N
        double variance = 0;
        if (total > 0) {
            double sqDiff = 0;
            for (Feedback f : list) {
                sqDiff += Math.pow(f.getScore() - avg, 2);
            }
            variance = sqDiff / total;
        }

        Statistics stats = new Statistics();
        stats.setStatTime(statTime);
        stats.setCourseId(courseId);
        stats.setCourseName(courseName);
        stats.setTotalCount(total);
        stats.setAvgScore(BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP));
        stats.setVariance(BigDecimal.valueOf(variance).setScale(4, RoundingMode.HALF_UP));
        stats.setScore1Count(scoreCounts[1]);
        stats.setScore2Count(scoreCounts[2]);
        stats.setScore3Count(scoreCounts[3]);
        stats.setScore4Count(scoreCounts[4]);
        stats.setScore5Count(scoreCounts[5]);
        stats.setScore6Count(scoreCounts[6]);
        return stats;
    }

    /** 查询某课程的最新统计快照（教师端"当前时段"展示） */
    public Statistics getLatest(Long courseId) {
        LambdaQueryWrapper<Statistics> wrapper = new LambdaQueryWrapper<Statistics>()
                .orderByDesc(Statistics::getStatTime)
                .orderByDesc(Statistics::getId)
                .last("LIMIT 1");
        if (courseId == null) {
            wrapper.isNull(Statistics::getCourseId);
        } else {
            wrapper.eq(Statistics::getCourseId, courseId);
        }
        return statisticsMapper.selectOne(wrapper);
    }

    /** 按课程 + 时间范围查询历史统计快照（倒序） */
    public List<Statistics> getHistory(Long courseId, LocalDateTime start, LocalDateTime end) {
        LambdaQueryWrapper<Statistics> wrapper = new LambdaQueryWrapper<Statistics>()
                .orderByDesc(Statistics::getStatTime)
                .orderByDesc(Statistics::getId);
        if (courseId != null) {
            wrapper.eq(Statistics::getCourseId, courseId);
        }
        if (start != null) {
            wrapper.ge(Statistics::getStatTime, start);
        }
        if (end != null) {
            wrapper.le(Statistics::getStatTime, end);
        }
        return statisticsMapper.selectList(wrapper);
    }
}
