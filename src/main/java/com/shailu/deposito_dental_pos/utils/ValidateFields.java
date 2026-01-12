package com.shailu.deposito_dental_pos.utils;

import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.springframework.stereotype.Component;


public class ValidateFields {

    public static boolean isEmpty(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    public static boolean isNumber(TextField field) {
        try {
            Double.parseDouble(field.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isInteger(TextField field) {
        try {
            Integer.parseInt(field.getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean showError(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validación");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        return false;
    }

    public static void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean validateStringEmpty(String text){

        return text == null || text.isBlank();
    }





}
