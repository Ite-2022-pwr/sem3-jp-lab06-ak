package ite.jp.ak.lab06.customer;

import ite.jp.ak.lab06.customer.service.CustomerServer;
import ite.jp.ak.lab06.customer.shared.Customer;
import ite.jp.ak.lab06.utils.dao.ITriggerable;
import ite.jp.ak.lab06.utils.model.Product;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

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

    private final Customer customer = Customer.getInstance();

    public void trigger() {
        Platform.runLater(this::refreshView);
    }

    public void initialize() {
        productsIdTableColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        productsNameTableColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    }

    public void fillProductsTableView() {
        productsTableView.getItems().clear();
        ObservableList<Product> products = FXCollections.observableArrayList(customer.getStoreOffer());
        productsTableView.setItems(products);
    }

    public void refreshView() {
        fillProductsTableView();
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

    public void registerToKeeper(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Błąd rejestracji");
        try {
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
}