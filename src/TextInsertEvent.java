public class TextInsertEvent extends TextEvent {

    private final String text;

    public TextInsertEvent(int offset, int id, String text) {
        super(offset, id);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public TextEvent getUndoEvent() {
        return new TextRemoveEvent(getOffset(), getID(), getText().length());
    }

    @Override
    public String toString() {
        return "TextInsertEvent: insert " + getText() + " at " + getOffset() + ", time: " + getTimeStamp();
    }
}
