/**
 * 
 * @author Jesper Buus Nielsen
 *
 */
public class TextInsertEvent extends MyTextEvent {

	private String text;
	
	public TextInsertEvent(int offset, int id, int timeStamp, String text) {
		super(offset, id, timeStamp);
		this.text = text;
	}
	public String getText() { return text; }
}

