package com.shailu.deposito_dental_pos.service;

import com.sun.javafx.print.PrintHelper;
import com.sun.javafx.print.Units;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
import org.springframework.stereotype.Service;

@Service
public class FXMLPrintService {


    public void printNode(Node node, String printerName) {
        Printer printer = Printer.getAllPrinters().stream()
                .filter(p -> p.getName().equalsIgnoreCase(printerName))
                .findFirst()
                .orElse(Printer.getDefaultPrinter());

        if (printer == null) {
            System.err.println("No hay impresora disponible");
            return;
        }

        // 🔥 FORZAR SCENE CON TAMAÑO REAL
        if (node instanceof Parent parent) {
            Scene scene = new Scene(parent, 140, 1000);
            parent.applyCss();
            parent.layout();
        }

        // DEBUG (déjalos por ahora)
        System.out.println("Node width  : " + node.getBoundsInParent().getWidth());
        System.out.println("Node height : " + node.getBoundsInParent().getHeight());
        System.out.println("Layout X    : " + node.getLayoutBounds().getWidth());
        System.out.println("Layout Y    : " + node.getLayoutBounds().getHeight());

        Paper thermalPaper = PrintHelper.createPaper(
                "THERMAL_58",
                140,
                1000,
                Units.POINT
        );

        PageLayout pageLayout = printer.createPageLayout(
                thermalPaper,
                PageOrientation.PORTRAIT,
                Printer.MarginType.HARDWARE_MINIMUM
        );

        PrinterJob job = PrinterJob.createPrinterJob(printer);
        if (job != null && job.printPage(pageLayout, node)) {
            job.endJob();
        }

        }
}
