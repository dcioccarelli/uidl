package net.uidl.carsales;

import java.util.List;

/**
 * User: doci
 * Date: 27/12/2007
 * Time: 20:59:15
 */
public interface OptionManager
{
    Option getOption(int id) throws Exception;

    int saveOption(Option o);

    List<Option> getAllOptions() throws Exception;
}
