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
		Driver pokedexService = new Driver();
		pokedexService.ejecutar();
	}
}
