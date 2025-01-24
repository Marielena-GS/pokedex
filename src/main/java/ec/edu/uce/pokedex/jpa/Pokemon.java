package ec.edu.uce.pokedex.jpa;

public class Pokemon {

    private int id;
    private String name;
    private int height;
    private int weight;
    private double stats_hp;
    private double stats_attack;
    private double stats_defense;
    private double stats_special_attack;
    private double stats_special_defense;
    private double stats_speed;
    private double stats_accuracy;
    private double stats_evasion;

    public Pokemon() { }

    public Pokemon(int id, String name, int height, int weight, double stats_hp, double stats_attack, double stats_defense, double stats_special_attack, double stats_special_defense, double stats_speed, double stats_accuracy, double stats_evasion) {
        this.id = id;
        this.name = name;
        this.height = height;
        this.weight = weight;
        this.stats_hp = stats_hp;
        this.stats_attack = stats_attack;
        this.stats_defense = stats_defense;
        this.stats_special_attack = stats_special_attack;
        this.stats_special_defense = stats_special_defense;
        this.stats_speed = stats_speed;
        this.stats_accuracy = stats_accuracy;
        this.stats_evasion = stats_evasion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public double getStats_hp() {
        return stats_hp;
    }

    public void setStats_hp(double stats_hp) {
        this.stats_hp = stats_hp;
    }

    public double getStats_attack() {
        return stats_attack;
    }

    public void setStats_attack(double stats_attack) {
        this.stats_attack = stats_attack;
    }

    public double getStats_defense() {
        return stats_defense;
    }

    public void setStats_defense(double stats_defense) {
        this.stats_defense = stats_defense;
    }

    public double getStats_special_attack() {
        return stats_special_attack;
    }

    public void setStats_special_attack(double stats_special_attack) {
        this.stats_special_attack = stats_special_attack;
    }

    public double getStats_special_defense() {
        return stats_special_defense;
    }

    public void setStats_special_defense(double stats_special_defense) {
        this.stats_special_defense = stats_special_defense;
    }

    public double getStats_speed() {
        return stats_speed;
    }

    public void setStats_speed(double stats_speed) {
        this.stats_speed = stats_speed;
    }

    public double getStats_accuracy() {
        return stats_accuracy;
    }

    public void setStats_accuracy(double stats_accuracy) {
        this.stats_accuracy = stats_accuracy;
    }

    public double getStats_evasion() {
        return stats_evasion;
    }

    public void setStats_evasion(double stats_evasion) {
        this.stats_evasion = stats_evasion;
    }
}
