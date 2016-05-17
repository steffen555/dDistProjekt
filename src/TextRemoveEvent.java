import java.util.HashMap;

public class TextRemoveEvent extends TextEvent {

    private int length;
    private TextEvent undoEvent;

    public TextRemoveEvent(int offset, int id, HashMap<Integer, Integer> timeStamp, int length) {
        super(offset, id, timeStamp);
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public void createUndoEvent(String text) {
        undoEvent = new TextInsertEvent(getOffset(), getID(), getTimeStamp(), text);
    }

    @Override
    public TextEvent getUndoEvent() {
        return undoEvent;
    }

    @Override
    public String toString(){
        return "TextRemoveEvent: remove " + getLength() + " characters at " + getOffset() + ", time: " + getTimeStamp();
    }
}
