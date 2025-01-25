package ec.edu.uce.pokedex.DataCharge;

import ec.edu.uce.pokedex.jpa.Abilities;
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

public class DriveAbility {

    private int id;
    private String name;

    public void ejecutar() {
        // Consultar los movimientos de Pokémon
        JSONObject abilityData = obtenerMoves();

        if (abilityData != null) {
            // Extraer y mostrar la información de los movimientos
            JSONArray ability = abilityData.getJSONArray("results");
            List<JSONObject> abilityList = Stream.iterate(0,i->i+1)
                    .limit(ability.length())
                    .map(ability::getJSONObject)
                    .collect(Collectors.toList());
            abilityList.stream().parallel()
                    .map(abilitys -> new Abilities(abilityList.indexOf(abilitys)+1,abilitys.optString("name")))
                    .forEach(abiliti -> System.out.println("ID: " + abiliti.getId() + " - Tipo: " + abiliti.getName()));

        } else {
            System.out.println("No se pudo obtener información de los movimientos.");
        }
    }

    public JSONObject obtenerMoves() {
        return obtenerDatosDeUrl("https://pokeapi.co/api/v2/ability?limit=367");
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
