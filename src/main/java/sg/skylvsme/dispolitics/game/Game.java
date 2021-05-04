package sg.skylvsme.dispolitics.game;

import com.vaadin.flow.component.dependency.CssImport;
import lombok.val;
import sg.skylvsme.dispolitics.model.City;
import sg.skylvsme.dispolitics.model.Country;
import sg.skylvsme.dispolitics.model.Player;
import sg.skylvsme.dispolitics.model.PlayerFilter;
import sg.skylvsme.dispolitics.model.order.Order;
import sg.skylvsme.dispolitics.security.SecurityConfiguration;

import java.util.ArrayList;
import java.util.List;

@CssImport("./styles/theme.css")
public class Game {

    private static int currentTurn;

    private static List<Country> countries;
    private static boolean isStarted = false;

    public static void init() {
        currentTurn = 0;
        countries = new ArrayList<>();

        val usa = new Country("США", "usa.png");
        val russia = new Country("Россия", "russia.png");
        val china = new Country("Китай", "china.png");

        usa.addCity(new City("Нью-Йорк"));
        usa.addCity(new City("Лос-Анджелес"));
        usa.addCity(new City("Вашингтон"));

        russia.addCity(new City("Москва"));
        russia.addCity(new City("Санкт-Петербург"));
        russia.addCity(new City("Краснодар"));

        china.addCity(new City("Пекин"));
        china.addCity(new City("Шанхай"));
        china.addCity(new City("Гуанчжоу"));

        countries.add(usa);
        countries.add(russia);
        countries.add(china);
    }

    public static Country getCountryByName(String name) {
        for (Country country : countries) {
            if (country.getName().equals(name)) return country;
        }
        return null;
    }

    public static List<City> getCities(PlayerFilter owner) {
        return null;
    }

    public static Player getCurrentPlayer() {
        for (Country country : getCountries()) {
            for (Player player : country.getPlayers()) {
                if (player.getOAuth2User().equals(SecurityConfiguration.getCurrentUser())) {
                    return player;
                }
            }
        }
        return null;
    }

    public static void start() {
        if (!isStarted) {
            isStarted = true;
            currentTurn = 1;
        }
    }

    public static Country getCountryByPlayer(Player player) {
        for (Country country : getCountries()) {
            for (Player countryPlayer : country.getPlayers()) {
                if (countryPlayer.equals(player)) {
                    return country;
                }
            }
        }
        return null;
    }

    public static Country getCurrentCountry(Player player) {
        return getCountryByPlayer(player);
    }

    public static Order getOrderByCountry(Country country) {
        for (Order order : TurnExecutor.getOrderExecutor().getOrders()) {
            if (order.getCountry() == country)
                return order;
        }
        return null;
    }

    public static Order getCurrentOrder(Player player) {
        return getOrderByCountry(getCountryByPlayer(player));
    }

    public static List<Country> getCountries() {
        return countries;
    }

    static void increaseCurrentTurn() {
        currentTurn++;
    }

    public static void nextTurn() {
        TurnExecutor.processTurn();
        TurnExecutor.nextTurn();
        increaseCurrentTurn();
    }

    public static boolean isStarted() {
        return isStarted;
    }

    public static int getCurrentTurn() {
        return currentTurn;
    }

}
