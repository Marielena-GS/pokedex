package ec.edu.uce.pokedex.jpa;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table (name = "Region")
public class Region {

    @Id @Column (name = "id")
    private int id;
    @Column (name = "name_region")
    private String name;

    @ManyToMany(mappedBy = "regions")
    private List<Pokemon> pokemones; // Relación Many-to-Many con Pokemon

    public List<Pokemon> getPokemones() {
        return pokemones;
    }

    public void setPokemones(List<Pokemon> pokemones) {
        this.pokemones = pokemones;
    }

    public Region() { }

    public Region(int id, String name) {
        this.id = id;
        this.name = name;
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
}
