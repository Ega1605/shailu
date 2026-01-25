package com.shailu.deposito_dental_pos.controller;

import com.shailu.deposito_dental_pos.model.dto.ProductDto;
import com.shailu.deposito_dental_pos.model.dto.SaleDetailsDto;
import com.shailu.deposito_dental_pos.model.dto.SalesDto;
import com.shailu.deposito_dental_pos.model.entity.AccountReceivable;
import com.shailu.deposito_dental_pos.model.entity.SaleDetail;
import com.shailu.deposito_dental_pos.model.enums.PaymentType;
import com.shailu.deposito_dental_pos.service.AccountReceivableService;
import com.shailu.deposito_dental_pos.service.SaleDetailsService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

    @FXML private ListView<String> lvProducts;

    private final ObservableList<SaleDetailsDto> sales =
            FXCollections.observableArrayList();

    private Long currentFilter;


    @FXML
    public void initialize() {

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


    }

    private void onSaleDoubleClick(SaleDetailsDto sale) {
        // Datos comunes
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

        // Validar tipo de pago
        if (sale.getPaymentType().equalsIgnoreCase(PaymentType.CREDIT.getPaymentType())) {
            loadCreditInfo(sale.getFolio());
            showCreditFields(true);
        } else {
            clearCreditFields();
            showCreditFields(false);
        }

    }

    private void loadProducts(Long saleId) {

        lvProducts.getItems().clear();

        List<SaleDetail> details =
                saleDetailService.findItemsBySaleId(saleId);

        for (SaleDetail detail : details) {

            String line = String.format(
                    "%s  x%d  $%.2f",
                    detail.getProduct().getName(),
                    detail.getQuantity(),
                    detail.getItemSubtotal()
            );

            lvProducts.getItems().add(line);
        }
    }


    private void loadCreditInfo(Long saleId) {

        AccountReceivable ar =
                accountReceivableService.findBySaleId(saleId);

        txtPaidDate.setText(
                ar.getPaidAt() != null
                        ? ar.getPaidAt()
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
                        : "—"
        );

        txtPaidAmount.setText(
                String.format("$ %.2f", ar.getPaidAmount())
        );

        txtRemainingBalance.setText(
                String.format("$ %.2f", ar.getRemainingBalance())
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
