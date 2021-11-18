import java.net.*;
import java.util.Set;
import java.io.*;
import com.github.cliftonlabs.json_simple.*;

public class SockManager implements Runnable {
    public enum ClientType {
        OBSERVER, PLAYER
    }

    // private enum PossibleKeys implements JsonKey {
    // ERROR("Error");

    // private final Object value;

    // PossibleKeys(final Object value) {
    // this.value = value;
    // }

    // @Override
    // public String getKey() {
    // return this.name();
    // }

    // @Override
    // public Object getValue() {
    // return this.value;
    // }
    // }

    // initialize socket output stream
    private Socket socket = null;
    private PrintWriter out = null;
    private BufferedReader in = null;
    private ClientType type;
    private boolean quit = false;

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

    // constructor para jugadores
    public SockManager(String address, int port) {
        type = ClientType.PLAYER;
        buildSocket(address, port);
        JsonObject json = new JsonObject();
        json.put("init", "player");
        out.println(Jsoner.serialize(json));
    }

    // constructor to put ip address and port
    public SockManager(String address, int port, int observedPlayer) {
        type = ClientType.OBSERVER;
        buildSocket(address, port);

        JsonObject json = new JsonObject();
        json.put("init", "observer");
        json.put("observee", observedPlayer);
        out.println(Jsoner.serialize(json));

    }

    public void sendJSON(JsonObject json) {
        out.println(Jsoner.serialize(json));
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
