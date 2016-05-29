public class TextRemoveEvent extends TextEvent {

    private final int length;
    private TextEvent undoEvent;

    public TextRemoveEvent(int offset, int id, int length) {
        super(offset, id);
        this.length = length;
    }

    public int getLength() {
        return length;
    }

    public void createUndoEvent(String text) {
        undoEvent = new TextInsertEvent(getOffset(), getID(), text);
    }

    @Override
    public TextEvent getUndoEvent() {
        return undoEvent;
    }

    @Override
    public String toString() {
        return "TextRemoveEvent: remove " + getLength() + " characters at " + getOffset() + ", time: " + getTimeStamp();
    }
}
