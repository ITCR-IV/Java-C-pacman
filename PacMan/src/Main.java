import javax.swing.*;

import com.github.cliftonlabs.json_simple.JsonObject;

public class Main {

    public static void main(String[] args) {
        Window pacManWindow = new Window();
        pacManWindow.setLocationRelativeTo(null);
        pacManWindow.setVisible(true);
        pacManWindow.setTitle("Pac-Man");
        pacManWindow.setSize(635, 775);
        pacManWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        SockManager manager = new SockManager(pacManWindow.getField(), "localhost", 8888);
        Thread listenerThread = new Thread(manager);
        listenerThread.start();

        try {
            listenerThread.join();
        } catch (InterruptedException i) {
            System.out.println(i);
        }

    }
}