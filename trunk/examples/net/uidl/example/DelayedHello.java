package net.uidl.example;

import java.io.Serializable;

public class DelayedHello implements Serializable
{
    public String sayHello(String who)
    {
        try{Thread.sleep(10000);}catch(Exception e){};
        return "hello " + who;
    }
}
