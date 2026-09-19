package com.shailu.deposito_dental_pos.controller;

import com.shailu.deposito_dental_pos.model.dto.CustomerDto;
import com.shailu.deposito_dental_pos.model.dto.ProductDto;
import com.shailu.deposito_dental_pos.model.entity.TaxRegime;
import com.shailu.deposito_dental_pos.service.CustomerService;
import com.shailu.deposito_dental_pos.service.TaxRegimeService;
import com.shailu.deposito_dental_pos.utils.ValidateFields;
import javafx.beans.property.SimpleStringProperty;
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

import static com.shailu.deposito_dental_pos.utils.ValidateFields.showInfo;

@Component
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private TaxRegimeService taxRegimeService;

    @FXML
    private ComboBox<TaxRegime> cmbTaxRegime;

    @FXML private TextField txtName;
    @FXML private TextField txtLastName;
    @FXML private TextField txtReasonSocial;
    @FXML private TextField txtRfc;
    @FXML private TextField txtAddress;
    @FXML private TextField txtZipCode;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtCreditLimit;
    @FXML private TextField txtCreditDays;
    @FXML private TextField txtDiscount;
    @FXML private ImageView btnCleanCustomerInfo;
    @FXML private Button btnAdd;



    @FXML private TextField txtSearch;

    @FXML private TableView<CustomerDto> tableCustomers;

    @FXML private TableColumn<CustomerDto, String> colCode;

    @FXML private TableColumn<CustomerDto, String> colName;

    @FXML private TableColumn<CustomerDto, String> colAddress;

    @FXML private TableColumn<CustomerDto, String> colPhone;

    @FXML private TableColumn<CustomerDto, String> colRfc;

    @FXML private TableColumn<CustomerDto, String> colTaxRegime;

    @FXML private Pagination pagination;

    private final ObservableList<CustomerDto> customers =
            FXCollections.observableArrayList();

    private Long customerId = null;

    @FXML
    public void initialize() {

        txtDiscount.setText("0");
        txtCreditDays.setText("0");
        txtCreditLimit.setText("0");

        // Click Clean Product
        btnCleanCustomerInfo.setCursor(Cursor.HAND);

        btnCleanCustomerInfo.setOnMousePressed(e -> {
            btnCleanCustomerInfo.setScaleX(0.85);
            btnCleanCustomerInfo.setScaleY(0.85);
        });

        btnCleanCustomerInfo.setOnMouseReleased(e -> {
            btnCleanCustomerInfo.setScaleX(1.0);
            btnCleanCustomerInfo.setScaleY(1.0);
        });

        btnCleanCustomerInfo.setOnMouseEntered(e -> {
            btnCleanCustomerInfo.setOpacity(1.0);
        });

        btnCleanCustomerInfo.setOnMouseExited(e -> {
            btnCleanCustomerInfo.setOpacity(0.54);
            btnCleanCustomerInfo.setScaleX(1.0);
            btnCleanCustomerInfo.setScaleY(1.0);
        });

        //load TaxRegime
        cmbTaxRegime.setItems(
                FXCollections.observableArrayList(taxRegimeService.findActive())
        );

        cmbTaxRegime.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(TaxRegime item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null
                        ? null
                        : item.getCode() + " - " + item.getDescription());
            }
        });

        cmbTaxRegime.setButtonCell(cmbTaxRegime.getCellFactory().call(null));



        btnAdd.setOnAction(e -> addCustomer());

        //visualization
        colCode.setCellValueFactory(
                new PropertyValueFactory<>("code")
        );
        colName.setCellValueFactory(
                cellData ->
                        new SimpleStringProperty(
                                cellData.getValue().getFirstName() + " " +
                                        cellData.getValue().getLastName()
                        )
        );
        colAddress.setCellValueFactory(
                new PropertyValueFactory<>("address")
        );
        colPhone.setCellValueFactory(
                new PropertyValueFactory<>("phone")
        );
        colRfc.setCellValueFactory(
                new PropertyValueFactory<>("rfc")
        );
        colTaxRegime.setCellValueFactory(
                new PropertyValueFactory<>("taxRegimeLabel")
        );

        //Filter table
        txtSearch.textProperty().addListener((obs, old, newValue) -> {
            pagination.setCurrentPageIndex(0);
            updatePagination();
        });

        //Pagination
        pagination.setPageFactory(this::createPage);

        //DoubleClick on customer
        tableCustomers.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                CustomerDto selected = tableCustomers
                        .getSelectionModel()
                        .getSelectedItem();

                if (selected != null) {
                    loadCustomer(selected);
                }
            }
        });

    }

    private void loadCustomer(CustomerDto customer) {

        this.customerId = customer.getId();

        txtName.setText(customer.getFirstName());
        txtLastName.setText(customer.getLastName());
        txtReasonSocial.setText(customer.getReasonSocial());
        txtRfc.setText(customer.getRfc());
        txtAddress.setText(customer.getAddress());
        txtPhone.setText(customer.getPhone());
        txtEmail.setText(customer.getEmail());
        txtZipCode.setText(customer.getZipCode());
        txtCreditLimit.setText(String.valueOf(customer.getCreditLimit()));
        txtCreditDays.setText(String.valueOf(customer.getCreditDays()));
        txtDiscount.setText(String.valueOf(customer.getSpecialDiscount()));
        selectTaxRegime(customer.getTaxRegimeId());
    }

    private void selectTaxRegime(Long taxRegimeId) {

        cmbTaxRegime.getItems().stream()
                .filter(r -> r.getId().equals(taxRegimeId))
                .findFirst()
                .ifPresent(cmbTaxRegime::setValue);
    }

    private void addCustomer() {

        if (!isFormValid()) {
            return;
        }

        CustomerDto customer = new CustomerDto();
        customer.setId(customerId);
        //customer.setTaxRegime(txtTaxRegime.getText());
        TaxRegime taxRegimeSelected = cmbTaxRegime.getValue();
        customer.setTaxRegimeId(taxRegimeSelected.getId());
        customer.setFirstName(txtName.getText());
        customer.setLastName(txtLastName.getText());
        customer.setReasonSocial(txtReasonSocial.getText());
        customer.setRfc(txtRfc.getText());
        customer.setAddress(txtAddress.getText());
        customer.setPhone(txtPhone.getText());
        customer.setEmail(txtEmail.getText());
        customer.setZipCode(txtZipCode.getText());
        customer.setCreditLimit(Double.valueOf(txtCreditLimit.getText()));
        customer.setCreditDays(Integer.valueOf(txtCreditDays.getText()));
        customer.setSpecialDiscount(Double.valueOf(txtDiscount.getText()));
        customer.setIsActive(true);

        if (customerId == null) {
            customerService.addCustomer(customer);
        } else {
            customerService.updateCustomer(customer);
        }

        showInfo("¡Cliente agregado correctamente!");

        clearForm();
        updatePagination();

    }

    private boolean isFormValid() {

        if (ValidateFields.isEmpty(txtName)) return ValidateFields.showError("El nombre es obligatorio");
        if (ValidateFields.isEmpty(txtLastName)) return ValidateFields.showError("El apellido es obligatorio");
        if (cmbTaxRegime.getValue() == null) {
            return ValidateFields.showError("El régimen fiscal es obligatorio");
        }
        if (ValidateFields.isEmpty(txtRfc)) return ValidateFields.showError("El RFC es obligatorio");
        if (ValidateFields.isEmpty(txtAddress)) return ValidateFields.showError("La dirección es obligatorio");
        if (ValidateFields.isEmpty(txtZipCode)) return ValidateFields.showError("La Codigo Postal es obligatorio");
        if (ValidateFields.isEmpty(txtPhone)) return ValidateFields.showError("El telefono es obligatorio");
        if (ValidateFields.isEmpty(txtEmail)) return ValidateFields.showError("El correo es obligatorio");

        if (!ValidateFields.isNumber(txtCreditLimit)) return ValidateFields.showError("Límite de crédito inválido");
        if (!ValidateFields.isNumber(txtCreditDays)) return ValidateFields.showError("Días de crédito inválido");
        if (!ValidateFields.isNumber(txtDiscount)) return ValidateFields.showError("Descuento");

        return true;
    }

    private void updatePagination() {
        pagination.setPageFactory(this::createPage);
    }

    private Node createPage(int pageIndex) {
        String filter = txtSearch.getText();
        int ROWS_PER_PAGE = 15;
        Page<CustomerDto> productPage = customerService.findPaginated(filter, pageIndex, ROWS_PER_PAGE);

        // Update totalPages dynamic
        pagination.setPageCount(productPage.getTotalPages() <= 0 ? 1 : productPage.getTotalPages());

        // load table
        customers.setAll(productPage.getContent());
        tableCustomers.setItems(customers);

        return new VBox(); // return empty node to refresh view
    }

    @FXML
    private void onCleanCustomerInfo() {
        clearForm();
    }

    private void clearForm() {
        txtName.clear();
        txtLastName.clear();
        txtReasonSocial.clear();;
        cmbTaxRegime.getSelectionModel().clearSelection();
        txtRfc.clear();
        txtAddress.clear();
        txtZipCode.clear();
        txtPhone.clear();
        txtEmail.clear();
        txtCreditLimit.clear();
        txtCreditDays.clear();
        txtDiscount.clear();
        customerId = null;
    }
}
