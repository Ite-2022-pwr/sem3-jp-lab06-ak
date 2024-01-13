package ite.jp.ak.lab06.customer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ite.jp.ak.lab06.customer.shared.Customer;
import ite.jp.ak.lab06.utils.dao.ICustomer;
import ite.jp.ak.lab06.utils.dao.IKeeper;
import ite.jp.ak.lab06.utils.dao.ITriggerable;
import ite.jp.ak.lab06.utils.model.*;
import ite.jp.ak.lab06.utils.network.Listener;

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
            case PutOrderRequest -> {
                var payload = packet.getPayload();
                try {
                    var order = payload.decodeData(Order.class);
                    putOrder(order);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            case ReturnReceiptRequest -> {
                var payload = packet.getPayload();
                try {
                    var receipt = payload.decodeData(Receipt.class);
                    returnReceipt(receipt);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> throw new RuntimeException("Unknown packet type");
        }
    }

    @Override
    public void register(User user) {
        customer.getCustomer().setId(user.getId());
    }

    @Override
    public void unregister(User user) {
        customer.unregister(user);
    }

    @Override
    public Product[] getOffer(User customer) {
        return new Product[0];
    }

    @Override
    public void putOrder(Order order) {
        customer.getOrders().add(order);
        trigger();
    }

    @Override
    public Order getOrder(User deliver) {
        return null;
    }

    @Override
    public void returnOrder(Order order) {

    }

    @Override
    public User getInfo(User user, User requestedUser) {
        switch (requestedUser.getRole()) {
            case Deliver -> customer.setLastRequestedDeliver(requestedUser);
            case Seller -> customer.setLastRequestedSeller(requestedUser);
            default -> throw new RuntimeException("Unknown user role");
        }
        return null;
    }

    @Override
    public void returnReceipt(Receipt receipt) {
        customer.getReceipts().add(receipt);
        trigger();
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
            case GetInfoResponse -> {
                var payload = response.getPayload();
                try {
                    var requestedUser = payload.decodeData(User.class);
                    getInfo(user, requestedUser);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> throw new RuntimeException("Unknown response type");
        }
    }
}
