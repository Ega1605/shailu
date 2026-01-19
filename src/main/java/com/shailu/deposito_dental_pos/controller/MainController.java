package com.shailu.deposito_dental_pos.controller;

import com.shailu.deposito_dental_pos.config.ScreenManager;
import com.shailu.deposito_dental_pos.config.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
        //lblUsuario.setText("Bienvenido: " +userSession.getUsername());
        System.out.println("Pantalla principal cargada");
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
    private void openDetalleVenta() {
        screenManager.show("detalleVenta.fxml", "Sales details", true);
    }


}
