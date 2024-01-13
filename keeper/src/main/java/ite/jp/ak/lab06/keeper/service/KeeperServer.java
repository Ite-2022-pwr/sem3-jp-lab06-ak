package ite.jp.ak.lab06.keeper.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ite.jp.ak.lab06.keeper.shared.Keeper;
import ite.jp.ak.lab06.utils.dao.*;
import ite.jp.ak.lab06.utils.enums.PacketType;
import ite.jp.ak.lab06.utils.enums.ResponseType;
import ite.jp.ak.lab06.utils.model.*;
import ite.jp.ak.lab06.utils.network.Listener;
import ite.jp.ak.lab06.utils.network.Sender;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

@Getter
@Setter
public class KeeperServer extends Listener implements IKeeper, ICustomer, IResponder {

    private final Keeper keeper = Keeper.getInstance();

    public KeeperServer(String host, Integer port, ITriggerable triggerable) {
        super(host, port, triggerable);
        keeper.getKeeper().setHost(host);
        keeper.getKeeper().setPort(port);
    }

    @Override
    public void processPacket(Packet packet) {
        System.out.println(packet);

        switch (packet.getType()) {
            case RegisterRequest -> {
                try {
                    var user = packet.getPayload().decodeData(User.class);
                    register(user);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            case UnregisterRequest -> {
                try {
                    var user = packet.getPayload().decodeData(User.class);
                    unregister(user);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            case GetOfferRequest -> {
                var products = keeper.getStoreOffer().toArray(new Product[0]);
                var response = new Response() {{
                    setType(ResponseType.GetOfferResponse);
                    try {
                        setPayload(Payload.fromObject(products, Product[].class));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }};
                response(packet.getSender(), response);
            }
            case GetInfoRequest -> {
                try {
                    var requestedUser = packet.getPayload().decodeData(User.class);
                    getInfo(packet.getSender(), requestedUser);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            case PutOrderRequest -> {
                try {
                    var order = packet.getPayload().decodeData(Order.class);
                    putOrder(order);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            case ReturnOrderRequest -> {
                try {
                    var order = packet.getPayload().decodeData(Order.class);
                    returnOrder(order);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            case GetOrderRequest -> {
                try {
                    var deliver = packet.getPayload().decodeData(User.class);
                    getOrder(deliver);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> throw new RuntimeException("Unknown packet type");
        }
    }

    @Override
    public void response(User user, Response response) {
        try {
            Payload payload = Payload.fromObject(response, Response.class);
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

    @Override
    public void register(User user) {
        var registeredUser = keeper.registerUser(user);
        trigger();
        Response response;
        try {
            response = Response.fromObject(ResponseType.RegisterResponse, registeredUser, User.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        response(user, response);
    }

    @Override
    public void unregister(User user) {
        keeper.unregisterUser(user);
        trigger();
    }

    public Product[] getOffer(User user) {
        var products = keeper.getStoreOffer();
        var response = new Response() {{
            setType(ResponseType.GetOfferResponse);
            try {
                setPayload(Payload.fromObject(products.toArray(new Product[0]), Product[].class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }};
        response(user, response);
        return null;
    }

    @Override
    public void putOrder(Order order) {
        keeper.addOrder(order);
        trigger();
    }

    @Override
    public Order getOrder(User deliver) {
        var order = keeper.getFirstOrderToAccept();
        order.setDeliver(deliver);
        var response = new Response() {{
            setType(ResponseType.GetOrderResponse);
            try {
                setPayload(Payload.fromObject(order, Order.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }};
        response(deliver, response);
        trigger();
        return order;
    }

    @Override
    public void returnOrder(Order order) {
        System.out.println("Returned order: " + order);
    }

    @Override
    public User getInfo(User user, User requestedUser) {
        var foundUser = keeper.getUserById(requestedUser.getId());
        var response = new Response() {{
            setType(ResponseType.GetInfoResponse);
            try {
                setPayload(Payload.fromObject(foundUser, User.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }};

        response(user, response);
        return foundUser;
    }

    /**
     * Unused
     * @param receipt
     */
    @Override
    public void returnReceipt(Receipt receipt) {
        // Not needed
    }
}
