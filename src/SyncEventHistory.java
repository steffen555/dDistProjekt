import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class SyncEventHistory extends Thread implements IEventHistory {

    protected LinkedBlockingQueue<IDTextEvent> eventHistory = new LinkedBlockingQueue<IDTextEvent>();
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Client client;
    private Server server;
    private DistributedTextEditor dte;
    private int id;

    public SyncEventHistory(int port, DistributedTextEditor distributedTextEditor) {
        this.client = new Client(socket, port);
        this.server = new Server(socket, port);
        dte = distributedTextEditor;
    }

    @Override
    public MyTextEvent take() throws InterruptedException {
        IDTextEvent idte = eventHistory.take();
        if (idte.getID() != id)
            return idte.getTextEvent();
        return null;
    }

    @Override
    public void add(MyTextEvent textEvent) {
        try {
            if (output == null) {
                output = new ObjectOutputStream(socket.getOutputStream());
            }
            IDTextEvent idte = new IDTextEvent(textEvent, id);
            output.writeObject(idte);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //eventHistory.add(textEvent);
    }

    public void startServer() {
        id = 42;
        socket = server.run();
    }

    public void startClient(String servername) {
        id = 1337;
        client.setServerName(servername);
        socket = client.run();
    }

    public String printServerAddress() {
        return server.printServerAddress();
    }

    public void deregisterOnPort() {
        server.deregisterOnPort();
        if (socket != null) {
            try {
                socket.close();
                socket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            if (socket != null) {
                try {
                    if (input == null) {
                        input = new ObjectInputStream(socket.getInputStream());
                    }
                    IDTextEvent inputEvent = (IDTextEvent) input.readObject();
                    eventHistory.add(inputEvent);
                } catch (IOException e) {
                    dte.Disconnect.actionPerformed(null);
                    return;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
