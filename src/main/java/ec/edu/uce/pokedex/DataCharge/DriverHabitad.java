package ec.edu.uce.pokedex.DataCharge;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class DriverHabitad {

    private int id;
    private String name;

    public void ejecutar() {
        // Consultar los hábitats de Pokémon
        JSONObject habitatData = obtenerHabitats();

        if (habitatData != null) {
            // Extraer y mostrar la información de los hábitats
            JSONArray habitats = habitatData.getJSONArray("results");
            for (int i = 0; i < habitats.length(); i++) {
                JSONObject habitat = habitats.getJSONObject(i);
                this.id = i;  // Asignar un ID secuencial basado en el índice
                this.name = habitat.getString("name");

                // Imprimir la información del hábitat
                System.out.println("Habitat ID: " + this.id);
                System.out.println("Nombre del hábitat: " + this.name);
            }
        } else {
            System.out.println("No se pudo obtener información de los hábitats.");
        }
    }

    public JSONObject obtenerHabitats() {
        return obtenerDatosDeUrl("https://pokeapi.co/api/v2/pokemon-habitat");
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
