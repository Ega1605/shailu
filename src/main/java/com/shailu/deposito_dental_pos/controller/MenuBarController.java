package com.shailu.deposito_dental_pos.controller;

import com.shailu.deposito_dental_pos.config.ScreenManager;
import javafx.fxml.FXML;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MenuBarController {
    @Autowired
    private ScreenManager screenManager;

    @FXML
    public void goToHome() { screenManager.show("main.fxml", "Inicio"); }
    @FXML public void goToSales() { screenManager.show("sales.fxml", "Ventas"); }
    @FXML public void goToClients() { screenManager.show("Clients.fxml", "Clientes"); }
    @FXML public void goToInventory() { screenManager.show("inventory.fxml", "Inventario"); }
    @FXML public void goToReports() { screenManager.show("Reports.fxml", "Reportes"); }
    @FXML public void goToInvoice() { screenManager.show("Invoice.fxml", "Facturación"); }
}
