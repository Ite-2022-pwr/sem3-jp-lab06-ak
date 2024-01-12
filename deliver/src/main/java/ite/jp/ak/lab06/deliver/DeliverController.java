package ite.jp.ak.lab06.deliver;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DeliverController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}