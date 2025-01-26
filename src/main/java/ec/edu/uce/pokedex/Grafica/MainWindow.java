package ec.edu.uce.pokedex.Grafica;

import ec.edu.uce.pokedex.DataCharge.DriverHabitad;
import ec.edu.uce.pokedex.DataCharge.DriverMove;
import ec.edu.uce.pokedex.DataCharge.DriveAbility;
import ec.edu.uce.pokedex.DataCharge.DriverTypes;
import ec.edu.uce.pokedex.DataCharge.DriverRegion;
import ec.edu.uce.pokedex.DataCharge.DriverPokemon;

import ec.edu.uce.pokedex.Observer.CargaDatosListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MainWindow extends JFrame implements CargaDatosListener {

    private final DriverMove driverMoveService;
    private final DriveAbility driveAbilityService;
    private final DriverHabitad driverHabitadService;
    private final DriverRegion driverRegionService;
    private final DriverTypes driverTypesService;
    private final DriverPokemon driverPokemonService;

    private JPanel mainPanel;
    private CardLayout cardLayout;
    private AtomicInteger driversCompletados;

    @Autowired
    public MainWindow(DriverMove driverMoveService, DriveAbility driveAbilityService, DriverHabitad driverHabitadService,
                      DriverRegion driverRegionService, DriverTypes driverTypesService, DriverPokemon driverPokemonService) {
        this.driverMoveService = driverMoveService;
        this.driveAbilityService = driveAbilityService;
        this.driverHabitadService = driverHabitadService;
        this.driverRegionService = driverRegionService;
        this.driverTypesService = driverTypesService;
        this.driverPokemonService = driverPokemonService;

        setTitle("Pokedex Main Window");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Inicializar el contador de drivers completados
        driversCompletados = new AtomicInteger(0);

        // Crear el CardLayout para cambiar entre formularios
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Panel de botones
        JPanel buttonPanel = new JPanel();
        JButton cargarDatosButton = new JButton("Cargar Datos");
        JButton pokedexButton = new JButton("POKEDEX");

        // Configurar el listener para "Cargar Datos"
        cargarDatosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Configurar el listener antes de ejecutar la carga de datos
                driverMoveService.setCargaDatosListener(MainWindow.this);
                driverMoveService.ejecutar();

                driveAbilityService.setCargaDatosListener(MainWindow.this);
                driveAbilityService.ejecutar();

                driverHabitadService.setCargaDatosListener(MainWindow.this);
                driverHabitadService.ejecutar();

                driverRegionService.setCargaDatosListener(MainWindow.this);
                driverRegionService.ejecutar();

                driverTypesService.setCargaDatosListener(MainWindow.this);
                driverTypesService.ejecutar();

                if (driversCompletados.get() == 5) {
                    driverPokemonService.setCargaDatosListener(MainWindow.this);
                    driverPokemonService.ejecutar();
                }
            }
        });

        // Configurar el listener para "POKEDEX"
        pokedexButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Crear la instancia de la ventana de Pokedex
                Inicio inicioWindow = new Inicio();
                // Mostrar la ventana
                inicioWindow.setVisible(true);
                // Opcional: Cerrar la ventana principal (MainWindow) si ya no es necesario
                setVisible(false);
            }
        });

        // Agregar los botones al panel
        buttonPanel.add(cargarDatosButton);
        buttonPanel.add(pokedexButton);

        // Agregar los paneles al CardLayout
        mainPanel.add(buttonPanel, "BUTTONS");

        // Configurar la ventana
        add(mainPanel);
    }

    @Override
    public void onCargaCompleta() {
        // Notificar cuando la carga de datos esté completa
        int completados = driversCompletados.incrementAndGet();
        System.out.println("Carga de un driver completada. Total completados: " + completados);

        if (completados == 6) {
            System.out.println("¡Todos los drivers han terminado la carga! Resultado: true");
        }
    }

}
