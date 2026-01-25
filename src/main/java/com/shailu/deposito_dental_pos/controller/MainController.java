package com.shailu.deposito_dental_pos.controller;

import com.shailu.deposito_dental_pos.config.ScreenManager;
import com.shailu.deposito_dental_pos.config.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MainController {

    @Autowired
    private UserSession userSession;
    @Autowired
    private ScreenManager screenManager;

    @FXML
    private Label lblUsuario, lblFecha, lblHora;


    public void initialize() {
        //Crontoller connects UI with business logic
        lblUsuario.setText(userSession.getUsername());
        System.out.println("Home loaded");
    }

    @FXML
    private void openInventory() {
        screenManager.show("inventory.fxml", "Inventario", true);
    }

    @FXML
    private void openSales() {
        screenManager.show("sales.fxml", "Sales", true);
    }

    @FXML
    private void openSaleDetails() {
        screenManager.show("saleDetails.fxml", "Sales details", true);
    }


}
