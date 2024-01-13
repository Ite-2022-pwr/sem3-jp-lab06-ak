package ite.jp.ak.lab06.customer.shared;

import com.fasterxml.jackson.core.JsonProcessingException;
import ite.jp.ak.lab06.utils.dao.IKeeper;
import ite.jp.ak.lab06.utils.dao.ISeller;
import ite.jp.ak.lab06.utils.enums.PacketType;
import ite.jp.ak.lab06.utils.enums.Role;
import ite.jp.ak.lab06.utils.model.*;
import ite.jp.ak.lab06.utils.network.Sender;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class Customer implements IKeeper, ISeller {
    private static Customer instance;

    private final User customer = new User() {{
        setRole(Role.Customer);
    }};

    private final User keeper = new User() {{
        setRole(Role.Keeper);
    }};

    private final List<Product> storeOffer = Collections.synchronizedList(new ArrayList<>());
    private final List<Order> orders = Collections.synchronizedList(new ArrayList<>());
    private final List<Receipt> receipts = Collections.synchronizedList(new ArrayList<>());

    private User lastRequestedDeliver;

    public synchronized void setLastRequestedDeliver(User user) {
        lastRequestedDeliver = user;
    }

    public synchronized User getLastRequestedDeliver() {
        return lastRequestedDeliver;
    }

    private User lastRequestedSeller;

    public synchronized void setLastRequestedSeller(User user) {
        lastRequestedSeller = user;
    }

    public synchronized User getLastRequestedSeller() {
        return lastRequestedSeller;
    }

    private Customer() {}

    public static Customer getInstance() {
        if (instance == null) {
            instance = new Customer();
        }
        return instance;
    }

    public boolean isKeeperKnown() {
        return keeper.getHost() != null && keeper.getPort() != null;
    }

    @Override
    public void register(User user) {
        if (!isKeeperKnown()) {
            throw new RuntimeException("Keeper is not known");
        }

        var packet = new Packet() {{
            setType(PacketType.RegisterRequest);
            setSender(user);
            try {
                setPayload(Payload.fromObject(user, User.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }};

        System.out.println(packet);

        var sender = new Sender();
        try {
            sender.sendTo(keeper, packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(User user) {
        if (!isKeeperKnown()) {
            throw new RuntimeException("Keeper is not known");
        }

        var packet = new Packet() {{
            setType(PacketType.UnregisterRequest);
            setSender(user);
            try {
                setPayload(Payload.fromObject(user, User.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }};

        var sender = new Sender();
        try {
            sender.sendTo(keeper, packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Product[] getOffer(User customer) {
        var packet = new Packet() {{
           setType(PacketType.GetOfferRequest);
           setSender(customer);
            try {
                setPayload(Payload.fromObject(customer, User.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }};
        var sender = new Sender();
        try {
            sender.sendTo(getKeeper(), packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void putOrder(Order order) {
        if (!isKeeperKnown()) {
            throw new RuntimeException("Keeper is not known");
        }
        var packet = new Packet() {{
            setType(PacketType.PutOrderRequest);
            setSender(customer);
            try {
                setPayload(Payload.fromObject(order, Order.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }};
        var sender = new Sender();
        try {
            sender.sendTo(getKeeper(), packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Order getOrder(User deliver) {
        return null;
    }

    @Override
    public void returnOrder(Order order) {
        var packet = new Packet() {{
            setType(PacketType.ReturnOrderRequest);
            setSender(customer);
            try {
                setPayload(Payload.fromObject(order, Order.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }};


        User deliver;
        getInfo(getCustomer(), order.getDeliver());
        while ((deliver = getLastRequestedDeliver()) == null) {
            System.out.println("Waiting for deliver");
        }
        try {
            var sender = new Sender();
            sender.sendTo(deliver, packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public User getInfo(User user, User requestedUser) {
        if (!isKeeperKnown()) {
            throw new RuntimeException("Keeper is not known");
        }
        var packet = new Packet() {{
            setType(PacketType.GetInfoRequest);
            setSender(user);
            try {
                setPayload(Payload.fromObject(requestedUser, User.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }};
        var sender = new Sender();
        try {
            sender.sendTo(getKeeper(), packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void response(User user, Response response) {

    }

    @Override
    public void acceptOrder(Order order) {
        // pobieramy informacje o pierwszym dostępnym sprzedawcy
        getInfo(getCustomer(), new User());
        User seller;
        while ((seller = getLastRequestedSeller()) == null) {
            System.out.println("Waiting for seller");
        }

        var packet = new Packet() {{
            setType(PacketType.AcceptOrderRequest);
            setSender(getCustomer());
            try {
                setPayload(Payload.fromObject(order, Order.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }};

        orders.remove(order);
        var sender = new Sender();
        try {
            sender.sendTo(seller, packet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
