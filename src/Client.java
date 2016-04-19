import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

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
    protected int portNumber = 40499;

    /**
     * Will print out the IP address of the local host on which this client runs.
     */
    protected void printLocalHostAddress() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            String localhostAddress = localhost.getHostAddress();
            System.out.println("I'm a client running with IP address " + localhostAddress);
        } catch (UnknownHostException e) {
            System.err.println("Cannot resolve the Internet address of the local host.");
            System.err.println(e);
            System.exit(-1);
        }
    }

    /**
     * Connects to the server on IP address serverName and port number portNumber.
     */
    protected Socket connectToServer(String serverName) {
        Socket res = null;
        try {
            res = new Socket(serverName, portNumber);
        } catch (IOException e) {
            // We return null on IOExceptions
        }
        return res;
    }

    public void run(String serverName) {
        System.out.println("Hello world!");
        System.out.println("Type CTRL-D to shut down the client.");

        printLocalHostAddress();

        Socket socket = connectToServer(serverName);

        if (socket != null) {
            System.out.println("Connected to " + socket);
            try {
                // For reading from standard input
                BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
                // For sending text to the server
                PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
                String s;
                // Read from standard input and send to server
                // Ctrl-D terminates the connection
                System.out.print("Type something for the server and then RETURN> ");
                while ((s = stdin.readLine()) != null && !toServer.checkError()) {
                    toServer.println(s);
                    receiveResponse(socket);
                    System.out.print("Type something for the server and then RETURN> ");
                }
                socket.close();
            } catch (IOException e) {
                // We ignore IOExceptions
            }
        }

        System.out.println("Goodbuy world!");
    }

    public static void receiveResponse(Socket socket) {
        System.out.println("Waiting for response");
        try {
            BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String s;
            // Read and print what the client is sending
            while ((s = fromClient.readLine()) != null) { // Ctrl-D terminates the connection
                System.out.println("From the server: " + s);
                break;
            }
        } catch (IOException e) {
            // We report but otherwise ignore IOExceptions
            System.err.println(e);
        }
    }

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.run(args[0]);
    }

}
