package ec.edu.uce.pokedex.DataCharge;

import ec.edu.uce.pokedex.jpa.Move;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DriverMove {
    private final ExecutorService executorService;

    public DriverMove(){
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public void ejecutar() {
        // Consultar los movimientos de Pokémon
        JSONObject movesData = obtenerMoves();

        if (movesData != null) {
            // Extraer y mostrar la información de los movimientos
            JSONArray moves = movesData.getJSONArray("results");
            List<JSONObject> moveList = Stream.iterate(0,i->i+1)
                    .limit(moves.length())
                    .map(moves::getJSONObject)
                    .collect(Collectors.toList());
            moveList.stream().parallel().forEach(mov -> executorService.execute(()->{
                Move newMove = new Move(moveList.indexOf(mov)+1,mov.optString("name"));
                        System.out.println("ID: " + newMove.getId() + " - Tipo: " + newMove.getName());
            }));
        } else {
            System.out.println("No se pudo obtener información de los movimientos.");
        }
    }

    public JSONObject obtenerMoves() {
        return obtenerDatosDeUrl("https://pokeapi.co/api/v2/move?limit=937");
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
