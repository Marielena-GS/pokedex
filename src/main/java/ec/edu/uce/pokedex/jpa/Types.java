package ec.edu.uce.pokedex.jpa;

import jakarta.persistence.*;

@Entity
@Table (name = "Types")
@Access(AccessType.FIELD)
public class Types {

    @Id @Column (name = "id")
    private int id;
    @Column (name = "name_types")
    private String name;

    public Types() { }

    public Types(int id, String name) {
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
