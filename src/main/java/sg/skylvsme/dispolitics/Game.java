package sg.skylvsme.dispolitics;

import lombok.Getter;
import lombok.val;
import net.dv8tion.jda.api.exceptions.AccountTypeException;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Game {

    public static Game INSTANCE = new Game();

    private int currentTurn;
    private List<Country> countries;
    private boolean isStarted = false;

    private Game() {
        this.currentTurn = 0;
        this.countries = new ArrayList<>();

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

        this.countries.add(usa);
        this.countries.add(russia);
        this.countries.add(china);
    }

    public Country getCountryByName(String name) {
        for (Country country : countries) {
            if (country.getName().equals(name)) return country;
        }
        return null;
    }

    public void start() {
        if (!isStarted)
            isStarted = true;
    }

}
