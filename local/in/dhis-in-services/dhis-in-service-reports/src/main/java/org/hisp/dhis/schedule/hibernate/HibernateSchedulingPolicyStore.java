package org.hisp.dhis.schedule.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.schedule.SchedulingPolicy;
import org.hisp.dhis.schedule.SchedulingPolicyStore;

/**
 * @author Mithilesh Kumar Thakur
 */
public class HibernateSchedulingPolicyStore implements SchedulingPolicyStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }
    
    // -------------------------------------------------------------------------
    // SchedulingPolicy
    // -------------------------------------------------------------------------

    public int addSchedulingPolicy(SchedulingPolicy paramSchedulingPolicy )
    {
        Session session = sessionFactory.getCurrentSession();

        return (Integer) session.save( paramSchedulingPolicy );
    }
    
    public void updateSchedulingPolicy(SchedulingPolicy paramSchedulingPolicy )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( paramSchedulingPolicy );
    }
    
    
    public void deleteSchedulingPolicy( SchedulingPolicy paramSchedulingPolicy )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( paramSchedulingPolicy );
    }

    public SchedulingPolicy getSchedulingPolicy(int id)
    {
      Session session = sessionFactory.getCurrentSession();
      
      return (SchedulingPolicy)session.get(SchedulingPolicy.class, Integer.valueOf(id) );
    }

    public SchedulingPolicy getSchedulingPolicyByName(String name)
    {
      Session session = sessionFactory.getCurrentSession();
      
      Criteria criteria = session.createCriteria(SchedulingPolicy.class);
      criteria.add(Restrictions.eq("name", name));
      
      return (SchedulingPolicy)criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<SchedulingPolicy> getAllSchedulingPolicies()
    {
      Session session = sessionFactory.getCurrentSession();
      
      return session.createCriteria(SchedulingPolicy.class).list();
      
    }

}
