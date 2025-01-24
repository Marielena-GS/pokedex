package ec.edu.uce.pokedex.DataCharge;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

    public class DriveLocation {

        private int id;
        private String name;

        public void ejecutar() {
            // Consultar los lugares de aparición de Pokémon
            JSONObject locationsData = obtenerLocations();

            if (locationsData != null) {
                // Extraer y mostrar la información de los lugares
                JSONArray locations = locationsData.getJSONArray("results");
                for (int i = 0; i < locations.length(); i++) {
                    JSONObject location = locations.getJSONObject(i);
                    this.id = i;  // Asignar un ID secuencial basado en el índice
                    this.name = location.getString("name");

                    // Imprimir la información del lugar
                    System.out.println("Location ID: " + this.id);
                    System.out.println("Nombre del lugar: " + this.name);
                }
            } else {
                System.out.println("No se pudo obtener información de los lugares.");
            }
        }

        public JSONObject obtenerLocations() {
            return obtenerDatosDeUrl("https://pokeapi.co/api/v2/location?limit=1039"); // Ajusta el límite si lo necesitas
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

