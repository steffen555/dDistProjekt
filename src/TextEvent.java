import java.util.HashMap;

/**
 * @author Jesper Buus Nielsen
 */
public abstract class TextEvent extends Event {

    private final int offset;
    private final int id;
    private boolean redoable;

    TextEvent(int offset, int id) {
        super(id);
        this.offset = offset;
        this.id = id;
        redoable = true;
    }

    int getOffset() {
        return offset;
    }

    int getID() {
        return id;
    }

    public abstract TextEvent getUndoEvent();

    void setRedoable(boolean redoable){
        this.redoable = redoable;
    }

    boolean isRedoable(){
        return redoable;
    }
}
