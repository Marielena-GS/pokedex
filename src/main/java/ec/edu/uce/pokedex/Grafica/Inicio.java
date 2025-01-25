package ec.edu.uce.pokedex.Grafica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Inicio extends JFrame {

    private JButton buttonNext;
    private JButton buttonBack;
    private JPanel imagePanel; // Panel para contener las 8 imágenes
    private int currentIndex = 1; // Índice inicial (1 para empezar con la primera imagen)
    private final int totalImages = 1205; // Total de imágenes disponibles

    public Inicio() {
        setTitle("Inicio");
        setSize(600, 600); // Ajustado para que quepan 8 imágenes
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear un panel para contener las imágenes
        imagePanel = new JPanel();
        imagePanel.setLayout(new GridLayout(2, 4, 10, 10)); // 2 filas y 4 columnas
        loadImages(); // Cargar las primeras 8 imágenes

        // Crear el botón para avanzar a la siguiente serie de imágenes
        buttonNext = new JButton("Siguiente");
        buttonNext.addActionListener(e -> {
            currentIndex += 8; // Avanzar a las siguientes 8 imágenes
            if (currentIndex > totalImages) {
                currentIndex = 1; // Si excede el total de imágenes, vuelve al principio
            }
            loadImages(); // Actualizar las imágenes mostradas
        });

        // Crear el botón para retroceder a la serie anterior de imágenes
        buttonBack = new JButton("Atrás");
        buttonBack.addActionListener(e -> {
            currentIndex -= 8; // Retroceder a las 8 imágenes anteriores
            if (currentIndex < 1) {
                currentIndex = totalImages - 7; // Si está antes de la primera imagen, muestra las últimas 8
            }
            loadImages(); // Actualizar las imágenes mostradas
        });

        // Crear un panel para los botones y organizar su disposición
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.add(buttonBack);
        buttonPanel.add(buttonNext);

        // Agregar componentes al JFrame
        setLayout(new BorderLayout());
        add(imagePanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadImages() {
        // Limpiar las imágenes anteriores del panel
        imagePanel.removeAll();

        // Cargar las 8 imágenes
        for (int i = 0; i < 8; i++) {
            int imageIndex = currentIndex + i;
            if (imageIndex > totalImages) {
                imageIndex = imageIndex - totalImages; // Hacer el recorrido circular
            }

            // Generar la ruta dinámica para la imagen
            String imagePath = String.format("src/main/resources/pokemon_sprites/%d.png", imageIndex);

            // Cargar y escalar la imagen
            ImageIcon originalIcon = new ImageIcon(imagePath);
            Image scaledImage = originalIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);

            // Crear una etiqueta para la imagen
            JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
            imageLabel.setHorizontalAlignment(JLabel.CENTER);

            // Agregar un MouseListener a la imagen
            int finalImageIndex = imageIndex; // Variable final para usar dentro del MouseListener
            imageLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    showImageInNewWindow(finalImageIndex); // Mostrar la imagen en una ventana separada
                }
            });

            // Agregar la etiqueta al panel
            imagePanel.add(imageLabel);
        }

        // Actualizar la interfaz para reflejar los cambios
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    // Método para mostrar una ventana emergente con un mensaje
    private void showImageInNewWindow(int imageIndex) {
        // Crear un nuevo JFrame para mostrar la imagen seleccionada
        JFrame imageFrame = new JFrame("Imagen Detallada");
        imageFrame.setSize(800, 600); // Tamaño de la ventana
        imageFrame.setLocationRelativeTo(null);
        imageFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Crear un panel principal para la imagen
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Cargar la imagen clickeada
        String imagePath = String.format("src/main/resources/pokemon_sprites/%d.png", imageIndex);
        ImageIcon originalIcon = new ImageIcon(imagePath);
        Image image = originalIcon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
        JLabel mainImageLabel = new JLabel(new ImageIcon(image));
        mainImageLabel.setHorizontalAlignment(JLabel.CENTER);

        // Crear un panel para los detalles (nombre, id, tipo)
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));

        // Aquí puedes colocar los detalles estáticos o dinámicos del Pokémon (simulados por ahora)
        String name = "Pokémon #" + imageIndex; // Nombre simulado
        String id = String.valueOf(imageIndex); // ID simulado
        String type = "Tipo: Fuego"; // Tipo simulado

        // Agregar las etiquetas con los detalles
        detailsPanel.add(new JLabel("Nombre: " + name));
        detailsPanel.add(new JLabel("ID: " + id));
        detailsPanel.add(new JLabel("Tipo: " + type));

        // Crear un panel para las tres imágenes inferiores
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayout(1, 3, 10, 10));

        // Cargar las imágenes de ID 1, 2 y la clickeada
        String[] imageIds = { "1", "2", String.valueOf(imageIndex) };
        String[] names = { "Pokémon #1", "Pokémon #2", "Pokémon #" + imageIndex };

        for (int i = 0; i < 3; i++) {
            String idImage = imageIds[i];
            String nameImage = names[i];
            String bottomImagePath = String.format("src/main/resources/pokemon_sprites/%s.png", idImage);
            ImageIcon bottomIcon = new ImageIcon(bottomImagePath);
            Image bottomImage = bottomIcon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            JLabel bottomImageLabel = new JLabel(new ImageIcon(bottomImage));
            bottomImageLabel.setHorizontalAlignment(JLabel.CENTER);

            // Crear un panel para cada imagen con su ID y Nombre debajo
            JPanel imageWithDetailsPanel = new JPanel();
            imageWithDetailsPanel.setLayout(new BorderLayout());
            imageWithDetailsPanel.add(bottomImageLabel, BorderLayout.CENTER);
            imageWithDetailsPanel.add(new JLabel(nameImage), BorderLayout.SOUTH);
            imageWithDetailsPanel.add(new JLabel("ID: " + idImage), BorderLayout.NORTH);

            bottomPanel.add(imageWithDetailsPanel); // Agregar al panel inferior
        }

        // Crear un panel para contener la imagen, los detalles y las imágenes inferiores
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(mainImageLabel, BorderLayout.CENTER);
        contentPanel.add(detailsPanel, BorderLayout.EAST); // Detalles a la derecha
        contentPanel.add(bottomPanel, BorderLayout.SOUTH); // Imágenes inferiores

        // Agregar el panel principal al JFrame
        imageFrame.add(contentPanel);
        imageFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Inicio::new);
    }
}
