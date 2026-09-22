package com.classcheck.service;

import com.classcheck.entity.Statistics;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

/**
 * 学情统计 Excel 导出服务（Apache POI，.xlsx）。
 * 列：统计时间、课程名称、提交人数、平均分、方差、1分~6分人数。
 */
@Service
public class StatsExportService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String[] HEADERS = {
            "统计时间", "课程名称", "提交人数", "平均分", "方差",
            "1分人数", "2分人数", "3分人数", "4分人数", "5分人数", "6分人数"
    };

    public byte[] export(List<Statistics> list) throws IOException {
        // 导出按统计时间正序排列，便于在 Excel 中查看变化趋势
        List<Statistics> data = list == null ? Collections.emptyList() : list;
        Collections.reverse(data);

        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("学情统计");

            // 标题行
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("课堂学情监控管理系统 - 学情统计数据");
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, HEADERS.length - 1));

            // 表头行
            Row headerRow = sheet.createRow(1);
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // 数据行
            int rowIndex = 2;
            for (Statistics s : data) {
                Row row = sheet.createRow(rowIndex++);
                int c = 0;
                row.createCell(c++).setCellValue(s.getStatTime() == null ? "" : FMT.format(s.getStatTime()));
                row.createCell(c++).setCellValue(s.getCourseName());
                row.createCell(c++).setCellValue(s.getTotalCount());
                row.createCell(c++).setCellValue(s.getAvgScore() == null ? "" : s.getAvgScore().toPlainString());
                row.createCell(c++).setCellValue(s.getVariance() == null ? "" : s.getVariance().toPlainString());
                row.createCell(c++).setCellValue(s.getScore1Count());
                row.createCell(c++).setCellValue(s.getScore2Count());
                row.createCell(c++).setCellValue(s.getScore3Count());
                row.createCell(c++).setCellValue(s.getScore4Count());
                row.createCell(c++).setCellValue(s.getScore5Count());
                row.createCell(c).setCellValue(s.getScore6Count());
            }

            // 列宽自适应
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
                // 中文列需要额外加宽
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 512);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
