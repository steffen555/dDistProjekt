import java.util.HashMap;

public class TextInsertEvent extends MyTextEvent {

    private String text;

    public TextInsertEvent(int offset, int id, HashMap<Integer, Integer> timeStamp, String text) {
        super(offset, id, timeStamp);
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @Override
    public MyTextEvent getUndoEvent() {
        TextRemoveEvent undo = new TextRemoveEvent(getOffset(), getID(), getTimeStamp(), getText().length());
        System.out.println("Created undo event: " + undo);
        return undo;
    }

    @Override
    public String toString(){
        return "TextInsertEvent: insert " + getText() + " at " + getOffset() + ", time: " + getTimeStamp();
    }
}
