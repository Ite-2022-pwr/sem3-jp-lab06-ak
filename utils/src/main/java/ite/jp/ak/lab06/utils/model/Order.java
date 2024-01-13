package ite.jp.ak.lab06.utils.model;

import ite.jp.ak.lab06.utils.enums.OrderStatus;
import lombok.Data;

@Data
public class Order {
    private Integer id;
    private Product[] products;
    private User customer;
    private User deliver;
    private OrderStatus status;
}
