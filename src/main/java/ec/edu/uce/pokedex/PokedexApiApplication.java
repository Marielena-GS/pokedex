package ec.edu.uce.pokedex;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PokedexApiApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(PokedexApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// ID del Pokémon que quieres consultar, en este caso Bulbasaur (ID 1)
		int idPokemon = 4;

		// Obtener los datos del Pokémon
		JSONObject pokemonData = obtenerPokemon(idPokemon);

		// Mostrar el JSON completo del Pokémon en la consola
		if (pokemonData != null) {
			System.out.println("Información del Pokémon:");
			System.out.println(pokemonData.toString(4)); // Con un indentado de 4 espacios

			// Obtener la cadena de evolución
			int evolucionChainId = obtenerEvolucionChainId(pokemonData);
			if (evolucionChainId != -1) {
				JSONObject evolucionData = obtenerCadenaEvolucion(evolucionChainId);
				System.out.println("\nCadena de Evolución:");
				System.out.println(evolucionData.toString(4));
			} else {
				System.out.println("No se encontró información sobre la cadena de evolución.");
			}
		} else {
			System.out.println("No se pudo obtener información del Pokémon.");
		}
	}

	//Método para hacer la consulta HTTP a la API de PokeAPI para obtener los datos del Pokémon
	public static JSONObject obtenerPokemon(int id) {
		String url = "https://pokeapi.co/api/v2/pokemon/" + id;

		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpGet request = new HttpGet(url);

			// Ejecutar la petición y obtener la respuesta
			try (CloseableHttpResponse response = client.execute(request)) {
				HttpEntity entity = response.getEntity();
				String jsonResponse = EntityUtils.toString(entity);

				// Convertir la respuesta JSON en un objeto JSONObject
				return new JSONObject(jsonResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Método para obtener el ID de la cadena de evolución desde los datos del Pokémon
	public static int obtenerEvolucionChainId(JSONObject pokemonData) {
		// Obtener el ID de la cadena de evolución del Pokémon
		try {
			JSONObject species = pokemonData.getJSONObject("species");
			String url = species.getString("url");
			// Hacer una petición a la URL de la especie para obtener el ID de la cadena de evolución
			JSONObject speciesData = obtenerDatosDeUrl(url);
			if (speciesData != null && speciesData.has("evolution_chain")) {
				JSONObject evolutionChain = speciesData.getJSONObject("evolution_chain");

				// Obtener la URL de la cadena de evolución como String
				String evolutionChainUrl = evolutionChain.getString("url");

				// Extraer el ID de la URL de la cadena de evolución
				String evolutionChainIdStr = evolutionChainUrl.replace("https://pokeapi.co/api/v2/evolution-chain/", "").replace("/", "");

				// Convertir el ID de la cadena de evolución a entero
				return Integer.parseInt(evolutionChainIdStr); // Retorna el ID de la cadena de evolución
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1; // Retorna -1 si no se encuentra la cadena de evolución
	}

	// Método para hacer la consulta HTTP a una URL generada (por ejemplo, la URL de la especie) y obtener los datos
	public static JSONObject obtenerDatosDeUrl(String url) {
		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpGet request = new HttpGet(url);

			// Ejecutar la petición y obtener la respuesta
			try (CloseableHttpResponse response = client.execute(request)) {
				HttpEntity entity = response.getEntity();
				String jsonResponse = EntityUtils.toString(entity);

				// Convertir la respuesta JSON en un objeto JSONObject
				return new JSONObject(jsonResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// Método para obtener la cadena de evolución usando el ID de la evolución
	public static JSONObject obtenerCadenaEvolucion(int evolucionChainId) {
		String url = "https://pokeapi.co/api/v2/evolution-chain/" + evolucionChainId;

		try (CloseableHttpClient client = HttpClients.createDefault()) {
			HttpGet request = new HttpGet(url);

			// Ejecutar la petición y obtener la respuesta
			try (CloseableHttpResponse response = client.execute(request)) {
				HttpEntity entity = response.getEntity();
				String jsonResponse = EntityUtils.toString(entity);

				// Convertir la respuesta JSON en un objeto JSONObject
				return new JSONObject(jsonResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
