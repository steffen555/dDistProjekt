/**
 * 
 * @author Jesper Buus Nielsen
 *
 */
public class TextInsertEvent extends MyTextEvent {

	private String text;
	
	public TextInsertEvent(int offset, int id, String text) {
		super(offset, id);
		this.text = text;
	}
	public String getText() { return text; }
}

