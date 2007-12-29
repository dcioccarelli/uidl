package net.uidl.carsales;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.hibernate.Session;
import org.hibernate.Query;
import net.uidl.util.BeanUtils;

import java.util.List;

/**
 * ModelManager implementation for carsales example application
 *
 * @author  Dominic Cioccarelli (uidl.net)
 * @version 1.0
 */
public class ModelManagerImpl extends HibernateDaoSupport implements ModelManager
{
    public Model getModel(String name) throws Exception
    {
        Model model = null;

        Session session = getSession(true);
        try
        {
            model = (Model)session.get(Model.class, name);
            model = (Model)BeanUtils.hibernateFilter(model);
        }
        finally
        {
            session.close();
        }

        return model;
    }

    public void saveModel(Model m)
    {
        getHibernateTemplate().saveOrUpdate(m);
        return;
    }

    public List<Model> getAllModels() throws Exception
    {
        List<Model> models = null;

        Session session = getSession(true);
        try
        {
            Query q = session.createQuery("from Model m order by m.name asc");
            models = q.list();
            models = (List<Model>)BeanUtils.hibernateCollectionFilter(models);
        }
        finally
        {
            session.close();
        }

        return models;
    }
}
