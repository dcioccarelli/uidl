package net.uidl.carsales;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.hibernate.*;
import net.uidl.util.BeanUtils;

import java.util.List;

/**
 * MakeManager implementation for carsales example application
 *
 * @author  Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */
public class MakeManagerImpl extends HibernateDaoSupport implements MakeManager
{
    public Make getMake(String name) throws Exception
    {
        Make make = null;

        Session session = getSession(true);
        try
        {
            make = (Make)session.get(Make.class, name);
            make = (Make)BeanUtils.hibernateFilter(make);
        }
        finally
        {
            session.close();
        }

        return make;
    }

    public void saveMake(Make m)
    {
        getHibernateTemplate().saveOrUpdate(m);
        return;
    }

    public List<Make> getAllMakes() throws Exception
    {
        List<Make> makes = null;

        Session session = getSession(true);
        try
        {
            Query q = session.createQuery("from Make m order by m.name asc");
            makes = q.list();
            makes = (List<Make>)BeanUtils.hibernateCollectionFilter(makes);
        }
        finally
        {
            session.close();
        }

        return makes;
    }
}
