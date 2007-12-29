package net.uidl.carsales;

import java.util.List;

/**
 * CustomerManager interface for carsales example application
 *
 * @author  Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */
public interface CustomerManager
{
    public Customer getCustomer(int id) throws Exception;

    public int saveCustomer(Customer c);

    public List<Customer> getAllCustomers() throws Exception;
}
