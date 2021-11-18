import java.net.*;
import java.io.*;
import org.json.simple.JSONObject;

public class SockManager {
    public enum ClientType {
        OBSERVER, PLAYER
    }

    // initialize socket output stream
    private Socket socket = null;
    private DataOutputStream out = null;
    private ClientType type;

    private void buildSocket(String address, int port) {
        // establish a connection
        try {
            socket = new Socket(address, port);
            System.out.println("Connected");

            // sends output to the socket
            out = new DataOutputStream(socket.getOutputStream());
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
        try {
            JSONObject json = new JSONObject();
            json.put("init", "player");
            out.writeUTF(json.toString());
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    // constructor to put ip address and port
    public SockManager(String address, int port, int observedPlayer) {
        type = ClientType.OBSERVER;
        buildSocket(address, port);

        try {
            JSONObject json = new JSONObject();
            json.put("init", "player");
            json.put("observee", observedPlayer);
            out.writeUTF(json.toString());
        } catch (IOException i) {
            System.out.println(i);
        }

    }

    // cerrar conexión
    public void closeSocket() {
        try {
            out.close();
            socket.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }
}