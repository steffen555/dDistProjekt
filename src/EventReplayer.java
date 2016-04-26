import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Takes the event recorded by the DocumentEventCapturer and replays
 * them in a JTextArea. The delay of 1 sec is only to make the individual
 * steps in the reply visible to humans.
 *
 * @author Jesper Buus Nielsen
 */
public class EventReplayer implements Runnable {

    private DocumentEventCapturer dec;
    private JTextArea area;

    public EventReplayer(DocumentEventCapturer dec, JTextArea area) {
        this.dec = dec;
        this.area = area;
    }

    public void run() {
        boolean wasInterrupted = false;
        while (!wasInterrupted) {
            //waitForOneSecond();
            try {
                MyTextEvent mte = dec.take();
                if (mte instanceof TextInsertEvent) {
                    final TextInsertEvent tie = (TextInsertEvent) mte;
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            try {
                                area.insert(tie.getText(), tie.getOffset());
                                //Sound by freesfx.co.uk
                                playSound("typewriter_key.mp3");
                            } catch (Exception e) {
                                System.err.println(e);
                    /* We catch all exceptions, as an uncaught exception would make the
				     * EDT unwind, which is now healthy.
				     */
                            }
                        }
                    });
                } else if (mte instanceof TextRemoveEvent) {
                    final TextRemoveEvent tre = (TextRemoveEvent) mte;
                    EventQueue.invokeLater(new Runnable() {
                        public void run() {
                            try {
                                area.replaceRange(null, tre.getOffset(), tre.getOffset() + tre.getLength());
                                //Sound by freesfx.co.uk
                                playSound("typewriter_key.mp3");
                            } catch (Exception e) {
                                System.err.println(e);
				    /* We catch all axceptions, as an uncaught exception would make the 
				     * EDT unwind, which is now healthy.
				     */
                            }
                        }
                    });
                }
            } catch (Exception _) {
                wasInterrupted = true;
            }
        }
        System.out.println("I'm the thread running the EventReplayer, now I die!");
    }

    public void waitForOneSecond() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException _) {
        }
    }

    public static synchronized void playSound(final String url) {
        new Thread(new Runnable() {
            // The wrapper thread is unnecessary, unless it blocks on the
            // Clip finishing; see comments.
            public void run() {
                try {
                    String soundFile = url;
                    InputStream in = new FileInputStream(soundFile);
                    AudioStream audioStream = new AudioStream(in);
                    AudioPlayer.player.start(audioStream);
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            }
        }).start();
    }
}
