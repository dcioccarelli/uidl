package net.uidl;

import javax.swing.*;

/**
 * Constructs an Applet version of the UIDL browser.
 * 
 * @see Browser
 * @author Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */

public class BrowserApplet extends JApplet
{
    public void init()
    {
        Browser browser = new Browser(this);
    }
}
