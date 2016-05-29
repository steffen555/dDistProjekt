import javax.swing.*;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressWarnings("Convert2Diamond")
class Communicator extends Thread {

    private final int portNumber;
    private final HashMap<Socket, ServerSocket> sockets;
    private final HashMap<Socket, EventReceiver> receivers;
    private final HashMap<Socket, ObjectOutputStream> outputs;
    private final HashMap<Socket, Set<String>> connections;
    private final LinkedBlockingQueue<InfoEvent> infoEventQueue;
    private final LinkedBlockingQueue<TextEvent> textEventQueue;
    private final ArrayList<TextEvent> events;
    private ServerSocket closeableServerSocket;
    private JLabel label1;
    private Set<String> myConnections;
    private Set<String> familyOfLastLostConnection;

    Communicator(int port, ArrayList<TextEvent> events) {
        sockets = new HashMap<Socket, ServerSocket>();
        receivers = new HashMap<Socket, EventReceiver>();
        this.portNumber = port;
        textEventQueue = new LinkedBlockingQueue<>();
        infoEventQueue = new LinkedBlockingQueue<>();
        outputs = new HashMap<Socket, ObjectOutputStream>();
        this.events = events;
        connections = new HashMap<Socket, Set<String>>();
        myConnections = new HashSet<String>();
        startActingOnInfo();
        familyOfLastLostConnection = new HashSet<String>();
    }

    private void startActingOnInfo() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    InfoEvent info = null;
                    try {
                        info = infoEventQueue.take();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (ConnectionsEvent.class.isAssignableFrom(info.getClass())) {
                        ConnectionsEvent conn = (ConnectionsEvent) info;
                        conn.addConnection(getServerAddress());
                        ConnectionsEvent my = new ConnectionsEvent(DistributedTextEditor.getId(), getServerAddress());
                        myConnections.add(conn.getIp());
                        my.addConnections(myConnections);
                        System.out.println("Received NewConnectionEvent");
                        System.out.println("recieved ID: " + conn.getIp());
                        System.out.println("recieved connections size: " + conn.getConnections().size());
                        System.out.println("recieved connections to string: " + conn.getConnections().toString());
                        //Do what we do with NewConnectionEvents
                        connections.put(conn.getReceivingSocket(), conn.getConnections());
                        System.out.println("myConnections to string: " + my.getConnections().toString());
                        sendExcept(my, conn.getReceivingSocket());
                        label1.setText(createConnectionsString());
                    }
                }
            }
        });
        t.start();
    }

    boolean connect(String serverName) {
        System.out.println("Connecting to server on " + serverName);
        try {
            Socket tempSocket = new Socket(serverName, portNumber);
            sockets.put(tempSocket, null);
            addReceiver(tempSocket);
            ConnectionsEvent my = new ConnectionsEvent(DistributedTextEditor.getId(), getServerAddress());
            my.addConnections(myConnections);
            send(my, tempSocket);
            System.out.println("Connected to server");
            label1.setText(createConnectionsString());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
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
                    ConnectionsEvent my = new ConnectionsEvent(DistributedTextEditor.getId(), getServerAddress());
                    my.addConnections(myConnections);
                    send(my, socket);
                    boolean knownIp = false;
                    for (String s : familyOfLastLostConnection) {
                        if (socket.getInetAddress().equals(s))
                            knownIp = true;
                    }
                    if (!knownIp) {
                        for (TextEvent mte : events) {
                            send(mte, socket);
                        }
                    }
                    label1.setText(createConnectionsString());
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
                forgetAbout(socket);
                label1.setText(createConnectionsString());
            } catch (IOException e) {
                System.err.println(e.toString());
            }
        }
    }

    String getServerAddress() {
        try {
            //InetAddress localhost = InetAddress.getLocalHost();
            InetAddress localhost = getCurrentIp();
            return localhost.getHostAddress();
        } catch (Exception e) { //UnknownHostEception
            System.err.println("Cannot resolve the Internet address of the local host.");
            System.err.println(e.toString());
            System.exit(-1);
        }
        return "Something went wrong.";
    }

    public InetAddress getCurrentIp() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) networkInterfaces
                        .nextElement();
                Enumeration<InetAddress> nias = ni.getInetAddresses();
                while (nias.hasMoreElements()) {
                    InetAddress ia = (InetAddress) nias.nextElement();
                    if (!ia.isLinkLocalAddress()
                            && !ia.isLoopbackAddress()
                            && ia instanceof Inet4Address) {
                        return ia;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            //LOG.error("unable to get current IP " + e.getMessage(), e);
        }
        return null;
    }

    void send(Event o) {
        for (Socket socket : sockets.keySet()) {
            send(o, socket);
        }
    }

    void sendExcept(Event o, Socket s) {
        for (Socket socket : sockets.keySet()) {
            if (socket != s)
                send(o, socket);
        }
    }

    private void send(Event o, Socket socket) {
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
                return textEventQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect(Socket socket) {
        EventReceiver er = receivers.get(socket);
        if (er != null)
            er.interrupt();
        label1.setText(createConnectionsString());
    }

    private void addReceiver(Socket socket) {
        receivers.put(socket, new EventReceiver(socket, textEventQueue, infoEventQueue, this));
        receivers.get(socket).start();
        label1.setText(createConnectionsString());
    }

    private void forgetAbout(Socket s) {
        System.out.println("Forgetting " + s.toString());
        sockets.remove(s);
        receivers.remove(s);
        outputs.remove(s);
        connections.remove(s);
        label1.setText(createConnectionsString());
    }

    public void addConnectionChangeListener(JLabel label) {
        label1 = label;
    }

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

    public void connectToNeighbour(Socket s) {
        familyOfLastLostConnection = connections.get(s);
        forgetAbout(s);
        String largest;
        if (familyOfLastLostConnection.size() != 0) {
            largest = Collections.max(familyOfLastLostConnection);
            if (!largest.equals(getServerAddress()))
                connect(largest);
        }
        label1.setText(createConnectionsString());
    }
}