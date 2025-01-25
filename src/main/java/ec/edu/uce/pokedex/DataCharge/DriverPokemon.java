package ec.edu.uce.pokedex.DataCharge;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashSet;
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
                String pokemonName = pokemonData.getString("name");
                int height = pokemonData.getInt("height");
                int weight = pokemonData.getInt("weight");

                // Obtener la URL de los encuentros
                String locationAreaUrl = pokemonData.getString("location_area_encounters");
                JSONArray locationAreaData = obtenerDatosDeEncuentros(locationAreaUrl);

                // Set para almacenar los IDs de las regiones sin duplicados
                Set<Integer> regionIds = new LinkedHashSet<>();
                Set<Integer> typeIds = new LinkedHashSet<>();

                // Obtener los tipos del Pokémon
                if (pokemonData.has("types")) {
                    JSONArray typesArray = pokemonData.getJSONArray("types");
                    for (int j = 0; j < typesArray.length(); j++) {
                        JSONObject typeInfo = typesArray.getJSONObject(j).getJSONObject("type");
                        String typeUrl = typeInfo.getString("url");

                        // Extraer el ID del tipo desde la URL
                        int typeId = extraerIdDesdeUrl(typeUrl);
                        typeIds.add(typeId);
                    }
                }

                if (locationAreaData != null) {
                    for (int j = 0; j < locationAreaData.length(); j++) {
                        JSONObject encounter = locationAreaData.getJSONObject(j);
                        JSONObject locationArea = encounter.getJSONObject("location_area");

                        // Obtener la URL de la ubicación
                        String locationAreaUrlFromEncounter = locationArea.getString("url");
                        JSONObject locationAreaDataResponse = obtenerDatosDeUrl(locationAreaUrlFromEncounter);

                        if (locationAreaDataResponse != null && locationAreaDataResponse.has("location")) {
                            JSONObject location = locationAreaDataResponse.getJSONObject("location");
                            String locationUrl = location.getString("url");

                            // Obtener datos de la ubicación
                            JSONObject locationData = obtenerDatosDeUrl(locationUrl);
                            if (locationData != null && locationData.has("region")) {
                                JSONObject region = locationData.getJSONObject("region");

                                // Obtener la URL de la región y sus datos
                                String regionUrl = region.getString("url");
                                JSONObject regionData = obtenerDatosDeUrl(regionUrl);

                                if (regionData != null) {
                                    int regionId = regionData.getInt("id");

                                    // Agregar el id al Set para evitar duplicados
                                    regionIds.add(regionId);
                                }
                            }
                        }
                    }
                }

                // Imprimir la información del Pokémon con sus regiones y tipos
                System.out.println("Información básica del Pokémon:");
                System.out.println("Nombre: " + pokemonName);
                System.out.println("Altura: " + height);
                System.out.println("Peso: " + weight);

                if (!regionIds.isEmpty()) {
                    System.out.print("IDs de las regiones: ");
                    for (int id : regionIds) {
                        System.out.print(id + " ");
                    }
                    System.out.println();
                } else {
                    System.out.println("IDs de las regiones: No se encontraron regiones.");
                }

                if (!typeIds.isEmpty()) {
                    System.out.print("IDs de los tipos: ");
                    for (int id : typeIds) {
                        System.out.print(id + " ");
                    }
                    System.out.println();
                } else {
                    System.out.println("IDs de los tipos: No se encontraron tipos.");
                }

                System.out.println("-----------------------------------"); // Separador entre Pokémon
            }
        } else {
            System.out.println("No se pudieron obtener los datos de los Pokémon.");
        }
    }

    private JSONObject obtenerDatosDeUrl(String url) {
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            return new JSONObject(jsonResponse); // Convierte la respuesta a JSONObject
        } catch (Exception e) {
            return null;
        }
    }

    private JSONArray obtenerDatosDeEncuentros(String url) {
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            return new JSONArray(jsonResponse); // Si la respuesta es un JSONArray directamente
        } catch (Exception e) {
            return null;
        }
    }

    private int extraerIdDesdeUrl(String url) {
        String[] partes = url.split("/");
        return Integer.parseInt(partes[partes.length - 1]);
    }
}
