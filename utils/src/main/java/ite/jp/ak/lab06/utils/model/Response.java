package ite.jp.ak.lab06.utils.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import ite.jp.ak.lab06.utils.enums.ResponseType;
import lombok.Data;

@Data
public class Response {
    private ResponseType type;
    private Payload payload;

    public static <T> Response fromObject(ResponseType type, T data, Class<T> dataType) throws JsonProcessingException {
        var response = new Response();
        response.setType(type);
        response.setPayload(Payload.fromObject(data, dataType));
        return response;
    }
}
