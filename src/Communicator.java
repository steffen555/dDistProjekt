import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Communicator {

    protected int portNumber;
    private Socket socket;
    ServerSocket serverSocket = null;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public Communicator(int port) {
        this.portNumber = port;
    }

    public boolean connect(String serverName) {
        System.out.println("Connecting to server on " + serverName);
        try {
            socket = new Socket(serverName, portNumber);
            System.out.println("Connected to server");
            return true;
        } catch (IOException e) {
            //e.printStackTrace();
        }
        System.err.println("Connection failed");
        return false;
    }

    public Socket listen() {
        socket = new Socket();
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

    public void deregister() {
        try {
            if (serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    public String getServerAddress() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            return localhost.getHostAddress();
        } catch (UnknownHostException e) {
            System.err.println("Cannot resolve the Internet address of the local host.");
            System.err.println(e.toString());
            System.exit(-1);
        }
        return "Something went wrong.";
    }

    public void send(Object o) {
        try {
            if (output == null) {
                output = new ObjectOutputStream(socket.getOutputStream());
            }
            output.writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object receiveObject() throws IOException, ClassNotFoundException {
        if (input == null)
            input = new ObjectInputStream(socket.getInputStream());
        return input.readObject();
    }
}
