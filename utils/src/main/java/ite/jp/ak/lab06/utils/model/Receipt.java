package ite.jp.ak.lab06.utils.model;

import lombok.Data;

@Data
public class Receipt {
    private User seller;
    private Order order;
}
