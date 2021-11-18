import java.net.*;
import java.util.Set;
import java.io.*;
import com.github.cliftonlabs.json_simple.*;

public class SockManager implements Runnable {
    public enum ClientType {
        OBSERVER, PLAYER
    }

    // initialize socket output stream
    private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private ClientType type;
    private boolean quit = false;
    private boolean offline = false;

    private void buildSocket(String address, int port) {
        // establish a connection
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");

            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(new BufferedInputStream(socket.getInputStream())));
        } catch (UnknownHostException u) {
            System.out.println(u);
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    // Este constructor conecta un jugador
    public SockManager(String address, int port) {
        type = ClientType.PLAYER;
        buildSocket(address, port);
        JsonObject json = new JsonObject();
        json.put("init", "player");
        try {
            out.println(Jsoner.serialize(json));
        } catch (NullPointerException n) {
            System.out.println(n);
            System.out.println("No se pudo abrir el socket.");
            offline = true;
        }

    }

    // Este constructor conecta un observador al jugador observedPlayer
    public SockManager(String address, int port, int observedPlayer) {
        type = ClientType.OBSERVER;
        buildSocket(address, port);

        JsonObject json = new JsonObject();
        json.put("init", "observer");
        json.put("observee", observedPlayer);
        try {
            out.println(Jsoner.serialize(json));
        } catch (NullPointerException n) {
            System.out.println(n);
            System.out.println("No se pudo abrir el socket.");
            offline = true;
        }
    }

    public void sendInfo(String infoMsg) {
        JsonObject json = new JsonObject();
        json.put("Info", infoMsg);
        sendJSON(json);
    }

    public void sendJSON(JsonObject json) {
        if (offline) {
            return;
        }
        synchronized (socket) {
            out.println(Jsoner.serialize(json));
        }
    }

    // cerrar conexión
    public void closeSocket() {
        synchronized (socket) {
            quit = true;
            if (socket.isClosed()) {
                return;
            }
            try {
                out.close();
                socket.close();
            } catch (IOException i) {
                System.out.println(i);
            }
        }
    }

    public void run() {
        if (offline) {
            return;
        }
        while (!quit) {
            try {
                JsonObject json = (JsonObject) Jsoner.deserialize(in);
                Set<String> keys = json.keySet();

                if (keys.contains("Error")) {
                    final JsonKey errorKey = Jsoner.mintJsonKey("Error", "Wrongful socket operation with server.");
                    quit = true;
                    System.out.println("Error en socket: " + json.getString(errorKey));
                    closeSocket();
                }

            } catch (JsonException j) {
                System.out.println(j);
                quit = true;
            }
        }
        System.out.println("Closing connection");
    }
}
