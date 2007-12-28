package net.uidl;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.util.Properties;

/**
 * Class to simplify Spring remoting access for UIDL scripts.
 * Used to remotely load a Spring configuration (e.g. application.xml)
 * from the UIDL server and replace "host" and "port" placeholders
 * with appropriate values.
 *  
 * @author  Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */

public class SpringHelper
{
    private String  host;
    private String  port;
    private String  hostPrefix;

    public SpringHelper(String host, String port, String hostPrefix)
    {
        this.host = host;
        this.port = port;
        this.hostPrefix = hostPrefix;
    }

    public GenericApplicationContext loadRemoteConfig(String file) throws MalformedURLException
    {
        GenericApplicationContext ctx = new GenericApplicationContext();
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);

        Properties props = new Properties();
        props.setProperty("server", host);
        props.setProperty("port", port);

        Resource res;
        res = new UrlResource(hostPrefix + file);
        xmlReader.loadBeanDefinitions(res);

        if (props != null)
        {
            PropertyPlaceholderConfigurer cfg = new PropertyPlaceholderConfigurer();
            cfg.setProperties(props);
            cfg.postProcessBeanFactory(ctx.getBeanFactory());
        }
        ctx.refresh();

        return ctx;
    }
}
