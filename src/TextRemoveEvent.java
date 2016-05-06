import java.util.HashMap;

public class TextRemoveEvent extends MyTextEvent {

    private int length;
    private MyTextEvent undoEvent;

    public TextRemoveEvent(int offset, int id, HashMap<Integer, Integer> timeStamp, int length, boolean undoable) {
        super(offset, id, timeStamp);
        this.length = length;
        if (undoable)
            createUndoEvent("hej");
    }

    public int getLength() {
        return length;
    }

    public void createUndoEvent(String text) {
        undoEvent = new TextInsertEvent(getOffset(), getID(), getTimeStamp(), text, false);
    }

    @Override
    public MyTextEvent getUndoEvent() {
        return undoEvent;
    }
}
