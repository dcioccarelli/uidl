package net.uidl.carsales;

import java.util.List;

/**
 * ModelManager interface for carsales example application
 *
 * @author  Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */
public interface ModelManager
{
    Model getModel(String name) throws Exception;

    void saveModel(Model m);

    List<Model> getAllModels() throws Exception;
}
