package ec.edu.uce.pokedex.DataCharge;

import ec.edu.uce.pokedex.Observer.CargaDatosListener;
import ec.edu.uce.pokedex.jpa.*;
import ec.edu.uce.pokedex.repositories.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class DriverPokemon {

    private final RestTemplate restTemplate;
    private final ExecutorService executorService;
    @Autowired
    private PokemonRepository pokemonRepository;
    @Autowired
    private TypesRepository typesRepository;
    @Autowired
    HabitatRepository habitatRepository;
    @Autowired
    RegionRepository regionRepository;
    @Autowired
    MoveRepository moveRepository;
    @Autowired
    AbilitiesRepository abilitiesRepository;

    private CargaDatosListener cargaDatosMoveListener; // Observer
    // Configurar el Listener
    public void setCargaDatosListener(CargaDatosListener listener) {
        this.cargaDatosMoveListener = listener;
    }

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
                    List<Integer> evolutionIds = new ArrayList<>();

                    // Obtener los tipos del Pokémon
                    if (pokemonData.has("types")) {
                        typeIds.addAll(pokemonData.getJSONArray("types").toList().stream()
                                .map(typeObj -> {
                                    JSONObject typeInfo = new JSONObject((Map<?, ?>) typeObj).getJSONObject("type");
                                    return extraerIdDesdeUrl(typeInfo.getString("url"));
                                })
                                .collect(Collectors.toSet()));
                    }

                    // Obtener el ID del hábitat y la cadena de evolución
                    if (pokemonData.has("species")) {
                        JSONObject species = pokemonData.getJSONObject("species");
                        String speciesUrl = species.getString("url");
                        JSONObject speciesData = obtenerDatosDeUrl(speciesUrl);

                        habitatId = obtenerHabitatId(speciesData);
                        evolutionIds = obtenerEvolutionIds(speciesData);
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
                    if (habitatId != null) {
                        Optional<Habitat> newHabitat = habitatRepository.findById(habitatId);
                        if (newHabitat.isPresent()) {
                            nuevoPokemon.setHabitat(newHabitat.get());
                        } else {
                            throw new RuntimeException("Habitat con ID " + habitatId + " no encontrado.");
                        }
                    }

                    List<Types> tiposList = new ArrayList<>();
                    for (Integer typeId : typeIds) {
                        Optional<Types> newTypes = typesRepository.findById(typeId);

                        // Verificar si el tipo existe antes de acceder a su valor
                        if (newTypes.isPresent()) {
                            tiposList.add(newTypes.get());
                        } else {
                            // Manejar el caso si no se encuentra el tipo
                            System.out.println("Tipo con ID " + typeId + " no encontrado.");
                        }
                    }
                    List<Region> regionesList = new ArrayList<>();
                    for (Integer regiones : regionIds) {
                        Optional<Region> newRegion = regionRepository.findById(regiones);
                        if (newRegion.isPresent()) {
                            regionesList.add(newRegion.get());
                        }
                    }

                    List<Move> movimientoList = new ArrayList<>();
                    for (Integer movimientos : moveIds) {
                        Optional<Move> newMove = moveRepository.findById(movimientos);
                        if (newMove.isPresent()) {
                            movimientoList.add(newMove.get());
                        }
                    }

                    List<Abilities> abilidadesList = new ArrayList<>();
                    for (Integer abilidades : abilityIds) {
                        Optional<Abilities> newAbilidades = abilitiesRepository.findById(abilidades);
                        if (newAbilidades.isPresent()) {
                            abilidadesList.add(newAbilidades.get());
                        }
                    }

                    nuevoPokemon.setAbilities(abilidadesList);
                    nuevoPokemon.setMoves(movimientoList);
                    nuevoPokemon.setRegions(regionesList);
                    nuevoPokemon.setTypes(tiposList);
                    nuevoPokemon.setEnvoles(evolutionIds);
                    pokemonRepository.save(nuevoPokemon);
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
            // Cerrar el pool de hilos y esperar a que finalicen
            executorService.shutdown();
            try {
                if (executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    if (cargaDatosMoveListener != null) {
                        cargaDatosMoveListener.onCargaCompleta();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
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

    private List<Integer> obtenerEvolutionIds(JSONObject speciesData) {
        List<Integer> evolutionIds = new ArrayList<>();
        if (speciesData != null && speciesData.has("evolution_chain") && !speciesData.isNull("evolution_chain")) {
            String evolutionChainUrl = speciesData.getJSONObject("evolution_chain").getString("url");
            JSONObject evolutionChainData = obtenerDatosDeUrl(evolutionChainUrl);

            if (evolutionChainData != null && evolutionChainData.has("chain")) {
                JSONObject chain = evolutionChainData.getJSONObject("chain");
                extraerIdsDeEvolucion(chain, evolutionIds);
            }
        }
        return evolutionIds;
    }

    private void extraerIdsDeEvolucion(JSONObject chain, List<Integer> evolutionIds) {
        if (chain.has("species")) {
            JSONObject species = chain.getJSONObject("species");
            if (species.has("url")) {
                evolutionIds.add(extraerIdDesdeUrl(species.getString("url")));
            }
        }

        if (chain.has("evolves_to")) {
            JSONArray evolvesToArray = chain.getJSONArray("evolves_to");
            for (int i = 0; i < evolvesToArray.length(); i++) {
                extraerIdsDeEvolucion(evolvesToArray.getJSONObject(i), evolutionIds);
            }
        }
    }
}
