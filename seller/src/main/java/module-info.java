module ite.jp.ak.lab06.seller {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;

    requires ite.jp.ak.lab06.utils;
    requires com.fasterxml.jackson.core;


    opens ite.jp.ak.lab06.seller to javafx.fxml;
    exports ite.jp.ak.lab06.seller;
}