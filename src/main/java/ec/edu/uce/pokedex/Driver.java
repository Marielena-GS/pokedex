package ec.edu.uce.pokedex;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

class Driver {

    public void ejecutar() {
        int idPokemon = 4; // ID de Charmander

        JSONObject pokemonData = obtenerPokemon(idPokemon);

        if (pokemonData != null) {
            JSONObject speciesData = obtenerDatosDeUrl(pokemonData.getJSONObject("species").getString("url"));

            System.out.println("Información del Pokémon:");
            System.out.println("ID: " + pokemonData.getInt("id"));
            System.out.println("Nombre: " + pokemonData.getString("name"));
            System.out.println("Peso: " + pokemonData.getInt("weight"));
            System.out.println("Especie: " + speciesData.getString("name"));

            // Habilidades
            JSONArray abilities = pokemonData.getJSONArray("abilities");
            List<String> habilidades = new ArrayList<>();
            for (int i = 0; i < abilities.length(); i++) {
                JSONObject ability = abilities.getJSONObject(i).getJSONObject("ability");
                int abilityId = extraerId(ability.getString("url"));
                habilidades.add(ability.getString("name") + " (ID: " + abilityId + ")");
            }
            System.out.println("Habilidades: " + habilidades);

            // Movimientos (máximo 5)
            JSONArray moves = pokemonData.getJSONArray("moves");
            List<String> movimientos = new ArrayList<>();
            for (int i = 0; i < Math.min(moves.length(), 5); i++) {
                JSONObject move = moves.getJSONObject(i).getJSONObject("move");
                int moveId = extraerId(move.getString("url"));
                movimientos.add(move.getString("name") + " (ID: " + moveId + ")");
            }
            System.out.println("Movimientos: " + movimientos);

            // Región y hábitat
            String habitat = speciesData.has("habitat") ? speciesData.getJSONObject("habitat").getString("name") : "Desconocido";
            String region = speciesData.has("generation") ? speciesData.getJSONObject("generation").getString("name") : "Desconocido";
            System.out.println("Hábitat: " + habitat);
            System.out.println("Región: " + region);

            // Cadena de evolución
            int evolucionChainId = obtenerEvolucionChainId(speciesData);
            if (evolucionChainId != -1) {
                JSONObject evolucionData = obtenerCadenaEvolucion(evolucionChainId);
                List<String> evoluciones = obtenerEvoluciones(evolucionData.getJSONObject("chain"));
                System.out.println("Evoluciones: " + evoluciones);
            } else {
                System.out.println("No se encontró información sobre la cadena de evolución.");
            }
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

    public int obtenerEvolucionChainId(JSONObject speciesData) {
        try {
            String evolutionChainUrl = speciesData.getJSONObject("evolution_chain").getString("url");
            return extraerId(evolutionChainUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public JSONObject obtenerCadenaEvolucion(int evolucionChainId) {
        return obtenerDatosDeUrl("https://pokeapi.co/api/v2/evolution-chain/" + evolucionChainId);
    }

    public List<String> obtenerEvoluciones(JSONObject evolutionChain) {
        List<String> evoluciones = new ArrayList<>();
        extraerEvoluciones(evolutionChain, evoluciones);
        return evoluciones;
    }

    private void extraerEvoluciones(JSONObject evolutionData, List<String> evoluciones) {
        String nombre = evolutionData.getJSONObject("species").getString("name");
        int id = extraerId(evolutionData.getJSONObject("species").getString("url"));
        evoluciones.add(nombre + " (ID: " + id + ")");

        JSONArray evolvesTo = evolutionData.getJSONArray("evolves_to");
        for (int i = 0; i < evolvesTo.length(); i++) {
            extraerEvoluciones(evolvesTo.getJSONObject(i), evoluciones);
        }
    }

    private int extraerId(String url) {
        String[] partes = url.split("/");
        return Integer.parseInt(partes[partes.length - 1]);
    }
}
