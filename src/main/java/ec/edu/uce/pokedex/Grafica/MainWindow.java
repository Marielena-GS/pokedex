package ec.edu.uce.pokedex.Grafica;

import ec.edu.uce.pokedex.Observer.CargaDatosListener;
import ec.edu.uce.pokedex.jpa.Habitat;
import ec.edu.uce.pokedex.jpa.Pokemon;
import ec.edu.uce.pokedex.jpa.Region;
import ec.edu.uce.pokedex.jpa.Types;
import ec.edu.uce.pokedex.repositories.PokemonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ec.edu.uce.pokedex.DataCharge.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MainWindow extends JFrame implements CargaDatosListener {

    @Autowired
    PokemonRepository pokemonRepository;
    // Servicios
    private final DriverMove driverMoveService;
    private final DriveAbility driveAbilityService;
    private final DriverHabitad driverHabitadService;
    private final DriverRegion driverRegionService;
    private final DriverTypes driverTypesService;
    private final DriverPokemon driverPokemonService;
    private JTextField searchField;
    private JComboBox<String> typeComboBox;
    private JComboBox<String> regionComboBox;
    private JComboBox<String> abilityComboBox;
    private JComboBox<String> moveComboBox;
    private JComboBox<String> habitatComboBox;
    private JButton searchButton;
    private JButton chargeData;
    private JButton nextButton;
    private JButton backButton;
    private JPanel imagePanel;
    private JPanel paginationPanel;
    private int currentPage = 1;
    private final int pageSize = 8;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    // Contador de drivers completados
    private AtomicInteger driversCompletados;

    //@Autowired
    public MainWindow(DriverMove driverMoveService, DriveAbility driveAbilityService, DriverHabitad driverHabitadService,
                      DriverRegion driverRegionService, DriverTypes driverTypesService, DriverPokemon driverPokemonService,
                      PokemonRepository pokemonRepository) {
        this.pokemonRepository = pokemonRepository;
        this.driverMoveService = driverMoveService;
        this.driveAbilityService = driveAbilityService;
        this.driverHabitadService = driverHabitadService;
        this.driverRegionService = driverRegionService;
        this.driverTypesService = driverTypesService;
        this.driverPokemonService = driverPokemonService;
        setTitle("Pokédex");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        driversCompletados = new AtomicInteger(0);
        // Barra de búsqueda y filtros
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBackground(new Color(173, 216, 230)); // Azul claro
        searchField = new JTextField(10);

        // Comboboxes for filters
        typeComboBox = new JComboBox<>(new String[] {"All Types", "Fire", "Water", "Grass", "Electric"});
        regionComboBox = new JComboBox<>(new String[] {"All Regions", "Kanto", "Johto", "Hoenn", "Sinnoh"});
        abilityComboBox = new JComboBox<>(new String[] {"All Abilities", "Overgrow", "Blaze", "Torrent", "Levitate"});
        moveComboBox = new JComboBox<>(new String[] {"All Moves", "Tackle", "Flamethrower", "Thunderbolt"});
        habitatComboBox = new JComboBox<>(new String[] {"All Habitats", "Forest", "Cave", "Water", "Desert"});

        searchButton = new JButton("Search");
        chargeData = new JButton("Charge");


        searchPanel.add(new JLabel("Search Pokémon by Name: "));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Type: "));
        searchPanel.add(typeComboBox);
        searchPanel.add(new JLabel("Region: "));
        searchPanel.add(regionComboBox);
        searchPanel.add(new JLabel("Ability: "));
        searchPanel.add(abilityComboBox);
        searchPanel.add(new JLabel("Move: "));
        searchPanel.add(moveComboBox);
        searchPanel.add(new JLabel("Habitat: "));
        searchPanel.add(habitatComboBox);
        searchPanel.add(searchButton);
        searchPanel.add(chargeData);

        // Panel de imágenes
        imagePanel = new JPanel(new GridLayout(2, 4, 10, 10));
        JScrollPane imageScrollPane = new JScrollPane(imagePanel);
        imageScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Panel de paginación
        paginationPanel = new JPanel(new FlowLayout());
        paginationPanel.setBackground(new Color(240, 230, 140)); // Amarillo claro
        backButton = new JButton("⬅ Back");
        nextButton = new JButton("Next ➡");
        JLabel pageInfo = new JLabel("Page " + currentPage, JLabel.CENTER);
        pageInfo.setFont(new Font("Arial", Font.BOLD, 14));
        paginationPanel.add(backButton);
        paginationPanel.add(pageInfo);
        paginationPanel.add(nextButton);

        // Añadir componentes al JFrame
        add(searchPanel, BorderLayout.NORTH);
        add(imageScrollPane, BorderLayout.CENTER);
        add(paginationPanel, BorderLayout.SOUTH);
        // Listeners
        backButton.addActionListener(e -> navigatePage(-1, pageInfo));
        nextButton.addActionListener(e -> navigatePage(1, pageInfo));

        chargeData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Iniciar la carga de datos
                cargarDatos();
            }
        });

        loadPage();
        setVisible(true);
    }
    private void loadPage() {
        imagePanel.removeAll();

        int startId = (currentPage - 1) * pageSize + 1; // Calculamos el inicio de la página actual
        System.out.println("Start ID: " + startId); // Para depurar el valor de startId
        for (int i = 0; i < pageSize; i++) {
            int pokemonId = startId + i; // ID del Pokémon actual
            System.out.println("Loading Pokémon ID: " + pokemonId); // Para depurar el ID de cada Pokémon

            // Panel principal con GridLayout para mostrar el ID arriba y la imagen en el centro
            JPanel cardPanel = new JPanel(new GridLayout(2, 1)); // 2 filas: ID arriba, imagen + nombre abajo
            cardPanel.setBackground(new Color(255, 250, 205)); // Amarillo claro
            cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            // Recuperamos el Pokémon de la base de datos
            Pokemon nuevoPokemon = pokemonRepository.findById(pokemonId);

            // Panel para el ID del Pokémon
            JPanel idPanel = new JPanel();
            idPanel.setBackground(new Color(255, 250, 205));
            JLabel idLabel = new JLabel("ID: " + (nuevoPokemon != null ? nuevoPokemon.getId() : "N/A"), JLabel.CENTER);
            idLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            idPanel.add(idLabel);
            cardPanel.add(idPanel);

            // Panel para la imagen y el nombre del Pokémon
            JPanel imageNamePanel = new JPanel();
            imageNamePanel.setBackground(new Color(255, 250, 205));
            imageNamePanel.setLayout(new BorderLayout());

            // Imagen del Pokémon
            JLabel pokemonLabel = new JLabel("Loading...");
            pokemonLabel.setHorizontalAlignment(JLabel.CENTER);
            pokemonLabel.setPreferredSize(new Dimension(100, 100)); // Ajusta el tamaño según tus necesidades
            imageNamePanel.add(pokemonLabel, BorderLayout.CENTER);

            // Nombre del Pokémon
            if (nuevoPokemon != null) {
                JLabel nameLabel = new JLabel(nuevoPokemon.getName(), JLabel.CENTER); // Nombre real del Pokémon
                nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
                nameLabel.setPreferredSize(new Dimension(100, 30));
                imageNamePanel.add(nameLabel, BorderLayout.SOUTH);
            }

            // Añadir el panel con imagen y nombre
            cardPanel.add(imageNamePanel);

            // Fetch Pokémon data asíncronamente
            executor.submit(() -> {
                ImageIcon icon = fetchPokemonSprite(pokemonId);
                if (icon != null) {
                    SwingUtilities.invokeLater(() -> {
                        pokemonLabel.setIcon(icon);
                        pokemonLabel.setText(null);
                        pokemonLabel.setHorizontalAlignment(JLabel.CENTER);
                        cardPanel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                showPokemonDetails(pokemonId);
                            }

                            @Override
                            public void mouseEntered(MouseEvent e) {
                                cardPanel.setBackground(new Color(240, 230, 140));
                            }

                            @Override
                            public void mouseExited(MouseEvent e) {
                                cardPanel.setBackground(new Color(255, 250, 205));
                            }
                        });
                    });
                } else {
                    SwingUtilities.invokeLater(() -> pokemonLabel.setText("Not found"));
                }
            });

            imagePanel.add(cardPanel);
        }

        imagePanel.revalidate();
        imagePanel.repaint();
    }


    private ImageIcon fetchPokemonSprite(int pokemonId) {
        try {
            // Simulación de la llamada a la API
            String imagePath = String.format("src/main/resources/pokemon_sprites/%d.png", pokemonId);
            ImageIcon icon = new ImageIcon(imagePath);
            Image scaledImage = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            return null;
        }
    }

    private void showPokemonDetails(int pokemonId) {
        executor.submit(() -> {
            Pokemon pokemon = pokemonRepository.findById(pokemonId);
            String name = pokemon.getName();

            // Tipo
            StringBuilder sb = new StringBuilder();
            sb.append("Tipo: ");
            for (Types types : pokemon.getTypes()) {
                sb.append(types.getName()).append(" ");
            }
            String type = sb.toString();

            // Evoluciones
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Evoluciones: ");
            for (Integer evoluciones : pokemon.getEnvoles()) {
                sb2.append(evoluciones).append(" ");
            }
            String evolutions = sb2.toString();

            // Características (izquierda)
            Optional<Pokemon> pokemon1 = pokemonRepository.findByIdAndLoadHabitatAndRegions(pokemonId);
            String height = "Altura: " + pokemon1.get().getHeight();
            String weight = "Peso: " + pokemon1.get().getWeight();
            StringBuilder sb3 = new StringBuilder();
            sb3.append("<html>Region:<br>");
            if (pokemon1.get().getRegions() != null) {
                for (Region regions : pokemon1.get().getRegions()) {
                    sb3.append(regions.getName()).append("<br>");
                }
            }
            sb3.append("</html>");
            String region = sb3.toString();

            StringBuilder sb4 = new StringBuilder();
            sb4.append("Habitat:\n");
            if (pokemon1.get().getHabitat() != null) {
                Habitat nuevoHabitat = pokemon1.get().getHabitat();
                sb4.append(nuevoHabitat.getName()).append("\n");
            }
            String habitat = sb4.toString();

            // Estadísticas (derecha)
            String statsHp = "HP: " + pokemon.getStats_hp();
            String statsAttack = "Ataque: " + pokemon.getStats_attack();
            String statsDefense = "Defensa: " + pokemon.getStats_defense();
            String statsSpecialAttack = "Atq. Esp.: " + pokemon.getStats_special_attack();
            String statsSpecialDefense = "Def. Esp.: " + pokemon.getStats_special_defense();
            String statsSpeed = "Velocidad: " + pokemon.getStats_speed();
            String statsAccuracy = "Precisión: " + pokemon.getStats_accuracy();
            String statsEvasion = "Evasión: " + pokemon.getStats_evasion();

            //evoluciones
            ImageIcon icon = fetchPokemonSprite(pokemonId);
            ImageIcon icon3;
            ImageIcon icon1;
            ImageIcon icon2;
            if (pokemon.getEnvoles().size() == 3) {
                icon1 =fetchPokemonSprite(pokemon.getEnvoles().get(0));
                icon2 =fetchPokemonSprite(pokemon.getEnvoles().get(1));
                icon3 =fetchPokemonSprite(pokemon.getEnvoles().get(2));
            }else {
                icon1 = fetchPokemonSprite(pokemon.getEnvoles().get(0));
                icon2 = fetchPokemonSprite(pokemon.getEnvoles().get(0));
                icon3 = fetchPokemonSprite(pokemon.getEnvoles().get(1));

            }
            List<Pokemon> evolesName = new ArrayList<>();
            for (Integer evolucion : pokemon.getEnvoles()) {
                Pokemon pokemonEvolesName = pokemonRepository.findById(evolucion).get();
                evolesName.add(pokemonEvolesName);
                if(pokemon.getEnvoles().size()!=3){
                    Pokemon pokemonEvolesName1 = pokemonRepository.findById(pokemon.getEnvoles().get(0)).get();
                    evolesName.add(pokemonEvolesName1);
                }
            }

            //fin


            SwingUtilities.invokeLater(() -> {
                JFrame detailFrame = new JFrame("Detalles de Pokémon");
                detailFrame.setSize(800, 600); // Aumentar el tamaño de la ventana
                detailFrame.setLocationRelativeTo(null);

                JPanel detailPanel = new JPanel(new BorderLayout());
                detailPanel.setBackground(new Color(230, 230, 250));

                // Nombre y tipo
                JLabel nameLabel = new JLabel("Nombre: " + name, JLabel.CENTER);
                nameLabel.setFont(new Font("Arial", Font.BOLD, 18));
                JLabel typeLabel = new JLabel(type, JLabel.CENTER);
                JLabel evolutionsLabel = new JLabel(evolutions, JLabel.CENTER);

                // Redimensionar la imagen central
                ImageIcon resizedIcon = new ImageIcon(icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH)); // Redimensionar
                JLabel iconLabel = new JLabel(resizedIcon);


                // Panel de detalles (derecha)
                JPanel rightPanel = new JPanel(new GridLayout(10, 1, 5, 5));  // Aumentamos el espacio entre filas
                rightPanel.setBackground(new Color(230, 230, 250));
                rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Agregar márgenes
                rightPanel.add(new JLabel(height));
                rightPanel.add(new JLabel(weight));
                rightPanel.add(new JLabel(region));
                rightPanel.add(new JLabel(habitat));
                rightPanel.add(new JLabel(statsHp));
                rightPanel.add(new JLabel(statsAttack));
                rightPanel.add(new JLabel(statsDefense));
                rightPanel.add(new JLabel(statsSpecialAttack));
                rightPanel.add(new JLabel(statsSpecialDefense));
                rightPanel.add(new JLabel(statsSpeed));
                rightPanel.add(new JLabel(statsAccuracy));
                rightPanel.add(new JLabel(statsEvasion));

                // Panel de imagen y datos (izquierda)
                JPanel leftPanel = new JPanel(new BorderLayout());
                leftPanel.setBackground(new Color(230, 230, 250));
                leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Agregar márgenes
                leftPanel.setPreferredSize(new Dimension(500, 600)); // Hacer el panel izquierdo más grande
                leftPanel.add(iconLabel, BorderLayout.CENTER);
                leftPanel.add(typeLabel, BorderLayout.NORTH);
                leftPanel.add(evolutionsLabel, BorderLayout.SOUTH);

                // Agregar tres imágenes en la parte inferior
                JPanel imagePanel = new JPanel(new GridLayout(1, 3, 10, 10)); // Panel para las 3 imágenes
                ImageIcon resizedIcon1 = new ImageIcon(icon1.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)); // Redimensionar
                JLabel iconLabel1 = new JLabel(resizedIcon1);
                ImageIcon resizedIcon2 = new ImageIcon(icon2.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)); // Redimensionar
                JLabel iconLabel2 = new JLabel(resizedIcon2);
                ImageIcon resizedIcon3 = new ImageIcon(icon3.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH)); // Redimensionar
                JLabel iconLabel3 = new JLabel(resizedIcon3);

                // Crear un JPanel para cada imagen y agregar los textos centrados
                JPanel imagePanel1 = new JPanel(new BorderLayout());
                imagePanel1.add(iconLabel1, BorderLayout.CENTER);
                JPanel textPanel1 = new JPanel();
                textPanel1.setLayout(new BoxLayout(textPanel1, BoxLayout.Y_AXIS));
                textPanel1.add(new JLabel("id: " +evolesName.get(0).getId()));
                textPanel1.add(new JLabel("name: " + evolesName.get(0).getName()));
                textPanel1.setAlignmentX(JPanel.CENTER_ALIGNMENT); // Centrar el texto
                imagePanel1.add(textPanel1, BorderLayout.SOUTH);

                JPanel imagePanel2 = new JPanel(new BorderLayout());
                imagePanel2.add(iconLabel2, BorderLayout.CENTER);
                JPanel textPanel2 = new JPanel();
                textPanel2.setLayout(new BoxLayout(textPanel2, BoxLayout.Y_AXIS));
                textPanel2.add(new JLabel("id: " + evolesName.get(1).getId()));
                textPanel2.add(new JLabel("name: " + evolesName.get(1).getName()));
                textPanel2.setAlignmentX(JPanel.CENTER_ALIGNMENT); // Centrar el texto
                imagePanel2.add(textPanel2, BorderLayout.SOUTH);

                JPanel imagePanel3 = new JPanel(new BorderLayout());
                imagePanel3.add(iconLabel3, BorderLayout.CENTER);
                JPanel textPanel3 = new JPanel();
                textPanel3.setLayout(new BoxLayout(textPanel3, BoxLayout.Y_AXIS));
                textPanel3.add(new JLabel("id: " + evolesName.get(2).getId()));
                textPanel3.add(new JLabel("name: " + evolesName.get(2).getName()));
                textPanel3.setAlignmentX(JPanel.CENTER_ALIGNMENT); // Centrar el texto
                imagePanel3.add(textPanel3, BorderLayout.SOUTH);

                // Agregar los paneles de imagen con texto al panel de imágenes
                imagePanel.add(imagePanel1);
                imagePanel.add(imagePanel2);
                imagePanel.add(imagePanel3);

                // Agregar la fila de imágenes al panel izquierdo
                leftPanel.add(imagePanel, BorderLayout.SOUTH);

                // Agregar todo al panel principal (panel de detalles)
                detailPanel.add(nameLabel, BorderLayout.NORTH);
                detailPanel.add(leftPanel, BorderLayout.WEST);  // Imagen y datos al lado izquierdo
                detailPanel.add(rightPanel, BorderLayout.EAST); // Información al lado derecho

                detailFrame.add(detailPanel);
                detailFrame.setVisible(true);
            });
        });
    }



    private void navigatePage(int direction, JLabel pageInfo) {
        currentPage += direction;
        if (currentPage < 1) currentPage = 1;
        pageInfo.setText("Page " + currentPage);
        loadPage();
    }

    private void displayFilteredPokemons(List<Pokemon> pokemons) {
        imagePanel.removeAll();

        for (Pokemon pokemon : pokemons) {
            JPanel cardPanel = new JPanel(new BorderLayout());
            cardPanel.setBackground(new Color(255, 250, 205)); // Amarillo claro
            cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            JLabel pokemonLabel = new JLabel(pokemon.getName());
            pokemonLabel.setHorizontalAlignment(JLabel.CENTER);
            cardPanel.add(pokemonLabel, BorderLayout.CENTER);

            imagePanel.add(cardPanel);
        }

        imagePanel.revalidate();
        imagePanel.repaint();
    }

    private void cargarDatos() {
        // Iniciar la carga de datos sin mostrar nada más en la UI
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

        driverPokemonService.setCargaDatosListener(MainWindow.this);
        driverPokemonService.ejecutar();
    }

    @Override
    public void onCargaCompleta() {
        // Notificar cuando la carga de datos esté completa
        int completados = driversCompletados.incrementAndGet();
        System.out.println("Carga de un driver completada. Total completados: " + completados);

        if (completados == 6) {
            System.out.println("¡Todos los drivers han terminado la carga!");
        }
    }
}
