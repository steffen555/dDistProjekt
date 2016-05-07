import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class WebEventHistory extends Thread implements IEventHistory {

    protected LinkedBlockingQueue<MyTextEvent> eventHistory = new LinkedBlockingQueue<MyTextEvent>();
    private Socket socket;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private Client client;
    private Server server;
    private DistributedTextEditor dte;
    private ArrayList<MyTextEvent> textEvents;
    private boolean justContinue;

    public WebEventHistory(int port, DistributedTextEditor distributedTextEditor) {
        this.client = new Client(socket, port);
        this.server = new Server(socket, port);
        dte = distributedTextEditor;
        textEvents = new ArrayList<MyTextEvent>();
        justContinue = true;
    }

    private void addTextEventToList(MyTextEvent textEvent) {
        if (textEvents.size() > 0) {
            MyTextEvent latest = textEvents.get(textEvents.size() - 1);
            boolean trouble = !LogicClock.happenedBefore(latest, textEvent);
            if (trouble) {
                justContinue = false;
                System.out.println("Wow! Relax, mate!");
                boolean noWorries = false;
                for (int i = textEvents.size() - 1; noWorries; i--) {
                    eventHistory.add(textEvents.get(i).getUndoEvent());
                    if (LogicClock.happenedBefore(textEvents.get(i - 1), textEvent)) {
                        eventHistory.add(textEvent);
                        for (int j = i; i < textEvents.size() - 1; i++) {
                            eventHistory.add(textEvents.get(j));
                        }
                        noWorries = true;
                        justContinue = true;
                    }
                }

                justContinue = true;
            }
        }
        textEvents.add(textEvent);
    }

    @Override
    public MyTextEvent take() throws InterruptedException {
        MyTextEvent mte = eventHistory.take();
        addTextEventToList(mte);
        System.out.println("Received MyTextEvent with time " + mte.getTimeStamp());
        LogicClock.setToMax(mte.getTimeStamp());
        return mte;
    }

    @Override
    public void add(MyTextEvent textEvent) {
        addTextEventToList(textEvent);
        try {
            if (output == null) {
                output = new ObjectOutputStream(socket.getOutputStream());
            }
            output.writeObject(textEvent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        socket = server.run();
    }

    public void startClient(String serverName) {
        client.setServerName(serverName);
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
            if (socket != null && justContinue) {
                try {
                    if (input == null) {
                        input = new ObjectInputStream(socket.getInputStream());
                    }
                    MyTextEvent inputEvent = (MyTextEvent) input.readObject();
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
