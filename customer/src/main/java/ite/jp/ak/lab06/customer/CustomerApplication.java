package ite.jp.ak.lab06.customer;

import ite.jp.ak.lab06.customer.shared.Customer;
import ite.jp.ak.lab06.utils.enums.PacketType;
import ite.jp.ak.lab06.utils.model.Packet;
import ite.jp.ak.lab06.utils.model.Payload;
import ite.jp.ak.lab06.utils.model.User;
import ite.jp.ak.lab06.utils.network.Sender;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CustomerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CustomerApplication.class.getResource("customer-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("Klient");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        var customer = Customer.getInstance();

        if (customer.getCustomer().getId() == 0 || customer.getKeeper().getHost() == null || customer.getKeeper().getPort() == null) {
            super.stop();
            System.exit(0);
        }
        var packet = new Packet() {{
            setType(PacketType.UnregisterRequest);
            setSender(customer.getCustomer());
            setPayload(Payload.fromObject(customer.getCustomer(), User.class));
        }};
        var sender = new Sender();
        sender.sendTo(customer.getKeeper(), packet);
        super.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}