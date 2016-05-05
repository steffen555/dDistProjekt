import java.io.Serializable;

/**
 * @author Jesper Buus Nielsen
 */
public class MyTextEvent implements Serializable {
    MyTextEvent(int offset, int id) {
        this.offset = offset;
        this.id = id;
    }

    private int offset;
    private int id;

    int getOffset() {
        return offset;
    }

    int getID() {
        return id;
    }
}
