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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DriverTypes {
    private final ExecutorService executorService;

    public DriverTypes() {
        this.executorService = Executors.newFixedThreadPool(10);
    }
    public void ejecutar() {
        JSONObject typesData = obtenerTipos();

        if (typesData != null) {
            JSONArray types = typesData.getJSONArray("results");

            // Convertir JSONArray a List<JSONObject> usando Stream
            List<JSONObject> typeList = Stream.iterate(0, i -> i + 1)
                    .limit(types.length())
                    .map(types::getJSONObject)
                    .collect(Collectors.toList());

            // Procesar en paralelo
            typeList.stream().parallel().forEach(tipo->executorService.execute(()->{
                Types newType = new Types(typeList.indexOf(tipo) + 1,tipo.optString("name"));
                        System.out.println("ID: " + newType.getId() + " - Tipo: " + newType.getName());
                    }));

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
