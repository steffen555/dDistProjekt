import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * A very simple server which will way for a connection from a client and print
 * what the client sends. When the client closes the connection, the server is
 * ready for the next client.
 */

public class Server {

    /*
     * Your group should use port number 40HGG, where H is your "hold nummer (1,2 or 3)
     * and GG is gruppe nummer 00, 01, 02, ... So, if you are in group 3 on hold 1 you
     * use the port number 40103. This will avoid the unfortunate situation that you
     * connect to each others servers.
     */
    protected int portNumber = 40499;
    protected ServerSocket serverSocket;

    /**
     * Will print out the IP address of the local host and the port on which this
     * server is accepting connections.
     */
    protected void printLocalHostAddress() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            String localhostAddress = localhost.getHostAddress();
            System.out.println("Contact this server on the IP address " + localhostAddress);
        } catch (UnknownHostException e) {
            System.err.println("Cannot resolve the Internet address of the local host.");
            System.err.println(e);
            System.exit(-1);
        }
    }

    /**
     * Will register this server on the port number portNumber. Will not start waiting
     * for connections. For this you should call waitForConnectionFromClient().
     */
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

    /**
     * Waits for the next client to connect on port number portNumber or takes the
     * next one in line in case a client is already trying to connect. Returns the
     * socket of the connection, null if there were any failures.
     */
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
        System.out.println("Hello world!");

        printLocalHostAddress();

        registerOnPort();

        while (true) {
            Socket socket = waitForConnectionFromClient();

            if (socket != null) {
                System.out.println("Connection from " + socket);
                try {
                    BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String s;
                    // Read and print what the client is sending
                    while ((s = fromClient.readLine()) != null) { // Ctrl-D terminates the connection
                        System.out.println("From the client: " + s);
                        respond(socket);
                    }
                    socket.close();
                } catch (IOException e) {
                    // We report but otherwise ignore IOExceptions
                    System.err.println(e);
                }
                System.out.println("Connection closed by client.");
            } else {
                // We rather agressively terminate the server on the first connection exception
                break;
            }
        }

        deregisterOnPort();

        System.out.println("Goodbuy world!");
    }

    private void respond(Socket socket) {
        System.out.println("Type response, please");
        try {
            // For reading from standard input
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
            // For sending text to the server
            PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
            String s;
            // Read from standard input and send to server
            // Ctrl-D terminates the connection
            System.out.print("Type something for the client and then RETURN> ");
            if ((s = stdin.readLine()) != null && !toServer.checkError()) {
                toServer.println(s);
                System.out.print("Waiting for client");
            }
        } catch (IOException e) {
            // We ignore IOExceptions
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.run();
    }

}
