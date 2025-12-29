package com.flipkart.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * ExcelReader - Utility class for reading test data from Excel files.
 * Supports reading data as Map or Object arrays for DataProvider.
 */
public class ExcelReader {
    
    private Workbook workbook;
    private String filePath;
    
    /**
     * Constructor - initializes the Excel workbook
     * @param filePath Path to the Excel file
     */
    public ExcelReader(String filePath) {
        this.filePath = filePath;
        try {
            FileInputStream fis = new FileInputStream(filePath);
            this.workbook = new XSSFWorkbook(fis);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load Excel file: " + filePath, e);
        }
    }
    
    /**
     * Get data from a sheet as 2D Object array (for TestNG DataProvider)
     * First row is treated as header
     * @param sheetName Name of the sheet
     * @return 2D Object array with test data
     */
    public Object[][] getDataAsArray(String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new RuntimeException("Sheet '" + sheetName + "' not found in " + filePath);
        }
        
        int rowCount = sheet.getLastRowNum();
        int colCount = sheet.getRow(0).getLastCellNum();
        
        Object[][] data = new Object[rowCount][colCount];
        
        for (int i = 1; i <= rowCount; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                for (int j = 0; j < colCount; j++) {
                    Cell cell = row.getCell(j);
                    data[i - 1][j] = getCellValue(cell);
                }
            }
        }
        
        return data;
    }
    
    /**
     * Get data from a sheet as List of Maps
     * Each row becomes a Map with column headers as keys
     * @param sheetName Name of the sheet
     * @return List of Maps containing row data
     */
    public List<Map<String, String>> getDataAsMap(String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            throw new RuntimeException("Sheet '" + sheetName + "' not found in " + filePath);
        }
        
        List<Map<String, String>> dataList = new ArrayList<>();
        
        // Get headers from first row
        Row headerRow = sheet.getRow(0);
        int colCount = headerRow.getLastCellNum();
        String[] headers = new String[colCount];
        for (int i = 0; i < colCount; i++) {
            headers[i] = getCellValueAsString(headerRow.getCell(i));
        }
        
        // Get data rows
        int rowCount = sheet.getLastRowNum();
        for (int i = 1; i <= rowCount; i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Map<String, String> rowData = new HashMap<>();
                for (int j = 0; j < colCount; j++) {
                    Cell cell = row.getCell(j);
                    rowData.put(headers[j], getCellValueAsString(cell));
                }
                dataList.add(rowData);
            }
        }
        
        return dataList;
    }
    
    /**
     * Get a specific cell value
     * @param sheetName Name of the sheet
     * @param row Row number (0-indexed)
     * @param col Column number (0-indexed)
     * @return Cell value as String
     */
    public String getCellData(String sheetName, int row, int col) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return null;
        }
        Row dataRow = sheet.getRow(row);
        if (dataRow == null) {
            return null;
        }
        Cell cell = dataRow.getCell(col);
        return getCellValueAsString(cell);
    }
    
    /**
     * Get cell value by column header
     * @param sheetName Name of the sheet
     * @param row Row number (0-indexed, excluding header)
     * @param colHeader Column header name
     * @return Cell value as String
     */
    public String getCellData(String sheetName, int row, String colHeader) {
        Sheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) {
            return null;
        }
        
        // Find column index by header
        Row headerRow = sheet.getRow(0);
        int colIndex = -1;
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            if (getCellValueAsString(headerRow.getCell(i)).equalsIgnoreCase(colHeader)) {
                colIndex = i;
                break;
            }
        }
        
        if (colIndex == -1) {
            return null;
        }
        
        Row dataRow = sheet.getRow(row + 1); // +1 to skip header
        if (dataRow == null) {
            return null;
        }
        return getCellValueAsString(dataRow.getCell(colIndex));
    }
    
    /**
     * Get row count in a sheet (excluding header)
     * @param sheetName Name of the sheet
     * @return Number of data rows
     */
    public int getRowCount(String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        return sheet != null ? sheet.getLastRowNum() : 0;
    }
    
    /**
     * Get column count in a sheet
     * @param sheetName Name of the sheet
     * @return Number of columns
     */
    public int getColCount(String sheetName) {
        Sheet sheet = workbook.getSheet(sheetName);
        return sheet != null ? sheet.getRow(0).getLastCellNum() : 0;
    }
    
    /**
     * Helper method to get cell value as Object
     */
    private Object getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                // Return as integer if no decimal
                double numValue = cell.getNumericCellValue();
                if (numValue == Math.floor(numValue)) {
                    return (int) numValue;
                }
                return numValue;
            case BOOLEAN:
                return cell.getBooleanCellValue();
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }
    
    /**
     * Helper method to get cell value as String
     */
    private String getCellValueAsString(Cell cell) {
        Object value = getCellValue(cell);
        return value != null ? value.toString() : "";
    }
    
    /**
     * Close the workbook
     */
    public void close() {
        try {
            if (workbook != null) {
                workbook.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing workbook: " + e.getMessage());
        }
    }
}
