module ite.jp.ak.lab06.utils {
    requires lombok;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    exports ite.jp.ak.lab06.utils.dao;
    exports ite.jp.ak.lab06.utils.model;
    exports ite.jp.ak.lab06.utils.enums;
    exports ite.jp.ak.lab06.utils.network;
}