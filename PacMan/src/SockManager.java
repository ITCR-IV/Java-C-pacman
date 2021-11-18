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
    PlayingField field;

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
    public SockManager(PlayingField playingField, String address, int port) {
        field = playingField;
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
    public SockManager(PlayingField playingField, String address, int port, int observedPlayer) {
        field = playingField;
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
            char[] msg = new char[6000];
            try {
                in.read(msg);
                System.out.println(msg);
                JsonObject json = (JsonObject) Jsoner.deserialize(new String(msg).replace("\0", ""));
                Set<String> keys = json.keySet();

                if (keys.contains("Error")) {
                    final JsonKey errorKey = Jsoner.mintJsonKey("Error", "Wrongful socket operation with server.");
                    quit = true;
                    System.out.println("Error en socket: " + json.getString(errorKey));
                    closeSocket();
                } else if (keys.contains("ghost")) {
                    System.out.println("Request de fantasma recibido: " + json.toJson());
                    final JsonKey ghostKey = Jsoner.mintJsonKey("ghost", "Clyde");
                    String ghost = json.getString(ghostKey);
                    System.out.println("Fantasma: " + ghost);
                    field.addGhost(ghost);
                } else if (keys.contains("pastillas")) {
                    System.out.println("Request de pastillas recibido: " + json.toJson());
                    final JsonKey pastiKey = Jsoner.mintJsonKey("pastillas", "grandes");
                    String tamano = json.getString(pastiKey);
                    System.out.println("Pastillas: " + tamano);
                    if (tamano.equals("peques")) {
                        field.resetPellets();
                    } else if (tamano.equals("grandes")) {
                        field.resetPowerPellets();
                    }
                } else if (keys.contains("fruta")) {
                    System.out.println("Request de pastillas recibido: " + json.toJson());
                    final JsonKey frutaKey = Jsoner.mintJsonKey("fruta", 1000);
                    int valor = json.getInteger(frutaKey);
                    System.out.println("Fruta: " + valor);
                    if (valor > 6000) {
                        field.addFruit("Key", valor);
                    } else if (valor > 5000) {
                        field.addFruit("Orange", valor);
                    } else if (valor > 4000) {
                        field.addFruit("Melon", valor);
                    } else if (valor > 3000) {
                        field.addFruit("Strawberry", valor);
                    } else if (valor > 2000) {
                        field.addFruit("Galaga", valor);
                    } else if (valor > 1000) {
                        field.addFruit("Cherry", valor);
                    } else {
                        field.addFruit("Apple", valor);
                    }

                } else if (keys.contains("aumentar")) {

                } else if (keys.contains("disminuir")) {

                } else {
                    // AQUÍ LLEGAN TODOS LOS MENSAJES QUE LE CORRESPONDEN A LOS OBSERVERS
                }

            } catch (JsonException j) {
                System.out.println(j);
                quit = true;
            } catch (IOException i) {
                System.out.println(i);
                quit = true;
            }
        }
        System.out.println("Closing connection");
    }
}
