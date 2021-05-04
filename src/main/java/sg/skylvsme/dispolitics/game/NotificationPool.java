package sg.skylvsme.dispolitics.game;

import lombok.val;
import sg.skylvsme.dispolitics.model.Country;

import java.util.ArrayList;
import java.util.List;

public class NotificationPool {

    static List<CountryNotification> notifications = new ArrayList<>();

    public static void addNotification(Country country, String message) {
        notifications.add(new CountryNotification(country, message));
    }

    public static void addNotification(CountryNotification countryNotification) {
        notifications.add(countryNotification);
    }

    public static List<CountryNotification> getNotificationsByCountry(Country country) {
        val list = new ArrayList<CountryNotification>();
        for (CountryNotification notification : notifications) {
            if (notification.getCountry() == country) {
                list.add(notification);
            }
        }
        return list;
    }

    public static void removeNotification(CountryNotification notification) {
        notifications.remove(notification);
    }

    public static void clearPool() {
        notifications.clear();
    }

}
