package ite.jp.ak.lab06.keeper;

import ite.jp.ak.lab06.keeper.service.KeeperServer;
import ite.jp.ak.lab06.keeper.shared.Keeper;
import ite.jp.ak.lab06.utils.dao.ITriggerable;
import ite.jp.ak.lab06.utils.model.Order;
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

public class KeeperController implements ITriggerable {

    // Server info
    @FXML private TextField hostTextField;
    @FXML private TextField portTextField;
    @FXML private Button startServerButton;
    @FXML private Label listeningLabel;

    // TabPane
    @FXML private TabPane tabPane;

    // Users tab
    @FXML private Tab usersTab;

    @FXML private TableView<User> usersTableView;
    @FXML private TableColumn<User, String> usersIdTableColumn;
    @FXML private TableColumn<User, String> usersRoleTableColumn;
    @FXML private TableColumn<User, String> usersHostTableColumn;
    @FXML private TableColumn<User, String> usersPortTableColumn;

    // Products tab
    @FXML private Tab productsTab;

    // Products table
    @FXML private TableView<Product> productsTableView;
    @FXML private TableColumn<Product, String> productsIdTableColumn;
    @FXML private TableColumn<Product, String> productsNameTableColumn;

    // Product info
    @FXML private Label productIdLabel;
    @FXML private Label productNameLabel;

    // Add new product
    @FXML private TextField newProductNameTextField;

    // Orders tab
    @FXML private Tab ordersTab;

    // Orders table
    @FXML private TableView<Order> ordersTableView;
    @FXML private TableColumn<Order, String> orderIdTableColumn;
    @FXML private TableColumn<Order, String> orderClientTableColumn;
    @FXML private TableColumn<Order, String> orderProductsTableColumn;
    @FXML private TableColumn<Order, String> orderStatusTableColumn;

    private final Keeper keeper = Keeper.getInstance();

    public void trigger() {
        Platform.runLater(this::refreshView);
    }

    public void initialize() {
        usersIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usersRoleTableColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        usersHostTableColumn.setCellValueFactory(new PropertyValueFactory<>("host"));
        usersPortTableColumn.setCellValueFactory(new PropertyValueFactory<>("port"));

        productsIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productsNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        orderIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        orderClientTableColumn.setCellValueFactory(new PropertyValueFactory<>("customer"));
        orderProductsTableColumn.setCellValueFactory(new PropertyValueFactory<>("products"));
        orderStatusTableColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        hostTextField.setText("localhost");
        portTextField.setText("1337");

        keeper.addProduct(new Product() {{
            setName("Kubek");
        }});
        keeper.addProduct(new Product() {{
            setName("Koszulka");
        }});
        keeper.addProduct(new Product() {{
            setName("Bluza");
        }});

        fillProductsTableView();
    }

    public void fillUsersTableView() {
        usersTableView.getItems().clear();
        ObservableList<User> users = FXCollections.observableArrayList(keeper.getRegisteredUsers());
        usersTableView.setItems(users);
    }

    public void fillProductsTableView() {
        productsTableView.getItems().clear();
        ObservableList<Product> products = FXCollections.observableArrayList(keeper.getStoreOffer());
        productsTableView.setItems(products);
    }

    public void fillOrdersTableView() {
        ordersTableView.getItems().clear();
        ObservableList<Order> orders = FXCollections.observableArrayList(keeper.getOrders());
        ordersTableView.setItems(orders);
    }

    public void refreshView() {
        fillUsersTableView();
        fillProductsTableView();
        fillOrdersTableView();
        productIdLabel.setText("ID: ");
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

            var serverThread = new Thread(new KeeperServer(host, port, this));
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

    public void addProduct(ActionEvent event) {
        var name = newProductNameTextField.getText();
        if (name.isBlank()) {
            return;
        }
        var product = new Product();
        product.setName(name);
        keeper.addProduct(product);
        newProductNameTextField.setText("");
        refreshView();
    }

    public void removeProduct(ActionEvent event) {
        var product = productsTableView.getSelectionModel().getSelectedItem();
        if (product == null) {
            return;
        }
        keeper.getStoreOffer().remove(product);
        refreshView();
    }

    public void onSelectedProduct(MouseEvent event) {
        var product = productsTableView.getSelectionModel().getSelectedItem();
        if (product == null) {
            return;
        }
        productIdLabel.setText("ID: " + product.getId());
        productNameLabel.setText("Nazwa: " + product.getName());
    }

}