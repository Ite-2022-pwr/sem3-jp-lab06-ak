package ite.jp.ak.lab06.seller;

import ite.jp.ak.lab06.seller.shared.Seller;
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

public class SellerApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SellerApplication.class.getResource("seller-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 400);
        stage.setTitle("Sprzedawca");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        var seller = Seller.getInstance();

        if (seller.getSeller().getId() == 0 || seller.getKeeper().getHost() == null || seller.getKeeper().getPort() == null) {
            super.stop();
            System.exit(0);
        }
        var packet = new Packet() {{
            setType(PacketType.UnregisterRequest);
            setSender(seller.getSeller());
            setPayload(Payload.fromObject(seller.getSeller(), User.class));
        }};
        var sender = new Sender();
        sender.sendTo(seller.getKeeper(), packet);
        super.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}