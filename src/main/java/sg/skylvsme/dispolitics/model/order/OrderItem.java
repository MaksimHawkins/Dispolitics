package sg.skylvsme.dispolitics.model.order;


import com.vaadin.flow.component.Component;
import sg.skylvsme.dispolitics.game.CountryNotification;

public interface OrderItem {

    public void execute();

    public Component getLayout();

    public CountryNotification getNotification();


    public boolean needDialog();

    public Component getDialog();

    public String getName();

    public String getDescription();

    public int getCost();

}
