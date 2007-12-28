package net.uidl.example;

import java.io.Serializable;

public class Hello implements Serializable
{
    public String sayHello(String who)
    {
        return "hello " + who;
    }
}
