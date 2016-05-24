import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressWarnings("Convert2Diamond")
class WebEventHistory extends Thread implements IEventHistory {

    private final LinkedBlockingQueue<TextEvent> eventHistory = new LinkedBlockingQueue<TextEvent>();
    private final ArrayList<TextEvent> textEvents;
    private boolean justContinue;
    private final Communicator comm;

    WebEventHistory(int port) {
        textEvents = new ArrayList<TextEvent>();
        justContinue = true;
        comm = new Communicator(port, textEvents);
    }

    @SuppressWarnings("UnusedReturnValue")
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
                System.out.println("Concurrency has been detected. But don't worry! We'll fix it.");
                justContinue = false;
                undo(textEvent);
                int lastBefore = textEvents.size() - 1;
                while (LogicClock.notHappenedBefore(textEvents.get(lastBefore), textEvent))
                    lastBefore--;
                System.out.println("Last event before the received: " + lastBefore + ", received events: " + textEvents.size());
                List<TextEvent> preConcurrent = textEvents.subList(lastBefore + 1, textEvents.size());
                CopyOnWriteArrayList<TextEvent> concurrent = new CopyOnWriteArrayList<TextEvent>(preConcurrent);
                System.out.println("Concurrent events: " + concurrent.size());
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
        System.out.println("Undid " + mte.toString() + "; did " + undoEvent.toString());
    }

    private void redo(TextEvent mte) {
        mte.setRedoable(false);
        eventHistory.add(mte);
        System.out.println("Redid " + mte.toString());
    }

    void undoLatestEvent() {
        TextEvent toUndo = textEvents.remove(textEvents.size() - 1);
        undo(toUndo);
        comm.send(toUndo.getUndoEvent());
    }

    @Override
    public TextEvent take() throws InterruptedException {
        TextEvent mte = eventHistory.take();
        System.out.println("Received " + mte.toString() + " Time: " + mte.getTimeStamp() + ",  redoable: " + mte.isRedoable());
        if (mte.isRedoable())
            addTextEventToList(mte);
        LogicClock.setToMax(mte.getTimeStamp());
        return mte;
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

    void startServer() {
        comm.start();
    }

    boolean startClient(String serverName) {
        return comm.connect(serverName);
    }

    String printServerAddress() {
        return comm.getServerAddress();
    }

    void deregisterOnPort() {
        comm.deregister();
    }

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            if (justContinue) {
                eventHistory.add((TextEvent) comm.receiveObject());
            }
        }
    }
}
