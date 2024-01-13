package ite.jp.ak.lab06.deliver;

import ite.jp.ak.lab06.deliver.service.DeliverServer;
import ite.jp.ak.lab06.deliver.shared.Deliver;
import ite.jp.ak.lab06.utils.dao.ITriggerable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class DeliverController implements ITriggerable {
    // Server info
    @FXML private TextField hostTextField;
    @FXML private TextField portTextField;
    @FXML private Button startServerButton;
    @FXML private Label listeningLabel;

    // Keeper info
    @FXML private TextField keeperHostTextField;
    @FXML private TextField keeperPortTextField;

    // Order label
    @FXML private Label orderLabel;

    private final Deliver deliver = Deliver.getInstance();

    public void initialize() {
        hostTextField.setText("localhost");
        portTextField.setText("5555");

        keeperHostTextField.setText("localhost");
        keeperPortTextField.setText("1337");
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

            var serverThread = new Thread(new DeliverServer(host, port, this));
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

            deliver.getKeeper().setHost(host);
            deliver.getKeeper().setPort(port);

            deliver.register(deliver.getDeliver());

        } catch (Exception e) {
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @Override
    public void trigger() {
        Platform.runLater(() -> {
            var order = deliver.getLastFetchOrder();
            if (order != null) {
                orderLabel.setText("Przyjęto zamówienie o ID: " + order.getId());
            } else {
                orderLabel.setText("Brak zamówień do przyjęcia");
            }
        });
    }

    public void getOrder(ActionEvent event) {
        deliver.getOrder(deliver.getDeliver());
    }
}