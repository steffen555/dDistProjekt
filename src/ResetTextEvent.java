public class ResetTextEvent extends InfoEvent {
    private String text;
    public ResetTextEvent(int id, String text) {
        super(id);
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
