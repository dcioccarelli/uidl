package net.uidl.util;

import java.util.Collection;

public class JavascriptConverter
{
    /*
    static public Scriptable javaToJS(Customer in)
    {
        Context cx = Context.enter();
        Scriptable scope = cx.initStandardObjects();
        // Object jsOut = Context.javaToJS(in, scope);
        return Context.toObject(in, scope);
    }
    */
    
    public static Collection copyCollection(Collection in, Class collectionType)
    {
        Collection out = null;
        /*
        try
        {
            out = (Collection)collectionType.newInstance();
            assert (out instanceof Collection) : "Input type is not a Collection.";

            Iterator i = in.iterator();

            while( i.hasNext() )
                out.add(javaToJS((Customer)i.next()));

        }
        catch(Exception e)
        {
            System.out.println("Exception copying collection: " + e.getMessage());
        }
        */
        return out;
    }
}
