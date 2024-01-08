package ite.jp.ak.lab06.utils.model;

import lombok.Data;

@Data
public class Order {
    private Integer id;
    private Product product;
    private User client;
}
