import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;

public class Communicator {

    protected int portNumber;
    private HashMap<Socket, ServerSocket> sockets;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public Communicator(int port) {
        sockets = new HashMap<Socket, ServerSocket>();
        this.portNumber = port;
    }

    public boolean connect(String serverName) {
        System.out.println("Connecting to server on " + serverName);
        try {
            sockets.put(new Socket(serverName, portNumber), null);
            System.out.println("Connected to server");
            return true;
        } catch (IOException e) {
            //e.printStackTrace();
        }
        System.err.println("Connection failed");
        return false;
    }

    public void listen() {
        Socket socket;
        ServerSocket serverSocket = null;
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
                sockets.put(socket, serverSocket);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deregister() {
        for (Socket socket : sockets.keySet()) {
            ServerSocket serverSocket = sockets.get(socket);
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
                if (socket != null) {
                    socket.close();
                }
                sockets.remove(socket);
            } catch (IOException e) {
                System.err.println(e.toString());
            }
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
        for (Socket socket : sockets.keySet()) {
            try {
                output = new ObjectOutputStream(socket.getOutputStream());
                output.writeObject(o);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Object receiveObject() throws IOException, ClassNotFoundException {
        for (Socket socket : sockets.keySet()) {
            input = new ObjectInputStream(socket.getInputStream());
            return input.readObject();
        }
        return null;
    }
}
