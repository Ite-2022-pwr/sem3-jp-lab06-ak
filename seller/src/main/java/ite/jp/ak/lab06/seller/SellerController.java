package ite.jp.ak.lab06.seller;

import ite.jp.ak.lab06.seller.service.SellerServer;
import ite.jp.ak.lab06.seller.shared.Seller;
import ite.jp.ak.lab06.utils.dao.ITriggerable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

public class SellerController implements ITriggerable {
    // Server info
    @FXML private TextField hostTextField;
    @FXML private TextField portTextField;
    @FXML private Button startServerButton;
    @FXML private Label listeningLabel;

    // Keeper info
    @FXML private TextField keeperHostTextField;
    @FXML private TextField keeperPortTextField;

    private final Seller seller = Seller.getInstance();

    public void initialize() {
        hostTextField.setText("localhost");
        portTextField.setText("6666");

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

            var serverThread = new Thread(new SellerServer(host, port, this));
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

            seller.getKeeper().setHost(host);
            seller.getKeeper().setPort(port);

            seller.register(seller.getSeller());

        } catch (Exception e) {
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @Override
    public void trigger() {

    }
}