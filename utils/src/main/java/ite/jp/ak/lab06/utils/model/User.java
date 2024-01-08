package ite.jp.ak.lab06.utils.model;

import ite.jp.ak.lab06.utils.enums.Role;
import lombok.Data;

@Data
public class User {
    private Integer id = 0;
    private String host;
    private Integer port;
    private Role role;
}
