package ite.jp.ak.lab06.seller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SellerController {
    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}