package net.uidl.carsales;

import java.util.List;

/**
 * MakeManager interface for carsales example application
 *
 * @author  Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */
public interface MakeManager
{
    public Make getMake(String name) throws Exception;

    public void saveMake(Make m);

    public List<Make> getAllMakes() throws Exception;
}
