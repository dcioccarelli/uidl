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
import java.util.List;
import java.lang.reflect.InvocationTargetException;

/**
 * Main class for UIDL browser
 *
 * @author  Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */

public class Browser extends JFrame
{
    private static String DEFAULT_HOME_PAGE = "http://localhost:8080/uidl/";
    
    // Object level attributes
    ErrorConsole        err = null;
    SourceConsole       source = null;
    Container           browserPane;
    JTextField          addressField;
    JApplet             applet = null;
    List<URL>           history = new LinkedList<URL>();
    int                 historyPos = 0;

    JLabel              statusBar = new JLabel();
    BorderLayout        borderLayout = new BorderLayout();
    JPanel              applicationPanel;

    Context             cx;
    ScriptableObject    scope;
    URL                 currentUrl;

    boolean             packFrame = false;

    /**
     * Main constructor
     *
     * Setting showAddressBar to false is useful for custom WebStart or
     * Applet configurations which load a specific page and limit the user's
     * navigation to this. This effectively transforms the UIDL Browser into
     * a display engine for a specific script.
     * 
     * @param url   The address of the remote ressource to load
     * @param showAddressBar Show address bar
     */
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
                loadPage(new URL(url));

            displayBrowserFrame();

        }
        catch (Exception e)
        {
            statusBar.setText(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Constructor for applets
     * @param applet The applet implementation (a sub-class of JApplet) should
     * pass itself to this constructor.
     * @see BrowserApplet
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
                loadPage(new URL(url));
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

    /**
     * Main method. Typically called from Java WebStart JNLP file.
     * @see Browser(String, boolean)
     * @param args Arguements specifying [1] initial URL and
     * [2] whether address bar should be shown. 
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
                break;
            case 2:
                browser = new Browser(args[0], args[1].equalsIgnoreCase("true"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Includes (interprets) a UIDL script from another UIDL script.
     * @param address Location of the script relative to the calling script
     * @throws IOException if the specified script can't be loaded
     */
    public void include(String address) throws IOException
    {
        URL url = new URL(getHostPrefix() + address);
        URLConnection con = url.openConnection();
        con.setUseCaches(false);
        InputStream is = con.getInputStream();
        evaluate(is, cx, scope, url.getPath());
    }

    /**
     * Loads a Java library (JAR file) from within a UIDL script.
     * @param address Location of the JAR file relative to the calling script
     * @throws IOException if the specified JAR file can't be loaded
     */
    public void loadRemoteLibrary(String address) throws IOException
    {
        URL url = new URL(getHostPrefix() + address);
        ClassPathHacker.addURL(url);
    }

    /**
     * Overridden so we can exit when window is closed
     */
    protected void processWindowEvent(WindowEvent e)
    {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING)
        {
            System.exit(0);
        }
    }

    /* ---------------------------------------------------------------------
                            Private Methods        
       ---------------------------------------------------------------------*/

    /**
     * Loads a page. This might be either a UIDL or HTML page.
     * Adds the loaded page to the browser history.
     * @param url URL of resource to load
     */
    private void loadPage(URL url)
    {
        historyPos ++;
        history.add(historyPos - 1, url);
        loadUrl(url);
    }

    // as above!
    private void loadUrl(URL url)
    {
        String title = "UIDL Browser";
        String addr = url.toString();

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
            if (addr.endsWith(".uidl") ||
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

    /**
     * Loads an HTML page. Most of the heavy lifting here is done by the JEditorPane
     * @param url URL of resource to load
     */
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

    /**
     * Loads a UIDL page. This involves:
     * <ul><li>Constructing a frame to display the script content. The
     * UIDL script will write to this frame directly.
     * <li>Making a variety of Java helper objects available to the
     * scope that the UIDL page was loaded into.
     * <li>Evaluating the UIDL script<ul>
     * @param url URL of UIDL page to load
     */
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

            // Main frame to host UIDL script GUI elements
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

            cx = Context.enter();
            scope = cx.initStandardObjects();

            // Make various Java objects from the current scope available to the
            // newly loaded UIDL page.
            if (applet != null)
            {
                Object jsApplet = Context.javaToJS(applet, scope);
                ScriptableObject.putProperty(scope, "applet", jsApplet);
            }
            Object jsBrowser = Context.javaToJS(this, scope);
            ScriptableObject.putProperty(scope, "browser", jsBrowser);
            Object jsFrame = Context.javaToJS(frame, scope);
            ScriptableObject.putProperty(scope, "frame", jsFrame);
            Object jsHost = Context.javaToJS(hostName, scope);
            ScriptableObject.putProperty(scope, "hostName", jsHost);
            Object jsPort = Context.javaToJS(hostPort, scope);
            ScriptableObject.putProperty(scope, "hostPort", jsPort);
            Object jsHostPrefix = Context.javaToJS(hostPrefix, scope);
            ScriptableObject.putProperty(scope, "hostPrefix", jsHostPrefix);

            initJsonRpcLirary();

            evaluate(url, cx, scope);
        }
        catch (Exception e)
        {
            statusBar.setText(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Evaluate JSON-RPC JavaScript client library (from res package)
     * Requires some additional Java objects (XMLHttpRequest and BrowserThread)
     */
    private void initJsonRpcLirary()
        throws IllegalAccessException, InvocationTargetException, InstantiationException, IOException
    {
        ScriptableObject.defineClass(scope, XMLHttpRequest.class);
        ScriptableObject.defineClass(scope, BrowserThread.class);
        evaluate("jsonrpc.js", cx, scope);

        return;
    }

    /**
     * Evaluate JavaScript file identified by the specified URL
     * @param url URL of the UIDL script
     * @param cx The context of the JS interpreter
     * @param scope The current JS scope
     * @throws IOException if resource can't be opened
     */
    private void evaluate(URL url, Context cx, ScriptableObject scope) throws IOException
    {
        InputStream is;

        URLConnection con = url.openConnection();
        con.setUseCaches(false);
        is = con.getInputStream();

        if (source == null)
            evaluate(is, cx, scope, url.getPath());
        else
            evaluateAndSave(is, cx, scope, url.getPath());
    }

    /**
     * Evaluate JavaScript file stored "net.uidl.res" package (inside uidl.jar file)
     * @param res Name of JavaScript file
     * @param cx The context of the JS interpreter
     * @param scope The current JS scope
     * @throws IOException if resource can't be opened
     */
    private void evaluate(String res, Context cx, ScriptableObject scope) throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("res/" + res)));
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

    private String getHostPrefix()
    {
        String addr = currentUrl.toString();
        return addr.substring(0, addr.lastIndexOf('/') + 1);
    }

    private void initBrowserFrame()
    {
        setIconImage(Toolkit.getDefaultToolkit().createImage(Browser.class.getResource("res/logo.gif")));

        this.setSize(new Dimension(900, 600));
        browserPane = (JPanel) this.getContentPane();
        browserPane.setLayout(borderLayout);
        browserPane.add(statusBar, BorderLayout.SOUTH);
        statusBar.setText("Ready.");
    }

    /* ---------------------------------------------------------------------
                           Boring GUI Code         
       ---------------------------------------------------------------------*/

    /**
     * Called from the constructor in order to render the main frame used by
     * the browser.
     * @see Browser(String, boolean)
     */
    private void displayBrowserFrame()
    {
        // Validate frames that have preset sizes
        // Pack frames that have useful preferred size info, e.g. from their layout
        if (packFrame)
            this.pack();
        else
            this.validate();

        // Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = this.getSize();

        if (frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if (frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;
        this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        this.setVisible(true);
    }

    /**
     * Construct the main toolbar used by the browser.
     * @param url The initial URL to display
     * @return the constructed toolbar 
     */
    private JToolBar initToolBar(String url)
    {
        addressField = new JTextField();
        JToolBar browserToolbar = new JToolBar();

        ImageIcon openFileImage   = new ImageIcon(Browser.class.getResource("res/openFile.gif"));
        ImageIcon closeFileImage  = new ImageIcon(Browser.class.getResource("res/closeFile.gif"));
        ImageIcon helpImage       = new ImageIcon(Browser.class.getResource("res/help.gif"));
        ImageIcon backImage       = new ImageIcon(Browser.class.getResource("res/back.gif"));
        ImageIcon nextImage       = new ImageIcon(Browser.class.getResource("res/next.gif"));
        ImageIcon goImage         = new ImageIcon(Browser.class.getResource("res/go.gif"));

        JButton openButton = new JButton();
        openButton.setIcon(openFileImage);
        openButton.addActionListener(openButtonListener);
        openButton.setToolTipText("Open File");

        JButton saveButton = new JButton();
        saveButton.setIcon(closeFileImage);
        saveButton.setToolTipText("Close File");

        JButton helpButton = new JButton();
        helpButton.setIcon(helpImage);
        helpButton.setToolTipText("Help");
        helpButton.addActionListener(helpListener);

        JButton backButton = new JButton();
        backButton.setIcon(backImage);
        backButton.setToolTipText("Back");
        backButton.addActionListener(backButtonListener);

        JButton nextButton = new JButton();
        nextButton.setIcon(nextImage);
        nextButton.setToolTipText("Next");
        nextButton.addActionListener(forwardButtonListener);

        JButton goButton = new JButton();
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

        if (url == null) url = DEFAULT_HOME_PAGE;
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

        jMenuFileExit.addActionListener(exitListener);

        ActionListener debugShowListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e){err.setVisible(true);}
        };

        jMenuFileDebugShow.addActionListener(debugShowListener);

        ActionListener sourceShowListener = new ActionListener()
        {
            public void actionPerformed(ActionEvent e){source.setVisible(true);}
        };

        jMenuFileSourceShow.addActionListener(sourceShowListener);

        jMenuHelp.setText("Help");
        jMenuHelpAbout.setText("About");

        jMenuHelpAbout.addActionListener(helpListener);

        fileMenu.add(jMenuFileDebugShow);
        fileMenu.add(jMenuFileSourceShow);
        fileMenu.add(jMenuFileExit);

        jMenuHelp.add(jMenuHelpAbout);

        menuBar.add(fileMenu);
        menuBar.add(jMenuHelp);

        return menuBar;
    }

    /* ---------------------------------------------------------------------
                       Listener Inner Classes        
       ---------------------------------------------------------------------*/

    class HyperlinkEventMonitor implements HyperlinkListener
    {
        public void hyperlinkUpdate(HyperlinkEvent e)
        {
            if (e.getEventType() == HyperlinkEvent.EventType.ENTERED)
                statusBar.setText(e.getURL().toString());
            else if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                loadPage(e.getURL());
            else if (e.getEventType() == HyperlinkEvent.EventType.EXITED)
                statusBar.setText(" ");
            else
                statusBar.setText(e.getEventType() + " " + e.getURL());
        }
    }

    ActionListener helpListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            JOptionPane.showMessageDialog(browserPane, "UIDL Browser version 2.0\nDominic Cioccarelli\nuidl.net");
        }
    };

    ActionListener loadUrlListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            try
            {
                loadPage(new URL(addressField.getText()));
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
    };

    ActionListener backButtonListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            historyPos --;
            if (historyPos < 1)
                historyPos = 1;
            loadUrl(history.get(historyPos - 1));
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
            loadUrl(history.get(historyPos - 1));
        }
    };

    ActionListener openButtonListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(browserPane);

            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File file = fc.getSelectedFile();
                statusBar.setText("Open: " + file.getName());
                try
                {
                    loadPage(file.toURL());
                }
                catch(Exception e2)
                {
                    e2.printStackTrace();
                    statusBar.setText("Error opening UIDL file: " + e2.getMessage());
                }
            }
            else
            {
                statusBar.setText("Operation cancelled by user.");
            }

            return;
        }
    };

    ActionListener exitListener = new ActionListener()
    {
        public void actionPerformed(ActionEvent e)
        {
            System.exit(0);
        }
    };
}
