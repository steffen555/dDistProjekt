import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Created by Steffen on 15/04/2016.
 */
public interface DocumentEventCapturer {

    public void insertString(DocumentFilter.FilterBypass fb, int offset,
                             String str, AttributeSet a)
            throws BadLocationException;

    public void remove(DocumentFilter.FilterBypass fb, int offset, int length)
            throws BadLocationException;

    public void replace(DocumentFilter.FilterBypass fb, int offset,
                        int length,
                        String str, AttributeSet a)
            throws BadLocationException;
}
