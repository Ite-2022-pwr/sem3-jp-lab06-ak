module ite.jp.ak.lab06.customer {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;

    requires ite.jp.ak.lab06.utils;
    requires com.fasterxml.jackson.core;


    opens ite.jp.ak.lab06.customer to javafx.fxml;
    exports ite.jp.ak.lab06.customer;
    exports ite.jp.ak.lab06.customer.service;
    exports ite.jp.ak.lab06.customer.shared;
}