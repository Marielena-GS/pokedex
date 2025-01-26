package ec.edu.uce.pokedex;

import ec.edu.uce.pokedex.DataCharge.*;
import ec.edu.uce.pokedex.Grafica.Inicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationPackage;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.swing.*;
import java.awt.*;

@SpringBootApplication
public class PokedexApiApplication implements CommandLineRunner {

	// Autowired para inyectar DriverPokemon
	@Autowired
	private DriverPokemon pokedexService;

	@Autowired
	private DriveAbility driverAbilityService;

	@Autowired
	private DriverHabitad driverHabitadService;

	@Autowired
	private DriverMove driverMoveService;

	@Autowired
	private DriverRegion driverRegionService;

	@Autowired
	private DriverTypes driverTypesService;

	public static void main(String[] args) {
		SpringApplication.run(PokedexApiApplication.class, args);


	}

	@Override
	public void run(String... args) throws Exception {

		driverTypesService.ejecutar();
		driverRegionService.ejecutar();
		driverAbilityService.ejecutar();
		driverHabitadService.ejecutar();
		driverMoveService.ejecutar();


		//Llamar al método ejecutar sin necesidad de crear una nueva instancia de DriverPokemon
		//pokedexService.ejecutar();
		//new Inicio();
	}
}
