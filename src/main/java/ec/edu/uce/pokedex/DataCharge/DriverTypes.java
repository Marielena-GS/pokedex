package ec.edu.uce.pokedex.DataCharge;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class DriverTypes {

        private int id;
        private String name;

        public void ejecutar() {
            // Consultar los tipos de Pokémon
            JSONObject typesData = obtenerTipos();

            if (typesData != null) {
                // Extraer y mostrar la información de los tipos
                JSONArray types = typesData.getJSONArray("results");
                for (int i = 0; i < types.length(); i++) {
                    JSONObject type = types.getJSONObject(i);
                    this.id = i + 1;  // Asignar un ID secuencial basado en el índice
                    this.name = type.getString("name");

                    // Imprimir la información del tipo
                    System.out.println("Tipo ID: " + this.id);
                    System.out.println("Nombre del tipo: " + this.name);
                }
            } else {
                System.out.println("No se pudo obtener información de los tipos.");
            }
        }

        public JSONObject obtenerTipos() {
            return obtenerDatosDeUrl("https://pokeapi.co/api/v2/type");
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
