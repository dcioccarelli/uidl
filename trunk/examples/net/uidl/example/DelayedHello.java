package net.uidl.example;

import java.io.Serializable;

/**
 * JSON-RPC example service. Echos input parameter and appends "hello"
 * after waiting 10 seconds.
 *
 * @author  Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */
public class DelayedHello implements Serializable
{
    public String sayHello(String who)
    {
        try{Thread.sleep(10000);}catch(Exception e){};
        return "hello " + who;
    }
}
