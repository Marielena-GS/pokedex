package ec.edu.uce.pokedex.Grafica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;

public class Inicio extends JFrame {

    private JTextField searchField;
    private JButton searchButton;
    private JButton nextButton;
    private JButton backButton;
    private JPanel imagePanel;
    private JPanel paginationPanel;
    private int currentPage = 1;
    private final int pageSize = 8;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public Inicio() {
        setTitle("Pokédex");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Barra de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout());
        searchPanel.setBackground(new Color(173, 216, 230)); // Azul claro
        searchField = new JTextField(20);
        searchButton = new JButton(" Search ");
        searchPanel.add(new JLabel(" Search Pokémon: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Panel de imágenes
        imagePanel = new JPanel(new GridLayout(2, 4, 10, 10));
        JScrollPane imageScrollPane = new JScrollPane(imagePanel);
        imageScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Panel de paginación
        paginationPanel = new JPanel(new FlowLayout());
        paginationPanel.setBackground(new Color(240, 230, 140)); // Amarillo claro
        backButton = new JButton("⬅ Back ");
        nextButton = new JButton(" Next ➡");
        JLabel pageInfo = new JLabel(" Page: " + currentPage, JLabel.CENTER);
        pageInfo.setFont(new Font("Serif", Font.PLAIN, 16));
        paginationPanel.add(backButton);
        paginationPanel.add(pageInfo);
        paginationPanel.add(nextButton);

        // Añadir componentes al JFrame
        add(searchPanel, BorderLayout.NORTH);
        add(imageScrollPane, BorderLayout.CENTER);
        add(paginationPanel, BorderLayout.SOUTH);

        // Listeners
        searchButton.addActionListener(e -> searchPokemon(pageInfo));
        backButton.addActionListener(e -> navigatePage(-1, pageInfo));
        nextButton.addActionListener(e -> navigatePage(1, pageInfo));

        loadPage();
        setVisible(true);
    }

    private void loadPage() {
        imagePanel.removeAll();

        int startId = (currentPage - 1) * pageSize + 1;
        for (int i = 0; i < pageSize; i++) {
            int pokemonId = startId + i;

            JPanel cardPanel = new JPanel(new BorderLayout());
            cardPanel.setBackground(new Color(255, 250, 205)); // Amarillo claro
            cardPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

            JLabel pokemonLabel = new JLabel("Loading...");
            pokemonLabel.setHorizontalAlignment(JLabel.CENTER);
            cardPanel.add(pokemonLabel, BorderLayout.CENTER);

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
            Image scaledImage = icon.getImage().getScaledInstance(140, 140, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } catch (Exception e) {
            return null;
        }
    }

    private void showPokemonDetails(int pokemonId) {
        executor.submit(() -> {
            String name = "Pokémon # " + pokemonId;
            String type = "Type: Fire";
            String evolutions = "Evolutions: " + pokemonId + " -> " + (pokemonId + 1) + " -> " + (pokemonId + 2);
            ImageIcon icon = fetchPokemonSprite(pokemonId);

            SwingUtilities.invokeLater(() -> {
                JFrame detailFrame = new JFrame("Pokémon Details");
                detailFrame.setSize(500, 400);
                detailFrame.setLocationRelativeTo(null);

                JPanel detailPanel = new JPanel(new BorderLayout());
                detailPanel.setBackground(new Color(230, 230, 250));

                JLabel nameLabel = new JLabel("\n Name: " + name, JLabel.CENTER);
                nameLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
                JLabel typeLabel = new JLabel(type, JLabel.CENTER);
                JLabel evolutionsLabel = new JLabel(evolutions, JLabel.CENTER);
                JLabel iconLabel = new JLabel(icon);

                JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
                bottomPanel.add(typeLabel);
                bottomPanel.add(evolutionsLabel);

                detailPanel.add(nameLabel, BorderLayout.NORTH);
                detailPanel.add(iconLabel, BorderLayout.CENTER);
                detailPanel.add(bottomPanel, BorderLayout.SOUTH);
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

    private void searchPokemon(JLabel pageInfo) {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Pokémon ID or Name.");
            return;
        }

        try {
            int pokemonId = Integer.parseInt(query);
            showPokemonDetails(pokemonId);
        } catch (NumberFormatException e) {
            String pokemonName = query.toLowerCase();
            // Simulación de búsqueda por nombre
            int pokemonId = fetchPokemonIdByName(pokemonName);
            if (pokemonId != -1) {
                showPokemonDetails(pokemonId);
            } else {
                JOptionPane.showMessageDialog(this, "Pokémon not found by name.");
            }
        }
    }

    private int fetchPokemonIdByName(String name) {
        // Simula un mapeo nombre-ID (reemplaza con una llamada real a la PokéAPI)
        if (name.equals("pikachu")) return 25;
        if (name.equals("charmander")) return 4;
        if (name.equals("bulbasaur")) return 1;
        return -1;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Inicio::new);
    }
}

