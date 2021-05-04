package sg.skylvsme.dispolitics.view.component;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import lombok.val;
import sg.skylvsme.dispolitics.model.City;
import sg.skylvsme.dispolitics.model.Country;
import sg.skylvsme.dispolitics.model.PlayerFilter;

import java.util.Collections;
import java.util.List;

public class CityField extends CustomField<City> {

    private final Country country;
    private final PlayerFilter playerFilter;
    private final ComboBox<City> cityComboBox;

    public CityField(PlayerFilter playerFilter, Country country) {
        this.playerFilter = playerFilter;
        this.country = country;
        this.cityComboBox = new ComboBox<>();
        cityComboBox.setItems(getCities());
        cityComboBox.setItemLabelGenerator(City::getName);
        cityComboBox.setRenderer(new ComponentRenderer<>(city -> {
            val text = new Div();
            text.setText(city.getName());

            val flag = country.getFlagImage();
            flag.setWidth("24px");
            flag.setHeight("24px");

            val wrapper = new HorizontalLayout();
            wrapper.setAlignItems(FlexComponent.Alignment.CENTER);

            if (flag != null) {
                wrapper.add(flag);
                text.getStyle().set("margin-left", "0.5em");
            }
            wrapper.add(text);
            return wrapper;
        }));
        cityComboBox.setSizeFull();
        this.add(cityComboBox);
    }

    @Override
    protected City generateModelValue() {
        return cityComboBox.getValue();
    }

    @Override
    protected void setPresentationValue(City city) {
        cityComboBox.setValue(city);
    }

    private List<City> getCities() {
        if (this.playerFilter == PlayerFilter.ONLY_ME) {
            return this.country.getCities();
        }
        //TODO Dodelat'
        return Collections.singletonList(country.getCities().get(0));
    }
}
