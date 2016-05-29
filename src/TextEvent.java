/**
 * @author Jesper Buus Nielsen
 */
public abstract class TextEvent extends Event {

    private final int offset;
    private final int id;
    private boolean redoable;

    public TextEvent(int offset, int id) {
        super(id);
        this.offset = offset;
        this.id = id;
        redoable = true;
    }

    public int getOffset() {
        return offset;
    }

    public int getID() {
        return id;
    }

    public abstract TextEvent getUndoEvent();

    public void setRedoable(boolean redoable) {
        this.redoable = redoable;
    }

    public boolean isRedoable() {
        return redoable;
    }
}
