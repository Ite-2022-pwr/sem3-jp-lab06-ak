package ite.jp.ak.lab06.customer;

import ite.jp.ak.lab06.customer.service.CustomerServer;
import ite.jp.ak.lab06.customer.shared.Customer;
import ite.jp.ak.lab06.utils.dao.ITriggerable;
import ite.jp.ak.lab06.utils.enums.OrderStatus;
import ite.jp.ak.lab06.utils.model.Order;
import ite.jp.ak.lab06.utils.model.Receipt;
import ite.jp.ak.lab06.utils.model.Packet;
import ite.jp.ak.lab06.utils.model.Product;
import ite.jp.ak.lab06.utils.model.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

public class CustomerController implements ITriggerable {
    // Server info
    @FXML private TextField hostTextField;
    @FXML private TextField portTextField;
    @FXML private Button startServerButton;
    @FXML private Label listeningLabel;

    // Keeper info
    @FXML private TextField keeperHostTextField;
    @FXML private TextField keeperPortTextField;

    // TabPane
    @FXML private TabPane tabPane;

    // Products tab
    @FXML private Tab productsTab;

    // Products table
    @FXML private TableView<Product> productsTableView;
    @FXML private TableColumn<Product, String> productsIdTableColumn;
    @FXML private TableColumn<Product, String> productsNameTableColumn;

    // Product info
    @FXML private Label productNameLabel;

    // Cart
    @FXML private TableView<Product> cartTableView;
    @FXML private TableColumn<Product, String> cartIdTableColumn;
    @FXML private TableColumn<Product, String> cartNameTableColumn;

    // Orders Tab
    @FXML private Tab ordersTab;

    // Orders table
    @FXML private TableView<Order> ordersTableView;
    @FXML private TableColumn<Order, String> ordersIdTableColumn;
    @FXML private TableColumn<Order, String> ordersStatusTableColumn;

    // To buy table
    @FXML private TableView<Product> toBuyTableView;
    @FXML private TableColumn<Product, String> toBuyIdTableColumn;
    @FXML private TableColumn<Product, String> toBuyNameTableColumn;

    // To return table
    @FXML private TableView<Product> toReturnTableView;
    @FXML private TableColumn<Product, String> toReturnIdTableColumn;
    @FXML private TableColumn<Product, String> toReturnNameTableColumn;

    // Receipts Tab
    @FXML private Tab receiptsTab;

    // Receipts table
    @FXML private TableView<Order> receiptsOrderTableView;
    @FXML private TableColumn<Order, String> receiptsOrderIdTableColumn;
    @FXML private TableColumn<Order, String> receiptsOrderStatusTableColumn;

    @FXML private Label sellerIdLabel;

    // Bought products table
    @FXML private TableView<Product> boughtProductsTableView;
    @FXML private TableColumn<Product, String> boughtProductsNameTableColumn;


    private final Customer customer = Customer.getInstance();
    private final List<Product> newOrderProductList = new ArrayList<>();
    private Product selectedProduct;

    private final List<Product> toBuyProductList = new ArrayList<>();
    private final List<Product> toReturnProductList = new ArrayList<>();

    public void trigger() {
        Platform.runLater(this::refreshView);
    }

    public void initialize() {
        productsIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productsNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        cartIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        cartNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        ordersIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        ordersStatusTableColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        toBuyIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        toBuyNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        toReturnIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        toReturnNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        receiptsOrderIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        receiptsOrderStatusTableColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        boughtProductsNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        hostTextField.setText("localhost");
        portTextField.setText("4444");

        keeperHostTextField.setText("localhost");
        keeperPortTextField.setText("1337");
    }

    public void fillProductsTableView() {
        productsTableView.getItems().clear();
        ObservableList<Product> products = FXCollections.observableArrayList(customer.getStoreOffer());
        productsTableView.setItems(products);
    }

    public void fillCartTableView() {
        cartTableView.getItems().clear();
        ObservableList<Product> products = FXCollections.observableArrayList(newOrderProductList);
        cartTableView.setItems(products);
    }

    public void fillOrdersTableView() {
        ordersTableView.getItems().clear();
        ObservableList<Order> orders = FXCollections.observableArrayList(customer.getOrders());
        ordersTableView.setItems(orders);
    }

    public void fillToBuyTableView() {
        toBuyTableView.getItems().clear();
        ObservableList<Product> products = FXCollections.observableArrayList(toBuyProductList);
        toBuyTableView.setItems(products);
    }

    public void fillToReturnTableView() {
        toReturnTableView.getItems().clear();
        ObservableList<Product> products = FXCollections.observableArrayList(toReturnProductList);
        toReturnTableView.setItems(products);
    }

    public void fillReceiptsTableView() {
        receiptsOrderTableView.getItems().clear();
        List<Order> receiptOrdersInfo = customer.getReceipts().stream().map(Receipt::getOrder).toList();
        ObservableList<Order> receiptOrders = FXCollections.observableArrayList(receiptOrdersInfo);
        receiptsOrderTableView.setItems(receiptOrders);
    }

    public void fillBoughtProductsTableView() {
        boughtProductsTableView.getItems().clear();
        Order selectedReceiptOrder = receiptsOrderTableView.getSelectionModel().getSelectedItem();
        if (selectedReceiptOrder == null || selectedReceiptOrder.getStatus() == OrderStatus.Cancelled) {
            return;
        }

        var receipt = customer.getReceipts().stream().filter(r -> r.getOrder().getId().equals(selectedReceiptOrder.getId())).findFirst().orElse(null);
        if (receipt != null) {
            sellerIdLabel.setText("ID sprzedawcy: " + receipt.getSeller().getId());
        }

        var orderInfo = List.of(selectedReceiptOrder.getProducts());
        ObservableList<Product> products = FXCollections.observableArrayList(orderInfo);
        boughtProductsTableView.setItems(products);
    }

    public void refreshView() {
        fillProductsTableView();
        fillOrdersTableView();
        fillReceiptsTableView();
        productNameLabel.setText("Nazwa: ");
    }

    public void startListening(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Błąd uruchamiania serwera");
        try {
            var host = hostTextField.getText();
            if (host.isBlank()) {
                throw new Exception("Host nie może być pusty");
            }
            var port = Integer.parseInt(portTextField.getText());

            var serverThread = new Thread(new CustomerServer(host, port, this));
            serverThread.setDaemon(true);
            serverThread.start();
//            testConnection(host, port);
//            startServerButton.setDisable(true);
//            startServerButton.setVisible(false);
            listeningLabel.setText("Nasłuchiwanie na " + host + ":" + port + " [Aktywne]");
        } catch (Exception e) {
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void testConnection(String host, Integer port) throws IOException {
        var socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
        socketChannel.close();
    }

    private boolean isServerActive() {
        return listeningLabel.getText().contains("Aktywne");
    }

    public void registerToKeeper(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Błąd rejestracji");
        try {
            if (!isServerActive()) {
                throw new Exception("Serwer nie jest aktywny - najpierw zacznij nasłuchiwać");
            }

            var host = keeperHostTextField.getText();
            if (host.isBlank()) {
                throw new Exception("Host nie może być pusty");
            }
            var port = Integer.parseInt(keeperPortTextField.getText());

            customer.getKeeper().setHost(host);
            customer.getKeeper().setPort(port);

            customer.register(customer.getCustomer());

        } catch (Exception e) {
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    public void refreshOffer(ActionEvent event) {
        customer.getOffer(customer.getCustomer());
    }

    public void addToCart(ActionEvent event) {
        var product = productsTableView.getSelectionModel().getSelectedItem();
        if (product == null) {
            return;
        }
        if (newOrderProductList.contains(product)) {
            return;
        }
        newOrderProductList.add(product);
        fillCartTableView();
    }

    public void onSelectedProductFromOffer(MouseEvent event) {
        var product = productsTableView.getSelectionModel().getSelectedItem();
        if (product == null) {
            return;
        }
        productNameLabel.setText("Nazwa: " + product.getName());
        this.selectedProduct = product;
    }

    public void putOrder(ActionEvent event) {
        var order = new Order();
        order.setCustomer(customer.getCustomer());
        order.setProducts(newOrderProductList.toArray(new Product[0]));
        customer.putOrder(order);
        newOrderProductList.clear();
        fillCartTableView();
    }

    public void onSelectedOrder(MouseEvent event) {
        var order = ordersTableView.getSelectionModel().getSelectedItem();
        if (order == null) {
            return;
        }
        toBuyProductList.clear();
        toReturnProductList.clear();

        for (var product : order.getProducts()) {
            if (!product.isToReturn()) {
                toBuyProductList.add(product);
            } else  {
                toReturnProductList.add(product);
            }
        }

        fillToBuyTableView();
        fillToReturnTableView();
    }

    public void onSelectedProductFromToBuy(MouseEvent event) {
        var product = toBuyTableView.getSelectionModel().getSelectedItem();
        if (product == null) {
            return;
        }
        product.setToReturn(true);
        onSelectedOrder(event);
    }

    public void onSelectedProductFromToReturn(MouseEvent event) {
        var product = toReturnTableView.getSelectionModel().getSelectedItem();
        if (product == null) {
            return;
        }
        product.setToReturn(false);
        onSelectedOrder(event);
    }

    public void returnOrder(ActionEvent event) {
        var order = ordersTableView.getSelectionModel().getSelectedItem();
        if (order == null || toReturnProductList.isEmpty()) {
            return;
        }

        var returnProductsOrder = new Order() {{
            setId(order.getId());
            setStatus(OrderStatus.Returned);
            setCustomer(order.getCustomer());
            setDeliver(order.getDeliver());
            setProducts(toReturnProductList.toArray(new Product[0]));
        }};
        customer.returnOrder(returnProductsOrder);
        var productList = new ArrayList<>(List.of(order.getProducts()));
        productList.removeAll(toReturnProductList);
        order.setProducts(productList.toArray(new Product[0]));
        toReturnProductList.clear();
        fillToReturnTableView();
        fillToBuyTableView();
    }

    public void acceptOrder(ActionEvent event) {
        var order = ordersTableView.getSelectionModel().getSelectedItem();
        if (order == null) {
            return;
        }

        customer.acceptOrder(order);
        toBuyProductList.clear();
        toReturnProductList.clear();
        refreshView();
        fillToBuyTableView();
        fillToReturnTableView();
        sellerIdLabel.setText("ID sprzedawcy: ");
    }

    public void onSelectedReceiptOrder(MouseEvent event) {
        fillBoughtProductsTableView();
    }
}