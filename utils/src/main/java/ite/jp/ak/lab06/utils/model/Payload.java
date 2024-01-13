package ite.jp.ak.lab06.utils.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@Data
public class Payload {
    private Class<?> type;
    private String data;

    public <T> T decodeData(Class<T> dataType) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        return objectMapper.readValue(this.data, dataType);
    }

    public static <T> Payload fromObject(T object, Class<T> objectType) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        var data = objectMapper.writeValueAsString(object);
        var payload = new Payload();
        payload.setData(data);
        payload.setType(objectType);
//        System.out.println(payload);
        return payload;
    }
}
