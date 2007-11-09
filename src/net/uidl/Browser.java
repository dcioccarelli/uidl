package net.uidl;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import xmlhttp.XMLHttpRequest;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 *
 * @author
 * @version 1.0
 */

public class Browser extends JFrame
{
    ErrorConsole        err = null;
    SourceConsole       source = null;
    Container           browserPane;
    JTextField          addressField;
    JApplet             applet = null;
    LinkedList          history = new LinkedList();
    int                 historyPos = 0;

    JLabel              statusBar = new JLabel();
    BorderLayout        borderLayout = new BorderLayout();
    JPanel              applicationPanel;

    Context             cx;
    ScriptableObject    scope;
    URL                 currentUrl;

    boolean             packFrame = false;

    public Browser(String url, boolean showAddressBar)
    {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);

        try
        {
            if (showAddressBar)
            {
                err = new ErrorConsole();
                source = new SourceConsole();
                System.setOut(err.getConsoleOut());
                System.setErr(err.getConsoleOut());
            }

            initBrowserFrame();
            if (showAddressBar)
            {
                JToolBar toolBar = initToolBar(url);
                browserPane.add(toolBar, BorderLayout.NORTH);

                JMenuBar menuBar = initMenuBar();
                this.setJMenuBar(menuBar);
            }

            if (url != null)
                getUrl(url);

            displayBrowserFrame();
        }
        catch (Exception e)
        {
            statusBar.setText(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Used for Applets
     */
    public Browser(JApplet applet)
    {
        String url = applet.getParameter("url");
        String addressBar = applet.getParameter("addressBar");

        boolean displayAddressBar;
        if (addressBar == null)
            displayAddressBar = true;
        else
            displayAddressBar = addressBar.equalsIgnoreCase("true");

        this.browserPane = applet.getContentPane();
        this.applet = applet;

        try
        {
            browserPane.setLayout(borderLayout);
            browserPane.add(statusBar, BorderLayout.SOUTH);

            if (url != null)
            {
                getUrl(url);
            }

            if (displayAddressBar)
            {
                err = new ErrorConsole();
                source = new SourceConsole();
                System.setOut(err.getConsoleOut());
                System.setErr(err.getConsoleOut());

                JToolBar toolBar = initToolBar(url);
                browserPane.add(toolBar, BorderLayout.NORTH);
                JMenuBar menuBar = initMenuBar();
                applet.setJMenuBar(menuBar);
            }
            statusBar.setText("Ready.");
        }
        catch (Exception e)
        {
            statusBar.setText(e.getMessage());
            e.printStackTrace();
        }
    }

    private void initBrowserFrame()
    {
        setIconImage(Toolkit.getDefaultToolkit().createImage(Browser.class.getResource("logo.gif")));

        this.setSize(new Dimension(900, 600));
        browserPane = (JPanel) this.getContentPane();
        browserPane.setLayout(borderLayout);
        browserPane.add(statusBar, BorderLayout.SOUTH);
        statusBar.setText("Ready.");
    }

    private void displayBrowserFrame()
    {
        // Validate frames that have preset sizes
        // Pack frames that have useful preferred size info, e.g. from their layout

        if (packFrame)
        {
            this.pack();
        }
        else
        {
            this.validate();
        }

        // Center the window

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize();

        if (frameSize.height > screenSize.height)
        {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width)
        {
            frameSize.width = screenSize.width;
        }
        this.setLocation((screenSize.width - frameSize.width) / 2,
                (screenSize.height - frameSize.height) / 2);
        this.setVisible(true);
    }

    private JToolBar initToolBar(String url)
    {
        addressField = new JTextField();
        JToolBar browserToolbar = new JToolBar();

        JButton openButton = new JButton();
        JButton saveButton = new JButton();
        JButton helpButton = new JButton();
        JButton backButton = new JButton();
        JButton nextButton = new JButton();
        JButton goButton = new JButton();

        ImageIcon openFileImage;
        ImageIcon closeFileImage;
        ImageIcon helpImage;
        ImageIcon backImage;
        ImageIcon nextImage;
        ImageIcon goImage;

        openFileImage =
                new ImageIcon(Browser.class.getResource("openFile.gif"));
        closeFileImage =
                new ImageIcon(Browser.class.getResource("closeFile.gif"));
        helpImage =
                new ImageIcon(Browser.class.getResource("help.gif"));
        backImage =
                new ImageIcon(Browser.class.getResource("back.gif"));
        nextImage =
                new ImageIcon(Browser.class.getResource("next.gif"));
        goImage =
                new ImageIcon(Browser.class.getResource("go.gif"));

        openButton.setIcon(openFileImage);
        openButton.addActionListener(openButtonListener);
        openButton.setToolTipText("Open File");

        saveButton.setIcon(closeFileImage);
        saveButton.setToolTipText("Close File");
        helpButton.setIcon(helpImage);
        helpButton.setToolTipText("Help");

        backButton.setIcon(backImage);
        backButton.setToolTipText("Back");
        backButton.addActionListener(backButtonListener);

        nextButton.setIcon(nextImage);
        nextButton.setToolTipText("Next");
        nextButton.addActionListener(forwardButtonListener);

        goButton.setIcon(goImage);
        goButton.setToolTipText("Load URL");
        goButton.addActionListener(loadUrlListener);

        browserToolbar.add(openButton);
        browserToolbar.add(saveButton, null);
        browserToolbar.add(helpButton, null);
        browserToolbar.add(backButton, null);
        browserToolbar.add(nextButton, null);
        browserToolbar.add(addressField, null);
        browserToolbar.add(goButton, null);

        if (url == null)
            url = "http://localhost:8080/net.uidl/";
        addressField.setText(url);
        addressField.addActionListener(loadUrlListener);

        return browserToolbar;
    }

    private JMenuBar initMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu();
        JMenuItem jMenuFileExit = new JMenuItem();
        JMenu jMenuHelp = new JMenu();
        JMenuItem jMenuHelpAbout = new JMenuItem();

        fileMenu.setText("File");

        JMenuItem jMenuFileDebugShow = new JMenuItem();
        jMenuFileDebugShow.setText("Show debug window");

        JMenuItem jMenuFileSourceShow = new JMenuItem();
        jMenuFileSourceShow.setText("Show source window");

        jMenuFileExit.setText("Exit");

        ActionListener exitListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                jMenuFileExit_actionPerformed(e);
            }
        };

        jMenuFileExit.addActionListener(exitListener);

        ActionListener debugShowListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                err.setVisible(true);
            }
        };

        jMenuFileDebugShow.addActionListener(debugShowListener);

        ActionListener sourceShowListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                source.setVisible(true);
            }
        };

        jMenuFileSourceShow.addActionListener(sourceShowListener);

        jMenuHelp.setText("Help");
        jMenuHelpAbout.setText("About");

        ActionListener helpListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                JOptionPane.showMessageDialog(browserPane, "UIDL Browser version 2.0\nCopyright 2005\nDominic Cioccarelli");
                // jMenuHelpAbout_actionPerformed(e);
            }
        };

        jMenuHelpAbout.addActionListener(helpListener);

        fileMenu.add(jMenuFileDebugShow);
        fileMenu.add(jMenuFileSourceShow);
        fileMenu.add(jMenuFileExit);

        jMenuHelp.add(jMenuHelpAbout);

        menuBar.add(fileMenu);
        menuBar.add(jMenuHelp);

        return menuBar;
    }

    /**
     * Component initialization
     */

    /**
     * File | Exit action performed
     */

    public void jMenuFileExit_actionPerformed(ActionEvent e)
    {
        System.exit(0);
    }

    /**
     * Overridden so we can exit when window is closed
     */

    protected void processWindowEvent(WindowEvent e)
    {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING)
        {
            jMenuFileExit_actionPerformed(null);
        }
    }

    private void loadUrl(String addr)
    {
        String title = "UIDL Browser";

        if (addressField != null)
        {
            addressField.setText(addr);
            int pos = addr.lastIndexOf("/") + 1;
            title = addr.substring(pos);
            if (title.trim().equals(""))
                title = "UIDL Browser";
            this.setTitle(title);
        }

        // clean up from previous frame

        if (applicationPanel != null)
            browserPane.remove(applicationPanel);

        applicationPanel = new JPanel();
        applicationPanel.setBackground(Color.white);
        applicationPanel.setLayout(new GridLayout());
        browserPane.add(applicationPanel, BorderLayout.CENTER);

        statusBar.setText("Loading: " + addr);
        browserPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        try
        {
            URL url = new URL(addr);

            if (addr.endsWith(".net.uidl") ||
                addr.endsWith(".es") ||
                addr.endsWith(".esw"))
            {
                loadUidlPage(url);
            }
            else
            {
                loadHtmlPage(url);
            }
            statusBar.setText("Done.");
        }
        catch (Exception e)
        {
            statusBar.setText("Error loading page: " + e.getMessage());
            e.printStackTrace();
        }

        browserPane.setCursor(Cursor.getDefaultCursor());
        return;
    }

    private void loadHtmlPage(URL url)
            throws IOException
    {
        JEditorPane htmlPane = new JEditorPane();
        JScrollPane scrollPane = new JScrollPane(htmlPane);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        htmlPane.setEditable(false);
        htmlPane.setFont(new Font("monospaced", Font.PLAIN, 12));
        htmlPane.setPage(url);
        applicationPanel.add(scrollPane, BorderLayout.CENTER);

        HyperlinkEventMonitor mon = new HyperlinkEventMonitor();
        htmlPane.addHyperlinkListener(mon);

        return;
    }

    class HyperlinkEventMonitor implements HyperlinkListener
    {
        public void hyperlinkUpdate(HyperlinkEvent e)
        {
            if (e.getEventType() == HyperlinkEvent.EventType.ENTERED)
            {
                statusBar.setText(e.getURL().toString());
            }
            else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
            {
                getUrl(e.getURL().toString());
            }
            else if (e.getEventType() == HyperlinkEvent.EventType.EXITED)
            {
                statusBar.setText(" ");
            }
            else
            {
                statusBar.setText(e.getEventType() + " " + e.getURL());
            }
        }
    }

    private void loadUidlPage(URL url)
    {
        try
        {
            currentUrl = url;
            String addr = url.toString();
            String hostName = url.getHost();
            String hostPort = new Integer(url.getPort()).toString();
            int pos = addr.lastIndexOf('/') + 1;
            String hostPrefix = addr.substring(0, pos);
            String title = addr.substring(pos);

            JInternalFrame frame;
            frame = new JInternalFrame(title, false);
            frame.setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);
            frame.setClosable(false);
            frame.setVisible(true);
            frame.setAutoscrolls(true);
            frame.setMaximizable(false);
            frame.setResizable(false);
            frame.toBack();
            applicationPanel.add(frame, BorderLayout.NORTH);

            this.cx = Context.enter();
            this.scope = cx.initStandardObjects();

            if (applet != null)
            {
                Object jsApplet = Context.javaToJS(applet, scope);
                ScriptableObject.putProperty(scope, "applet", jsApplet);
            }
            Object jsBrowser = Context.javaToJS(this, scope);
            ScriptableObject.putProperty(scope, "browser", jsBrowser);
            Object jsApplicationPanel = Context.javaToJS(applicationPanel, scope);
            ScriptableObject.putProperty(scope, "application", jsApplicationPanel);
            Object jsBrowserPlane = Context.javaToJS(browserPane, scope);
            ScriptableObject.putProperty(scope, "browserPane", jsBrowserPlane);
            Object jsFrame = Context.javaToJS(frame, scope);
            ScriptableObject.putProperty(scope, "frame", jsFrame);
            Object jsHost = Context.javaToJS(hostName, scope);
            ScriptableObject.putProperty(scope, "hostName", jsHost);
            Object jsPort = Context.javaToJS(hostPort, scope);
            ScriptableObject.putProperty(scope, "hostPort", jsPort);
            Object jsHostPrefix = Context.javaToJS(hostPrefix, scope);
            ScriptableObject.putProperty(scope, "hostPrefix", jsHostPrefix);
            ScriptableObject.defineClass(scope, XMLHttpRequest.class);
            ScriptableObject.defineClass(scope, BrowserThread.class);
            evaluate("jsonrpc.js", cx, scope);
            evaluate(url, cx, scope);

            // dumpSource(url);
            // frame.setSelected(true);
            // frame.toBack();
        }
        catch (Exception e)
        {
            statusBar.setText(e.getMessage());
            e.printStackTrace();
        }
    }

    private void evaluate(URL url, Context cx, ScriptableObject scope) throws IOException
    {
        URLConnection con = url.openConnection();
        con.setUseCaches(false);
        InputStream is = con.getInputStream();
        if (source == null)
            evaluate(is, cx, scope, url.getPath());
        else
            evaluateAndSave(is, cx, scope, url.getPath());
    }

    private void evaluate(String res, Context cx, ScriptableObject scope) throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(res)));
        Object result = cx.evaluateReader(scope, in, res, 1, null);
    }

    private void evaluate(InputStream is, Context cx, ScriptableObject scope, String filename) throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        Object result = cx.evaluateReader(scope, in, filename, 1, null);
        System.err.println(cx.toString(result));
    }

    private void evaluateAndSave(InputStream is, Context cx, ScriptableObject scope, String filename) throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));

        StringBuffer scriptCode = new StringBuffer();
        String line;

        source.clear();

        while ((line = in.readLine()) != null)
        {
            scriptCode.append(line + "\n");
            source.getConsoleOut().println(line);
        }
        Object result = cx.evaluateString(scope, scriptCode.toString(), filename, 1, null);
        System.err.println(cx.toString(result));
    }

    private void dumpSource(URL url) throws IOException
    {
        URLConnection con = url.openConnection();
        con.setUseCaches(false);
        InputStream is = con.getInputStream();
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        String line;
        source.clear();
        while ((line = in.readLine()) != null)
        {
            source.getConsoleOut().println(line);
        }
    }

    public void include(String address) throws IOException
    {
        URL url = new URL(getHostPrefix() + address);
        URLConnection con = url.openConnection();
        con.setUseCaches(false);
        InputStream is = con.getInputStream();
        evaluate(is, cx, scope, url.getPath());
    }

    public void loadRemoteLibrary(String address) throws IOException
    {
        URL url = new URL(getHostPrefix() + address);
        ClassPathHacker.addURL(url);
    }

    private String getHostPrefix()
    {
        String addr = currentUrl.toString();
        return addr.substring(0, addr.lastIndexOf('/') + 1);
    }

    ActionListener loadUrlListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            getUrl(addressField.getText());
        }
    };

    ActionListener backButtonListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            historyPos --;
            if (historyPos < 1)
                historyPos = 1;
            loadUrl((String)history.get(historyPos - 1));
        }
    };

    ActionListener forwardButtonListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            if (history.size() == 0)
                return;

            historyPos ++;
            if (historyPos > history.size())
                historyPos = history.size();
            loadUrl((String)history.get(historyPos - 1));
        }
    };

    ActionListener openButtonListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            statusBar.setText("Open: " + e.getActionCommand());
            return;
        }
    };

    private void getUrl(String addr)
    {
        historyPos ++;
        history.add(historyPos - 1, addr);
        loadUrl(addr);
    }

    /**
     * Main method
     */

    public static void main(String[] args)
    {
        try
        {
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            Browser browser;
            switch(args.length)
            {
            case 0:
                browser = new Browser(null, true);
                break;
            case 1:
                browser = new Browser(args[0], true);
            case 2:
                browser = new Browser(args[0], args[1].equalsIgnoreCase("true"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
