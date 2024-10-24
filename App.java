import javax.swing.*;
import java.awt.*;

public class App {

    public static void main(String[] args) throws Exception{

        int boardWidth = 360;
        int boardHeight = 640;

        JFrame frame = new JFrame("Flappy Bird");
        frame.setSize(boardWidth, boardHeight);
        //frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        frame.pack(); //esto se hace para que las dimensiones del frame no tengan en cuenta la barra del titulo de la ventana
        flappyBird.requestFocus();
        frame.setVisible(true);

    }
}
