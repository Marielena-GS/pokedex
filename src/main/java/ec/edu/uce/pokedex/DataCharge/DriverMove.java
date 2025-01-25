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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DriverMove {

    private int id;
    private String name;

    public DriverMove(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public DriverMove() {
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
            moveList.stream().parallel()
                    .map(move->new Move(moveList.indexOf(move)+1, move.optString("name")))
                    .forEach(movimiento->System.out.println("ID: " + movimiento.getId() + " - Tipo: " + movimiento.getName()));

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
