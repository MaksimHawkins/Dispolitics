package sg.skylvsme.dispolitics.model.order;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Label;
import lombok.AllArgsConstructor;
import sg.skylvsme.dispolitics.game.CountryNotification;
import sg.skylvsme.dispolitics.model.Country;

@AllArgsConstructor
public class TestOrderItem implements OrderItem {

    private Country country;

    @Override
    public void execute() {

    }

    @Override
    public Component getLayout() {
        return new Label("123");
    }

    @Override
    public CountryNotification getNotification() {
        return null;
    }

    @Override
    public boolean needDialog() {
        return false;
    }

    @Override
    public Component getDialog() {
        return null;
    }

    @Override
    public String getName() {
        return "Тестовый приказ";
    }

    @Override
    public String getDescription() {
        return "Делает что-то тестовое";
    }

    @Override
    public int getCost() {
        return 100;
    }
}
