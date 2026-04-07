package com.shailu.deposito_dental_pos.controller;

import com.shailu.deposito_dental_pos.config.ScreenManager;
import com.shailu.deposito_dental_pos.model.dto.SaleDetailsDto;
import com.shailu.deposito_dental_pos.model.dto.SalesDto;
import com.shailu.deposito_dental_pos.model.entity.AccountReceivable;
import com.shailu.deposito_dental_pos.model.entity.SaleDetail;
import com.shailu.deposito_dental_pos.model.entity.Sales;
import com.shailu.deposito_dental_pos.model.enums.PaymentType;
import com.shailu.deposito_dental_pos.model.enums.SaleStatus;
import com.shailu.deposito_dental_pos.service.AccountReceivablePaymentService;
import com.shailu.deposito_dental_pos.service.AccountReceivableService;
import com.shailu.deposito_dental_pos.service.SaleDetailsService;
import com.shailu.deposito_dental_pos.service.SalesService;
import com.shailu.deposito_dental_pos.utils.UIUtils;
import com.shailu.deposito_dental_pos.utils.ValidateFields;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class SaleDetailsController {



    @Autowired
    private SaleDetailsService saleDetailService;

    @Autowired
    private SalesController salesController;

    @Autowired
    private ScreenManager screenManager;

    @Autowired
    private AccountReceivablePaymentService accountReceivablePaymentService;



    @Autowired
    private SalesService salesService;

    @Autowired
    private AccountReceivableService accountReceivableService;

    @FXML private TableColumn<SaleDetailsDto, Long> colFolio;

    @FXML private TableColumn<SaleDetailsDto, String> colName;

    @FXML private TableColumn<SaleDetailsDto, Double> colTotal;

    @FXML private TableColumn<SaleDetailsDto, Timestamp> colDate;

    @FXML private TableColumn<SaleDetailsDto, PaymentType> colType;

    @FXML private  TableColumn<SaleDetailsDto, SaleStatus> colStatus;

    public TableColumn colUpdate;


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

    @FXML private ImageView btnResetDates;


    @FXML
    private TableView<SaleDetail> lvProducts;

    @FXML
    private TableColumn<SaleDetail, String> colNameDetail;

    @FXML
    private TableColumn<SaleDetail, Integer> colQuantityDetail;

    @FXML
    private TableColumn<SaleDetail, Double> colTotalDetail;

    @FXML
    public DatePicker dpFilterDate;


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

        colStatus.setCellValueFactory(
                new PropertyValueFactory<>("status")
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


        //update button
        Callback<TableColumn<SaleDetailsDto, Void>, TableCell<SaleDetailsDto, Void>> cellFactory = param -> new TableCell<>() {
            private final Button btnEdit = new Button();

            {
                // Estilo rápido para que se vea circular y azul
                btnEdit.setText("Editar ✎");
                btnEdit.setStyle(
                        "-fx-background-color: #f0f0f0; " +
                                "-fx-text-fill: #333333; " +
                                "-fx-border-color: #cccccc; " +
                                "-fx-border-radius: 3; " +
                                "-fx-background-radius: 3; " +
                                "-fx-font-size: 10px; " +
                                "-fx-cursor: hand;"
                );

                // Efecto visual cuando pasas el mouse (opcional)
                btnEdit.setOnMouseEntered(e -> btnEdit.setStyle(btnEdit.getStyle() + "-fx-background-color: #e0e0e0;"));
                btnEdit.setOnMouseExited(e -> btnEdit.setStyle(btnEdit.getStyle() + "-fx-background-color: #f0f0f0;"));

                btnEdit.setOnAction(event -> {
                    // Obtenemos el objeto de la fila actual
                    SaleDetailsDto sale = getTableView().getItems().get(getIndex());
                    System.out.println("updating sale: " + sale.getFolio());
                    // Aquí llamas a tu lógica de edición
                    if (ValidateFields.showConfirm("¿Desea editar la venta #" + sale.getFolio() +"?")) {
                        try {
                            saleDetailService.cancelSale(sale.getFolio());

                            Sales saleEntity = salesService.findSale(sale.getFolio());

                            salesController.loadSaleFromSaleDetails(saleEntity);

                            screenManager.show("sales.fxml", "Ventas", true);

                        } catch(Exception e){
                            ValidateFields.showError("Error: " + e.getMessage());
                        }

                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEdit);
                    setAlignment(Pos.CENTER);
                    setGraphic(btnEdit);
                }
            }
        };

        colUpdate.setCellFactory(cellFactory);


    }

    @FXML
    private void clearSearch() {
        txtSearch.clear();
    }

    @FXML
    private void clearDate() {
        dpFilterDate.setValue(null);
        pagination.setCurrentPageIndex(0);
        createPage(0);
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
                        result.getAmount(),
                        result.getPaymentType()
                );

                loadCreditInfo(currentAccountReceivable.getSales().getId());
        });

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

        btnRegisterPayment.setDisable(currentAccountReceivable.getRemainingBalance() <= 0);

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
        LocalDate dateFilter = dpFilterDate.getValue();
        if (text != null && !text.isBlank()) {
            try {
                filter = Long.valueOf(text.trim());
            } catch (NumberFormatException e) {
                filter = null;
            }
        }

        int ROWS_PER_PAGE = 15;

        Page<SaleDetailsDto> productPage =
                saleDetailService.findPaginated(currentFilter,dateFilter, pageIndex, ROWS_PER_PAGE);


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

    @FXML
    public void cancelSale() {

        SaleDetailsDto selectedItem = salesTable.getSelectionModel().getSelectedItem();


        if (ValidateFields.showConfirm("¿Desea cancelar la venta #" + selectedItem.getFolio() +"?")) {

            saleDetailService.cancelSale(selectedItem.getFolio());
            updatePagination();
        }

    }

    public void searchByDate(ActionEvent actionEvent) {

        LocalDate selectedDate = dpFilterDate.getValue();

        if (selectedDate == null) {
            ValidateFields.showError("Por favor, selecciona una fecha para buscar.");
            return;
        }

        txtSearch.clear();

        pagination.setCurrentPageIndex(0);
        createPage(0);
    }
}
