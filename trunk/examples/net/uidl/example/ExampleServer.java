package net.uidl.example;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.jetty.nio.SelectChannelConnector;

public class ExampleServer
{
    public static void main(String[] args) throws Exception
    {
        Server server = new Server();
        Connector connector = new SelectChannelConnector();
        connector.setPort(8080);
        server.addConnector(connector);

        WebAppContext wac = new WebAppContext();
        wac.setContextPath("/uidl");
        wac.setWar("webapp");
        server.setHandler(wac);
        server.setStopAtShutdown(true);

        server.start();
    }
}
