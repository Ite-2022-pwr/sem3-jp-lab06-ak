package ite.jp.ak.lab06.utils.model;

import ite.jp.ak.lab06.utils.enums.ResponseType;
import lombok.Data;

@Data
public class Response {
    private ResponseType type;
    private Payload payload;
}
