package net.uidl.carsales;

import net.uidl.util.BeanUtils;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.hibernate.Session;
import org.hibernate.Query;

import java.util.*;

public class CustomerManagerImpl extends HibernateDaoSupport implements CustomerManager
{
    public Customer getCustomer(int id) throws Exception
    {
        Customer customer = null;

        Session session = getSession(true);
        try
        {
            customer = (Customer)session.get(Customer.class, id);
            customer = (Customer)BeanUtils.hibernateFilter(customer);
        }
        finally
        {
            session.close();
        }

        return customer;
    }

    public int saveCustomer(Customer c)
    {
        getHibernateTemplate().saveOrUpdate(c);
        return c.getId();
    }

    public List<Customer> getAllCustomers() throws Exception
    {
        List<Customer> customers = null;

        Session session = getSession(true);
        try
        {
            Query q = session.createQuery("from Customer c order by c.id asc");
            customers = (List<Customer>)BeanUtils.hibernateCollectionFilter(q.list());
        }
        finally
        {
            session.close();
        }

        return customers;
    }

    /*
    public List<Customer> getAllCustomers() throws Exception
    {
        return getHibernateTemplate().find("from Customer c order by c.id asc");
    }
    */
}
