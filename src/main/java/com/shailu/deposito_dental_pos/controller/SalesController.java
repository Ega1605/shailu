package com.shailu.deposito_dental_pos.controller;

import com.shailu.deposito_dental_pos.model.dto.ProductDto;
import com.shailu.deposito_dental_pos.model.dto.SalesDto;
import com.shailu.deposito_dental_pos.service.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SalesController {



    @FXML
    private BorderPane mainContainer; // Debes poner fx:id="mainContainer" en el BorderPane del FXML

    @FXML private TableView<SalesDto> tableSales;
    @FXML private TableColumn<SalesDto, String> colName;
    @FXML private TableColumn<SalesDto, String> colDesc;
    @FXML private TableColumn<SalesDto, Double> colPrice;
    @FXML private TableColumn<SalesDto, Integer> colQty;
    @FXML private TableColumn<SalesDto, Double> colSubtotal;

    @FXML private TextField txtName;
    @FXML private TextField txtDesc;
    @FXML private TextField txtPrice;
    @FXML private TextField txtQuantity;
    @FXML public TextField searchProduct;
    @FXML private Label lblSubtotal;
    @FXML private Label lblTax;
    @FXML private Label lblTotal;

    @FXML private Button btnAddProduct;

    @Autowired
    private ProductService productService;

    private final ObservableList<SalesDto> saleItems =
            FXCollections.observableArrayList();

    private static final double TAX = 0.16;

    @FXML
    public void initialize() {
        String css = getClass().getResource("/styles/sales.css").toExternalForm();
        mainContainer.getStylesheets().add(css);

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

        txtName.setOnAction(e -> searchProductByName(txtName.getText()));

        txtName.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                searchProductByName(txtName.getText());
            }
        });
    }

    private void searchProductByCodeBar(String productCode) {

        if (productCode == null || productCode.isBlank()) {
            return;
        }

        productService.findByBarCode(productCode)
                .ifPresentOrElse(
                        this::fillProductFields,
                        this::clearProductFields
                );
    }

    private void searchProductByName(String productName) {

        if (productName == null || productName.isBlank()) {
            return;
        }

        productService.searchProductByName(productName)
                .ifPresentOrElse(
                        this::fillProductFields,
                        this::clearProductFields
                );
    }


    private void fillProductFields(ProductDto product) {

        txtName.setText(product.getName());
        txtPrice.setText(String.valueOf(product.getPrice()));

        btnAddProduct.requestFocus();
    }

    private void clearProductFields() {
        txtName.clear();
        txtPrice.clear();
        txtQuantity.clear();
    }

    private void addProductToSale() {

        if (txtName.getText().isEmpty()) return;

        String name = txtName.getText();
        String description = txtDesc.getText();
        double price = Double.parseDouble(txtPrice.getText());
        int quantity = Integer.parseInt(txtQuantity.getText());

        SalesDto item = new SalesDto(name,description,price, quantity);

        saleItems.add(item);

        calculateTotals();
        clearProductFields();
    }

    private void calculateTotals() {

        double subtotal = saleItems.stream()
                .mapToDouble(SalesDto::getSubtotal)
                .sum();

        lblSubtotal.setText(String.format("%.2f", subtotal));
        lblTotal.setText(String.format("%.2f", subtotal));
    }


}
