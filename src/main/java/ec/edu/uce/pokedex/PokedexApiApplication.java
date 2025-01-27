package ec.edu.uce.pokedex;

import ec.edu.uce.pokedex.Grafica.MainWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"ec.edu.uce.pokedex.Grafica", "ec.edu.uce.pokedex.DataCharge", "ec.edu.uce.pokedex.repositories", "ec.edu.uce.pokedex.Service"})
public class PokedexApiApplication implements CommandLineRunner {

	@Autowired
	private MainWindow mainWindow;

	public static void main(String[] args) {
		SpringApplication.run(PokedexApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		mainWindow.setVisible(true);  // Muestra la ventana principal cuando la aplicación arranca
	}
}
