import javax.swing.*;
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
    private final LinkedBlockingQueue<Event> eventQueue;
    private final ArrayList<TextEvent> events;
    private ServerSocket closeableServerSocket;
    private JLabel label1;

    Communicator(int port, ArrayList<TextEvent> events) {
        sockets = new HashMap<Socket, ServerSocket>();
        receivers = new HashMap<Socket, EventReceiver>();
        this.portNumber = port;
        eventQueue = new LinkedBlockingQueue<Event>();
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
            label1.setText(createConnectionsString());
            return true;
        } catch (IOException e) {
            //e.printStackTrace();
        }
        System.err.println("Connection failed");
        label1.setText(createConnectionsString());
        return false;
    }

    public void run() {
        Socket socket = null;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(portNumber);
            closeableServerSocket = serverSocket;
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
                if (socket != null) {
                    sockets.put(socket, serverSocket);
                    closeableServerSocket = serverSocket;
                    System.out.println("Connected to client");
                    addReceiver(socket);
                    for (TextEvent mte : events) {
                        send(mte, socket);
                    }
                }
            } catch (SocketException e) {
                if (socket != null) {
                    forgetAbout(socket);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void deregister() {
        try {
            closeableServerSocket.close();
            System.out.println("Closed server socket.");
        } catch (NullPointerException e) {
            System.err.println("Serversocket already closed.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (Socket socket : sockets.keySet()) {
            try {
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
                //e.printStackTrace();
                //System.err.println("Interrupted receriveObject().");
            }
        }
    }

    public void disconnect(Socket socket) {
        EventReceiver er = receivers.get(socket);
        if (er != null)
            er.interrupt();
        receivers.remove(socket);
        label1.setText(createConnectionsString());
    }

    private void addReceiver(Socket socket) {
        receivers.put(socket, new EventReceiver(socket, eventQueue, this));
        receivers.get(socket).start();
        label1.setText(createConnectionsString());
    }

    private void forgetAbout(Socket s) {
        System.out.println("Forgetting " + s.toString());
        sockets.remove(s);
        receivers.remove(s);
        outputs.remove(s);
    }

    public void addConnectionChangeListener(JLabel label) {
        label1 = label;
    }

    ;

    private String createConnectionsString() {
        String string;
        if (sockets.size() == 0) {
            string = "No connections.";
        } else {
            string = "Connected to: ";
            for (Socket socket : sockets.keySet()) {
                string = string + socket.getInetAddress() + ":" + socket.getPort() + ", ";
            }
        }
        return string;
    }

    ;
}