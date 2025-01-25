package ec.edu.uce.pokedex.DataCharge;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class DriverPokemon {

    private final RestTemplate restTemplate;

    public DriverPokemon(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void ejecutar() {
        int pokemonId = 25; // Cambia el ID si es necesario
        String pokemonUrl = "https://pokeapi.co/api/v2/pokemon/" + pokemonId + "/";

        // Obtener los datos básicos del Pokémon
        JSONObject pokemonData = obtenerDatosDeUrl(pokemonUrl);

        if (pokemonData != null) {
            String pokemonName = pokemonData.getString("name");
            int height = pokemonData.getInt("height");
            int weight = pokemonData.getInt("weight");

            System.out.println("Información básica del Pokémon:");
            System.out.println("Nombre: " + pokemonName);
            System.out.println("Altura: " + height);
            System.out.println("Peso: " + weight);

            // Obtener la URL de los encuentros
            String locationAreaUrl = pokemonData.getString("location_area_encounters");
            System.out.println("URL de los encuentros: " + locationAreaUrl);

            // Realizar la solicitud para obtener los datos de los encuentros
            JSONArray locationAreaData = obtenerDatosDeEncuentros(locationAreaUrl);

            if (locationAreaData != null) {
                // Procesar los encuentros
                for (int i = 0; i < locationAreaData.length(); i++) {
                    JSONObject encounter = locationAreaData.getJSONObject(i);
                    JSONObject locationArea = encounter.getJSONObject("location_area");

                    // Obtener la URL de la ubicación
                    String locationAreaUrlFromEncounter = locationArea.getString("url");
                    System.out.println("URL de location-area: " + locationAreaUrlFromEncounter);

                    // Realizar la consulta de la location-area
                    JSONObject locationAreaDataResponse = obtenerDatosDeUrl(locationAreaUrlFromEncounter);

                    if (locationAreaDataResponse != null) {
                        // Verificar si la respuesta es un objeto JSON y proceder
                        if (locationAreaDataResponse.has("location")) {
                            JSONObject location = locationAreaDataResponse.getJSONObject("location");
                            String locationUrl = location.getString("url");
                            System.out.println("URL de location: " + locationUrl);

                            // Realizar la consulta de la location
                            JSONObject locationData = obtenerDatosDeUrl(locationUrl);

                            if (locationData != null) {
                                // Verificar si la respuesta contiene la región
                                if (locationData.has("region")) {
                                    JSONObject region = locationData.getJSONObject("region");
                                    String regionName = region.getString("name");
                                    System.out.println("Región del Pokémon: " + regionName);

                                    // Obtener la URL de la región
                                    String regionUrl = region.getString("url");
                                    System.out.println("URL de la región: " + regionUrl);

                                    // Obtener los datos de la región
                                    JSONObject regionData = obtenerDatosDeUrl(regionUrl);

                                    if (regionData != null) {
                                        // Obtener el id de la región
                                        int regionId = regionData.getInt("id");
                                        System.out.println("ID de la región: " + regionId);
                                    } else {
                                        System.out.println("No se pudo obtener los datos de la región.");
                                    }
                                } else {
                                    System.out.println("No se encontró la región en location.");
                                }
                            }
                        } else {
                            System.out.println("No se encontró el campo 'location' en la respuesta de location-area.");
                        }
                    } else {
                        System.out.println("No se pudo obtener los datos de la URL de location-area.");
                    }
                }
            } else {
                System.out.println("No se pudieron obtener los datos de los encuentros.");
            }
        } else {
            System.out.println("No se pudieron obtener los datos del Pokémon.");
        }
    }

    private JSONObject obtenerDatosDeUrl(String url) {
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            return new JSONObject(jsonResponse); // Convierte la respuesta a JSONObject
        } catch (Exception e) {
            System.out.println("Error al obtener los datos de la URL: " + url);
            e.printStackTrace();
            return null;
        }
    }

    // Método que maneja la URL de los encuentros de location_area
    private JSONArray obtenerDatosDeEncuentros(String url) {
        try {
            String jsonResponse = restTemplate.getForObject(url, String.class);
            System.out.println("Respuesta cruda de los encuentros: " + jsonResponse); // Depuración

            // Intentar parsear como JSONObject primero
            try {
                JSONObject jsonObject = new JSONObject(jsonResponse);
                return jsonObject.getJSONArray("results"); // Devolver los resultados de los encuentros
            } catch (org.json.JSONException e) {
                // Si no es un JSONObject válido, podría ser un JSONArray
                try {
                    return new JSONArray(jsonResponse); // Si la respuesta es un JSONArray directamente
                } catch (org.json.JSONException ex) {
                    System.out.println("Error al parsear como JSONArray: " + ex.getMessage());
                    return null;
                }
            }
        } catch (Exception e) {
            System.out.println("Error al obtener los datos de los encuentros de la URL: " + url);
            e.printStackTrace();
            return null;
        }
    }
}
