import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.DefaultEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class DistributedTextEditor extends JFrame {

    private final int port = 40501;

    private final JTextArea area1 = new JTextArea(30, 120);
    private final JTextField portNumber = new JTextField(Integer.toString(port));
    private JLabel label1 = new JLabel("No connections.");

    private final JFileChooser dialog = new JFileChooser(System.getProperty("user.dir"));

    private String currentFile = "Untitled";
    private boolean changed = false;
    private boolean connected = false;

    private WebEventHistory history;
    private DocumentEventCapturer dec;
    private final DistributedTextEditor thisOne = this;
    private static int id;
    private EventReplayer er;

    public DistributedTextEditor() {
        Disconnect.setEnabled(false);
        area1.setFont(new Font("Monospaced", Font.PLAIN, 12));

        Container content = getContentPane();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        label1.setHorizontalTextPosition(JLabel.LEFT);

        JScrollPane scroll1 =
                new JScrollPane(area1,
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        content.add(scroll1, BorderLayout.CENTER);
        content.add(label1);

        JMenuBar JMB = new JMenuBar();
        setJMenuBar(JMB);
        JMenu file = new JMenu("File");
        JMenu edit = new JMenu("Edit");
        JMB.add(file);
        JMB.add(edit);

        //file.add(Listen);
        file.add(Connect);
        file.add(Disconnect);
        file.addSeparator();
        file.add(Save);
        file.add(SaveAs);
        Action quit = new AbstractAction("Quit") {
            public void actionPerformed(ActionEvent e) {
                saveOld();
                System.exit(0);
            }
        };
        file.add(quit);

        ActionMap m = area1.getActionMap();
        Action copy = m.get(DefaultEditorKit.copyAction);
        edit.add(copy);
        Action paste = m.get(DefaultEditorKit.pasteAction);
        edit.add(paste);
        edit.getItem(0).setText("Copy");
        edit.getItem(1).setText("Paste");

        Save.setEnabled(false);
        SaveAs.setEnabled(false);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        KeyListener k1 = new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                changed = true;
                Save.setEnabled(true);
                SaveAs.setEnabled(true);
            }
        };
        area1.addKeyListener(k1);
        setTitle("Disconnected");
        setVisible(true);

        area1.getInputMap().put(KeyStroke.getKeyStroke('+'), "undoMapKey");
        area1.getActionMap().put("undoMapKey", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                history.undoLatestEvent();
            }
        });

        id = ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE);

        setUp();
    }

    private final Action Listen = new AbstractAction("Listen") {
        public void actionPerformed(ActionEvent e) {
            saveOld();
            area1.setText("");
            setUp();
            String ip = history.printServerAddress();
            setTitle("I'm listening on " + ip + ":" + port);
            if (connected) {
                history.startServer();
                return;
            }
            history.startServer();
            history.start();
            changed = false;
            connected = true;
            Save.setEnabled(false);
            SaveAs.setEnabled(false);
            Disconnect.setEnabled(true);
        }
    };

    private final Action Connect = new AbstractAction("Connect") {
        public void actionPerformed(ActionEvent e) {
            String localIp = history.printServerAddress();
            String ip;
            ip = JOptionPane.showInputDialog(thisOne, "IP address:", "localhost");
            if (ip != null) {
                saveOld();
                area1.setText("");
                setTitle("Connecting to " + ip + ":" + portNumber.getText() + ". " + "I'm listening on " + localIp + ":" + port);
                setTitle("Connected to " + ip + ":" + portNumber.getText() + ". " + "I'm listening on " + localIp + ":" + port);
                //setUp();
                connected = true;
                if (connected) {
                    history.startClient(ip);
                    Connect.setEnabled(false);
                    return;
                }
                if (history.startClient(ip)) {
                    //history.start();
                    setTitle("Connected to " + ip + ":" + portNumber.getText() + ". " + "I'm listening on " + localIp + ":" + port);
                    changed = false;
                    connected = true;
                    Save.setEnabled(false);
                    SaveAs.setEnabled(false);
                    Disconnect.setEnabled(true);
                    Connect.setEnabled(false);
                } else {
                    disconnect();
                }
            }
        }
    };

    private final Action Disconnect = new AbstractAction("Disconnect") {
        public void actionPerformed(ActionEvent e) {
            disconnect();
        }
    };

    public void disconnect() {
        //Clean up
        er.interrupt();
        er = null;
        history.deregisterOnPort();
        history.interrupt();

        //Start from new
        Connect.setEnabled(true);
        setUp();
    }

    private final Action Save = new AbstractAction("Save") {
        public void actionPerformed(ActionEvent e) {
            if (!currentFile.equals("Untitled"))
                saveFile(currentFile);
            else
                saveFileAs();
        }
    };

    private final Action SaveAs = new AbstractAction("Save as...") {
        public void actionPerformed(ActionEvent e) {
            saveFileAs();
        }
    };

    private void setUp() {
        history = new WebEventHistory(port);
        dec = new DocumentEventCapturer(history, id, area1);
        area1.setText("");
        enableDEC();
        er = new EventReplayer(dec, area1, thisOne, id);
        er.start();
        history.startServer();
        history.start();
        String ip = history.printServerAddress();
        setTitle("I'm listening on " + ip + ":" + port);
        changed = false;
        connected = true;
        Save.setEnabled(false);
        SaveAs.setEnabled(false);
        Disconnect.setEnabled(true);
        Connect.setEnabled(true);
        history.addConnectionChangeListener(label1);
        history.addDisableConnect(Connect);
    }

    private void saveFileAs() {
        if (dialog.showSaveDialog(null) == JFileChooser.APPROVE_OPTION)
            saveFile(dialog.getSelectedFile().getAbsolutePath());
    }

    private void saveOld() {
        if (changed) {
            if (JOptionPane.showConfirmDialog(this, "Would you like to save " + currentFile + " ?", "Save", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                saveFile(currentFile);
        }
    }

    private void saveFile(String fileName) {
        try {
            FileWriter w = new FileWriter(fileName);
            area1.write(w);
            w.close();
            currentFile = fileName;
            changed = false;
            Save.setEnabled(false);
        } catch (IOException ignored) {
        }
    }

    public void enableDEC() {
        ((AbstractDocument) area1.getDocument()).setDocumentFilter(dec);
        area1.setEditable(true);
    }

    public void disableDEC() {
        area1.setEditable(false);
        ((AbstractDocument) area1.getDocument()).setDocumentFilter(null);
    }

    public static int getId() {
        return id;
    }

    public static void main(String[] arg) {
        //Use GTK-theme on linux-systems, so we don't get that ugly SWING-UI
        for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("com.sun.java.swing.plaf.gtk.GTKLookAndFeel".equals(info.getClassName())) {
                try {
                    UIManager.setLookAndFeel(info.getClassName());
                } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        new DistributedTextEditor();
    }

}
