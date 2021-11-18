import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Window pacManWindow = new Window();
        pacManWindow.setVisible(true);
        pacManWindow.setTitle("Pac-Man");
        pacManWindow.setSize(635,775);
        pacManWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pacManWindow.setLocationRelativeTo(null);

    }
}
