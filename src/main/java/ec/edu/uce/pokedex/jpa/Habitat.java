package ec.edu.uce.pokedex.jpa;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table (name = "Habitat")
public class Habitat {

    @Id @Column (name = "id")
    private int id;
    @Column (name = "name_habitat")
    private String name;

   @OneToMany(mappedBy = "habitat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pokemon> pokemons;  // Un hábitat tiene varios Pokémon

    public List<Pokemon> getPokemons() {
        return pokemons;
    }

    public void setPokemons(List<Pokemon> pokemons) {
        this.pokemons = pokemons;
    }

    public Habitat() { }

    public Habitat(int id, String name) {
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
