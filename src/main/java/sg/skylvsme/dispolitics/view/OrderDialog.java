package sg.skylvsme.dispolitics.view;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.val;
import sg.skylvsme.dispolitics.game.Game;
import sg.skylvsme.dispolitics.messaging.GameBroadcaster;
import sg.skylvsme.dispolitics.model.order.InvestCityOrderItem;
import sg.skylvsme.dispolitics.model.order.Order;
import sg.skylvsme.dispolitics.model.order.OrderItem;
import sg.skylvsme.dispolitics.model.order.TestOrderItem;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OrderDialog extends Dialog {

    VerticalLayout orderItemsLayout;
    Order order;

    public OrderDialog(Order order) {
        this.order = order;

        orderItemsLayout = new VerticalLayout();
        add(new H3("Добавить пункт приказа"), orderItemsLayout);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        orderItemsLayout.add(getOrderItemLayout());
    }

    private Component getOrderItemLayout() {
        val orderItems = new VerticalLayout();
        for (OrderItem availableOrderItem : getAvailableOrderItems()) {
            orderItems.add(getOrderItemWrapper(availableOrderItem));
        }
        return orderItems;
    }

    private List<OrderItem> getAvailableOrderItems() {
        return Stream.of(
                new InvestCityOrderItem(this.order.getCountry().getCities().get(0), this.order.getCountry()),
                new TestOrderItem(this.order.getCountry())
        ).collect(Collectors.toList());
    }

    private Component getOrderItemWrapper(OrderItem orderItem) {
        val layout = new HorizontalLayout();
        layout.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        layout.setWidthFull();

        val orderItemInfo = new VerticalLayout();
        val header = new HorizontalLayout();
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        val orderName = new H4(orderItem.getName());
        //orderName.getStyle().set("margin-top", "0em");
        header.addAndExpand(orderName);

        val cost = new HorizontalLayout();
        cost.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        cost.add(IconManager.getMoneyIcon(), new Label(String.valueOf(orderItem.getCost())));
        header.add(cost);

        orderItemInfo.add(header);
        val desc = new Label(orderItem.getDescription());
        //desc.getStyle().set("margin-top", "0em");
        orderItemInfo.add(desc);

        layout.addAndExpand(orderItemInfo);

        val chooseButton = new Button("Выбрать");
        chooseButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_PRIMARY);
        chooseButton.getElement().getStyle().set("min-width", "100px");
        chooseButton.addClickListener(event -> {
            this.order.addOrderItem(orderItem);
            GameBroadcaster.broadcast("");
            this.close();
        });
        layout.add(chooseButton);
        this.setResizable(true);
        return layout;
    }

}
