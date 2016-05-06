import java.util.HashMap;

public class TextInsertEvent extends MyTextEvent {

    private String text;
    private MyTextEvent undoEvent;

    public TextInsertEvent(int offset, int id, HashMap<Integer, Integer> timeStamp, String text) {
        super(offset, id, timeStamp);
        this.text = text;
        undoEvent = new TextRemoveEvent(getOffset(), getID(), getTimeStamp(), getText().length());
    }

    public String getText() {
        return text;
    }

    @Override
    public MyTextEvent getUndoEvent() {
        return undoEvent;
    }
}

