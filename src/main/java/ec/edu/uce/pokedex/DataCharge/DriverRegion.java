package ec.edu.uce.pokedex.DataCharge;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class DriverRegion {

    private int id;
    private String name;

    public void ejecutar() {
        // Consultar los movimientos de Pokémon
        JSONObject movesData = obtenerMoves();

        if (movesData != null) {
            // Extraer y mostrar la información de los movimientos
            JSONArray moves = movesData.getJSONArray("results");
            for (int i = 0; i < moves.length(); i++) {
                JSONObject move = moves.getJSONObject(i);
                this.id = i;  // Asignar un ID secuencial basado en el índice
                this.name = move.getString("name");

                // Imprimir la información del movimiento
                System.out.println("Move ID: " + this.id);
                System.out.println("Region: " + this.name);
            }
        } else {
            System.out.println("No se pudo obtener información de los movimientos.");
        }
    }

    public JSONObject obtenerMoves() {
        return obtenerDatosDeUrl("https://pokeapi.co/api/v2/region");
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
