package sg.skylvsme.dispolitics.game;

import sg.skylvsme.dispolitics.model.Country;
import sg.skylvsme.dispolitics.model.order.Order;
import sg.skylvsme.dispolitics.model.order.OrderItem;

import java.util.ArrayList;
import java.util.List;

public class OrderExecutor {

    private List<Order> orders;

    public OrderExecutor() {
        init();
    }

    public void init() {
        orders = new ArrayList<>();
        for (Country country : Game.getCountries()) {
            orders.add(new Order(country));
        }
    }

    public void execute() {
        for (Order order : orders) {
            for (OrderItem orderItem : order.getOrderItems()) {
                orderItem.execute();
                if (orderItem.getNotification() != null) {
                    NotificationPool.addNotification(orderItem.getNotification());
                }
            }
        }
    }

    List<Order> getOrders() {
        return this.orders;
    }

}
