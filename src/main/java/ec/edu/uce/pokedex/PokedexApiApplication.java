package ec.edu.uce.pokedex;

import ec.edu.uce.pokedex.DataCharge.*;
import ec.edu.uce.pokedex.Grafica.Inicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;
import java.awt.*;

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
		/*DriveAbility pokedexAbilityService = new DriveAbility();
		pokedexAbilityService.ejecutar();
		DriverHabitad pokedexHabitadService = new DriverHabitad();
		pokedexHabitadService.ejecutar();
		DriverMove pokedexMoveService = new DriverMove();
		pokedexMoveService.ejecutar();
		DriverRegion pokedexRegionService = new DriverRegion();
		pokedexRegionService.ejecutar();
		DriverTypes pokedexTypeService = new DriverTypes();
		pokedexTypeService.ejecutar();*/
		// Llamar al método ejecutar sin necesidad de crear una nueva instancia de DriverPokemon
		//pokedexService.ejecutar();
		new Inicio();

	}
}
