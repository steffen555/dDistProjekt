import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class WebEventHistory extends Thread implements IEventHistory {

    private final LinkedBlockingQueue<TextEvent> eventHistory = new LinkedBlockingQueue<TextEvent>();
    private final ArrayList<TextEvent> textEvents;
    private boolean justContinue;
    private final Communicator comm;

    public WebEventHistory(int port) {
        textEvents = new ArrayList<TextEvent>();
        justContinue = true;
        comm = new Communicator(port, textEvents);
    }

    private boolean addTextEventToList(TextEvent textEvent) {
        System.out.println("Event list size: " + textEvents.size());
        if (textEvents.size() > 0) {
            TextEvent latest = textEvents.get(textEvents.size() - 1);
            boolean iAmTheGreatest = true;
            if (textEvent.getOffset() == latest.getOffset() && textEvent.getClass() == TextInsertEvent.class && latest.getClass() == TextInsertEvent.class) {
                TextInsertEvent textInsertEvent = (TextInsertEvent) textEvent;
                TextInsertEvent latestTextInsertEvent = (TextInsertEvent) latest;
                iAmTheGreatest = textInsertEvent.getText().hashCode() < latestTextInsertEvent.getText().hashCode();
            }
            boolean trouble = LogicClock.notHappenedBefore(latest, textEvent) && latest.getOffset() <= textEvent.getOffset();
            if (trouble && iAmTheGreatest) {
                justContinue = false;
                undo(textEvent);
                int lastBefore = textEvents.size() - 1;
                while (LogicClock.notHappenedBefore(textEvents.get(lastBefore), textEvent))
                    lastBefore--;
                List<TextEvent> preConcurrent = textEvents.subList(lastBefore + 1, textEvents.size());
                CopyOnWriteArrayList<TextEvent> concurrent = new CopyOnWriteArrayList<TextEvent>(preConcurrent);
                for (int i = concurrent.size() - 1; i >= 0; i--) {
                    undo(concurrent.get(i));
                }

                redo(textEvent);
                textEvents.add(lastBefore, textEvent);
                for (TextEvent aConcurrent : concurrent) {
                    redo(aConcurrent);
                }
                justContinue = true;
                return true;
            }
        }
        textEvents.add(textEvent);
        return false;
    }

    private void undo(TextEvent mte) {
        TextEvent undoEvent = mte.getUndoEvent();
        undoEvent.setRedoable(false);
        eventHistory.add(undoEvent);
    }

    private void redo(TextEvent mte) {
        mte.setRedoable(false);
        eventHistory.add(mte);
    }

    public void undoLatestEvent() {
        TextEvent toUndo = textEvents.remove(textEvents.size() - 1);
        undo(toUndo);
        comm.send(toUndo.getUndoEvent());
    }

    @Override
    public TextEvent take() {
        TextEvent mte = null;
        try {
            mte = eventHistory.take();
            if (mte.isRedoable())
                addTextEventToList(mte);
            LogicClock.setToMax(mte.getTimeStamp());
            return mte;
        } catch (InterruptedException e) {
            //e.printStackTrace();
            //System.err.println("Interrupted take().");
        }
        return null;
    }

    @Override
    public void add(TextEvent textEvent) {
        while (!justContinue) {
            int i = 0;
            i++;
            if (i < 0)
                System.out.println("hello");
        }
        addTextEventToList(textEvent);
        textEvent.setRedoable(true);
        comm.send(textEvent);
    }

    public void startServer() {
        comm.start();
    }

    public boolean startClient(String serverName) {
        return comm.connect(serverName);
    }

    public String printServerAddress() {
        return comm.getServerAddress();
    }

    public void deregisterOnPort() {
        comm.deregister();
        comm.interrupt();
    }

    @Override
    public void run() {
        while (true) {
            if (justContinue) {
                eventHistory.add((TextEvent) comm.receiveObject());
            }
        }
    }

    public void addConnectionChangeListener(JLabel label1) {
        comm.addConnectionChangeListener(label1);
    }

    ;

    public void addDisableConnect(Action connect) {
        comm.addDisableConnect(connect);
    }
}