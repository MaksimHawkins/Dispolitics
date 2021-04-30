package sg.skylvsme.dispolitics.entity;

import lombok.Getter;

@Getter
public class City {

    private final String name;
    private int health;
    private int economyAmplifier;



    public City(String name) {
        this.name = name;
        this.health = 100;
    }

    public void attackCity(int damage) {
        this.health -= damage;
    }

}
