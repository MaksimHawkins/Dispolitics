package sg.skylvsme.dispolitics.model.order;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.val;
import sg.skylvsme.dispolitics.game.CountryNotification;
import sg.skylvsme.dispolitics.model.City;
import sg.skylvsme.dispolitics.model.Country;

@AllArgsConstructor
public class InvestCityOrderItem implements OrderItem {

    private City city;
    private Country country;
    private final String name = "Инвестиция в город";
    private final String description = "Увеличивает население города на 1, прибавляя с него доход";

    @Override
    public void execute() {
        if (!this.country.getCities().contains(city))
            throw new RuntimeException("Country doesn't contain specified city");

        this.country.addMoney(-200);
        this.city.increaseEconomyAmplifier(3);
    }

    @Override
    public Component getLayout() {
        val layout = new HorizontalLayout();
        layout.add("Инвестиция в город " + city.getName());
        return layout;
    }

    @Override
    public CountryNotification getNotification() {
        return new CountryNotification(this.country, "Успешно инвестировано в город " + this.city.getName());
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
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public int getCost() {
        return 200;
    }


}
