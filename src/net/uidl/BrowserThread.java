package net.uidl;

import org.mozilla.javascript.*;

public class BrowserThread extends ScriptableObject
{
    private NativeFunction nativeFunction;
    private NativeFunction callback;

    public BrowserThread()
    {
    }

    public void jsConstructor()
    {
    }

    public String getClassName()
    {
        return "BrowserThread";
    }

    public Object jsGet_nativeFunction()
    {
        return nativeFunction;
    }

    public void jsSet_nativeFunction(NativeFunction function)
    {
        System.out.println("Setting native function to: " + function);
        nativeFunction = function;
    }

    public void jsSet_callback(NativeFunction function)
    {
        callback = function;
    }

    public Object jsGet_callback()
    {
        return callback;
    }

    public void jsFunction_startThread()
    {
        Runnable r = new Runnable()
        {
            public void run()
            {
                Object res = callMethod();
                performCallback(res);
            }
        };
        Thread asyncThread = new Thread(r);
        asyncThread.start();
    }

    public Object jsFunction_startThreadAndWait()
    {
        WaitThread asyncThread = new WaitThread();
        asyncThread.start();
        try
        {
            asyncThread.join();
        }
        catch (Exception e){}

        return asyncThread.res;
    }

    private Object callMethod()
    {
        if (nativeFunction != null)
        {
            Context cx = Context.enter();
            try
            {
                Scriptable scope = cx.initStandardObjects();
                return nativeFunction.call(cx, scope, this, new Object[]{});
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                Context.exit();
            }
        }

        return null;
    }

    private void performCallback(Object res)
    {
        if (callback != null)
        {
            Context cx = Context.enter();
            try
            {
                Scriptable scope = cx.initStandardObjects();
                callback.call(cx, scope, this, new Object[]{res});
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                Context.exit();
            }
        }
    }

    public class WaitThread extends Thread
    {
        public Object res = null;

        public void run()
        {
            res = callMethod();
        }
    }
}