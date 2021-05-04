package sg.skylvsme.dispolitics.view.util;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.server.VaadinServlet;
import lombok.val;

public class IconManager {

    public static Image getHeartIcon() {
        return getIcon("heart.png");
    }

    public static Image getPopulationIcon() {
        return getIcon("man.png");
    }

    public static Image getMoneyIcon() {
        return getIcon("money.png");
    }

    private static Image getIcon(String iconLocation) {
        val icon = new Image(VaadinServlet.getCurrent().getServletContext().getContextPath() + "/icons/" + iconLocation, "");
        icon.setHeight("24px");
        icon.setWidth("24px");
        return icon;
    }

}
