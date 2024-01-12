package ite.jp.ak.lab06.customer.shared;

import com.fasterxml.jackson.core.JsonProcessingException;
import ite.jp.ak.lab06.utils.dao.IKeeper;
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
public class Customer implements IKeeper {
    private static Customer instance;

    private final User customer = new User() {{
        setRole(Role.Customer);
    }};

    private final User keeper = new User() {{
        setRole(Role.Keeper);
    }};

    private final List<Product> storeOffer = Collections.synchronizedList(new ArrayList<>());

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
        return null;
    }
}
