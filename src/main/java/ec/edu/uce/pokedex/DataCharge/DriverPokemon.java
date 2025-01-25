package ec.edu.uce.pokedex.DataCharge;

import ec.edu.uce.pokedex.jpa.Pokemon;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DriverPokemon {

    public void ejecutar() {
        // Obtenemos la lista de Pokémon
        JSONObject listOfPokemons = obtenerPokemonLista();

        if (listOfPokemons != null) {
            // Extraemos el array "results", que contiene los Pokémon
            JSONArray results = listOfPokemons.getJSONArray("results");

            // Iteramos sobre los resultados y obtenemos detalles de cada Pokémon
            for (int i = 0; i < results.length(); i++) {
                Pokemon pokemon = new Pokemon();
                JSONObject pokemonInfo = results.getJSONObject(i);
                String pokemonUrl = pokemonInfo.getString("url");

                // Obtener los detalles de cada Pokémon
                JSONObject pokemonData = obtenerDatosDeUrl(pokemonUrl);

                // Aquí procesas la información detallada del Pokémon
                if (pokemonData != null) {
                    // Información básica
                    pokemon.setId(pokemonData.getInt("id"));
                    pokemon.setName(pokemonData.getString("name"));
                    pokemon.setHeight(pokemonData.getInt("height"));
                    pokemon.setWeight(pokemonData.getInt("weight"));

                    // Estadísticas
                    JSONArray stats = pokemonData.getJSONArray("stats");

                    // Convertimos el array de stats a una lista utilizando Streams
                    List<JSONObject> statList = Stream.iterate(0, j -> j + 1)
                            .limit(stats.length())
                            .map(stats::getJSONObject)
                            .collect(Collectors.toList());
                    // Procesamos los stats y asignamos los valores correspondientes
                    statList.stream().parallel()
                            .map(stat -> {
                                String statName = stat.getJSONObject("stat").getString("name");
                                int baseStat = stat.getInt("base_stat");
                                return new StatInfo(statName, baseStat);
                            })
                            .forEach(statInfo -> {
                                switch (statInfo.getName()) {
                                    case "hp":
                                        pokemon.setStats_hp(statInfo.getBaseStat());
                                        break;
                                    case "attack":
                                        pokemon.setStats_attack(statInfo.getBaseStat());
                                        break;
                                    case "defense":
                                        pokemon.setStats_defense(statInfo.getBaseStat());
                                        break;
                                    case "special-attack":
                                        pokemon.setStats_special_attack(statInfo.getBaseStat());
                                        break;
                                    case "special-defense":
                                        pokemon.setStats_special_defense(statInfo.getBaseStat());
                                        break;
                                    case "speed":
                                        pokemon.setStats_speed(statInfo.getBaseStat());
                                        break;
                                    case "accuracy":
                                        pokemon.setStats_accuracy(statInfo.getBaseStat());
                                        break;
                                    case "evasion":
                                        pokemon.setStats_evasion(statInfo.getBaseStat());
                                        break;
                                }
                            });

                    // Obtener los tipos de Pokémon
                    JSONArray types = pokemonData.getJSONArray("types");
                    StringBuilder typeIds = new StringBuilder();

                    // Recorrer el arreglo de tipos y hacer una solicitud adicional para obtener el ID
                    for (int j = 0; j < types.length(); j++) {
                        JSONObject typeInfo = types.getJSONObject(j);
                        String typeUrl = typeInfo.getJSONObject("type").getString("url");

                        // Obtener los detalles del tipo a partir de la URL
                        JSONObject typeData = obtenerDatosDeUrl(typeUrl);

                        if (typeData != null && typeData.has("id")) {
                            int typeId = typeData.getInt("id");
                            typeIds.append(typeId).append(" "); // Concatenamos los IDs de los tipos
                        } else {
                            System.out.println("ID no encontrado para el tipo con URL: " + typeUrl);
                        }
                    }

                    // Imprimir información del Pokémon
                    System.out.println("Información del Pokémon:");
                    System.out.println("ID: " + pokemon.getId());
                    System.out.println("Nombre: " + pokemon.getName());
                    System.out.println("Altura: " + pokemon.getHeight());
                    System.out.println("Peso: " + pokemon.getWeight());
                    System.out.println("HP: " + pokemon.getStats_hp());
                    System.out.println("Ataque: " + pokemon.getStats_attack());
                    System.out.println("Defensa: " + pokemon.getStats_defense());
                    System.out.println("Ataque Especial: " + pokemon.getStats_special_attack());
                    System.out.println("Defensa Especial: " + pokemon.getStats_special_defense());
                    System.out.println("Velocidad: " + pokemon.getStats_speed());
                    System.out.println("Precisión: " + pokemon.getStats_accuracy());
                    System.out.println("Evasión: " + pokemon.getStats_evasion());
                    System.out.println("Tipos (IDs): " + typeIds.toString().trim());  // Mostrar los IDs de los tipos
                }
            }
        } else {
            System.out.println("No se pudo obtener la lista de Pokémon.");
        }
    }

    // Clase auxiliar para representar cada estadística de Pokémon
    public static class StatInfo {
        private final String name;
        private final int baseStat;

        public StatInfo(String name, int baseStat) {
            this.name = name;
            this.baseStat = baseStat;
        }

        public String getName() {
            return name;
        }

        public int getBaseStat() {
            return baseStat;
        }
    }

    // Obtener la lista de Pokémon
    public JSONObject obtenerPokemonLista() {
        return obtenerDatosDeUrl("https://pokeapi.co/api/v2/pokemon?limit=1304");
    }

    // Obtener los detalles de un Pokémon a partir de la URL
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
