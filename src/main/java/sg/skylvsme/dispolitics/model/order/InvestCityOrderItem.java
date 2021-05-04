package sg.skylvsme.dispolitics.model.order;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.AllArgsConstructor;
import lombok.val;
import sg.skylvsme.dispolitics.game.CountryNotification;
import sg.skylvsme.dispolitics.messaging.GameBroadcaster;
import sg.skylvsme.dispolitics.model.City;
import sg.skylvsme.dispolitics.model.Country;
import sg.skylvsme.dispolitics.model.PlayerFilter;
import sg.skylvsme.dispolitics.view.component.CityField;

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
        return true;
    }

    @Override
    public Dialog getDialog(Order order) {
        val dialog = new Dialog();
        val dialogLayout = new VerticalLayout();
        dialogLayout.add(new H3("Выберите город для инвестиции"));

        val cityField = new CityField(PlayerFilter.ONLY_ME, this.country);
        cityField.addValueChangeListener(event -> {
            this.city = cityField.getValue();
        });
        cityField.setWidthFull();

        dialogLayout.add(cityField);

        val chooseButton = new Button("Выбрать");
        chooseButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        chooseButton.addClickListener(event -> {
            dialog.close();
            order.addOrderItem(this);
            GameBroadcaster.broadcast("");
        });
        dialogLayout.add(chooseButton);
        dialogLayout.setHorizontalComponentAlignment(FlexComponent.Alignment.END, chooseButton);
        dialog.add(dialogLayout);
        return dialog;
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
