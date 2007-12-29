package net.uidl.carsales;

import java.util.List;

/**
 * OptionManager interface for carsales example application
 *
 * @author  Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */

public interface OptionManager
{
    Option getOption(int id) throws Exception;

    int saveOption(Option o);

    List<Option> getAllOptions() throws Exception;
}
