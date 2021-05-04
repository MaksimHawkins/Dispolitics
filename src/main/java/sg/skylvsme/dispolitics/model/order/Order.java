package sg.skylvsme.dispolitics.model.order;

import lombok.Getter;
import sg.skylvsme.dispolitics.model.Country;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Order {

    private Country country;
    private List<OrderItem> orderItems;

    public Order(Country country) {
        this.country = country;
        this.orderItems = new ArrayList<>();
    }

    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
    }

}
