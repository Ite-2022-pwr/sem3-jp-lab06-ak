package ite.jp.ak.lab06.seller.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import ite.jp.ak.lab06.utils.dao.ICustomer;
import ite.jp.ak.lab06.utils.dao.IKeeper;
import ite.jp.ak.lab06.utils.dao.ISeller;
import ite.jp.ak.lab06.utils.dao.ITriggerable;
import ite.jp.ak.lab06.utils.enums.OrderStatus;
import ite.jp.ak.lab06.utils.enums.PacketType;
import ite.jp.ak.lab06.utils.model.*;
import ite.jp.ak.lab06.utils.network.Listener;
import ite.jp.ak.lab06.seller.shared.Seller;
import ite.jp.ak.lab06.utils.network.Sender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SellerServer extends Listener implements ISeller, IKeeper, ICustomer {

    private final Seller seller = Seller.getInstance();

    public SellerServer(String host, Integer port, ITriggerable triggerable) {
        super(host, port, triggerable);
        seller.getSeller().setHost(host);
        seller.getSeller().setPort(port);
    }

    @Override
    public void returnReceipt(Receipt receipt) {
        var packet = new Packet() {{
            setType(PacketType.ReturnReceiptRequest);
            setSender(seller.getSeller());
            try {
                setPayload(Payload.fromObject(receipt, Receipt.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }};

        seller.getInfo(seller.getSeller(), receipt.getOrder().getCustomer());
        new Thread(() -> {
            User customer;
            while ((customer = seller.getLastRequestedUser()) == null) {}
            try {
                var sender = new Sender();
                sender.sendTo(customer, packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            seller.setLastRequestedUser(null);
        }).start();
    }

    @Override
    public void register(User user) {
        seller.getSeller().setId(user.getId());
    }

    @Override
    public void unregister(User user) {
        seller.unregister(user);
    }

    @Override
    public Product[] getOffer(User customer) {
        return new Product[0];
    }

    @Override
    public void putOrder(Order order) {

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
                setSender(seller.getSeller());
                setPayload(Payload.fromObject(order, Order.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }};
        var sender = new Sender();
        try {
            sender.sendTo(seller.getKeeper(), packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User getInfo(User user, User requestedUser) {
        return null;
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
                    seller.setLastRequestedUser(requestedUser);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> throw new RuntimeException("Unknown response type");
        }
    }

    @Override
    public void acceptOrder(Order order) {
        var productList = new ArrayList<>(List.of(order.getProducts()));

        var toReturn = productList.stream().filter(Product::isToReturn).toList();
        var toBuy = productList.stream().filter(p -> !p.isToReturn()).toList();

        var receiptOrder = new Order() {{
            setCustomer(order.getCustomer());
            setDeliver(order.getDeliver());
            setId(order.getId());
            setStatus(toBuy.isEmpty() ? OrderStatus.Cancelled : OrderStatus.Payed);
            setProducts(toBuy.toArray(new Product[0]));
        }};
        var receipt = new Receipt() {{
            setSeller(seller.getSeller());
            setOrder(receiptOrder);
        }};

        var returnProductsOrder = new Order() {{
            setCustomer(order.getCustomer());
            setDeliver(order.getDeliver());
            setId(order.getId());
            setStatus(OrderStatus.Returned);
            setProducts(toReturn.toArray(new Product[0]));
        }};

        returnOrder(returnProductsOrder);
        returnReceipt(receipt);
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
            case AcceptOrderRequest -> {
                var payload = packet.getPayload();
                try {
                    var order = payload.decodeData(Order.class);
                    acceptOrder(order);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> throw new RuntimeException("Unknown packet type");
        }
    }
}
