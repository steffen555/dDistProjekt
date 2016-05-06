import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Jesper Buus Nielsen
 */
public abstract class MyTextEvent implements Serializable {

    MyTextEvent(int offset, int id, HashMap<Integer, Integer> timeStamp) {
        this.offset = offset;
        this.id = id;
        this.timeStamp = timeStamp;
    }

    private int offset;
    private int id;
    private HashMap<Integer,Integer> timeStamp;

    int getOffset() {
        return offset;
    }

    int getID() {
        return id;
    }

    HashMap<Integer, Integer> getTimeStamp() {
        return timeStamp;
    }

    public abstract MyTextEvent getUndoEvent();
}
