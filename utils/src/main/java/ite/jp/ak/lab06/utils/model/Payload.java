package ite.jp.ak.lab06.utils.model;

import lombok.Data;

@Data
public class Payload {
    private Class<?> type;
    private String data;
}
