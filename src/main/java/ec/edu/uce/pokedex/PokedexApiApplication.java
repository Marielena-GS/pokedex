package ec.edu.uce.pokedex;

import ec.edu.uce.pokedex.DataCharge.*;
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
		DriverTypes pokedexServiceType = new DriverTypes();
		pokedexServiceType.ejecutar();

		DriverPokemon pokedexService = new DriverPokemon();
		pokedexService.ejecutar();
		//DriverHabitad pokedexServiceHabitad = new DriverHabitad();
		//pokedexServiceHabitad.ejecutar();
		//DriverMove pokedexServiceMoves = new DriverMove();
		//pokedexServiceMoves.ejecutar();
		//DriveAbility pokedexServiceAbility = new DriveAbility();
		//pokedexServiceAbility.ejecutar();
		//DriveLocation pokedexServceLocation = new DriveLocation();
		//pokedexServceLocation.ejecutar();
		//DriverRegion pokedexServiceRegion = new DriverRegion();
		//pokedexServiceRegion.ejecutar();

	}
}
