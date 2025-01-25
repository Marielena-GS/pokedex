package ec.edu.uce.pokedex.DataCharge;

import ec.edu.uce.pokedex.jpa.Move;
import ec.edu.uce.pokedex.jpa.Region;
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

public class DriverRegion {
    private final ExecutorService executor;

    public DriverRegion(){
        this.executor = Executors.newFixedThreadPool(10);
    }

    public void ejecutar() {
        // Consultar los movimientos de Pokémon
        JSONObject regionData = obtenerRegion();

        if (regionData != null) {
            // Extraer y mostrar la información de los movimientos
            JSONArray region = regionData.getJSONArray("results");
            List<JSONObject> regionList = Stream.iterate(0,i->i+1)
                    .limit(region.length())
                    .map(region::getJSONObject)
                    .collect(Collectors.toList());

            // Procesar en paralelo
            regionList.stream().parallel().forEach(regions -> executor.execute(()->{
                        Region newRegion = new Region(regionList.indexOf(regions) + 1,regions.optString("name"));
                        System.out.println("ID: " + newRegion.getId() + " - Tipo: " + newRegion.getName());
                    }));

        } else {
            System.out.println("No se pudo obtener información de las regiones.");
        }
    }

    public JSONObject obtenerRegion() {
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
