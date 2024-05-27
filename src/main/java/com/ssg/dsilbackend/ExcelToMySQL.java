package com.ssg.dsilbackend;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelToMySQL {
    public static void main(String[] args) {
        String jdbcURL = "jdbc:mysql://db-n56ke-kr.vpc-pub-cdb.ntruss.com:3306/dsilDB?useUnicode=true&serverTimezone=Asia/Seoul";
        String username = "dsiluser";
        String password = "whatdang444!"; // 여기에 올바른 비밀번호를 입력하세요

        String excelFilePath = "C:/ProgramData/MySQL/MySQL Server 8.0/Uploads/sheet4.xlsx";

        int batchSize = 20;

        Connection connection = null;

        try {
            long start = System.currentTimeMillis();

            FileInputStream inputStream = new FileInputStream(new File(excelFilePath));

            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet firstSheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = firstSheet.iterator();

            connection = DriverManager.getConnection(jdbcURL, username, password);
            connection.setAutoCommit(false);

            String sql = "INSERT INTO images (image_url) VALUES (?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            int count = 0;

            rowIterator.next(); // Skip header row

            while (rowIterator.hasNext()) {
                Row nextRow = rowIterator.next();
                Iterator<Cell> cellIterator = nextRow.cellIterator();

                while (cellIterator.hasNext()) {
                    Cell nextCell = cellIterator.next();

                    String imageURL = "";

                    if (nextCell.getCellType() == CellType.STRING) {
                        imageURL = nextCell.getStringCellValue();
                    } else if (nextCell.getCellType() == CellType.NUMERIC) {
                        imageURL = String.valueOf(nextCell.getNumericCellValue());
                    } else if (nextCell.getCellType() == CellType.BOOLEAN) {
                        imageURL = String.valueOf(nextCell.getBooleanCellValue());
                    }

                    statement.setString(1, imageURL);

                    statement.addBatch();

                    if (count % batchSize == 0) {
                        statement.executeBatch();
                    }
                }

            }

            workbook.close();

            // execute the remaining queries
            statement.executeBatch();

            connection.commit();
            connection.close();

            long end = System.currentTimeMillis();
            System.out.printf("Import done in %d ms\n", (end - start));

        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
