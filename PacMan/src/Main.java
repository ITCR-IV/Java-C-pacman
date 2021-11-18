import javax.swing.*;

import com.github.cliftonlabs.json_simple.JsonObject;

public class Main {

    public static void main(String[] args) {
        SockManager manager = new SockManager("localhost", 8888);
        Thread listenerThread = new Thread(manager);
        listenerThread.start();

        Window pacManWindow = new Window();
        pacManWindow.setLocationRelativeTo(null);
        pacManWindow.setVisible(true);
        pacManWindow.setTitle("Pa c-Man");
        pacManWindow.setSize(635, 775);
        pacManWindow.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

         try {
            listenerThread.join();
        } catch (InterruptedException i) {
            System.out.println(i);
        }

    }
}