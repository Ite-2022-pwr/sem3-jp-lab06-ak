package ite.jp.ak.lab06.deliver.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ite.jp.ak.lab06.deliver.shared.Deliver;
import ite.jp.ak.lab06.utils.dao.ICustomer;
import ite.jp.ak.lab06.utils.dao.IKeeper;
import ite.jp.ak.lab06.utils.dao.ITriggerable;
import ite.jp.ak.lab06.utils.enums.PacketType;
import ite.jp.ak.lab06.utils.model.*;
import ite.jp.ak.lab06.utils.network.Listener;
import ite.jp.ak.lab06.utils.network.Sender;

import java.io.IOException;

public class DeliverServer extends Listener implements ICustomer, IKeeper {

    private final Deliver deliver = Deliver.getInstance();

    public DeliverServer(String host, Integer port, ITriggerable triggerable) {
        super(host, port, triggerable);
        deliver.getDeliver().setHost(host);
        deliver.getDeliver().setPort(port);
    }

    @Override
    public void processPacket(Packet packet) {
        System.out.println(packet);

        switch (packet.getType()) {
            case Response -> {
                var payload = packet.getPayload();
                try {
                    var response = payload.decodeData(Response.class);
                    response(packet.getSender(), response);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            case ReturnOrderRequest -> {
                var payload = packet.getPayload();
                try {
                    var order = payload.decodeData(Order.class);
                    returnOrder(order);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> throw new RuntimeException("Unknown packet type");
        }
    }
    @Override
    public void register(User user) {
        deliver.getDeliver().setId(user.getId());
    }

    @Override
    public void unregister(User user) {
        deliver.unregister(user);
    }

    @Override
    public Product[] getOffer(User customer) {
        return new Product[0];
    }

    @Override
    public void putOrder(Order order) {
        var packet = new Packet() {{
            setType(PacketType.PutOrderRequest);
            try {
                setPayload(Payload.fromObject(order, Order.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }};

        deliver.getInfo(deliver.getDeliver(), order.getCustomer());
        System.out.println(deliver.getLastRequestedUser());
        new Thread(() -> {
            User customer;
            while ((customer = deliver.getLastRequestedUser()) == null) {}
            try {
                var sender = new Sender();
                sender.sendTo(customer, packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            deliver.setLastRequestedUser(null);
        }).start();
//            var customer = order.getCustomer();
        deliver.setLastFetchOrder(null);
    }

    @Override
    public Order getOrder(User deliver) {
        return null;
    }

    @Override
    public void returnOrder(Order order) {
        var packet = new Packet() {{
            setType(PacketType.ReturnOrderRequest);
            try {
                setSender(deliver.getDeliver());
                setPayload(Payload.fromObject(order, Order.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }};
        var sender = new Sender();
        try {
            sender.sendTo(deliver.getKeeper(), packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getInfo(User user, User requestedUser) {
        return null;
    }

    @Override
    public void returnReceipt(Receipt receipt) {

    }

    @Override
    public void response(User user, Response response) {
        switch (response.getType()) {
            case RegisterResponse -> {
                var payload = response.getPayload();
                try {
                    var registeredUser = payload.decodeData(User.class);
                    register(registeredUser);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            case GetInfoResponse -> {
                try {
                    var requestedUser = response.getPayload().decodeData(User.class);
                    deliver.setLastRequestedUser(requestedUser);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            case GetOrderResponse -> {
                try {
                    var order = response.getPayload().decodeData(Order.class);
                    deliver.setLastFetchOrder(order);
                    trigger();
                    putOrder(order);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> throw new RuntimeException("Unknown response type");
        }
    }
}
