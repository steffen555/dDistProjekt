
public class TextRemoveEvent extends MyTextEvent {

	private int length;
	
	public TextRemoveEvent(int offset, int id, int length) {
		super(offset, id);
		this.length = length;
	}
	
	public int getLength() { return length; }
}
