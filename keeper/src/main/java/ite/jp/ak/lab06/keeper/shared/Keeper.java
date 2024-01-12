package ite.jp.ak.lab06.keeper.shared;

import ite.jp.ak.lab06.utils.enums.OrderStatus;
import ite.jp.ak.lab06.utils.enums.Role;
import ite.jp.ak.lab06.utils.model.Order;
import ite.jp.ak.lab06.utils.model.Product;
import ite.jp.ak.lab06.utils.model.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Getter
public class Keeper {
    private static Keeper instance;

    private final User keeper = new User() {{
        setRole(Role.Keeper);
        setId(1);
    }};

    private Keeper() {}

    public static Keeper getInstance() {
        if (instance == null) {
            instance = new Keeper();
        }
        return instance;
    }

    private final List<User> registeredUsers = Collections.synchronizedList(new ArrayList<>());
    private final List<Product> storeOffer = Collections.synchronizedList(new ArrayList<>());
    private final List<Order> orders = Collections.synchronizedList(new ArrayList<>());

    private Integer nextUserId = 1;
    private Integer nextProductId = 1;
    private Integer nextOrderId = 1;

    public User registerUser(User user) {
        if (registeredUsers.contains(user)) {
            return user;
        }
        user.setId(nextUserId++);
        registeredUsers.add(user);
        return user;
    }

    public void unregisterUser(User user) {
        registeredUsers.remove(user);
    }

    public void addProduct(Product product) {
        product.setId(nextProductId++);
        storeOffer.add(product);
    }

    public void addOrder(Order order) {
        order.setId(nextOrderId++);
        orders.add(order);
    }

    public User getUserById(Integer userId) {
        return registeredUsers.stream().filter(u -> u.getId().equals(userId)).findFirst().orElse(null);
    }

    public Order getFirstOrderToAccept() {
        var order = orders.stream().filter(o -> o.getStatus() == OrderStatus.Ordered).findFirst().orElse(null);
        if (order != null) {
            order.setStatus(OrderStatus.Accepted);
        }
        return order;
    }

    public List<User> getAllCustomers() {
        return registeredUsers.stream().filter(user -> user.getRole() == Role.Customer).toList();
    }

    public List<User> getAllDelivers() {
        return registeredUsers.stream().filter(user -> user.getRole() == Role.Deliver).toList();
    }

    public List<User> getAllSellers() {
        return registeredUsers.stream().filter(user -> user.getRole() == Role.Seller).toList();
    }

}
