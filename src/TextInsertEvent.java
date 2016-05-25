class TextInsertEvent extends TextEvent {

    private final String text;

    TextInsertEvent(int offset, int id, String text) {
        super(offset, id);
        this.text = text;
    }

    String getText() {
        return text;
    }

    @Override
    public TextEvent getUndoEvent() {
        TextRemoveEvent undo = new TextRemoveEvent(getOffset(), getID(), getText().length());
        System.out.println("Created undo event: " + undo);
        return undo;
    }

    @Override
    public String toString(){
        return "TextInsertEvent: insert " + getText() + " at " + getOffset() + ", time: " + getTimeStamp();
    }
}
