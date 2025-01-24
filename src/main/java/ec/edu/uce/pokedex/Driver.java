package ec.edu.uce.pokedex;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

class Driver {

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

    public void ejecutar() {
        int idPokemon = 4; // ID de Charmander

        JSONObject pokemonData = obtenerPokemon(idPokemon);

        if (pokemonData != null) {
            // Información básica
            this.id = pokemonData.getInt("id");
            this.name = pokemonData.getString("name");
            this.height = pokemonData.getInt("height");
            this.weight = pokemonData.getInt("weight");

            // Estadísticas
            JSONArray stats = pokemonData.getJSONArray("stats");
            for (int i = 0; i < stats.length(); i++) {
                JSONObject stat = stats.getJSONObject(i).getJSONObject("stat");
                int baseStat = stats.getJSONObject(i).getInt("base_stat");
                String statName = stat.getString("name");

                if (statName.equals("hp")) {
                    stats_hp = baseStat;
                } else if (statName.equals("attack")) {
                    stats_attack = baseStat;
                } else if (statName.equals("defense")) {
                    stats_defense = baseStat;
                } else if (statName.equals("special-attack")) {
                    stats_special_attack = baseStat;
                } else if (statName.equals("special-defense")) {
                    stats_special_defense = baseStat;
                } else if (statName.equals("speed")) {
                    stats_speed = baseStat;
                } else if (statName.equals("accuracy")) {
                    stats_accuracy = baseStat;
                } else if (statName.equals("evasion")) {
                    stats_evasion = baseStat;
                }
            }

            // Imprimir información
            System.out.println("Información del Pokémon:");
            System.out.println("ID: " + this.id);
            System.out.println("Nombre: " + this.name);
            System.out.println("Altura: " + this.height);
            System.out.println("Peso: " + this.weight);
            System.out.println("HP: " + this.stats_hp);
            System.out.println("Ataque: " + this.stats_attack);
            System.out.println("Defensa: " + this.stats_defense);
            System.out.println("Ataque Especial: " + this.stats_special_attack);
            System.out.println("Defensa Especial: " + this.stats_special_defense);
            System.out.println("Velocidad: " + this.stats_speed);
            System.out.println("Precisión: " + this.stats_accuracy);
            System.out.println("Evasión: " + this.stats_evasion);
        } else {
            System.out.println("No se pudo obtener información del Pokémon.");
        }
    }

    public JSONObject obtenerPokemon(int id) {
        return obtenerDatosDeUrl("https://pokeapi.co/api/v2/pokemon/" + id);
    }

    public JSONObject obtenerDatosDeUrl(String url) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(request)) {
                HttpEntity entity = response.getEntity();
                String jsonResponse = EntityUtils.toString(entity);
                return new JSONObject(jsonResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
