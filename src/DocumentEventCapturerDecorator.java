import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Created by Steffen on 15/04/2016.
 */
public class DocumentEventCapturerDecorator implements DocumentEventCapturer {

    private DocumentEventCapturer dec = new DocumentEventCapturerImpl();

    @Override
    public void insertString(DocumentFilter.FilterBypass fb, int offset, String str, AttributeSet a) throws BadLocationException {
        dec.insertString(fb, offset, str, a);
    }

    @Override
    public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
        dec.remove(fb, offset, length);
    }

    @Override
    public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String str, AttributeSet a) throws BadLocationException {
        dec.replace(fb, offset, length, str, a);
    }
}
