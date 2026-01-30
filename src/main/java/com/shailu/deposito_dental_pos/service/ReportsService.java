package com.shailu.deposito_dental_pos.service;

import com.shailu.deposito_dental_pos.model.enums.PaymentType;
import com.shailu.deposito_dental_pos.model.projection.ProductOutProjection;
import com.shailu.deposito_dental_pos.model.projection.SalesByPaymentProjection;
import com.shailu.deposito_dental_pos.repository.InventoryMovementsRepository;
import com.shailu.deposito_dental_pos.repository.SalesRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class ReportsService {

    @Autowired
    private InventoryMovementsRepository inventoryMovementsRepository;

    @Autowired
    private SalesRepository salesRepository;

    public void createDailyReport(){

        ZoneId zone = ZoneId.of("America/Mexico_City");
        LocalDate today = LocalDate.now(zone);

        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(20, 0);

        List<ProductOutProjection> productOut = inventoryMovementsRepository.getDailyProductOut(start, end);
        List<SalesByPaymentProjection> salesByPayment = salesRepository.getSalesByPaymentType(start, end);

        createExcel(productOut, salesByPayment, today);

    }

    private void createExcel(List<ProductOutProjection> products, List<SalesByPaymentProjection> sales, LocalDate date) {

        try (Workbook workbook = new XSSFWorkbook()) {

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 11);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            CellStyle textStyle = workbook.createCellStyle();
            textStyle.setBorderBottom(BorderStyle.THIN);

            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.setBorderBottom(BorderStyle.THIN);
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);

            CellStyle moneyStyle = workbook.createCellStyle();
            moneyStyle.setDataFormat(
                    workbook.createDataFormat().getFormat("$#,##0.00")
            );
            moneyStyle.setAlignment(HorizontalAlignment.RIGHT);
            moneyStyle.setBorderBottom(BorderStyle.THIN);

            // ===============================
            // Sheet 1: Products sold
            // ===============================
            Sheet productSheet = workbook.createSheet("Salida de Productos");

            Row header = productSheet.createRow(0);

            String[] productHeaders = {"Código", "Producto", "Cantidad Salida"};

            for (int i = 0; i < productHeaders.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(productHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (ProductOutProjection p : products) {
                Row row = productSheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(p.getCode());
                row.getCell(0).setCellStyle(textStyle);

                row.createCell(1).setCellValue(p.getName());
                row.getCell(1).setCellStyle(textStyle);

                row.createCell(2).setCellValue(p.getTotalQuantity());
                row.getCell(2).setCellStyle(numberStyle);
            }

            // ===============================
            // Sheet 2: Sales by Payment Type
            // ===============================
            Sheet salesSheet = workbook.createSheet("Ventas por Pago");

            Row header2 = salesSheet.createRow(0);
            String[] salesHeaders = {"Tipo de Pago", "Total Ventas", "Monto Total"};

            for (int i = 0; i < salesHeaders.length; i++) {
                Cell cell = header2.createCell(i);
                cell.setCellValue(salesHeaders[i]);
                cell.setCellStyle(headerStyle);
            }

            rowIdx = 1;
            for (SalesByPaymentProjection s : sales) {
                String paymentLabel = PaymentType
                        .valueOf(s.getPaymentType())
                        .getPaymentType();

                Row row = salesSheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(paymentLabel);
                row.getCell(0).setCellStyle(textStyle);

                row.createCell(1).setCellValue(s.getTotalSales());
                row.getCell(1).setCellStyle(numberStyle);

                row.createCell(2).setCellValue(s.getTotalAmount().doubleValue());
                row.getCell(2).setCellStyle(moneyStyle);
            }

            for (int i = 0; i < 3; i++) {
                productSheet.autoSizeColumn(i);
                salesSheet.autoSizeColumn(i);
            }

            productSheet.createFreezePane(0, 1);
            salesSheet.createFreezePane(0, 1);


            // ===============================
            // SAVE FILE
            // ===============================
            String dateFile = date.toString();

            String userHome = System.getProperty("user.home");
            Path reportsDir = Paths.get(userHome, "Documents", "reportes");
            Files.createDirectories(reportsDir);
            Path path = reportsDir.resolve("Reporte-Diario-" + dateFile + ".xlsx");

            try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
                workbook.write(fos);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel report", e);
        }
    }

}
