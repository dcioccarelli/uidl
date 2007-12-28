package net.uidl;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.OutputStream;
import java.io.PrintStream;


/**
 * Class which allows the display of the source of the current UIDL page.
 *
 * @author Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */

public class SourceConsole extends JFrame
{
    BorderLayout borderLayout = new BorderLayout();
    private JTextArea theTextArea;
    private static PrintStream consoleOut;

    public SourceConsole()
    {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);

        JMenuBar menuBar = initMenuBar();
        this.setJMenuBar(menuBar);

        try
        {
            jbInit();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        this.pack();

        // Center the window

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize();

        if (frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if (frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;

        this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        this.setVisible(false);
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    /**
     * Component initialization
     */
    private void jbInit() throws Exception
    {
        theTextArea = new JTextArea(10, 80);
        theTextArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        theTextArea.setBackground(Color.blue);
        theTextArea.setForeground(Color.white);

        theTextArea.setVisible(true);

        this.pack();
        int scrwidth = Toolkit.getDefaultToolkit().getScreenSize().width;
        int scrheight = Toolkit.getDefaultToolkit().getScreenSize().height;
        int conwidth = getSize().width;
        int conheight = getSize().height;

        setLocation((scrwidth - conwidth) / 2, (scrheight - conheight) / 2);

        consoleOut = new PrintStream(new TextAreaOutputStream(theTextArea), true);

        //imageLabel.setIcon(new ImageIcon(AboutBox.class.getResource("[Your Image]")));
        this.setTitle("Source Window");
        this.setResizable(true);

        JScrollPane scrollPane = new JScrollPane(theTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        this.getContentPane().setLayout(borderLayout);
        this.getContentPane().add(scrollPane, BorderLayout.CENTER);
    }

    public void clear()
    {
        theTextArea.setText("");
        return;
    }

    public PrintStream getConsoleOut()
    {
        return consoleOut;
    }

    public JMenuBar initMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu();
        fileMenu.setText("File");

        JMenuItem jMenuFileExit = new JMenuItem();
        jMenuFileExit.setText("Exit");

        ActionListener exitListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                setVisible(false);
            }
        };
        jMenuFileExit.addActionListener(exitListener);

        fileMenu.add(jMenuFileExit);

        menuBar.add(fileMenu);

        return menuBar;
    }

    public class TextAreaOutputStream extends OutputStream
    {
        JTextArea theTextArea;
        String buffer;

        /**
         * Connect the stream to a TextArea.
         */
        public TextAreaOutputStream(JTextArea textArea)
        {
            buffer = "";
            theTextArea = textArea;
        }

        /**
         * Add the contents in the internal buffer to the TextArea and
         * delete the buffer.
         */
        synchronized public void flush()
        {
            theTextArea.append(buffer);
            String text = theTextArea.getText();
            theTextArea.setCaretPosition(text.length());
            buffer = "";
        }

        /**
         * Write to the internal buffer.
         */
        synchronized public void write(int b)
        {
            if (b < 0) b += 256;
            buffer += (char) b;
        }
    }
}
