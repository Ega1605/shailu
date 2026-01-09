package com.shailu.deposito_dental_pos.controller;

import com.shailu.deposito_dental_pos.model.dto.ProductDto;
import com.shailu.deposito_dental_pos.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InventoryController {

    public static final double TAX = 0.16D;
    @Autowired
    private ProductService productService;


    @FXML private TextField txtCode;
    @FXML private TextField txtBarCode;
    @FXML private TextField txtName;
    @FXML private TextField txtDescription;
    @FXML private TextField txtPurchasePrice;
    @FXML private TextField txtProfit;

    @FXML private Button btnAdd;

    @FXML
    private TableView<ProductDto> tableProducts;

    @FXML
    private TableColumn<ProductDto, String> colCode;

    @FXML
    private TableColumn<ProductDto, String> colName;

    @FXML
    private TableColumn<ProductDto, Integer> colStock;

    @FXML
    private TableColumn<ProductDto, Double> colPrice;

    private final ObservableList<ProductDto> products =
            FXCollections.observableArrayList();


    @FXML
    public void initialize() {

        colCode.setCellValueFactory(
                new PropertyValueFactory<>("code")
        );
        colName.setCellValueFactory(
                new PropertyValueFactory<>("name")
        );
        colStock.setCellValueFactory(
                new PropertyValueFactory<>("currentStock")
        );
        colPrice.setCellValueFactory(
                new PropertyValueFactory<>("price")
        );

        txtBarCode.setOnAction(e -> onBarCodeScanned());

        txtBarCode.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                onBarCodeScanned();
            }
        });
        btnAdd.setOnAction(e -> addProduct());
        loadProducts();
    }

    private void onBarCodeScanned() {

        String barCode = txtBarCode.getText();

        if (barCode == null || barCode.isBlank()) {
            return;
        }

        productService.findByBarCode(barCode)
                .ifPresentOrElse(
                        this::fillProductFields,
                        this::clearProductFields
                );
    }

    private void addProduct() {

        ProductDto product = new ProductDto();
        product.setCode(txtCode.getText());
        product.setBarCode(txtBarCode.getText());
        product.setName(txtName.getText());
        product.setDescription(txtDescription.getText());
        product.setPurchasePrice(Double.valueOf(txtPurchasePrice.getText()));
        product.setProfit(Double.valueOf(txtProfit.getText()));
        product.setTax(TAX);

        productService.addProduct(product);

        clearForm();
        loadProducts();

    }

    private void loadProducts() {
        products.clear();
        products.addAll(productService.findAll());
        tableProducts.setItems(products);
    }

    private void clearForm() {
        txtCode.clear();
        txtBarCode.clear();
        clearProductFields();
    }

    private void fillProductFields(ProductDto product) {

        txtCode.setText(product.getCode());
        txtName.setText(product.getName());
        txtDescription.setText(product.getDescription());
        txtPurchasePrice.setText(String.valueOf(product.getPurchasePrice()));
        txtProfit.setText(String.valueOf(product.getProfit()));

        btnAdd.requestFocus();
    }

    private void clearProductFields() {
        txtName.clear();
        txtDescription.clear();
        txtPurchasePrice.clear();
        txtProfit.clear();
    }



}
