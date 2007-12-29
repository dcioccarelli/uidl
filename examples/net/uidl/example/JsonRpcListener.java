package net.uidl.example;

import org.jabsorb.JSONRPCBridge;
import javax.servlet.*;

/**
 * JSON-RPC example. Listener is responsible for registering JSON-RPC
 * example services (hello and delayedHello) from the servlet's web.xml.
 *
 * @author  Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */
public final class JsonRpcListener implements ServletContextListener
{
    public void contextInitialized(ServletContextEvent event)
    {
        Hello hello = new Hello();
        DelayedHello dhello = new net.uidl.example.DelayedHello();
        JSONRPCBridge.getGlobalBridge().registerObject("hello", hello);
        JSONRPCBridge.getGlobalBridge().registerObject("delayedHello", dhello);
    }

    public void contextDestroyed(ServletContextEvent event) {}
}
