package ite.jp.ak.lab06.keeper;

import ite.jp.ak.lab06.keeper.service.KeeperServer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class KeeperApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(KeeperApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("Magazynier");
        stage.setScene(scene);
        stage.show();

//        var server = new KeeperServer("localhost", 1337, null);
//        server.run();
    }

    public static void main(String[] args) {
        launch();
    }
}