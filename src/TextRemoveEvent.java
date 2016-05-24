import java.util.HashMap;

class TextRemoveEvent extends TextEvent {

    private final int length;
    private TextEvent undoEvent;

    TextRemoveEvent(int offset, int id, HashMap<Integer, Integer> timeStamp, int length) {
        super(offset, id);
        this.length = length;
    }

    int getLength() {
        return length;
    }

    void createUndoEvent(String text) {
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
