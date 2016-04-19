import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Alexandra on 19/04/16.
 */
public class Server {

    protected int portNumber = 40501;
    protected ServerSocket serverSocket;

    protected String printLocalHostAddress() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            String localhostAddress = localhost.getHostAddress();
            return localhostAddress;
        } catch (UnknownHostException e) {
            System.err.println("Cannot resolve the Internet address of the local host.");
            System.err.println(e);
            System.exit(-1);
        }
        return "No connection";
    }

    protected void registerOnPort() {
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            serverSocket = null;
            System.err.println("Cannot open server socket on port number" + portNumber);
            System.err.println(e);
            System.exit(-1);
        }
    }

    public void deregisterOnPort() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
                serverSocket = null;
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    protected Socket waitForConnectionFromClient() {
        Socket res = null;
        try {
            res = serverSocket.accept();
        } catch (IOException e) {
            // We return null on IOExceptions
        }
        return res;
    }

    public void run() {
        printLocalHostAddress();

        registerOnPort();

        System.out.println("hej");

        /*while (true) {
            Socket socket = waitForConnectionFromClient();

            if (socket != null) {
                System.out.println("Server from " + socket);
                try {
                    BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String s;
                    // Read and print what the client is sending
                    while ((s = fromClient.readLine()) != null) { // Ctrl-D terminates the connection
                        System.out.println("From the client: " + s);
                    }
                    socket.close();
                } catch (IOException e) {
                    // We report but otherwise ignore IOExceptions
                    System.err.println(e);
                }
                System.out.println("Server closed by client.");
            } else {
                // We rather agressively terminate the server on the first connection exception
                break;
            }
        }*/
    }

}
