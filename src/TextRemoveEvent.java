
public class TextRemoveEvent extends MyTextEvent {

	private int length;
	
	public TextRemoveEvent(int offset, int id, int timeStamp, int length) {
		super(offset, id, timeStamp);
		this.length = length;
	}
	
	public int getLength() { return length; }
}
