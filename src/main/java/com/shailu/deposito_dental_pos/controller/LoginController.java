package com.shailu.deposito_dental_pos.controller;

import com.shailu.deposito_dental_pos.config.ScreenManager;
import com.shailu.deposito_dental_pos.service.UserService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @Autowired
    private UserService userService;

    @Autowired
    private ScreenManager screenManager;


    @FXML
    public void onLogin() {
        boolean success = userService.login(
                usernameField.getText(),
                passwordField.getText()
        );

        if (success) {
            errorLabel.setText("Login correcto");
            // aquí abrimos la siguiente pantalla
            screenManager.show("main.fxml", "Inicio");
        } else {
            errorLabel.setText("Usuario o contraseña incorrectos");
        }
    }
}
