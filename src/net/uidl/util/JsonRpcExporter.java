package net.uidl.util;

import org.jabsorb.JSONRPCBridge;
import java.util.*;

/**
 * Utility class for exposing Spring managed objects via JSON-RPC.
 *
 * @author  Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */
public class JsonRpcExporter
{
    HashMap                         jsonObjects;

    public void setJsonObjects(HashMap jsonObjects)
    {
        this.jsonObjects = jsonObjects;
    }

    public void init()
    {
        Set serviceNames = jsonObjects.keySet();
        Iterator i = serviceNames.iterator();

        for (Object key : serviceNames)
        {
            String serviceName = (String)key;
            Object service = jsonObjects.get(serviceName);
            JSONRPCBridge.getGlobalBridge().registerObject(serviceName, service);
        }

        return;
    }
}
