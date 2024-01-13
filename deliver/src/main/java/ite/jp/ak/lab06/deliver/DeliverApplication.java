package ite.jp.ak.lab06.deliver;

import ite.jp.ak.lab06.deliver.shared.Deliver;
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

public class DeliverApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(DeliverApplication.class.getResource("deliver-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("Dostawca");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        var deliver = Deliver.getInstance();

        if (deliver.getDeliver().getId() == 0 || deliver.getKeeper().getHost() == null || deliver.getKeeper().getPort() == null) {
            super.stop();
            System.exit(0);
        }
        var packet = new Packet() {{
            setType(PacketType.UnregisterRequest);
            setSender(deliver.getDeliver());
            setPayload(Payload.fromObject(deliver.getDeliver(), User.class));
        }};
        var sender = new Sender();
        sender.sendTo(deliver.getKeeper(), packet);
        super.stop();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch();
    }
}