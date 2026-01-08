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

        tableProducts.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        tableProducts.prefWidthProperty().bind(
                colCode.widthProperty()
                        .add(colName.widthProperty())
                        .add(colPrice.widthProperty())
                        .add(colStock.widthProperty())
        );

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
        btnAdd.setOnAction(e -> addProduct());
        loadProducts();
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
        txtName.clear();
        txtDescription.clear();
        txtPurchasePrice.clear();
        txtProfit.clear();
    }


}
