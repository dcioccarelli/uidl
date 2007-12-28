package net.uidl.carsales;

import java.util.List;

/**
 * User: doci
 * Date: 27/12/2007
 * Time: 10:19:43
 */
public interface MakeManager
{
    public Make getMake(String name) throws Exception;

    public void saveMake(Make m);

    public List<Make> getAllMakes() throws Exception;
}
