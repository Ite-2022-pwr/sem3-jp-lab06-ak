package ite.jp.ak.lab06.utils.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ite.jp.ak.lab06.utils.enums.PacketType;
import lombok.Data;

@Data
public class Packet {
    private PacketType type;
    private User sender; // ok
    private Payload payload;

    public String encode() throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }

    public static Packet fromString(String data) throws JsonProcessingException {
        var objectMapper = new ObjectMapper();
        return objectMapper.readValue(data, Packet.class);
    }
}
