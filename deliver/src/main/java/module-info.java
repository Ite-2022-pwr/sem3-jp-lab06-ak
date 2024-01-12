module ite.jp.ak.lab06.deliver {
    requires javafx.controls;
    requires javafx.fxml;
    requires lombok;

    requires ite.jp.ak.lab06.utils;
    requires com.fasterxml.jackson.core;


    opens ite.jp.ak.lab06.deliver to javafx.fxml;
    exports ite.jp.ak.lab06.deliver;
}