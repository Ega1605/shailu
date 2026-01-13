package com.shailu.deposito_dental_pos.controller;

import com.shailu.deposito_dental_pos.config.UserSession;
import com.shailu.deposito_dental_pos.model.dto.ProductDto;
import com.shailu.deposito_dental_pos.model.dto.SalesDto;
import com.shailu.deposito_dental_pos.service.ProductService;
import com.shailu.deposito_dental_pos.service.SalesService;
import com.shailu.deposito_dental_pos.utils.ValidateFields;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.TextFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class SalesController {



    @FXML
    private BorderPane mainContainer;

    @FXML private TableView<SalesDto> tableSales;
    @FXML private TableColumn<SalesDto, String> colName;
    @FXML private TableColumn<SalesDto, String> colDesc;
    @FXML private TableColumn<SalesDto, Double> colPrice;
    @FXML private TableColumn<SalesDto, Integer> colQty;
    @FXML private TableColumn<SalesDto, Double> colSubtotal;

    @FXML private TextField txtCode;
    @FXML private TextField txtName;
    @FXML private TextField txtDesc;
    @FXML private TextField txtPrice;
    @FXML private TextField txtQuantity;
    @FXML public TextField searchProduct;
    @FXML private Label lblTotal;

    @FXML private Button btnAddProduct;

    @Autowired
    private ProductService productService;

    @Autowired
    private SalesService salesService;

    @Autowired
    private UserSession userSession;

    private final ObservableList<SalesDto> saleItems =
            FXCollections.observableArrayList();

    private static final double TAX = 0.16;

    @FXML
    public void initialize() {
        String css = getClass().getResource("/styles/sales.css").toExternalForm();
        mainContainer.getStylesheets().add(css);

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("description"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        tableSales.setItems(saleItems);


        btnAddProduct.setOnAction(e -> addProductToSale());


        txtQuantity.setOnAction(e -> addProductToSale());

        searchProduct.setOnAction(e -> searchProductByCodeBar(searchProduct.getText()));

        searchProduct.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                searchProductByCodeBar(searchProduct.getText());
            }
        });



        txtCode.setOnAction(e -> searchProductByCode(txtCode.getText()));

        tableSales.setEditable(true);
        //When the column changes to editable status convert to TextField
        colQty.setCellFactory(TextFieldTableCell.forTableColumn(new javafx.util.converter.IntegerStringConverter()));

        // Manage when the user enter de value
        colQty.setOnEditCommit(event -> {
            // get SalesDto for row
            SalesDto item = event.getRowValue();

            // get new value for quantity
            Integer newQty = event.getNewValue();

            if (newQty != null && newQty > 0) {
                item.setQuantity(newQty);
                item.setSubtotal(item.getPrice() * item.getQuantity());
                tableSales.refresh();
                calculateTotals();
            } else {

                tableSales.refresh();
            }
        });

        //searching product by description
        TextFields.bindAutoCompletion(txtName, suggestionRequest -> {
            String userText = suggestionRequest.getUserText();
            if (userText == null || userText.length() < 3) {
                return Collections.emptyList();
            }
            return productService.searchProductsByName(userText);
        }, new StringConverter<ProductDto>() {
            @Override
            public String toString(ProductDto product) {
                return product == null ? "" : product.getName();
            }

            @Override
            public ProductDto fromString(String string) {
                return null;
            }
        }).setOnAutoCompleted(event -> {
            ProductDto selectedProduct = event.getCompletion();
            fillProductFields(selectedProduct);
        });

    }

    private void searchProductByCodeBar(String productCodeBar) {
        if(ValidateFields.validateStringEmpty(productCodeBar)) return;
        handleSearchProductResult(productService.findByBarCode(productCodeBar), productCodeBar);
    }

    private void searchProductByCode(String code) {

        if(ValidateFields.validateStringEmpty(code)) return;
        handleSearchProductResult(productService.findByCode(code), code);
    }

    private void handleSearchProductResult(Optional<ProductDto> result, String code){

        result.ifPresent(this::fillProductFields);
    }



    private void fillProductFields(ProductDto product) {

        txtCode.setText(product.getCode());
        txtName.setText(product.getName());
        txtDesc.setText(product.getDescription());
        txtPrice.setText(String.valueOf(product.getPrice()));

        btnAddProduct.requestFocus();
    }

    private void clearProductFields() {
        searchProduct.clear();
        txtCode.clear();
        txtDesc.clear();;
        txtName.clear();
        txtPrice.clear();
        txtQuantity.clear();
    }

    private void addProductToSale() {

        if (!isFormValid()) return;

        String code = txtCode.getText();
        String name = txtName.getText();
        String description = txtDesc.getText();
        double price = Double.parseDouble(txtPrice.getText());
        int quantity = Integer.parseInt(txtQuantity.getText());

        SalesDto item = new SalesDto(code,name,description,price, quantity);

        saleItems.add(item);

        calculateTotals();
        clearProductFields();
    }

    @FXML
    private void finalizeSale() {
        try {

            List<SalesDto> items = tableSales.getItems();

            if (items.isEmpty()) {
                ValidateFields.showError("No hay productos en la venta");
                return;
            }


            salesService.processSale(items, userSession.getUsername());

            ValidateFields.showInfo("Venta procesada con éxito");

            // clean
            cancelSale();

        } catch (Exception e) {
            ValidateFields.showError("Error al procesar: " + e.getMessage());
        }
    }

    private boolean isFormValid() {
        if (ValidateFields.isEmpty(txtCode)) return ValidateFields.showError("El código es obligatorio");
        if (ValidateFields.isEmpty(txtName)) return ValidateFields.showError("El nombre es obligatorio");
        if (ValidateFields.isEmpty(txtDesc)) return ValidateFields.showError("La descripción es obligatoria");
        if (ValidateFields.isEmpty(txtPrice)) return ValidateFields.showError("El precio es obligatorio");
        if (ValidateFields.isEmpty(txtQuantity)) return ValidateFields.showError("La cantidad es obligatoria");

        return true;
    }

    private void calculateTotals() {

        double total = saleItems.stream()
                .mapToDouble(SalesDto::getSubtotal)
                .sum();

        lblTotal.setText(String.format("%.2f", total));
    }

    @FXML
    private void cancelSale() {
        clearProductFields();
        tableSales.getItems().clear();
        lblTotal.setText("0.00");
    }


}
