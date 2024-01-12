package ite.jp.ak.lab06.customer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ite.jp.ak.lab06.customer.shared.Customer;
import ite.jp.ak.lab06.utils.dao.ICustomer;
import ite.jp.ak.lab06.utils.dao.IKeeper;
import ite.jp.ak.lab06.utils.dao.ITriggerable;
import ite.jp.ak.lab06.utils.model.*;
import ite.jp.ak.lab06.utils.network.Listener;

import java.util.ArrayList;
import java.util.List;

public class CustomerServer extends Listener implements ICustomer, IKeeper {

    private final Customer customer = Customer.getInstance();

    public CustomerServer(String host, Integer port, ITriggerable triggerable) {
        super(host, port, triggerable);
        customer.getCustomer().setHost(host);
        customer.getCustomer().setPort(port);
    }

    @Override
    public void processPacket(Packet packet) {
        System.out.println(packet);
        // TODO: implement

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
            default -> {
                throw new RuntimeException("Unknown packet type");
            }
        }
    }

    @Override
    public void register(User user) {
        customer.getCustomer().setId(user.getId());
    }

    @Override
    public void unregister(User user) {
        // TODO: implement
    }

    @Override
    public Product[] getOffer(User customer) {
        // TODO: implement
        return new Product[0];
    }

    @Override
    public void putOrder(Order order) {
        // TODO: implement
    }

    @Override
    public Order getOrder(User deliver) {
        // TODO: implement
        return null;
    }

    @Override
    public void returnOrder(Order order) {
        // TODO: implement
    }

    @Override
    public User getInfo(User user, User requestedUser) {
        // TODO: implement
        return null;
    }

    @Override
    public void returnReceipt(Receipt receipt) {
        // TODO: implement
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
            case GetOfferResponse -> {
                var payload = response.getPayload();
                try {
                    var products = payload.decodeData(Product[].class);
                    customer.getStoreOffer().clear();
                    customer.getStoreOffer().addAll(List.of(products));
                    trigger();
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> {
                throw new RuntimeException("Unknown response type");
            }
        }
    }
}
