package ec.edu.uce.pokedex.jpa;

public class Locations {
    private int id;
    private String name;

    public Locations() { }

    public Locations(int id, String name) {
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
