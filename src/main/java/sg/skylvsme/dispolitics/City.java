package sg.skylvsme.dispolitics;

import lombok.Getter;

@Getter
public class City {

    private final String name;
    private int health;

    public City(String name) {
        this.name = name;
        this.health = 100;
    }

    public void attackCity(int damage) {
        this.health -= damage;
    }

}
