import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {

    protected int portNumber;
    protected ServerSocket serverSocket;
    private Socket socket;

    public Server(Socket socket, int port) {
        this.socket = socket;
        this.portNumber = port;
    }

    protected String printServerAddress() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            String localhostAddress = localhost.getHostAddress();
            return localhostAddress;
        } catch (UnknownHostException e) {
            System.err.println("Cannot resolve the Internet address of the local host.");
            System.err.println(e);
            System.exit(-1);
        }
        return "Something went wrong.";
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
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Socket run() {
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Waiting for client");
        while (true) {
            try {
                socket = serverSocket.accept();
                System.out.println("Connected to client");
                return socket;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
