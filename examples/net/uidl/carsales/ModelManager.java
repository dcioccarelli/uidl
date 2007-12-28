package net.uidl.carsales;

import java.util.List;

/**
 * User: doci
 * Date: 27/12/2007
 * Time: 23:39:59
 */
public interface ModelManager
{
    Model getModel(String name) throws Exception;

    void saveModel(Model m);

    List<Model> getAllModels() throws Exception;
}
