package com.shailu.deposito_dental_pos.controller;

import com.shailu.deposito_dental_pos.config.ScreenManager;
import com.shailu.deposito_dental_pos.model.dto.ProductDto;
import com.shailu.deposito_dental_pos.model.dto.SaleDetailsDto;
import com.shailu.deposito_dental_pos.model.dto.SalesDto;
import com.shailu.deposito_dental_pos.model.entity.AccountReceivable;
import com.shailu.deposito_dental_pos.model.entity.SaleDetail;
import com.shailu.deposito_dental_pos.model.enums.PaymentType;
import com.shailu.deposito_dental_pos.service.AccountReceivablePaymentService;
import com.shailu.deposito_dental_pos.service.AccountReceivableService;
import com.shailu.deposito_dental_pos.service.SaleDetailsService;
import com.shailu.deposito_dental_pos.utils.ValidateFields;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class SaleDetailsController {



    @Autowired
    private SaleDetailsService saleDetailService;

    @Autowired
    private ScreenManager screenManager;

    @Autowired
    private AccountReceivablePaymentService accountReceivablePaymentService;

    @Autowired
    private AccountReceivableService accountReceivableService;

    @FXML private TableColumn<SaleDetailsDto, Long> colFolio;

    @FXML private TableColumn<SaleDetailsDto, String> colName;

    @FXML private TableColumn<SaleDetailsDto, Double> colTotal;

    @FXML private TableColumn<SaleDetailsDto, Timestamp> colDate;

    @FXML private TableColumn<SaleDetailsDto, PaymentType> colType;

    @FXML
    private TableView<SaleDetailsDto> salesTable;

    @FXML private TextField txtSearch;

    @FXML private Pagination pagination;

    @FXML private Text txtSaleId;

    @FXML private Text txtCustomer;

    @FXML private Text txtTotal;

    @FXML private Text txtDate;

    @FXML private Text txtPaymentType;

    @FXML private Text txtPaidDate;

    @FXML private Text txtPaidAmount;

    @FXML private Text txtRemainingBalance;

    @FXML private Button btnRegisterPayment;

    @FXML private ImageView btnResetSearch;


    @FXML
    private TableView<SaleDetail> lvProducts;

    @FXML
    private TableColumn<SaleDetail, String> colNameDetail;

    @FXML
    private TableColumn<SaleDetail, Integer> colQuantityDetail;

    @FXML
    private TableColumn<SaleDetail, Double> colTotalDetail;


    private final ObservableList<SaleDetailsDto> sales =
            FXCollections.observableArrayList();

    private Long currentFilter;

    private SaleDetailsDto saleSelected;
    private AccountReceivable currentAccountReceivable;



    @FXML
    public void initialize() {

        btnRegisterPayment.setDisable(true);

        colNameDetail.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        cell.getValue().getProduct().getName()
                )
        );

        colQuantityDetail.setCellValueFactory(
                new PropertyValueFactory<>("quantity")
        );

        colTotalDetail.setCellValueFactory(
                new PropertyValueFactory<>("itemSubtotal")
        );

        colTotalDetail.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : String.format("$ %.2f", value));
            }
        });

        colFolio.setCellValueFactory(new PropertyValueFactory<>("folio"));
        colName.setCellValueFactory(
                new PropertyValueFactory<>("customerName")
        );
        //where get the value
        colTotal.setCellValueFactory(
                new PropertyValueFactory<>("total")
        );
        //how to show the value
        colTotal.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$ %.2f", item));
                }
            }
        });

        colDate.setCellValueFactory(
                new PropertyValueFactory<>("createdDate")
        );

        colDate.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toLocalDateTime()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                }
            }
        });

        colType.setCellValueFactory(
                new PropertyValueFactory<>("paymentType")
        );

        txtSearch.textProperty().addListener((obs, old, newValue) -> {
            if (newValue == null || newValue.isBlank()) {
                currentFilter = null;
            } else {
                try {
                    currentFilter = Long.valueOf(newValue.trim());
                } catch (NumberFormatException e) {
                    currentFilter = null;
                }
            }

            pagination.setCurrentPageIndex(0);
            updatePagination();
        });

        //table Loaded
        Platform.runLater(this::updatePagination);

        //Search Intro
        txtSearch.setOnAction(e -> {
            pagination.setCurrentPageIndex(0);
            updatePagination();
        });

        //when user clicked twice on a row
        salesTable.setRowFactory(tv -> {
            TableRow<SaleDetailsDto> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    SaleDetailsDto selectedSale = row.getItem();
                    onSaleDoubleClick(selectedSale);
                }
            });

            return row;
        });

        //ClearSearch

        btnResetSearch.setCursor(Cursor.HAND);

        btnResetSearch.setOnMouseEntered(e -> {
            btnResetSearch.setOpacity(1.0);
        });

        btnResetSearch.setOnMouseExited(e -> {
            btnResetSearch.setOpacity(0.5);
        });

        // Click Reset Search
        btnResetSearch.setOnMousePressed(e -> {
            btnResetSearch.setScaleX(0.85);
            btnResetSearch.setScaleY(0.85);
        });

        btnResetSearch.setOnMouseReleased(e -> {
            btnResetSearch.setScaleX(1.0);  // change size of the image
            btnResetSearch.setScaleY(1.0);
        });


    }

    @FXML
    private void clearSearch() {
        txtSearch.clear();
    }

    @FXML
    private void onRegisterPayment() {

        screenManager.showDialog(
                "registerPaymentDialog.fxml",
                "Registrar pago",
                controller -> {//executes just after load fxml but before the user can see the window
                    RegisterPaymentDialogController c = (RegisterPaymentDialogController) controller;
                    c.init(currentAccountReceivable.getRemainingBalance());
                },
                controller -> {
                    RegisterPaymentDialogController c = (RegisterPaymentDialogController) controller;
                    return c.getResult();
                }
        ).ifPresent(
            result -> {

                accountReceivablePaymentService.savePayment(
                        currentAccountReceivable,
                        result.get().getAmount(),
                        result.get().getPaymentType()
                );

                loadCreditInfo(currentAccountReceivable.getSales().getId());
        });


        /*if (saleSelected == null) return;

        double amount;

        try {
            amount = Double.parseDouble(txtPaidAmount.getText());
        } catch (NumberFormatException e) {
            ValidateFields.showError("Cantidad inválida");
            return;
        }

        AccountReceivable updated =
                accountReceivableService.registerPayment(
                        saleSelected.getFolio(), amount
                );

        refreshCreditInfo(updated);*/
    }

    private void refreshCreditInfo(AccountReceivable ar) {

        txtPaidAmount.setText(String.format("$ %.2f", ar.getPaidAmount()));
        txtRemainingBalance.setText(
                String.format("$ %.2f", ar.getRemainingBalance())
        );

        if (ar.getPaidAt() != null) {
            txtPaidDate.setText(
                    ar.getPaidAt()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );
        }
    }



    private void onSaleDoubleClick(SaleDetailsDto sale) {

        this.saleSelected = sale;
        txtSaleId.setText(String.valueOf(sale.getFolio()));
        txtCustomer.setText(sale.getCustomerName());
        txtTotal.setText(String.format("$ %.2f", sale.getTotal()));
        txtDate.setText(
                sale.getCreatedDate()
                        .toLocalDateTime()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        txtPaymentType.setText(sale.getPaymentType());

        // load Product's list
        loadProducts(sale.getFolio());

        // validate Payment type
        if (sale.getPaymentType().equalsIgnoreCase(PaymentType.CREDIT.getPaymentType())) {
            loadCreditInfo(sale.getFolio());
            showCreditFields(true);
            btnRegisterPayment.setDisable(false);
        } else {
            clearCreditFields();
            showCreditFields(false);
            btnRegisterPayment.setDisable(true);
        }

    }

    private void loadProducts(Long saleId) {

        List<SaleDetail> details =
                saleDetailService.findItemsBySaleId(saleId);

        lvProducts.getItems().setAll(details);
    }


    private void loadCreditInfo(Long saleId) {

        this.currentAccountReceivable =
                accountReceivableService.findBySaleId(saleId);

        txtPaidDate.setText(
                currentAccountReceivable.getPaidAt() != null
                        ? currentAccountReceivable.getPaidAt()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : "—"
        );

        txtPaidAmount.setText(
                String.format("$ %.2f", currentAccountReceivable.getPaidAmount())
        );

        txtRemainingBalance.setText(
                String.format("$ %.2f", currentAccountReceivable.getRemainingBalance())
        );
    }

    private void showCreditFields(boolean show) {
        txtPaidDate.setVisible(show);
        txtPaidAmount.setVisible(show);
        txtRemainingBalance.setVisible(show);

        txtPaidDate.setManaged(show);
        txtPaidAmount.setManaged(show);
        txtRemainingBalance.setManaged(show);
    }

    private void clearCreditFields() {
        txtPaidDate.setText("");
        txtPaidAmount.setText("");
        txtRemainingBalance.setText("");
    }

    // this method  is called when you change the number of page
    private Node createPage(int pageIndex) {

        Long filter = null;

        String text = txtSearch.getText();
        if (text != null && !text.isBlank()) {
            try {
                filter = Long.valueOf(text.trim());
            } catch (NumberFormatException e) {
                filter = null;
            }
        }

        int ROWS_PER_PAGE = 15;

        Page<SaleDetailsDto> productPage =
                saleDetailService.findPaginated(currentFilter, pageIndex, ROWS_PER_PAGE);


        // Update totalPages dynamic
        pagination.setPageCount(productPage.getTotalPages() <= 0 ? 1 : productPage.getTotalPages());

        // load table
        sales.setAll(productPage.getContent());
        salesTable.setItems(sales);

        return new VBox(); // return empty node to refresh view
    }

    private void updatePagination() {
        pagination.setPageFactory(this::createPage);
    }
}
