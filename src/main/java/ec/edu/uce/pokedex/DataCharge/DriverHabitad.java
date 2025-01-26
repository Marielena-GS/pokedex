package ec.edu.uce.pokedex.DataCharge;

import ec.edu.uce.pokedex.jpa.Habitat;
import ec.edu.uce.pokedex.repositories.HabitatRepository;
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
public class DriverHabitad {
    private final ExecutorService executorService;

    @Autowired
    private HabitatRepository habitatRepository;

    public void save(Habitat habitat)
    {
        habitatRepository.save(habitat);
        habitatRepository.findById(habitat.getId());
    }

    public DriverHabitad() {
        this.executorService = Executors.newFixedThreadPool(10);
    }


    public void ejecutar() {
        // Consultar los hábitats de Pokémon
        JSONObject habitatData = obtenerHabitats();

        if (habitatData != null) {
            // Extraer y mostrar la información de los hábitats
            JSONArray habitats = habitatData.getJSONArray("results");
            List<JSONObject> habitadList = Stream.iterate(0,i->i+1)
                    .limit(habitats.length())
                    .map(habitats::getJSONObject)
                    .collect(Collectors.toList());

            habitadList.stream().parallel().forEach(habita -> executorService.execute(()->{
                Habitat newHabitat = new Habitat(habitadList.indexOf(habita)+1,habita.optString("name"));
                habitatRepository.save(newHabitat);
            }));

        } else {
            System.out.println("No se pudo obtener información de los hábitats.");
        }
    }

    public JSONObject obtenerHabitats() {
        return obtenerDatosDeUrl("https://pokeapi.co/api/v2/pokemon-habitat");
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
