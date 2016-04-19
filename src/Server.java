import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {

    protected int portNumber = 40501;
    protected ServerSocket serverSocket;
    private Socket socket;

    public Server(Socket socket) {
        this.socket = socket;
    }

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

    protected void waitForConnectionFromClient() {
        try {
            socket = serverSocket.accept();
        } catch (IOException e) {
            // We return null on IOExceptions
        }
    }

    public void run() {
        printLocalHostAddress();
        registerOnPort();
        System.out.println("Waiting for client");
        while (socket == null) {
            waitForConnectionFromClient();
        }
        System.out.println("Connected to client");
    }

}
