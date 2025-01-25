package ec.edu.uce.pokedex.DataCharge;

import ec.edu.uce.pokedex.jpa.Pokemon;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class DriverPokemon {

    private final RestTemplate restTemplate;
    private final ExecutorService executorService;
    private int pokemonCount = 0;  // Contador de Pokémon procesados

    public DriverPokemon(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        // Crear un ExecutorService con un número fijo de hilos
        this.executorService = Executors.newFixedThreadPool(10); // Puedes ajustar el tamaño del pool de hilos
    }

    public void ejecutar() {
        String allPokemonUrl = "https://pokeapi.co/api/v2/pokemon?limit=1304";

        // Obtener la lista de todos los Pokémon
        JSONObject allPokemonData = obtenerDatosDeUrl(allPokemonUrl);

        if (allPokemonData != null && allPokemonData.has("results")) {
            JSONArray pokemonList = allPokemonData.getJSONArray("results");

            // List to hold the tasks for concurrent execution
            List<Callable<Void>> tasks = new ArrayList<>();

            pokemonList.toList().forEach(pokemonObj -> {
                tasks.add(() -> {
                    JSONObject pokemon = new JSONObject((Map<?, ?>) pokemonObj);
                    String pokemonUrl = pokemon.getString("url");

                    // Obtener los datos del Pokémon específico
                    JSONObject pokemonData = obtenerDatosDeUrl(pokemonUrl);
                    if (pokemonData == null) return null;

                    int pokemonId = pokemonData.getInt("id");

                    // Filtro: Ignorar Pokémon con ID mayor a 1304
                    if (pokemonId > 1304) {
                        return null;
                    }

                    Pokemon nuevoPokemon = new Pokemon();
                    // Obtener datos básicos del Pokémon
                    nuevoPokemon.setId(pokemonData.getInt("id"));
                    nuevoPokemon.setName(pokemonData.getString("name"));
                    nuevoPokemon.setHeight(pokemonData.getInt("height"));
                    nuevoPokemon.setWeight(pokemonData.getInt("weight"));

                    // Inicialización de sets y mapas
                    Set<Integer> regionIds = new LinkedHashSet<>();
                    Set<Integer> typeIds = new LinkedHashSet<>();
                    Set<Integer> locationIds = new LinkedHashSet<>();
                    Set<Integer> moveIds = new LinkedHashSet<>();
                    Set<Integer> abilityIds = new LinkedHashSet<>();
                    Integer habitatId = null;

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

                    // Incrementar el contador
                    synchronized (this) {  // Sincronización para evitar problemas de concurrencia
                        pokemonCount++;
                    }
                    System.out.println("Pokémon procesado: " + pokemonCount);
                    return null;
                });
            });

            try {
                // Ejecutar las tareas de manera asíncrona
                List<Future<Void>> futures = executorService.invokeAll(tasks);
                // Esperar a que todas las tareas finalicen
                for (Future<Void> future : futures) {
                    future.get();
                }
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Error al ejecutar tareas en paralelo: " + e.getMessage());
            }
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
}
