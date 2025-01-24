package ec.edu.uce.pokedex;

import ec.edu.uce.pokedex.DataCharge.Driver;
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
