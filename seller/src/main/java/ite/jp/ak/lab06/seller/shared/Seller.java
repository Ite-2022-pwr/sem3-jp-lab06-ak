package ite.jp.ak.lab06.seller.shared;

import com.fasterxml.jackson.core.JsonProcessingException;
import ite.jp.ak.lab06.utils.dao.IKeeper;
import ite.jp.ak.lab06.utils.dao.ISeller;
import ite.jp.ak.lab06.utils.enums.PacketType;
import ite.jp.ak.lab06.utils.enums.Role;
import ite.jp.ak.lab06.utils.model.*;
import ite.jp.ak.lab06.utils.network.Sender;
import lombok.Getter;

import java.io.IOException;

@Getter
public class Seller implements IKeeper, ISeller {
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

    private static Seller instance;

    private Seller() {}

    public static Seller getInstance() {
        if (instance == null) {
            instance = new Seller();
        }
        return instance;
    }

    private final User seller = new User() {{
        setRole(Role.Seller);
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

    public boolean isKeeperKnown() {
        return keeper.getHost() != null && keeper.getPort() != null;
    }

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
    public void response(User user, Response response) {

    }

    @Override
    public void acceptOrder(Order order) {

    }
}
