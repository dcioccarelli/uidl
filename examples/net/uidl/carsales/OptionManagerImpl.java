package net.uidl.carsales;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.hibernate.Session;
import org.hibernate.Query;
import net.uidl.util.BeanUtils;

import java.util.List;

/**
 * OptionManager implementation for carsales example application
 *
 * @author  Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */
public class OptionManagerImpl extends HibernateDaoSupport implements OptionManager
{
    public Option getOption(int id) throws Exception
    {
        Option option = null;

        Session session = getSession(true);
        try
        {
            option = (Option)session.get(Option.class, id);
            option = (Option) BeanUtils.hibernateFilter(option);
        }
        finally
        {
            session.close();
        }

        return option;
    }

    public int saveOption(Option o)
    {
        getHibernateTemplate().saveOrUpdate(o);
        return o.getId();
    }

    public List<Option> getAllOptions() throws Exception
    {
        List<Option> options = null;

        Session session = getSession(true);
        try
        {
            Query q = session.createQuery("from Option c order by c.id asc");
            options = (List<Option>)BeanUtils.hibernateCollectionFilter(q.list());
        }
        finally
        {
            session.close();
        }

        return options;
    }
}
