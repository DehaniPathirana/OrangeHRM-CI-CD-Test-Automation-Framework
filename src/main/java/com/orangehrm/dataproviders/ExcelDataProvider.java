// src/main/java/com/orangehrm/dataproviders/ExcelDataProvider.java
package com.orangehrm.dataproviders;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import lombok.extern.log4j.Log4j2;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class ExcelDataProvider {

    /**
     * Read test data from Excel file
     */
    public static Object[][] getTestData(String filePath, String sheetName) {
        List<Map<String, String>> testData = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new RuntimeException("Sheet '" + sheetName + "' not found in Excel file");
            }

            // Get header row
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Header row not found in sheet: " + sheetName);
            }

            List<String> headers = new ArrayList<>();
            for (Cell cell : headerRow) {
                headers.add(cell.getStringCellValue());
            }

            // Read data rows
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row dataRow = sheet.getRow(i);
                if (dataRow != null) {
                    Map<String, String> rowData = new HashMap<>();

                    for (int j = 0; j < headers.size(); j++) {
                        Cell cell = dataRow.getCell(j);
                        String cellValue = getCellValueAsString(cell);
                        rowData.put(headers.get(j), cellValue);
                    }

                    testData.add(rowData);
                }
            }

            log.info("Successfully read {} rows of test data from {}", testData.size(), filePath);

        } catch (IOException e) {
            log.error("Failed to read Excel file: {}", filePath, e);
            throw new RuntimeException("Failed to read test data", e);
        }

        // Convert to Object[][] for TestNG DataProvider
        Object[][] result = new Object[testData.size()][1];
        for (int i = 0; i < testData.size(); i++) {
            result[i][0] = testData.get(i);
        }

        return result;
    }

    /**
     * Get cell value as string regardless of cell type
     */
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                } else {
                    yield String.valueOf((long) cell.getNumericCellValue());
                }
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getCellFormula();
            default -> "";
        };
    }
}