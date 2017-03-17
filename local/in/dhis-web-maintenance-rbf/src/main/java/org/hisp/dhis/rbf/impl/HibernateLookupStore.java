package org.hisp.dhis.rbf.impl;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.common.hibernate.HibernateIdentifiableObjectStore;
import org.hisp.dhis.rbf.api.Lookup;
import org.hisp.dhis.rbf.api.LookupStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HibernateLookupStore
    extends HibernateIdentifiableObjectStore<Lookup>
    implements LookupStore
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
    // Lookup implementation
    // -------------------------------------------------------------------------
    
    @SuppressWarnings( "unchecked" )
    public Collection<Lookup> getAllLookupsByType( String type )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Lookup.class );

        criteria.add( Restrictions.eq( "type", type ) );

        return criteria.list();
    }

    @Override
    public void addLookup( Lookup lookup )
    {
        Session session = sessionFactory.getCurrentSession();
        session.save( lookup );
        
        session.flush();        
    }

    @Override
    public void updateLookup( Lookup lookup )
    {
        Session session = sessionFactory.getCurrentSession();
        
        session.update( lookup );  
        
        session.flush();
    }

    @Override
    public void deleteLookup( Lookup lookup )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( lookup );
    }

    @Override
    public Lookup getLookup( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Lookup.class );
        criteria.add( Restrictions.eq( "id", id ) );
        
        return (Lookup)criteria.uniqueResult();
    }

    @Override
    public Lookup getLookupByName( String name )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Lookup.class );
        criteria.add( Restrictions.eq( "name", name ) );
        
        return (Lookup)criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<Lookup> getAllLookups()
    {  
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Lookup.class );

        return criteria.list();        
    }
    
    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<Lookup> getAllLookupsSortByType()
    {  
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Lookup.class );
        criteria.addOrder( Order.asc( "description" ) );
        return criteria.list();        
    }
    
}
