package org.hisp.dhis.loginattempt.hibernate;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.loginattempt.LoginAttempt;
import org.hisp.dhis.loginattempt.LoginAttemptStore;
import org.hisp.dhis.user.User;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */
@Transactional
public class HibernateLoginAttemptStore implements LoginAttemptStore
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
    // LoginAttempt Store Methods Imple
    // -------------------------------------------------------------------------

    @Override
    public void addLoginAttempt( LoginAttempt loginAttempt )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( loginAttempt );
    }

    @Override
    public void updateLoginAttempt( LoginAttempt loginAttempt )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( loginAttempt );
    }

    @Override
    public void deleteLoginAttempt( LoginAttempt loginAttempt )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( loginAttempt );
    }
    
    @Override
    public LoginAttempt getLoginAttempt( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LoginAttempt.class );
        criteria.add( Restrictions.eq( "id", id ) );
        
        return (LoginAttempt)criteria.uniqueResult();
    }
    
    @Override
    public LoginAttempt getLoginAttemptByUser( User user )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LoginAttempt.class );
        criteria.add( Restrictions.eq( "user", user ) );

        return (LoginAttempt) criteria.uniqueResult();
    }
    
    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<LoginAttempt> getAllLoginAttempts()
    {  
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LoginAttempt.class );

        return criteria.list();        
    }
    
}
