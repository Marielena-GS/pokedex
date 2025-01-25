package ec.edu.uce.pokedex.DataCharge;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

@Service
public class DriverPokemon {

    private final RestTemplate restTemplate;

    public DriverPokemon(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void ejecutar() {
        String allPokemonUrl = "https://pokeapi.co/api/v2/pokemon?limit=1304";

        // Obtener la lista de todos los Pokémon
        JSONObject allPokemonData = obtenerDatosDeUrl(allPokemonUrl);

        if (allPokemonData != null && allPokemonData.has("results")) {
            JSONArray pokemonList = allPokemonData.getJSONArray("results");

            // Procesar cada Pokémon
            for (int i = 0; i < pokemonList.length(); i++) {
                JSONObject pokemon = pokemonList.getJSONObject(i);
                String pokemonUrl = pokemon.getString("url");

                // Obtener los datos del Pokémon específico
                JSONObject pokemonData = obtenerDatosDeUrl(pokemonUrl);
                if (pokemonData == null) continue;

                // Obtener datos básicos del Pokémon
                int pokemonId = pokemonData.getInt("id");
                String pokemonName = pokemonData.getString("name");
                int height = pokemonData.getInt("height");
                int weight = pokemonData.getInt("weight");

                // Sets y mapas para almacenar información sin duplicados
                Set<Integer> regionIds = new LinkedHashSet<>();
                Set<Integer> typeIds = new LinkedHashSet<>();
                Set<Integer> locationIds = new LinkedHashSet<>();
                Set<Integer> moveIds = new LinkedHashSet<>();
                Set<Integer> abilityIds = new LinkedHashSet<>();
                Map<String, Integer> stats = new LinkedHashMap<>(); // Mapa para los stats
                Integer habitatId = null;

                // Obtener los tipos del Pokémon
                if (pokemonData.has("types")) {
                    JSONArray typesArray = pokemonData.getJSONArray("types");
                    for (int j = 0; j < typesArray.length(); j++) {
                        JSONObject typeInfo = typesArray.getJSONObject(j).getJSONObject("type");
                        int typeId = extraerIdDesdeUrl(typeInfo.getString("url"));
                        typeIds.add(typeId);
                    }
                }

                // Obtener el ID del hábitat
                if (pokemonData.has("species")) {
                    JSONObject species = pokemonData.getJSONObject("species");
                    String speciesUrl = species.getString("url");

                    JSONObject speciesData = obtenerDatosDeUrl(speciesUrl);
                    if (speciesData != null && speciesData.has("habitat")) {
                        habitatId = extraerIdDesdeUrl(speciesData.getJSONObject("habitat").getString("url"));
                    }
                }

                // Obtener los IDs de las ubicaciones
                if (pokemonData.has("location_area_encounters")) {
                    String locationAreaUrl = pokemonData.getString("location_area_encounters");
                    JSONArray locationAreaData = obtenerDatosDeEncuentros(locationAreaUrl);

                    if (locationAreaData != null) {
                        for (int j = 0; j < locationAreaData.length(); j++) {
                            JSONObject encounter = locationAreaData.getJSONObject(j);
                            JSONObject locationArea = encounter.getJSONObject("location_area");

                            String locationAreaUrlFromEncounter = locationArea.getString("url");
                            JSONObject locationAreaDataResponse = obtenerDatosDeUrl(locationAreaUrlFromEncounter);

                            if (locationAreaDataResponse != null && locationAreaDataResponse.has("location")) {
                                JSONObject location = locationAreaDataResponse.getJSONObject("location");
                                String locationUrl = location.getString("url");

                                JSONObject locationData = obtenerDatosDeUrl(locationUrl);
                                if (locationData != null && locationData.has("id")) {
                                    locationIds.add(locationData.getInt("id"));
                                }

                                if (locationData != null && locationData.has("region")) {
                                    JSONObject region = locationData.getJSONObject("region");
                                    int regionId = extraerIdDesdeUrl(region.getString("url"));
                                    regionIds.add(regionId);
                                }
                            }
                        }
                    }
                }

                // Obtener los IDs de los movimientos
                if (pokemonData.has("moves")) {
                    JSONArray movesArray = pokemonData.getJSONArray("moves");
                    for (int j = 0; j < movesArray.length(); j++) {
                        JSONObject moveInfo = movesArray.getJSONObject(j).getJSONObject("move");
                        int moveId = extraerIdDesdeUrl(moveInfo.getString("url"));
                        moveIds.add(moveId);
                    }
                }

                // Obtener los IDs de las habilidades
                if (pokemonData.has("abilities")) {
                    JSONArray abilitiesArray = pokemonData.getJSONArray("abilities");
                    for (int j = 0; j < abilitiesArray.length(); j++) {
                        JSONObject abilityInfo = abilitiesArray.getJSONObject(j).getJSONObject("ability");
                        int abilityId = extraerIdDesdeUrl(abilityInfo.getString("url"));
                        abilityIds.add(abilityId);
                    }
                }

                // Obtener los stats del Pokémon
                if (pokemonData.has("stats")) {
                    JSONArray statsArray = pokemonData.getJSONArray("stats");
                    for (int j = 0; j < statsArray.length(); j++) {
                        JSONObject statInfo = statsArray.getJSONObject(j);
                        String statName = statInfo.getJSONObject("stat").getString("name");
                        int baseStat = statInfo.getInt("base_stat");
                        stats.put(statName, baseStat);
                    }
                }

                // Imprimir la información del Pokémon
                System.out.println("Información básica del Pokémon:");
                System.out.println("ID: " + pokemonId);
                System.out.println("Nombre: " + pokemonName);
                System.out.println("Altura: " + height);
                System.out.println("Peso: " + weight);

                if (!regionIds.isEmpty()) {
                    System.out.print("IDs de las regiones: ");
                    imprimirSet(regionIds);
                } else {
                    System.out.println("IDs de las regiones: No se encontraron.");
                }

                if (!typeIds.isEmpty()) {
                    System.out.print("IDs de los tipos: ");
                    imprimirSet(typeIds);
                } else {
                    System.out.println("IDs de los tipos: No se encontraron.");
                }

                if (habitatId != null) {
                    System.out.println("ID del hábitat: " + habitatId);
                } else {
                    System.out.println("ID del hábitat: No se encontró.");
                }

                if (!locationIds.isEmpty()) {
                    System.out.print("IDs de las ubicaciones: ");
                    imprimirSet(locationIds);
                } else {
                    System.out.println("IDs de las ubicaciones: No se encontraron.");
                }

                if (!moveIds.isEmpty()) {
                    System.out.print("IDs de los movimientos: ");
                    imprimirSet(moveIds);
                } else {
                    System.out.println("IDs de los movimientos: No se encontraron.");
                }

                if (!abilityIds.isEmpty()) {
                    System.out.print("IDs de las habilidades: ");
                    imprimirSet(abilityIds);
                } else {
                    System.out.println("IDs de las habilidades: No se encontraron.");
                }

                if (!stats.isEmpty()) {
                    System.out.println("Stats:");
                    for (Map.Entry<String, Integer> entry : stats.entrySet()) {
                        System.out.println(entry.getKey() + ": " + entry.getValue());
                    }
                } else {
                    System.out.println("Stats: No se encontraron.");
                }

                System.out.println("-----------------------------------");
            }
        } else {
            System.out.println("No se pudieron obtener los datos de los Pokémon.");
        }
    }

    private JSONObject obtenerDatosDeUrl(String url) {
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            return new JSONObject(jsonResponse);
        } catch (Exception e) {
            return null;
        }
    }

    private JSONArray obtenerDatosDeEncuentros(String url) {
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            return new JSONArray(jsonResponse);
        } catch (Exception e) {
            return null;
        }
    }

    private int extraerIdDesdeUrl(String url) {
        String[] partes = url.split("/");
        return Integer.parseInt(partes[partes.length - 1]);
    }

    private void imprimirSet(Set<Integer> set) {
        for (int id : set) {
            System.out.print(id + " ");
        }
        System.out.println();
    }
}
