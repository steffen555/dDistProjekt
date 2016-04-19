import java.io.IOException;
import java.net.Socket;

/**
 * A very simple client which will connect to a server, read from a prompt and
 * send the text to the server.
 */

public class Client {

    /*
     * Your group should use port number 40HGG, where H is your "hold nummer (1,2 or 3)
     * and GG is gruppe nummer 00, 01, 02, ... So, if you are in group 3 on hold 1 you
     * use the port number 40103. This will avoid the unfortunate situation that you
     * connect to each others servers.
     */
    protected int portNumber;
    private Socket socket;

    public Client(Socket socket, int port) {
        this.socket = socket;
        this.portNumber = port;
    }

    public void run(String serverName) {
        System.out.println("Connecting to server on " + serverName);
        try {
            socket = new Socket(serverName, portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (socket != null)
            System.out.println("Connected to server");
        else
            System.err.println("Connection failed");
    }
}
