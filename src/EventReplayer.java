import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;

/**
 * Takes the event recorded by the DocumentEventCapturer and replays
 * them in a JTextArea. The delay of 1 sec is only to make the individual
 * steps in the reply visible to humans.
 *
 * @author Jesper Buus Nielsen
 */
class EventReplayer implements Runnable {

    private final DocumentEventCapturer dec;
    private final JTextArea area;
    private final DistributedTextEditor dte;
    private final int id;

    EventReplayer(DocumentEventCapturer dec, JTextArea area, DistributedTextEditor thisOne, int id) {
        this.dec = dec;
        this.area = area;
        this.dte = thisOne;
        this.id = id;
    }

    public void run() {
        boolean wasInterrupted = false;
        while (!wasInterrupted) {
            try {
                TextEvent mte = dec.take();
                if (mte.getID() != id || !mte.isRedoable()) {
                    dte.disableDEC();
                    if (mte instanceof TextInsertEvent) {
                        final TextInsertEvent tie = (TextInsertEvent) mte;
                        Runnable runnable = new Thread() {
                            public void run() {
                                try {
                                    area.insert(tie.getText(), tie.getOffset());
                                    // Sound by freesfx.co.uk
                                    playSound();
                                } catch (Exception e) {
                                    System.err.println(e.toString());
                        /* We catch all exceptions, as an uncaught exception would make the
                         * EDT unwind, which is now healthy.
                         */
                                }
                            }
                        };
                        EventQueue.invokeAndWait(runnable);
                        ((Thread) runnable).join();
                    } else if (mte instanceof TextRemoveEvent) {
                        ((TextRemoveEvent) mte).createUndoEvent(area.getText().substring(mte.getOffset(), mte.getOffset() + ((TextRemoveEvent) mte).getLength()));
                        final TextRemoveEvent tre = (TextRemoveEvent) mte;
                        Runnable runnable = new Thread() {
                            public void run() {
                                try {
                                    area.replaceRange(null, tre.getOffset(), tre.getOffset() + tre.getLength());
                                    // Sound by freesfx.co.uk
                                    playSound();
                                } catch (Exception e) {
                                    e.printStackTrace();
                        /* We catch all exceptions, as an uncaught exception would make the
                         * EDT unwind, which is now healthy.
                         */
                                }
                            }
                        };
                        EventQueue.invokeAndWait(runnable);
                        ((Thread) runnable).join();
                    }
                    dte.enableDEC();
                }
            } catch (StringIndexOutOfBoundsException e) {
                System.out.println("WTF, the string index is out of bounds!");
            } catch (Exception e) {
                wasInterrupted = true;
                e.printStackTrace();
            }
        }
        System.out.println("I'm the thread running the EventReplayer, now I die!");
        dte.disconnect();
    }

    private static synchronized void playSound() {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    Clip clip = AudioSystem.getClip();
                    AudioInputStream inputStream = AudioSystem.getAudioInputStream(
                            DistributedTextEditor.class.getResourceAsStream("" + "typewriter_key.wav"));
                    clip.open(inputStream);
                    clip.start();
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }
}
