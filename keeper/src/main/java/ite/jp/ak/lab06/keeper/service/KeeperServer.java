package ite.jp.ak.lab06.keeper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ite.jp.ak.lab06.keeper.shared.Keeper;
import ite.jp.ak.lab06.utils.dao.*;
import ite.jp.ak.lab06.utils.enums.PacketType;
import ite.jp.ak.lab06.utils.enums.ResponseType;
import ite.jp.ak.lab06.utils.enums.Role;
import ite.jp.ak.lab06.utils.model.*;
import ite.jp.ak.lab06.utils.network.Listener;
import ite.jp.ak.lab06.utils.network.Sender;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

@Getter
@Setter
public class KeeperServer extends Listener implements Runnable, IKeeper, ICustomer, IResponder {

    private final Keeper keeper = Keeper.getInstance();
    private final ITriggerable triggerable;

    public KeeperServer(String host, Integer port, ITriggerable triggerable) {
        super(host, port);
        keeper.getKeeper().setHost(host);
        keeper.getKeeper().setPort(port);
        this.triggerable = triggerable;
    }

    public void trigger() {
        if (triggerable != null) {
            triggerable.trigger();
        }
    }

    public void run() {
        try {
            this.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void processPacket(Packet packet) {
        System.out.println(packet);
        // TODO: implement

        switch (packet.getType()) {
            case RegisterRequest:
                try {
                    var user = packet.getPayload().decodeData(User.class);
                    register(user);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                throw new RuntimeException("Unknown packet type");
        }
    }

    public void response(User user, Response response) {
        try {
            Payload payload = Payload.fromObject(response);
            Packet packet = new Packet();
            packet.setType(PacketType.Response);
            packet.setSender(keeper.getKeeper());
            packet.setPayload(payload);

            var sender = new Sender();
            sender.sendTo(user, packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void register(User user) {
        var registeredUser = keeper.registerUser(user);
        trigger();
        Response response;
        try {
            response = Response.fromObject(ResponseType.RegisterResponse, registeredUser);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response(user, response);
    }

    public void unregister(User user) {
        keeper.unregisterUser(user);
    }

    public Product[] getOffer(User user) {
        // TODO: implement
        return null;
    }

    public void putOrder(Order order) {
        // TODO: implement
    }

    public Order getOrder(User deliver) {
        // TODO: implement
        return null;
    }

    public void returnOrder(Order order) {
        // TODO: implement
    }

    public User getInfo(User user, User requestedUser) {
        // TODO: implement
        return null;
    }

    /**
     * Unused
     * @param receipt
     */
    public void returnReceipt(Receipt receipt) {
        // Not needed
    }
}
