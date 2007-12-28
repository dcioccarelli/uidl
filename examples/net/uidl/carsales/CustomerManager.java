package net.uidl.carsales;

import java.util.List;

public interface CustomerManager
{
    public Customer getCustomer(int id) throws Exception;

    public int saveCustomer(Customer c);

    public List<Customer> getAllCustomers() throws Exception;
}
