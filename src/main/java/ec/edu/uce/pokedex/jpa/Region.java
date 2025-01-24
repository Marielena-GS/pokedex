package ec.edu.uce.pokedex.jpa;

public class Region {
    private int id;
    private String locations;
    private String name;

    public Region() { }

    public Region(int id, String locations, String name) {
        this.id = id;
        this.locations = locations;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
