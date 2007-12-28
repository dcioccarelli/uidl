package net.uidl.example;

import org.jabsorb.JSONRPCBridge;
import javax.servlet.*;

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
