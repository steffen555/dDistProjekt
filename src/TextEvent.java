import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Jesper Buus Nielsen
 */
public abstract class TextEvent implements Serializable {

    private int offset;
    private int id;
    private HashMap<Integer,Integer> timeStamp;
    private boolean redoable;

    TextEvent(int offset, int id, HashMap<Integer, Integer> timeStamp) {
        this.offset = offset;
        this.id = id;
        this.timeStamp = timeStamp;
        redoable = true;
    }

    int getOffset() {
        return offset;
    }

    int getID() {
        return id;
    }

    HashMap<Integer, Integer> getTimeStamp() {
        return timeStamp;
    }

    public abstract TextEvent getUndoEvent();

    public void setRedoable(boolean redoable){
        this.redoable = redoable;
    }

    public boolean isRedoable(){
        return redoable;
    }
}
