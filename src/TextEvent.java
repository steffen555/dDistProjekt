public abstract class TextEvent extends Event {

    private final int offset;
    private boolean redoable;

    public TextEvent(int offset, int id) {
        super(id);
        this.offset = offset;
        redoable = true;
    }

    int getOffset() {
        return offset;
    }

    public abstract TextEvent getUndoEvent();

    void setRedoable(boolean redoable) {
        this.redoable = redoable;
    }

    boolean isRedoable() {
        return redoable;
    }
}
