package com.ty.service;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.ty.Util.DateUtils;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class ReportService{

    public String generateWeeklyStrategyReport(String strategyName, List<Map<String, Object>> dataList) throws IOException{
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 表頭樣式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFSheet sheet = workbook.createSheet(strategyName);
        sheet.setColumnWidth(0, 10 * 256);
        sheet.setColumnWidth(1, 15 * 256);
        sheet.setColumnWidth(2, 15 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setHorizontallyCenter(true);
        Row titleRow = sheet.createRow(0);
        titleRow.setRowStyle(cellStyle);
        titleRow.createCell(0).setCellValue("上市櫃");
        titleRow.createCell(1).setCellValue("股名");
        titleRow.createCell(2).setCellValue("外資買賣超");
        titleRow.createCell(3).setCellValue("投信買賣超");
        titleRow.createCell(4).setCellValue("自營商買賣超");
        titleRow.createCell(5).setCellValue("自營商避險");
        int rowNum = 1;
        for(Map<String, Object> data : dataList){
            Row row = sheet.createRow(rowNum);
            row.setRowStyle(cellStyle);
            row.createCell(0).setCellValue(data.get("上市櫃").toString());
            row.createCell(1, CellType.STRING).setCellValue(data.get("stock_name").toString());
            row.createCell(2, CellType.NUMERIC).setCellValue(Integer.valueOf(data.get("fi").toString()));
            row.createCell(3, CellType.NUMERIC).setCellValue(Integer.valueOf(data.get("it").toString()));
            row.createCell(4, CellType.NUMERIC).setCellValue(Integer.valueOf(data.get("ds").toString()));
            row.createCell(5, CellType.NUMERIC).setCellValue(Integer.valueOf(data.get("dh").toString()));
            rowNum++;
        }
        String path = "D:\\WeeklyStockReport\\" + strategyName + "_" + DateUtils.todayDate + ".xlsx";
        FileOutputStream out = new FileOutputStream(path);
        workbook.write(out);
        out.close();
        workbook.close();
        return path;
    }

    public String generateStrategyReport(String strategyName, List<Map<String, Object>> dataList) throws IOException{
        XSSFWorkbook workbook = new XSSFWorkbook();
        // 表頭樣式
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setAlignment(HorizontalAlignment.RIGHT);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        XSSFSheet sheet = workbook.createSheet(strategyName);
        sheet.setColumnWidth(0, 10 * 256);
        sheet.setColumnWidth(1, 15 * 256);
        sheet.setColumnWidth(2, 15 * 256);
        sheet.setColumnWidth(3, 15 * 256);
        sheet.setColumnWidth(4, 15 * 256);
        sheet.setColumnWidth(5, 15 * 256);
        sheet.setColumnWidth(6, 15 * 256);
        sheet.setColumnWidth(7, 15 * 256);
        sheet.setColumnWidth(8, 15 * 256);
        sheet.setHorizontallyCenter(true);
        Row titleRow = sheet.createRow(0);
        titleRow.setRowStyle(cellStyle);
        titleRow.createCell(0).setCellValue("上市櫃");
        titleRow.createCell(1).setCellValue("股號");
        titleRow.createCell(2).setCellValue("股名");
        titleRow.createCell(3).setCellValue("外資買賣超");
        titleRow.createCell(4).setCellValue("投信買賣超");
        titleRow.createCell(5).setCellValue("自營商買賣超");
        titleRow.createCell(6).setCellValue("自營商避險");
        titleRow.createCell(7).setCellValue("收盤價");
        titleRow.createCell(8).setCellValue("今日漲跌點");
        int rowNum = 1;
        for(Map<String, Object> data : dataList){
            Row row = sheet.createRow(rowNum);
            row.setRowStyle(cellStyle);
            row.createCell(0).setCellValue(data.get("上市櫃").toString());
            row.createCell(1, CellType.NUMERIC).setCellValue(Integer.valueOf(data.get("股號").toString()));
            row.createCell(2, CellType.STRING).setCellValue(data.get("股名").toString());
            row.createCell(3, CellType.NUMERIC).setCellValue((int) data.get("外資買賣超"));
            row.createCell(4, CellType.NUMERIC).setCellValue((int) data.get("投信買賣超"));
            row.createCell(5, CellType.NUMERIC).setCellValue((int) data.get("自營商買賣超"));
            row.createCell(6, CellType.NUMERIC).setCellValue((int) data.get("自營商避險"));
            row.createCell(7, CellType.NUMERIC).setCellValue(Double.valueOf(data.get("收盤價").toString()));
            row.createCell(8, CellType.NUMERIC).setCellValue(Double.valueOf(data.get("今日漲跌點").toString()));
            rowNum++;
        }
        String path = "D:\\DailyStockReport\\" + strategyName + "_" + DateUtils.todayDate + ".xlsx";
        FileOutputStream out = new FileOutputStream(path);
        workbook.write(out);
        out.close();
        workbook.close();
        return path;
    }
}
