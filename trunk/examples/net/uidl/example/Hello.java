package net.uidl.example;

import java.io.Serializable;

/**
 * JSON-RPC example service. Echos input parameter and appends "hello".
 *
 * @author  Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */
public class Hello implements Serializable
{
    public String sayHello(String who)
    {
        return "hello " + who;
    }
}
