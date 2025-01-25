package ec.edu.uce.pokedex.Grafica;

import javax.swing.*;
import java.awt.*;

public class Inicio extends JFrame {

    private JButton button1;

    public Inicio() {
        setTitle("Inicio");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        // Cargar la imagen original
        ImageIcon originalIcon = new ImageIcon("src/main/resources/pokemon_sprites/7.png");

        // Escalar la imagen al tamaño deseado
        Image scaledImage = originalIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH);

        // Crear un nuevo ImageIcon con la imagen escalada
        ImageIcon scaledIcon = new ImageIcon(scaledImage);

        // Crear un JLabel con la imagen escalada
        JLabel label = new JLabel(scaledIcon);

        // Agregar el JLabel al JFrame
        add(label, BorderLayout.CENTER);



        setVisible(true);
    }

}
