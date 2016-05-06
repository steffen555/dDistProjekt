import java.util.HashMap;

public class TextInsertEvent extends MyTextEvent {

    private String text;
    private MyTextEvent undoEvent;

    public TextInsertEvent(int offset, int id, HashMap<Integer, Integer> timeStamp, String text, boolean undoable) {
        super(offset, id, timeStamp);
        this.text = text;
        if (undoable)
            undoEvent = new TextRemoveEvent(getOffset(), getID(), getTimeStamp(), getText().length(), false);
    }

    public String getText() {
        return text;
    }

    @Override
    public MyTextEvent getUndoEvent() {
        return undoEvent;
    }
}

