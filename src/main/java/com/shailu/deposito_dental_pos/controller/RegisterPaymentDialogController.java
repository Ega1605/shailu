package com.shailu.deposito_dental_pos.controller;

import com.shailu.deposito_dental_pos.model.dto.RegisterPaymentDto;
import com.shailu.deposito_dental_pos.model.enums.PaymentType;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RegisterPaymentDialogController {

    @FXML
    private TextField txtAmount;
    @FXML private ComboBox<PaymentType> cbPaymentType;
    private Stage dialogStage;
    private boolean confirmed = false;

    public void init(Double remainingBalance) {
        this.confirmed = false;
        txtAmount.setText(String.format("%.2f", remainingBalance));
        cbPaymentType.setItems(
                FXCollections.observableArrayList(PaymentType.values())
        );
        cbPaymentType.getSelectionModel().select(PaymentType.CASH);
    }

    @FXML
    private void onRegister() {
        if (txtAmount.getText().isBlank()) return;
        this.confirmed = true;
        this.dialogStage.close();
    }

    @FXML
    private void onCancel() {
        this.confirmed = false;
        this.dialogStage.close();
    }

    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }

    public Optional<RegisterPaymentDto> getResult() {
        if (!confirmed) return Optional.empty();

        return Optional.of(
                new RegisterPaymentDto(
                        Double.valueOf(txtAmount.getText()),
                        cbPaymentType.getValue()
                )
        );
    }
}
