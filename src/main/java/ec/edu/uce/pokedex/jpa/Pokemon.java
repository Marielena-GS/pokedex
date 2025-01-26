package ec.edu.uce.pokedex.jpa;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "Pokemon")
public class Pokemon {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "pokemon_name")
    private String name;

    @Column(name = "pokemon_heigth")
    private int height;

    @Column(name = "pokemon_weight")
    private int weight;

    @Column(name = "pokemon_stat_hp")
    private double stats_hp;

    @Column(name = "pokemon_stat_attack")
    private double stats_attack;

    @Column(name = "pokemon_stat_defense")
    private double stats_defense;

    @Column(name = "pokemon_stat_special_attack")
    private double stats_special_attack;

    @Column(name = "pokemon_special_defense")
    private double stats_special_defense;

    @Column(name = "pokemon_stat_speed")
    private double stats_speed;

    @Column(name = "pokemon_stat_accuracy")
    private double stats_accuracy;

    @Column(name = "pokemon_stat_evasion")
    private double stats_evasion;

    @ElementCollection
    @Column(name = "pokemon_envoles")
    private List<Integer> envoles;  // Aquí no es necesario el @ManyToMany, ya que es una lista simple de enteros.


    // Relación Many-to-Many con Types
    @ManyToMany
    @JoinTable(
            name = "pokemon_types",
            joinColumns = @JoinColumn(name = "pokemon_id"),
            inverseJoinColumns = @JoinColumn(name = "type_id")
    )
    private List<Types> types;  // Relación Many-to-Many con la entidad Types

    @ManyToOne
    @JoinColumn(name = "habitat_id") // Clave foránea
    private Habitat habitat;  // Relación Many-to-One con Habitat


    @ManyToMany
    @JoinTable(
            name = "pokemon_region",
            joinColumns = @JoinColumn(name = "pokemon_id"),
            inverseJoinColumns = @JoinColumn(name = "region_id")
    )
    private List<Region> regions; // Relación Many-to-Many con Region

    @ManyToMany
    @JoinTable(
            name = "pokemon_moves",
            joinColumns = @JoinColumn(name = "pokemon_id"),
            inverseJoinColumns = @JoinColumn(name = "move_id")
    )
    private List<Move> moves; // Relación Many-to-Many con Move

    @ManyToMany
    @JoinTable(
            name = "pokemon_abilities",
            joinColumns = @JoinColumn(name = "pokemon_id"),
            inverseJoinColumns = @JoinColumn(name = "ability_id")
    )
    private List<Abilities> abilities; // Relación Many-to-Many con Abilities

    public List<Abilities> getAbilities() {
        return abilities;
    }

    public void setAbilities(List<Abilities> abilities) {
        this.abilities = abilities;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public void setMoves(List<Move> moves) {
        this.moves = moves;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions = regions;
    }

    public Habitat getHabitat() {
        return habitat;
    }

    public void setHabitat(Habitat habitat) {
        this.habitat = habitat;
    }

    public List<Types> getTypes() {
        return types;
    }

    public void setTypes(List<Types> types) {
        this.types = types;
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

    public List<Integer> getEnvoles() {
        return envoles;
    }

    public void setEnvoles(List<Integer> envoles) {
        this.envoles = envoles;
    }

    public Pokemon() {
    }

    public Pokemon(int id, String name, int height, int weight, double stats_hp, double stats_attack, double stats_defense, double stats_special_attack, double stats_special_defense, double stats_speed, double stats_accuracy, double stats_evasion, List<Integer> envoles) {
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
        this.envoles = envoles;
    }
}
