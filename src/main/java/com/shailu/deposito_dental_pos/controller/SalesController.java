package com.shailu.deposito_dental_pos.controller;

import com.shailu.deposito_dental_pos.config.UserSession;
import com.shailu.deposito_dental_pos.model.dto.*;
import com.shailu.deposito_dental_pos.model.entity.Sales;
import com.shailu.deposito_dental_pos.model.enums.PaymentType;
import com.shailu.deposito_dental_pos.model.enums.SaleStatus;
import com.shailu.deposito_dental_pos.service.CustomerService;
import com.shailu.deposito_dental_pos.service.FXMLPrintService;
import com.shailu.deposito_dental_pos.service.ProductService;
import com.shailu.deposito_dental_pos.service.SalesService;
import com.shailu.deposito_dental_pos.utils.ValidateFields;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.TextFields;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @FXML private TextField txtCustomerSearch;
    @FXML private Label lblSelectedCustomer;
    @FXML private TextArea txtNotes;

    @FXML private VBox cashContainer;
    @FXML private TextField txtCashReceived;
    @FXML private TextField txtChange;


    @FXML private Button btnAddProduct;

    @Autowired
    private ProductService productService;

    @Autowired
    private SalesService salesService;

    @Autowired
    private UserSession userSession;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private FXMLPrintService fxmlPrintService;

    @Autowired
    private ApplicationContext springContext;

    private final ObservableList<SalesDto> saleItems =
            FXCollections.observableArrayList();

    private Long selectedCustomerIdSale = 1L;

    @FXML private ComboBox<PaymentType> cbPaymentType;
    @FXML private ComboBox<SaleStatus> cbStatus;

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



        //Customers

        lblSelectedCustomer.setText("Público General");
        TextFields.bindAutoCompletion(txtCustomerSearch, suggestionRequest -> {
            String text = suggestionRequest.getUserText();
            if (text == null || text.trim().length() < 3) return Collections.emptyList();
            return customerService.findByName(suggestionRequest.getUserText());
        }, new StringConverter<CustomerDto>() {
            @Override
            public String toString(CustomerDto customer) {
                return customer == null ? "" : customer.getFirstName() + " " + customer.getLastName();
            }
            @Override
            public CustomerDto fromString(String string) { return null; }
        }).setOnAutoCompleted(event -> {
            CustomerDto customer = event.getCompletion();
            selectedCustomerIdSale = customer.getId();
            lblSelectedCustomer.setText(customer.getFirstName() + " " + customer.getLastName());
            txtCustomerSearch.clear();
        });

        //Fields for paymentType
        cbPaymentType.setItems(FXCollections.observableArrayList(PaymentType.values()));
        cbPaymentType.setValue(PaymentType.CASH);  //Default

        //Fields for paymentType

        cbStatus.setItems(FXCollections.observableArrayList(SaleStatus.values()));
        cbStatus.setValue(SaleStatus.COMPLETED); // Default

        //chagePayment

        cbPaymentType.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isCash = newVal != null && newVal.toString().equalsIgnoreCase(PaymentType.CASH.getPaymentType());

            cashContainer.setVisible(isCash);
            cashContainer.setManaged(isCash);

            if (!isCash) {
                txtCashReceived.clear();
                txtChange.clear();
            }
        });

        txtCashReceived.textProperty().addListener((obs, oldVal, newVal) -> {
            calculateChange();
        });

        Platform.runLater(() -> {
            updateCashVisibility(cbPaymentType.getValue());
        });





    }

    private void calculateChange() {
        try {
            double total = Double.parseDouble(lblTotal.getText());
            double cash = Double.parseDouble(txtCashReceived.getText());

            double change = cash - total;

            if (change >= 0) {
                txtChange.setText(String.format("%.2f", change));
            } else {
                txtChange.setText("0.00");
            }

        } catch (NumberFormatException e) {
            txtChange.setText("0.00");
        }
    }

    private void updateCashVisibility(PaymentType paymentType) {

        boolean isCash = paymentType == PaymentType.CASH;

        cashContainer.setVisible(isCash);
        cashContainer.setManaged(isCash);

        if (!isCash) {
            txtCashReceived.clear();
            txtChange.clear();
        }
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

        if (PaymentType.CASH.getPaymentType().equalsIgnoreCase(cbPaymentType.getValue().toString())) {

            if (txtCashReceived.getText().isBlank()) {
                ValidateFields.showError("Ingresa el efectivo recibido");
                return;
            }

            double cash = Double.parseDouble(txtCashReceived.getText());
            double total = Double.parseDouble(lblTotal.getText());

            if (cash < total) {
                ValidateFields.showError("El efectivo es menor al total");
                return;
            }
        }

        try {

            CurrentSaleDto currentSaleDto = new CurrentSaleDto();

            List<SalesDto> items = tableSales.getItems();

            if (items.isEmpty()) {
                ValidateFields.showError("No hay productos en la venta");
                return;
            }

            currentSaleDto.setItems(items);
            currentSaleDto.setPaymentType(cbPaymentType.getValue());
            currentSaleDto.setStatus(cbStatus.getValue());
            currentSaleDto.setNotes(txtNotes.getText());
            currentSaleDto.setTotal(Double.valueOf(lblTotal.getText()));

            Sales sale = salesService.processSale(currentSaleDto, userSession.getUsername(), selectedCustomerIdSale);
            //print Sale
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ticket_template.fxml"));
                loader.setControllerFactory(springContext::getBean);
                VBox ticketNode = loader.load();

                ((Label) ticketNode.lookup("#lblTotal")).setText("$ " + lblTotal.getText());
                ((Label) ticketNode.lookup("#lblSaleId")).setText(String.format("Venta: #%06d", sale.getId()));
                ((Label) ticketNode.lookup("#lblCustomer"))
                        .setText(sale.getCustomer().getFirstName() +" "+ sale.getCustomer().getLastName());

                DateTimeFormatter formatter =
                        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                ((Label) ticketNode.lookup("#lblDateTime"))
                        .setText("Fecha: " + LocalDateTime.now().format(formatter));


                GridPane gridProducts = (GridPane) ticketNode.lookup("#gridProducts");

                int row = 0;
                for (SalesDto item : items) {

                    Label lblName = new Label(item.getName());
                    lblName.setWrapText(true);
                    lblName.setStyle("-fx-font-size: 8px; -fx-font-family: Monospaced;");

                    Label lblUnit = new Label(String.format("%.2f", item.getPrice()));
                    lblUnit.setStyle("-fx-font-size: 8px; -fx-font-family: Monospaced;");

                    Label lblQty = new Label("x" + item.getQuantity());
                    lblQty.setStyle("-fx-font-size: 8px; -fx-font-family: Monospaced;");

                    double subtotal = item.getPrice() * item.getQuantity();
                    Label lblSub = new Label(String.format("%.2f", subtotal));
                    lblSub.setStyle("-fx-font-size: 8px; -fx-font-family: Monospaced;");

                    gridProducts.add(lblName, 0, row);
                    gridProducts.add(lblUnit, 1, row);
                    gridProducts.add(lblQty, 2, row);
                    gridProducts.add(lblSub, 3, row);

                    row++;
                }

                showInfo("Venta finalizada.\nImprimiendo ticket…");

                fxmlPrintService.printNode(ticketNode, "POS-58");

            } catch (Exception printEx) {
                System.err.println("Error de impresión: " + printEx.getMessage());
            }

            // clean
            cancelSale();
            resetToDefaultCustomer();

        } catch (Exception e) {
            ValidateFields.showError("Error al procesar: " + e.getMessage());
        }
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Venta");
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.initOwner(lblTotal.getScene().getWindow());
        alert.show();
    }

    private void resetToDefaultCustomer() {
        selectedCustomerIdSale = 1L;
        lblSelectedCustomer.setText("Público General");
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
        txtCashReceived.setText("0.00");
        txtChange.setText("0.00");
        resetToDefaultCustomer();
    }

    @FXML
    private void removeSelectedItem() {

        SalesDto selectedItem = tableSales.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {

            saleItems.remove(selectedItem);

            calculateTotals();

        } else {
            ValidateFields.showError("Por favor, selecciona un artículo de la tabla para eliminarlo.");
        }
    }



}
