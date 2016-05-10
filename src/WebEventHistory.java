import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
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

    public boolean addTextEventToList(MyTextEvent textEvent) {
        System.out.println("Event list size: " + textEvents.size());
        if (textEvents.size() > 0) {
            MyTextEvent latest = textEvents.get(textEvents.size() - 1);
            boolean iAmTheGreatest = true;
            if (textEvent.getOffset() == latest.getOffset() && textEvent.getClass() == TextInsertEvent.class && latest.getClass() == TextInsertEvent.class) {
                TextInsertEvent textInsertEvent = (TextInsertEvent) textEvent;
                TextInsertEvent latestTextInsertEvent = (TextInsertEvent) latest;
                iAmTheGreatest = textInsertEvent.getText().hashCode() < latestTextInsertEvent.getText().hashCode();
            }
            boolean trouble = !LogicClock.happenedBefore(latest, textEvent) && latest.getOffset() < textEvent.getOffset();
            if (trouble && iAmTheGreatest) {
                undo(textEvent);
                System.out.println("Concurrency has been detected. But don't worry! We'll fix it.");
                justContinue = false;
                int lastBefore = textEvents.size() - 1;
                while (!LogicClock.happenedBefore(textEvents.get(lastBefore), textEvent))
                    lastBefore--;
                System.out.println("Last event before the received: " + lastBefore + ", received events: " + textEvents.size());
                List<MyTextEvent> preConcurrent = textEvents.subList(lastBefore + 1, textEvents.size());
                CopyOnWriteArrayList<MyTextEvent> concurrent = new CopyOnWriteArrayList<MyTextEvent>(preConcurrent);
                System.out.println("Concurrent events: " + concurrent.size());
                for (int i = concurrent.size() - 1; i >= 0; i--) {
                    undo(concurrent.get(i));
                }

                redo(textEvent);
                textEvents.add(lastBefore, textEvent);
                for (MyTextEvent aConcurrent : concurrent) {
                    redo(aConcurrent);
                }
                justContinue = true;
                return true;
            }
        }
        textEvents.add(textEvent);
        return false;
    }

    private void undo(MyTextEvent mte) {
        MyTextEvent undoEvent = mte.getUndoEvent();
        undoEvent.setRedoable(false);
        eventHistory.add(undoEvent);
        System.out.println("Undid " + mte.toString() + "; did " + undoEvent.toString());
    }

    private void redo(MyTextEvent mte) {
        mte.setRedoable(false);
        eventHistory.add(mte);
        System.out.println("Redid " + mte.toString());
    }

    public void undoLatestEvent() {
        System.out.println("Whoops!");
    }

    @Override
    public MyTextEvent take() throws InterruptedException {
        MyTextEvent mte = eventHistory.take();
        System.out.println("Received " + mte.toString() + " Time: " + mte.getTimeStamp() + ",  redoable: " + mte.isRedoable());
        if (mte.isRedoable() && addTextEventToList(mte)) {
        }
        LogicClock.setToMax(mte.getTimeStamp());
        return mte;
    }

    @Override
    public void add(MyTextEvent textEvent) {
        addTextEventToList(textEvent);
        textEvent.setRedoable(true);
        send(textEvent);
    }

    private void send(MyTextEvent mte) {
        try {
            if (output == null) {
                output = new ObjectOutputStream(socket.getOutputStream());
            }
            output.writeObject(mte);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startServer() {
        socket = server.run();
    }

    public boolean startClient(String serverName) {
        client.setServerName(serverName);
        socket = client.run();
        if (socket == null) {
            System.out.println("Error while connecting, aborting.");
            return false;
        }
        return true;
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
