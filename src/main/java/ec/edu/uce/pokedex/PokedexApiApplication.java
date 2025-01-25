package ec.edu.uce.pokedex;

import ec.edu.uce.pokedex.DataCharge.DriverPokemon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PokedexApiApplication implements CommandLineRunner {

	// Autowired para inyectar DriverPokemon
	@Autowired
	private DriverPokemon pokedexService;

	public static void main(String[] args) {
		SpringApplication.run(PokedexApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// Llamar al método ejecutar sin necesidad de crear una nueva instancia de DriverPokemon
		pokedexService.ejecutar();
	}
}
