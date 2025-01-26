package ec.edu.uce.pokedex.DataCharge;

import ec.edu.uce.pokedex.jpa.Abilities;
import ec.edu.uce.pokedex.repositories.AbilitiesRepository;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Service
public class DriveAbility {
    private final ExecutorService executorService;

    @Autowired
    private AbilitiesRepository abilitiesRepository;

    public void save(Abilities abilities)
    {
        abilitiesRepository.save(abilities);
        abilitiesRepository.findById(abilities.getId());
    }

    public DriveAbility() {
        // Crear un pool de hilos con un número fijo de hilos
        this.executorService = Executors.newFixedThreadPool(10); // Puedes ajustar el número de hilos
    }

    public void ejecutar() {
        // Consultar los movimientos de Pokémon
        JSONObject abilityData = obtenerMoves();

        if (abilityData != null) {
            // Extraer y mostrar la información de las habilidades
            JSONArray ability = abilityData.getJSONArray("results");
            List<JSONObject> abilityList = Stream.iterate(0, i -> i + 1)
                    .limit(ability.length())
                    .map(ability::getJSONObject)
                    .collect(Collectors.toList());

            // Ejecutar cada tarea en un hilo del pool
            abilityList.stream().parallel().forEach(abilitys -> executorService.execute(() -> {
                // Obtener el nombre y ID de la habilidad
                // Crear y mostrar la habilidad
                Abilities abiliti = new Abilities(abilityList.indexOf(abilitys) + 1, abilitys.optString("name"));
                System.out.println("ID: " + abiliti.getId() + " - Nombre: " + abiliti.getName());
            }));

        } else {
            System.out.println("No se pudo obtener información de las habilidades.");
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

    // Cerrar el pool de hilos al finalizar
    public void shutdown() {
        executorService.shutdown();
    }
}
