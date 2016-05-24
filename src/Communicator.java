import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressWarnings("Convert2Diamond")
class Communicator extends Thread {

    private final int portNumber;
    private final HashMap<Socket, ServerSocket> sockets;
    private final HashMap<Socket, EventReceiver> receivers;
    private final HashMap<Socket, ObjectOutputStream> outputs;
    @SuppressWarnings("unused")
    private ObjectOutputStream output;
    @SuppressWarnings("unused")
    private ObjectInputStream input;
    private final LinkedBlockingQueue<Object> eventQueue;
    private final ArrayList<TextEvent> events;

    Communicator(int port, ArrayList<TextEvent> events) {
        sockets = new HashMap<Socket, ServerSocket>();
        receivers = new HashMap<Socket, EventReceiver>();
        this.portNumber = port;
        eventQueue = new LinkedBlockingQueue<Object>();
        outputs = new HashMap<Socket, ObjectOutputStream>();
        this.events = events;
    }

    boolean connect(String serverName) {
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

    public void run() {
        Socket socket = null;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Waiting for client");
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                if (serverSocket != null) {
                    socket = serverSocket.accept();
                }
                System.out.println("Connected to client");
                sockets.put(socket, serverSocket);
                addReceiver(socket);
                for (TextEvent mte : events) {
                    send(mte, socket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void deregister() {
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

    String getServerAddress() {
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

    void send(Object o) {
        for (Socket socket : sockets.keySet()) {
            send(o, socket);
        }
    }

    void sendExcept(Object o, Socket s) {
        for (Socket socket : sockets.keySet()) {
            if (socket != s)
                send(o, socket);
        }
    }

    private void send(Object o, Socket socket) {
        try {
            if (!outputs.containsKey(socket))
                outputs.put(socket, new ObjectOutputStream(socket.getOutputStream()));
            outputs.get(socket).writeObject(o);
        } catch (IOException e) {
            e.printStackTrace();
            if (e.getClass() == SocketException.class)
                forgetAbout(socket);
        }
    }

    Object receiveObject() {
        while (true) {
            try {
                return eventQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect(Socket socket) {
        EventReceiver er = receivers.get(socket);
        if (er != null)
            er.interrupt();
        receivers.remove(socket);
    }

    private void addReceiver(Socket socket) {
        receivers.put(socket, new EventReceiver(socket, eventQueue, this));
        receivers.get(socket).start();
    }

    private void forgetAbout(Socket s) {
        System.out.println("Forgetting " + s.toString());
        sockets.remove(s);
        receivers.remove(s);
        outputs.remove(s);
    }
}
