package ec.edu.uce.pokedex.DataCharge;

import ec.edu.uce.pokedex.jpa.Types;
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

public class DriverTypes {

    private int id;
    private String name;


    public static void ejecutar() {
        JSONObject typesData = obtenerTipos();

        if (typesData != null) {
            JSONArray types = typesData.getJSONArray("results");

            // Convertir JSONArray a List<JSONObject> usando Stream
            List<JSONObject> typeList = Stream.iterate(0, i -> i + 1)
                    .limit(types.length())
                    .map(types::getJSONObject)
                    .collect(Collectors.toList());

            // Procesar en paralelo
            typeList.stream().parallel()
                    .map(type -> new Types(typeList.indexOf(type) + 1, type.optString("name")))
                    .forEach(tipo -> System.out.println("ID: " + tipo.getId() + " - Tipo: " + tipo.getName()));

        } else {
            System.out.println("No se pudo obtener información de los tipos.");
        }
    }

    public static JSONObject obtenerTipos() {
        return obtenerDatosDeUrl("https://pokeapi.co/api/v2/type");
    }

    public static JSONObject obtenerDatosDeUrl(String url) {
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
