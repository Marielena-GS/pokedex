package ec.edu.uce.pokedex.DataCharge;

import ec.edu.uce.pokedex.jpa.Locations;
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

public class DriveLocation {

        public void ejecutar() {
            // Consultar los lugares de aparición de Pokémon
            JSONObject locationsData = obtenerLocations();


            if (locationsData != null) {
                // Extraer y mostrar la información de los lugares
                JSONArray locations = locationsData.getJSONArray("results");
                List<JSONObject> locationList = Stream.iterate(0,i->i+1)
                        .limit(locations.length())
                        .map(locations::getJSONObject)
                        .collect(Collectors.toList());
                locationList.stream().parallel()
                        .map(locaciones ->new Locations(locationList.indexOf(locaciones)+1,locaciones.optString("name")))
                        .forEach(locacion -> System.out.println("ID: " + locacion.getId() + " - Tipo: " + locacion.getName()));

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

