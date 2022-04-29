package com.company.utils;

import com.company.Interfaces.ExporterInterface;
import com.company.vinfo.Deceased;
import com.company.vinfo.VakcinaInfoParser;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.company.C.URL.*;

public class Exporters implements ExporterInterface {

    /**
     * It receives a (Deceased model) list and writes that into an output.txt file.
     * @param listOfDeceased List of lines what generated from Deceased model list.
     */
    @Override
    public void deceasedModelListToTxt(List<Deceased> listOfDeceased) {
        try {
            FileWriter writer;
            writer = new FileWriter(TXTPATH);
            for (Deceased deceased : listOfDeceased) {
                writer.write(deceased.toString() + System.lineSeparator());
            }
            writer.close();
        } catch (IOException e) {
            log("deceasedModelListToTxt()", "IOException + " + e);
        }
    }

    /**
     *serialize a Deceased model list.
     */
    @Override
    public void serializeDeceasedList(List<Deceased> listOfDeceased) {

        if (!listOfDeceased.isEmpty()) {
            try
            {
                FileOutputStream fos = new FileOutputStream(SERIALIZEDPATH);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(listOfDeceased);
                oos.close();
                fos.close();
            }
            catch (IOException e)
            {
                log("serializeDeceasedList()", "IOException + " + e);
            }
        }
        else {
            log("serializeDeceasedList()", "Deceased list is empty.");
        }
    }

    /**
     *  It makes an excel table from the Deceased List and save it into a file.
     */
    @Override
    public void deceasedModelListToXls(List<Deceased> listOfDeceased) {

        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet("Virusinfo");
        String filename = "d:\\"+ sheet.getSheetName()+"_"+java.time.LocalDate.now()+".xls";

        //<editor-fold desc="Excel setup">
        sheet.setColumnWidth(0, 8*256);
        sheet.setColumnWidth(1, 6*256);
        sheet.setColumnWidth(2, 4*256);
        sheet.setColumnWidth(3, 30*256);
        sheet.setColumnWidth(4, 30*256);
        sheet.setColumnWidth(5, 30*256);
        sheet.setColumnWidth(6, 30*256);
        sheet.setColumnWidth(7, 30*256);
        sheet.setColumnWidth(8, 30*256);
        Row header = sheet.createRow(0);
        header.createCell(0, CellType.STRING).setCellValue("Sorszám");
        header.createCell(1, CellType.STRING).setCellValue("Nem");
        header.createCell(2, CellType.STRING).setCellValue("Kor");
        header.createCell(3, CellType.STRING).setCellValue("Betegség 1");
        header.createCell(4, CellType.STRING).setCellValue("Betegség 2");
        header.createCell(5, CellType.STRING).setCellValue("Betegség 3");
        header.createCell(6, CellType.STRING).setCellValue("Betegség 4");
        header.createCell(7, CellType.STRING).setCellValue("Betegség 5");
        header.createCell(8, CellType.STRING).setCellValue("Betegség 6");
        //</editor-fold>

        for (int i = 0; i < listOfDeceased.size(); i++) {
            Row row = sheet.createRow(i+1);
            for (int j = 0; j < 9; j++) {
                Cell cell = row.createCell(j);
                if(j == 0) {cell.setCellValue(listOfDeceased.get(i).getNo());}
                if(j == 1) {cell.setCellValue(listOfDeceased.get(i).getSex());}
                if(j == 2) {cell.setCellValue(listOfDeceased.get(i).getAge());}
                if(j == 3) {cell.setCellValue(listOfDeceased.get(i).getI1());}
                if(j == 4) {cell.setCellValue(listOfDeceased.get(i).getI2());}
                if(j == 5) {cell.setCellValue(listOfDeceased.get(i).getI3());}
                if(j == 6) {cell.setCellValue(listOfDeceased.get(i).getI4());}
                if(j == 7) {cell.setCellValue(listOfDeceased.get(i).getI5());}
                if(j == 8) {cell.setCellValue(listOfDeceased.get(i).getI6());}
            }
        }
        saveXls(wb, filename);
    }

    /**
     *  It makes a sqlite database file from the Deceased List and save it into a file.
     */
    @Override
    public void deceasedModelListToSqlite(List<Deceased> listOfDeceased) {
        Connection connection;
        Statement statement;

        try {
            Class.forName("org.sqlite.JDBC");
            File sqlitefile = new File(SQLITEPATH);
            if (sqlitefile.exists()) {
                sqlitefile.delete();
            }
            connection = DriverManager.getConnection(JDBCDRIVER + SQLITEPATH);
            statement = connection.createStatement();
            connection.setAutoCommit(false);

            String sql = "CREATE TABLE DECEASED " +
                    "(ID INT PRIMARY KEY     NOT NULL," +
                    " SEX            TEXT    NOT NULL, " +
                    " AGE            INT     NOT NULL, " +
                    " ILLNESS1       TEXT, " +
                    " ILLNESS2       TEXT, " +
                    " ILLNESS3       TEXT, " +
                    " ILLNESS4       TEXT, " +
                    " ILLNESS5       TEXT, " +
                    " ILLNESS6       TEXT) ";

            statement.executeUpdate(sql);

            for (int i = 0; i < listOfDeceased.size(); i++) {
                sql = "INSERT INTO DECEASED (ID, SEX, AGE, ILLNESS1, ILLNESS2, ILLNESS3, ILLNESS4, ILLNESS5, ILLNESS6) "+
                        "VALUES (" + i +", '" + listOfDeceased.get(i).getSex() + "', " + listOfDeceased.get(i).getAge() +", " +
                        "'" + listOfDeceased.get(i).getI1() +"', " +
                        "'" + listOfDeceased.get(i).getI2() +"', " +
                        "'" + listOfDeceased.get(i).getI3() +"', " +
                        "'" + listOfDeceased.get(i).getI4() +"', " +
                        "'" + listOfDeceased.get(i).getI5() +"', " +
                        "'" + listOfDeceased.get(i).getI6() +"'); ";
                statement.executeUpdate(sql);
            }

            statement.close();
            connection.commit();
            connection.close();

        } catch (ClassNotFoundException e) {
            log("deceasedModelListToSqlite()", "Uncompatible driver error + " + e);
        } catch (SQLException e) {
            log("deceasedModelListToSqlite()", "SQL error. + " + e);
        }
    }

    /**
     * It saves the xls file.
     * @param wb Apache POI workbook
     * @param filename Name of the file
     */
    private void saveXls(Workbook wb, String filename) {
        try {
            FileOutputStream outputStream = new FileOutputStream(filename);
            wb.write(outputStream);
            wb.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void log(String methodName, String message) {
        Logger.getLogger(Exporters.class.getName())
                .log(Level.INFO, "Exporters/" + methodName + " - " + message);
    }

}
