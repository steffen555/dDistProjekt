import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Communicator {

    protected int portNumber;
    private HashMap<Socket, ServerSocket> sockets;
    private HashMap<Socket, EventReceiver> receivers;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private LinkedBlockingQueue eventQueue;

    public Communicator(int port) {
        sockets = new HashMap<Socket, ServerSocket>();
        receivers = new HashMap<Socket, EventReceiver>();
        this.portNumber = port;
    }

    public boolean connect(String serverName) {
        System.out.println("Connecting to server on " + serverName);
        try {
            Socket tempSocket = new Socket(serverName, portNumber);
            sockets.put(tempSocket, null);
            addReceiver(tempSocket);
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
                addReceiver(socket);
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

    public void disconnect(Socket socket) {
        receivers.get(socket).interrupt();
        receivers.remove(socket);
    }

    public void addReceiver(Socket socket) {
        receivers.put(socket, new EventReceiver(socket, eventQueue, this));
        receivers.get(socket).start();
    }
}
