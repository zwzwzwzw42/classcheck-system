package com.classcheck.controller;

import com.classcheck.common.Result;
import com.classcheck.entity.Statistics;
import com.classcheck.service.StatsExportService;
import com.classcheck.service.StatsService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 学情统计接口（教师）：当前时段快照、历史查询、手动立即统计、导出 Excel
 */
@RestController
@RequestMapping("/api/teacher/stats")
public class StatisticsController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final StatsService statsService;
    private final StatsExportService statsExportService;

    public StatisticsController(StatsService statsService, StatsExportService statsExportService) {
        this.statsService = statsService;
        this.statsExportService = statsExportService;
    }

    /** 当前时段最新统计快照 */
    @GetMapping("/latest")
    public Result<Statistics> latest(@RequestParam(required = false) Long courseId) {
        return Result.success(statsService.getLatest(courseId));
    }

    /** 历史统计：按课程 + 时间范围组合查询 */
    @GetMapping("/history")
    public Result<List<Statistics>> history(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime) {
        LocalDateTime start = parse(startTime);
        LocalDateTime end = parse(endTime);
        return Result.success(statsService.getHistory(courseId, start, end));
    }

    /** 导出历史统计为 Excel（.xlsx），筛选条件与历史查询一致 */
    @GetMapping("/export")
    public void export(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            HttpServletResponse response) throws IOException {
        LocalDateTime start = parse(startTime);
        LocalDateTime end = parse(endTime);
        List<Statistics> list = statsService.getHistory(courseId, start, end);
        byte[] bytes = statsExportService.export(list);

        String fileName = "学情统计_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
        String encoded = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replace("+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);
        response.setContentLength(bytes.length);
        response.getOutputStream().write(bytes);
        response.getOutputStream().flush();
    }

    /** 手动立即执行一次统计（演示用，无需等待定时周期） */
    @PostMapping("/refresh")
    public Result<Integer> refresh() {
        int count = statsService.computeAndSave();
        return Result.success("统计完成，生成 " + count + " 条快照", count);
    }

    private LocalDateTime parse(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        text = text.trim();
        try {
            if (text.length() <= 10) {
                return LocalDateTime.parse(text + " 00:00:00", FMT);
            }
            return LocalDateTime.parse(text, FMT);
        } catch (Exception e) {
            throw new IllegalArgumentException("时间格式应为 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss");
        }
    }
}
