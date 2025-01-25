package ec.edu.uce.pokedex.DataCharge;

import ec.edu.uce.pokedex.jpa.Pokemon;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DriverPokemon {

    private final RestTemplate restTemplate;

    public DriverPokemon(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void ejecutar() {
        String allPokemonUrl = "https://pokeapi.co/api/v2/pokemon?limit=1304";
        Pokemon nuevoPokemon = new Pokemon();
        // Obtener la lista de todos los Pokémon
        JSONObject allPokemonData = obtenerDatosDeUrl(allPokemonUrl);

        if (allPokemonData != null && allPokemonData.has("results")) {
            JSONArray pokemonList = allPokemonData.getJSONArray("results");

            // Procesar cada Pokémon en paralelo
            pokemonList.toList().parallelStream().forEach(pokemonObj -> {
                JSONObject pokemon = new JSONObject((Map<?, ?>) pokemonObj);
                String pokemonUrl = pokemon.getString("url");

                // Obtener los datos del Pokémon específico
                JSONObject pokemonData = obtenerDatosDeUrl(pokemonUrl);
                if (pokemonData == null) return;

                // Obtener datos básicos del Pokémon
                nuevoPokemon.setId(pokemonData.getInt("id"));
                nuevoPokemon.setName(pokemonData.getString("name"));
                nuevoPokemon.setHeight(pokemonData.getInt("height"));
                nuevoPokemon.setWeight(pokemonData.getInt("weight"));

                //int pokemonId = pokemonData.getInt("id");
                //String pokemonName = pokemonData.getString("name");
                //int height = pokemonData.getInt("height");
                //int weight = pokemonData.getInt("weight");

                // Inicialización de sets y mapas
                Set<Integer> regionIds = new LinkedHashSet<>();
                Set<Integer> typeIds = new LinkedHashSet<>();
                Set<Integer> locationIds = new LinkedHashSet<>();
                Set<Integer> moveIds = new LinkedHashSet<>();
                Set<Integer> abilityIds = new LinkedHashSet<>();
                Integer habitatId = null;

                // Variables para los stats del Pokémon
                int hp = 0;
                int attack = 0;
                int defense = 0;
                int specialAttack = 0;
                int specialDefense = 0;
                int speed = 0;

                // Obtener los tipos del Pokémon
                if (pokemonData.has("types")) {
                    typeIds.addAll(pokemonData.getJSONArray("types").toList().stream()
                            .map(typeObj -> {
                                JSONObject typeInfo = new JSONObject((Map<?, ?>) typeObj).getJSONObject("type");
                                return extraerIdDesdeUrl(typeInfo.getString("url"));
                            })
                            .collect(Collectors.toSet()));
                }

                // Obtener el ID del hábitat
                if (pokemonData.has("species")) {
                    JSONObject species = pokemonData.getJSONObject("species");
                    String speciesUrl = species.getString("url");
                    JSONObject speciesData = obtenerDatosDeUrl(speciesUrl);
                    habitatId = obtenerHabitatId(speciesData);
                }

                // Obtener los IDs de las ubicaciones
                if (pokemonData.has("location_area_encounters")) {
                    String locationAreaUrl = pokemonData.getString("location_area_encounters");
                    JSONArray locationAreaData = obtenerDatosDeEncuentros(locationAreaUrl);

                    if (locationAreaData != null) {
                        locationIds.addAll(locationAreaData.toList().stream()
                                .flatMap(encounterObj -> {
                                    JSONObject encounter = new JSONObject((Map<?, ?>) encounterObj);
                                    JSONObject locationArea = encounter.getJSONObject("location_area");
                                    String locationAreaUrlFromEncounter = locationArea.getString("url");
                                    JSONObject locationAreaDataResponse = obtenerDatosDeUrl(locationAreaUrlFromEncounter);

                                    if (locationAreaDataResponse != null && locationAreaDataResponse.has("location")) {
                                        JSONObject location = locationAreaDataResponse.getJSONObject("location");
                                        String locationUrl = location.getString("url");
                                        JSONObject locationData = obtenerDatosDeUrl(locationUrl);

                                        if (locationData != null && locationData.has("region")) {
                                            JSONObject region = locationData.getJSONObject("region");
                                            regionIds.add(extraerIdDesdeUrl(region.getString("url")));
                                        }
                                    }
                                    return locationIds.stream();
                                }).collect(Collectors.toSet()));
                    }
                }

                // Obtener los IDs de los movimientos
                if (pokemonData.has("moves")) {
                    moveIds.addAll(pokemonData.getJSONArray("moves").toList().stream()
                            .map(moveObj -> {
                                JSONObject moveInfo = new JSONObject((Map<?, ?>) moveObj).getJSONObject("move");
                                return extraerIdDesdeUrl(moveInfo.getString("url"));
                            })
                            .collect(Collectors.toSet()));
                }

                // Obtener los IDs de las habilidades
                if (pokemonData.has("abilities")) {
                    abilityIds.addAll(pokemonData.getJSONArray("abilities").toList().stream()
                            .map(abilityObj -> {
                                JSONObject abilityInfo = new JSONObject((Map<?, ?>) abilityObj).getJSONObject("ability");
                                return extraerIdDesdeUrl(abilityInfo.getString("url"));
                            })
                            .collect(Collectors.toSet()));
                }

                // Obtener los stats del Pokémon y asignarlos a variables
                if (pokemonData.has("stats")) {
                    JSONArray statsArray = pokemonData.getJSONArray("stats");
                    for (int i = 0; i < statsArray.length(); i++) {
                        JSONObject statObj = statsArray.getJSONObject(i);
                        String statName = statObj.getJSONObject("stat").getString("name");
                        int statValue = statObj.getInt("base_stat");

                        switch (statName) {
                            case "hp":
                                nuevoPokemon.setStats_hp(statValue);
                                break;
                            case "attack":
                                nuevoPokemon.setStats_attack(statValue);
                                break;
                            case "defense":
                                nuevoPokemon.setStats_defense(statValue);
                                break;
                            case "special-attack":
                                nuevoPokemon.setStats_special_attack(statValue);
                                break;
                            case "special-defense":
                                nuevoPokemon.setStats_special_defense(statValue);
                                break;
                            case "speed":
                                nuevoPokemon.setStats_speed(statValue);
                                break;
                            default:
                                break;
                        }
                    }
                }

                // Imprimir la información del Pokémon
                imprimirInformacionPokemon(nuevoPokemon.getId(), nuevoPokemon.getName(),
                        nuevoPokemon.getHeight(), nuevoPokemon.getWeight(), regionIds, typeIds, habitatId, locationIds, moveIds, abilityIds,
                        nuevoPokemon.getStats_hp(), nuevoPokemon.getStats_attack(), nuevoPokemon.getStats_defense(), nuevoPokemon.getStats_special_attack()
                        , nuevoPokemon.getStats_special_defense(), nuevoPokemon.getStats_speed());
            });
        } else {
            System.out.println("No se pudieron obtener los datos de los Pokémon.");
        }
    }

    private void imprimirInformacionPokemon(int id, String name, int height, int weight, Set<Integer> regionIds, Set<Integer> typeIds, Integer habitatId, Set<Integer> locationIds, Set<Integer> moveIds, Set<Integer> abilityIds,
                                            double hp, double attack, double defense, double specialAttack, double specialDefense, double speed) {
        System.out.println("Información básica del Pokémon:");
        System.out.println("ID: " + id);
        System.out.println("Nombre: " + name);
        System.out.println("Altura: " + height);
        System.out.println("Peso: " + weight);

        System.out.print("IDs de las regiones: ");
        System.out.println(regionIds.isEmpty() ? "No se encontraron." : regionIds);

        System.out.print("IDs de los tipos: ");
        System.out.println(typeIds.isEmpty() ? "No se encontraron." : typeIds);

        System.out.println("ID del hábitat: " + (habitatId != null ? habitatId : "No se encontró."));

        System.out.print("IDs de las ubicaciones: ");
        System.out.println(locationIds.isEmpty() ? "No se encontraron." : locationIds);

        System.out.print("IDs de los movimientos: ");
        System.out.println(moveIds.isEmpty() ? "No se encontraron." : moveIds);

        System.out.print("IDs de las habilidades: ");
        System.out.println(abilityIds.isEmpty() ? "No se encontraron." : abilityIds);

        // Imprimir los stats de forma más detallada
        System.out.println("Stats:");
        System.out.println("HP: " + hp);
        System.out.println("Ataque: " + attack);
        System.out.println("Defensa: " + defense);
        System.out.println("Ataque especial: " + specialAttack);
        System.out.println("Defensa especial: " + specialDefense);
        System.out.println("Velocidad: " + speed);

        System.out.println("-----------------------------------");
    }


    private JSONObject obtenerDatosDeUrl(String url) {
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            return new JSONObject(jsonResponse);
        } catch (Exception e) {
            System.err.println("Error al obtener datos desde URL: " + url + " - " + e.getMessage());
            return null;
        }
    }

    private JSONArray obtenerDatosDeEncuentros(String url) {
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            return new JSONArray(jsonResponse);
        } catch (Exception e) {
            System.err.println("Error al obtener datos de encuentros desde URL: " + url + " - " + e.getMessage());
            return null;
        }
    }

    private int extraerIdDesdeUrl(String url) {
        String[] partes = url.split("/");
        return Integer.parseInt(partes[partes.length - 1]);
    }

    private Integer obtenerHabitatId(JSONObject speciesData) {
        if (speciesData != null && speciesData.has("habitat") && !speciesData.isNull("habitat")) {
            JSONObject habitat = speciesData.getJSONObject("habitat");
            if (habitat.has("url")) {
                return extraerIdDesdeUrl(habitat.getString("url"));
            }
        }
        return null;
    }

    private void imprimirInformacionPokemon(int id, String name, int height, int weight, Set<Integer> regionIds, Set<Integer> typeIds, Integer habitatId, Set<Integer> locationIds, Set<Integer> moveIds, Set<Integer> abilityIds, Map<String, Integer> stats) {
        System.out.println("Información básica del Pokémon:");
        System.out.println("ID: " + id);
        System.out.println("Nombre: " + name);
        System.out.println("Altura: " + height);
        System.out.println("Peso: " + weight);

        System.out.print("IDs de las regiones: ");
        System.out.println(regionIds.isEmpty() ? "No se encontraron." : regionIds);

        System.out.print("IDs de los tipos: ");
        System.out.println(typeIds.isEmpty() ? "No se encontraron." : typeIds);

        System.out.println("ID del hábitat: " + (habitatId != null ? habitatId : "No se encontró."));

        System.out.print("IDs de las ubicaciones: ");
        System.out.println(locationIds.isEmpty() ? "No se encontraron." : locationIds);

        System.out.print("IDs de los movimientos: ");
        System.out.println(moveIds.isEmpty() ? "No se encontraron." : moveIds);

        System.out.print("IDs de las habilidades: ");
        System.out.println(abilityIds.isEmpty() ? "No se encontraron." : abilityIds);

        System.out.println("Stats:");
        stats.forEach((key, value) -> System.out.println(key + ": " + value));

        System.out.println("-----------------------------------");
    }
}
