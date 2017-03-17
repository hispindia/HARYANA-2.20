package org.hisp.dhis.rbf.impl;

import java.util.Collection;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.rbf.api.QualityScorePayment;
import org.hisp.dhis.rbf.api.QualityScorePaymentStore;

public class HibernateQualityScorePaymentStore implements QualityScorePaymentStore
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
    // Implementation
    // -------------------------------------------------------------------------

    @Override
    public void addQualityScorePayment( QualityScorePayment qualityScorePayment )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( qualityScorePayment );

        session.flush();
    }

    @Override
    public void updateQualityScorePayment( QualityScorePayment qualityScorePayment )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( qualityScorePayment );

        session.flush();
    }

    @Override
    public void deleteQualityScorePayment( QualityScorePayment qualityScorePayment )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( qualityScorePayment );
    }

    @Override
    public QualityScorePayment getQualityScorePayment( int id )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( QualityScorePayment.class );
        criteria.add( Restrictions.eq( "id", id ) );
        
        return (QualityScorePayment) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<QualityScorePayment> getAllQualityScorePayments()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( QualityScorePayment.class );

        return criteria.list();        
    }
    
}
