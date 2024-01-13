package ite.jp.ak.lab06.deliver.shared;

import com.fasterxml.jackson.core.JsonProcessingException;
import ite.jp.ak.lab06.utils.dao.ICustomer;
import ite.jp.ak.lab06.utils.dao.IKeeper;
import ite.jp.ak.lab06.utils.enums.PacketType;
import ite.jp.ak.lab06.utils.enums.Role;
import ite.jp.ak.lab06.utils.model.*;
import ite.jp.ak.lab06.utils.network.Sender;
import lombok.Getter;

import java.io.IOException;

@Getter
public class Deliver implements IKeeper, ICustomer {

    private static Deliver instance;

    private Deliver() {}

    public static Deliver getInstance() {
        if (instance == null) {
            instance = new Deliver();
        }
        return instance;
    }

    private final User deliver = new User() {{
        setRole(Role.Deliver);
    }};

    private final User keeper = new User() {{
        setRole(Role.Keeper);
    }};

    private User lastRequestedUser;

    public synchronized void setLastRequestedUser(User user) {
        lastRequestedUser = user;
    }

    public synchronized User getLastRequestedUser() {
        return lastRequestedUser;
    }

    private Order lastFetchOrder;

    public synchronized void setLastFetchOrder(Order order) {
        lastFetchOrder = order;
    }

    public synchronized Order getLastFetchOrder() {
        return lastFetchOrder;
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
    public void returnReceipt(Receipt receipt) {

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
        return new Product[0];
    }

    @Override
    public void putOrder(Order order) {

    }

    @Override
    public Order getOrder(User deliver) {
        if (!isKeeperKnown()) {
            throw new RuntimeException("Keeper is not known");
        }
        var packet = new Packet() {{
            setType(PacketType.GetOrderRequest);
            setSender(deliver);
            try {
                setPayload(Payload.fromObject(deliver, User.class));
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
    public void returnOrder(Order order) {
        // TODO: implement
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
        // TODO: implement
    }
}
