package ec.edu.uce.pokedex;

import ec.edu.uce.pokedex.DataCharge.DriverPokemon;
import ec.edu.uce.pokedex.DataCharge.DriverTypes;
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
		//DriverPokemon pokedexService = new DriverPokemon();
		//pokedexService.ejecutar();
		DriverTypes pokedexServiceType = new DriverTypes();
		pokedexServiceType.ejecutar();
	}
}
