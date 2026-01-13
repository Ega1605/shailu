package com.shailu.deposito_dental_pos.controller;

import com.shailu.deposito_dental_pos.config.ScreenManager;
import com.shailu.deposito_dental_pos.config.UserSession;
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

    @Autowired
    private UserSession userSession;


    @FXML
    public void onLogin() {

        String userName = usernameField.getText();
        boolean success = userService.login(
                userName,
                passwordField.getText()
        );

        if (success) {
            errorLabel.setText("Login correcto");
            userSession.setUsername(userName);
            screenManager.show("main.fxml", "Inicio");
        } else {
            errorLabel.setText("Usuario o contraseña incorrectos");
        }
    }
}
