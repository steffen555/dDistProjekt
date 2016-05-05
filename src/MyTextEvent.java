import java.io.Serializable;

/**
 * @author Jesper Buus Nielsen
 */
public class MyTextEvent implements Serializable {

    MyTextEvent(int offset, int id, int timeStamp) {
        this.offset = offset;
        this.id = id;
        this.timeStamp = timeStamp;
    }

    private int offset;
    private int id;
    private int timeStamp;

    int getOffset() {
        return offset;
    }

    int getID() {
        return id;
    }

    int getTimeStamp() {
        return timeStamp;
    }
}
