package com.shailu.deposito_dental_pos.service;

import com.shailu.deposito_dental_pos.model.dto.SalesDto;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class PdfQuotationService {

    private static final float MARGIN = 40;

    private static final Color PURPLE = new Color(153, 43, 145);
    private static final Color LIGHT_PURPLE = new Color(245, 225, 243);
    private static final Color LIGHT_GRAY = new Color(248, 248, 248);
    private static final Color BORDER = new Color(80, 80, 80);

    private static final PDType1Font FONT =
            new PDType1Font(Standard14Fonts.FontName.HELVETICA);

    private static final PDType1Font FONT_BOLD =
            new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);

    private static final NumberFormat CURRENCY =
            NumberFormat.getCurrencyInstance(new Locale("es", "MX"));

    public Path generateQuotation(
            List<SalesDto> items,
            String customerName,
            String notes
    ) throws IOException {

        // ----------------------------------------
        // Crear carpeta de cotizaciones
        // ----------------------------------------

        Path quotationDirectory = Paths.get(
                System.getProperty("user.home"),
                "Documents",
                "Cotizaciones"
        );

        Files.createDirectories(quotationDirectory);

        String fileName = "Cotizacion_"
                + customerName+"_"
                + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HH_mm_ss"))
                + ".pdf";

        Path outputFile = quotationDirectory.resolve(fileName);

        // ----------------------------------------
        // Crear PDF
        // ----------------------------------------

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);

            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();

            try (PDPageContentStream content =
                         new PDPageContentStream(document, page)) {

                float currentY = pageHeight - MARGIN;

                // ================================
                // HEADER
                // ================================

                currentY = drawHeader(
                        document,
                        content,
                        currentY,
                        pageWidth
                );

                // ================================
                // CUSTOMER + DATE
                // ================================

                currentY -= 15;

                content.beginText();
                content.setFont(FONT_BOLD, 9);
                content.newLineAtOffset(MARGIN, currentY);
                content.showText(
                        customerName == null || customerName.isBlank()
                                ? "A QUIEN CORRESPONDA"
                                : customerName.toUpperCase()
                );
                content.endText();

                String date = LocalDate.now()
                        .format(
                                DateTimeFormatter.ofPattern(
                                        "dd 'DE' MMMM 'DE' yyyy",
                                        new Locale("es", "MX")
                                )
                        )
                        .toUpperCase();

                content.beginText();
                content.setFont(FONT, 9);

                float dateWidth = FONT.getStringWidth(date) / 1000 * 9;

                content.newLineAtOffset(
                        pageWidth - MARGIN - dateWidth,
                        currentY
                );

                content.showText(date);
                content.endText();

                // ================================
                // TABLE
                // ================================

                currentY -= 30;

                TableResult tableResult = drawProductsTable(
                        content,
                        items,
                        currentY,
                        pageWidth
                );

                currentY = tableResult.y();

                // ================================
                // TOTAL
                // ================================

                currentY -= 10;

                float total = calculateTotal(items);

                currentY = drawTotal(
                        content,
                        total,
                        currentY,
                        pageWidth
                );

                // Observaciones
                if (notes != null && !notes.isBlank()) {

                    currentY -= 15;

                    currentY = drawNotes(
                            content,
                            notes,
                            currentY,
                            pageWidth
                    );
                }

                // ================================
                // FOOTER
                // ================================

                drawFooter(
                        content,
                        pageWidth,
                        MARGIN
                );
            }

            document.save(outputFile.toFile());
        }

        return outputFile;
    }

    // ============================================================
    // HEADER
    // ============================================================

    private float drawHeader(
            PDDocument document,
            PDPageContentStream content,
            float y,
            float pageWidth
    ) throws IOException {

        // ========================================
        // LOGO
        // ========================================

        float logoWidth = 75;
        float logoHeight = 75;

        try (InputStream logoStream =
                     getClass().getResourceAsStream("/Shailu.jpeg")) {

            if (logoStream != null) {

                byte[] logoBytes = readBytes(logoStream);

                PDImageXObject logo =
                        PDImageXObject.createFromByteArray(
                                document,
                                logoBytes,
                                "logo"
                        );

                // Centrar el logo
                float logoX =
                        (pageWidth - logoWidth) / 2;

                content.drawImage(
                        logo,
                        logoX,
                        y - logoHeight,
                        logoWidth,
                        logoHeight
                );
            }
        }

        // ========================================
        // INFORMACIÓN DE LA EMPRESA
        // ========================================

        float textStartY = y - logoHeight - 10;

        drawCenteredText(
                content,
                "DEPÓSITO DENTAL SHAILU",
                FONT_BOLD,
                14,
                0,
                textStartY,
                pageWidth,
                18
        );

        drawCenteredText(
                content,
                "AV. AGUASCALIENTES SUR 1054",
                FONT,
                9,
                0,
                textStartY - 17,
                pageWidth,
                13
        );

        drawCenteredText(
                content,
                "COL. JARDINES DE CASANUEVA",
                FONT,
                9,
                0,
                textStartY - 30,
                pageWidth,
                13
        );

        drawCenteredText(
                content,
                "CP. 20297 AGUASCALIENTES, AGS",
                FONT,
                9,
                0,
                textStartY - 43,
                pageWidth,
                13
        );

        // ========================================
        // TÍTULO COTIZACIÓN
        // ========================================

        float quotationY = textStartY - 75;

        drawCenteredText(
                content,
                "COTIZACIÓN",
                FONT_BOLD,
                18,
                0,
                quotationY,
                pageWidth,
                25
        );

        return quotationY - 35;
    }


    // ============================================================
    // PRODUCTS TABLE
    // ============================================================

    private TableResult drawProductsTable(
            PDPageContentStream content,
            List<SalesDto> items,
            float y,
            float pageWidth
    ) throws IOException {

        float tableWidth = pageWidth - (MARGIN * 2);

        // Column widths
        float descriptionWidth = tableWidth * 0.57f;
        float quantityWidth = tableWidth * 0.12f;
        float priceWidth = tableWidth * 0.155f;
        float amountWidth = tableWidth * 0.155f;

        float xDescription = MARGIN;
        float xQuantity = xDescription + descriptionWidth;
        float xPrice = xQuantity + quantityWidth;
        float xAmount = xPrice + priceWidth;

        // ----------------------------------------
        // Header
        // ----------------------------------------

        float headerHeight = 25;

        content.setNonStrokingColor(PURPLE);

        content.addRect(
                MARGIN,
                y - headerHeight,
                tableWidth,
                headerHeight
        );

        content.fill();

        content.setStrokingColor(BORDER);
        content.setLineWidth(0.6f);

        content.addRect(
                MARGIN,
                y - headerHeight,
                tableWidth,
                headerHeight
        );

        content.stroke();

        drawHeaderCell(
                content,
                "PRODUCTO",
                xDescription,
                y,
                descriptionWidth,
                headerHeight
        );

        drawHeaderCell(
                content,
                "CANTIDAD",
                xQuantity,
                y,
                quantityWidth,
                headerHeight
        );

        drawHeaderCell(
                content,
                "PRECIO",
                xPrice,
                y,
                priceWidth,
                headerHeight
        );

        drawHeaderCell(
                content,
                "IMPORTE",
                xAmount,
                y,
                amountWidth,
                headerHeight
        );

        y -= headerHeight;

        // ----------------------------------------
        // Product rows
        // ----------------------------------------

        int rowNumber = 0;

        for (SalesDto item : items) {

            String description = item.getName() == null
                    ? ""
                    : item.getName().toUpperCase();

            List<String> lines = wrapText(
                    description,
                    FONT_BOLD,
                    8,
                    descriptionWidth - 10
            );

            float lineHeight = 10;
            float rowHeight = Math.max(
                    25,
                    lines.size() * lineHeight + 8
            );

            // Alternating rows
            if (rowNumber % 2 == 1) {

                content.setNonStrokingColor(LIGHT_PURPLE);

                content.addRect(
                        MARGIN,
                        y - rowHeight,
                        tableWidth,
                        rowHeight
                );

                content.fill();
            } else {

                content.setNonStrokingColor(Color.WHITE);

                content.addRect(
                        MARGIN,
                        y - rowHeight,
                        tableWidth,
                        rowHeight
                );

                content.fill();
            }

            // Border
            content.setStrokingColor(BORDER);
            content.setLineWidth(0.5f);

            content.addRect(
                    MARGIN,
                    y - rowHeight,
                    tableWidth,
                    rowHeight
            );

            content.stroke();

            // Vertical lines
            drawVerticalLine(
                    content,
                    xQuantity,
                    y,
                    rowHeight
            );

            drawVerticalLine(
                    content,
                    xPrice,
                    y,
                    rowHeight
            );

            drawVerticalLine(
                    content,
                    xAmount,
                    y,
                    rowHeight
            );

            // Description
            float textY = y - 13;

            for (String line : lines) {

                drawText(
                        content,
                        line,
                        FONT_BOLD,
                        7.5f,
                        xDescription + 5,
                        textY
                );

                textY -= lineHeight;
            }

            // Quantity
            String quantity =
                    String.valueOf(item.getQuantity());

            drawCenteredText(
                    content,
                    quantity,
                    FONT,
                    8,
                    xQuantity,
                    y,
                    quantityWidth,
                    rowHeight
            );

            // Price
            String price =
                    formatCurrency(item.getPrice());

            drawRightText(
                    content,
                    price,
                    FONT_BOLD,
                    8,
                    xPrice,
                    y,
                    priceWidth,
                    rowHeight
            );

            // Amount
            double amount =
                    item.getPrice() * item.getQuantity();

            drawRightText(
                    content,
                    formatCurrency(amount),
                    FONT_BOLD,
                    8,
                    xAmount,
                    y,
                    amountWidth,
                    rowHeight
            );

            y -= rowHeight;

            rowNumber++;
        }

        return new TableResult(y);
    }

    // ============================================================
    // TOTAL
    // ============================================================

    private float drawTotal(
            PDPageContentStream content,
            double total,
            float y,
            float pageWidth
    ) throws IOException {

        float tableWidth = pageWidth - (MARGIN * 2);

        float totalLabelWidth = tableWidth * 0.845f;
        float totalValueWidth = tableWidth * 0.155f;

        float rowHeight = 30;

        float xTotalValue =
                MARGIN + totalLabelWidth;

        content.setNonStrokingColor(LIGHT_GRAY);

        content.addRect(
                MARGIN,
                y - rowHeight,
                tableWidth,
                rowHeight
        );

        content.fill();

        content.setStrokingColor(BORDER);
        content.setLineWidth(0.6f);

        content.addRect(
                MARGIN,
                y - rowHeight,
                tableWidth,
                rowHeight
        );

        content.stroke();

        drawVerticalLine(
                content,
                xTotalValue,
                y,
                rowHeight
        );

        drawRightText(
                content,
                "TOTAL",
                FONT_BOLD,
                9,
                MARGIN,
                y,
                totalLabelWidth - 8,
                rowHeight
        );

        drawRightText(
                content,
                formatCurrency(total),
                FONT_BOLD,
                10,
                xTotalValue,
                y,
                totalValueWidth - 8,
                rowHeight
        );

        return y - rowHeight;
    }

    // ============================================================
    // NOTES
    // ============================================================

    private float drawNotes(
            PDPageContentStream content,
            String notes,
            float y,
            float pageWidth
    ) throws IOException {

        float boxWidth = pageWidth - (MARGIN * 2);
        float padding = 8;

        float titleHeight = 16;

        // Título
        drawText(
                content,
                "OBSERVACIONES",
                FONT_BOLD,
                8,
                MARGIN,
                y
        );

        y -= 5;

        // Calcular líneas necesarias
        List<String> lines = wrapText(
                notes,
                FONT,
                8,
                boxWidth - (padding * 2)
        );

        float lineHeight = 11;

        float boxHeight =
                Math.max(
                        30,
                        lines.size() * lineHeight + padding * 2
                );

        // Fondo
        content.setNonStrokingColor(
                new Color(248, 248, 248)
        );

        content.addRect(
                MARGIN,
                y - boxHeight,
                boxWidth,
                boxHeight
        );

        content.fill();

        // Borde
        content.setStrokingColor(
                new Color(190, 190, 190)
        );

        content.setLineWidth(0.5f);

        content.addRect(
                MARGIN,
                y - boxHeight,
                boxWidth,
                boxHeight
        );

        content.stroke();

        // Texto
        float textY = y - padding - 8;

        for (String line : lines) {

            drawText(
                    content,
                    line,
                    FONT,
                    8,
                    MARGIN + padding,
                    textY
            );

            textY -= lineHeight;
        }

        return y - boxHeight;
    }

    // ============================================================
    // FOOTER
    // ============================================================

    private void drawFooter(
            PDPageContentStream content,
            float pageWidth,
            float margin
    ) throws IOException {

        String footer =
                "PRECIOS SUJETOS A CAMBIO SIN PREVIO AVISO, "
                        + "INCLUYEN I.V.A. • COTIZACIÓN SUJETA A STOCK";

        // Línea separadora
        content.setStrokingColor(new Color(180, 180, 180));
        content.setLineWidth(0.5f);

        content.moveTo(margin, 48);
        content.lineTo(pageWidth - margin, 48);
        content.stroke();

        // Texto centrado
        drawCenteredText(
                content,
                footer,
                FONT,
                7,
                0,
                30,
                pageWidth,
                12,
                new Color(90, 90, 90)
        );
    }

    // ============================================================
    // HELPER METHODS
    // ============================================================

    private void drawHeaderCell(
            PDPageContentStream content,
            String text,
            float x,
            float y,
            float width,
            float height
    ) throws IOException {

        drawCenteredText(
                content,
                text,
                FONT_BOLD,
                7,
                x,
                y,
                width,
                height,
                Color.WHITE
        );
    }

    private void drawText(
            PDPageContentStream content,
            String text,
            PDType1Font font,
            float fontSize,
            float x,
            float y
    ) throws IOException {

        content.beginText();
        content.setFont(font, fontSize);
        content.setNonStrokingColor(Color.BLACK);
        content.newLineAtOffset(x, y);
        content.showText(sanitize(text));
        content.endText();
    }

    private void drawCenteredText(
            PDPageContentStream content,
            String text,
            PDType1Font font,
            float fontSize,
            float x,
            float y,
            float width,
            float height
    ) throws IOException {

        drawCenteredText(
                content,
                text,
                font,
                fontSize,
                x,
                y,
                width,
                height,
                Color.BLACK
        );
    }

    private void drawCenteredText(
            PDPageContentStream content,
            String text,
            PDType1Font font,
            float fontSize,
            float x,
            float y,
            float width,
            float height,
            Color color
    ) throws IOException {

        float textWidth =
                font.getStringWidth(text)
                        / 1000
                        * fontSize;

        float textX =
                x + (width - textWidth) / 2;

        float textY =
                y - (height / 2) - (fontSize / 2) + 3;

        content.beginText();
        content.setFont(font, fontSize);
        content.setNonStrokingColor(color);
        content.newLineAtOffset(textX, textY);
        content.showText(sanitize(text));
        content.endText();
    }

    private void drawRightText(
            PDPageContentStream content,
            String text,
            PDType1Font font,
            float fontSize,
            float x,
            float y,
            float width,
            float height
    ) throws IOException {

        float textWidth =
                font.getStringWidth(text)
                        / 1000
                        * fontSize;

        float textX =
                x + width - textWidth;

        float textY =
                y - (height / 2) - (fontSize / 2) + 3;

        content.beginText();
        content.setFont(font, fontSize);
        content.setNonStrokingColor(Color.BLACK);
        content.newLineAtOffset(textX, textY);
        content.showText(sanitize(text));
        content.endText();
    }

    private void drawVerticalLine(
            PDPageContentStream content,
            float x,
            float y,
            float height
    ) throws IOException {

        content.moveTo(x, y);
        content.lineTo(x, y - height);
        content.stroke();
    }

    private List<String> wrapText(
            String text,
            PDType1Font font,
            float fontSize,
            float maxWidth
    ) {

        List<String> lines = new ArrayList<>();

        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {

            String testLine = currentLine.isEmpty()
                    ? word
                    : currentLine + " " + word;

            float width;

            try {
                width = font.getStringWidth(testLine)
                        / 1000
                        * fontSize;
            } catch (IOException e) {
                width = maxWidth + 1;
            }

            if (width <= maxWidth) {

                currentLine = new StringBuilder(testLine);

            } else {

                if (!currentLine.isEmpty()) {
                    lines.add(currentLine.toString());
                }

                currentLine = new StringBuilder(word);
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    private float calculateTotal(List<SalesDto> items) {

        return (float) items.stream()
                .mapToDouble(item ->
                        item.getPrice() * item.getQuantity()
                )
                .sum();
    }

    private String formatCurrency(double value) {
        return CURRENCY.format(value);
    }

    private String sanitize(String text) {

        if (text == null) {
            return "";
        }

        // Helvetica de PDFBox no soporta todos los caracteres Unicode.
        return text
                .replace("–", "-")
                .replace("—", "-")
                .replace("“", "\"")
                .replace("”", "\"")
                .replace("’", "'");
    }

    private byte[] readBytes(InputStream inputStream)
            throws IOException {

        ByteArrayOutputStream buffer =
                new ByteArrayOutputStream();

        byte[] data = new byte[4096];

        int bytesRead;

        while ((bytesRead = inputStream.read(data)) != -1) {
            buffer.write(data, 0, bytesRead);
        }

        return buffer.toByteArray();
    }

    private record TableResult(float y) {
    }
}
