package com.shailu.deposito_dental_pos.controller;

import com.shailu.deposito_dental_pos.model.dto.ProductDto;
import com.shailu.deposito_dental_pos.service.ProductService;
import com.shailu.deposito_dental_pos.utils.ValidateFields;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
    @FXML private TextField txtQuantity;
    @FXML private ImageView btnResetSearch;

    @FXML private Button btnAdd;

    @FXML private TextField txtSearch;
    @FXML private Button btnDelete;
    @FXML private TableView<ProductDto> tableProducts;

    @FXML private TableColumn<ProductDto, String> colCode;

    @FXML private TableColumn<ProductDto, String> colName;

    @FXML private TableColumn<ProductDto, Integer> colStock;

    @FXML private TableColumn<ProductDto, Double> colPrice;

    @FXML private Pagination pagination;

    private final ObservableList<ProductDto> products =
            FXCollections.observableArrayList();


    @FXML
    public void initialize() {

        //visualization
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

        //Pagination
        pagination.setPageFactory(this::createPage);

        //Listeners

        txtBarCode.setOnAction(e -> onBarCodeScanned());

        txtBarCode.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal ) {
                onBarCodeScanned();
            }
        });
        btnAdd.setOnAction(e -> addProduct());

        txtCode.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal && txtCode.getText() != null && !txtCode.getText().trim().isEmpty()) {
                fillFieldsByCode();
            }
        });
        //Filter table
        txtSearch.textProperty().addListener((obs, old, newValue) -> {
            pagination.setCurrentPageIndex(0);
            updatePagination();
        });

        //table Loaded
        updatePagination();

        //Initial Focus
        Platform.runLater(() -> {
            txtBarCode.requestFocus();
            txtBarCode.selectAll();
        });

        //ClearSearch

        btnResetSearch.setCursor(Cursor.HAND);

        btnResetSearch.setOnMouseEntered(e -> {
            btnResetSearch.setOpacity(1.0);
        });

        btnResetSearch.setOnMouseExited(e -> {
            btnResetSearch.setOpacity(0.5);
        });

        // Click
        btnResetSearch.setOnMousePressed(e -> {
            btnResetSearch.setScaleX(0.85);
            btnResetSearch.setScaleY(0.85);
        });

        btnResetSearch.setOnMouseReleased(e -> {
            btnResetSearch.setScaleX(1.0);  // change size of the image
            btnResetSearch.setScaleY(1.0);
        });
    }

    // this method  is called when you change the number of page
    private Node createPage(int pageIndex) {
        String filter = txtSearch.getText();
        int ROWS_PER_PAGE = 15;
        Page<ProductDto> productPage = productService.findPaginated(filter, pageIndex, ROWS_PER_PAGE);

        // Update totalPages dynamic
        pagination.setPageCount(productPage.getTotalPages() <= 0 ? 1 : productPage.getTotalPages());

        // load table
        products.setAll(productPage.getContent());
        tableProducts.setItems(products);

        return new VBox(); // return empty node to refresh view
    }

    private void updatePagination() {
        pagination.setPageFactory(this::createPage);
    }

    private void onBarCodeScanned() {

        handleSearchProductResult(productService.findByBarCode(txtBarCode.getText()),txtBarCode.getText());

    }

    private void fillFieldsByCode() {

        handleSearchProductResult(productService.findByCode(txtCode.getText()),txtCode.getText());
    }

    private void handleSearchProductResult(Optional<ProductDto> result, String code){
        if (code == null || code.isBlank()) {
            return;
        }

        result.ifPresent(this::fillProductFields);
    }

    private void addProduct() {

        if (!isFormValid()) {
            return;
        }

        ProductDto product = new ProductDto();
        product.setCode(txtCode.getText());
        product.setBarCode(txtBarCode.getText());
        product.setName(txtName.getText());
        product.setDescription(txtDescription.getText());
        product.setPurchasePrice(Double.valueOf(txtPurchasePrice.getText()));
        product.setProfit(Double.valueOf(txtProfit.getText()));
        product.setTax(TAX);
        product.setQuantity(Integer.parseInt(txtQuantity.getText()));

        productService.addProduct(product);

        clearForm();
        updatePagination();

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
        txtQuantity.clear();
    }

    private boolean isFormValid() {

        if (ValidateFields.isEmpty(txtCode)) return ValidateFields.showError("El código es obligatorio");
        if (ValidateFields.isEmpty(txtName)) return ValidateFields.showError("El nombre es obligatorio");
        if (ValidateFields.isEmpty(txtDescription)) return ValidateFields.showError("La descripción es obligatoria");
        if (ValidateFields.isEmpty(txtPurchasePrice)) return ValidateFields.showError("El precio es obligatorio");
        if (ValidateFields.isEmpty(txtProfit)) return ValidateFields.showError("La ganancia es obligatoria");
        if (ValidateFields.isEmpty(txtQuantity)) return ValidateFields.showError("La cantidad es obligatoria");
        if (!ValidateFields.isNumber(txtPurchasePrice)) return ValidateFields.showError("Precio inválido");
        if (!ValidateFields.isNumber(txtProfit)) return ValidateFields.showError("Ganancia inválida");
        if (!ValidateFields.isInteger(txtQuantity)) return ValidateFields.showError("Cantidad inválida");

        return true;
    }

    @FXML
    private void clearSearch() {
        txtSearch.clear();
    }


    @FXML
    private void deleteSelectedProduct() {

        ProductDto selectedProduct = tableProducts.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            ValidateFields.showError("Por favor, selecciona un producto de la tabla");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Eliminación");
        alert.setHeaderText("¿Estás seguro de eliminar este producto?");
        alert.setContentText(selectedProduct.getName());

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                productService.deleteById(selectedProduct.getId());

                updatePagination();
                ValidateFields.showError("Producto eliminado correctamente");
            }
        });
    }
}
