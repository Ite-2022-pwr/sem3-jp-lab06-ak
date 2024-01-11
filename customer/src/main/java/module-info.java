module ite.jp.ak.lab06.customer {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;

    requires ite.jp.ak.lab06.utils;


    opens ite.jp.ak.lab06.customer to javafx.fxml;
    exports ite.jp.ak.lab06.customer;
}