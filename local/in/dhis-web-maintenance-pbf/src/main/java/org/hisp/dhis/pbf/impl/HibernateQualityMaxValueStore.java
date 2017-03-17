package org.hisp.dhis.pbf.impl;

import java.util.Collection;
import java.util.Date;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.pbf.api.QualityMaxValue;
import org.hisp.dhis.pbf.api.QualityMaxValueStore;
import org.springframework.jdbc.core.JdbcTemplate;

public class HibernateQualityMaxValueStore implements QualityMaxValueStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -------------------------------------------------------------------------
    // QualityMaxValue
    // -------------------------------------------------------------------------
    
	@Override
	public void addQuantityMaxValue(QualityMaxValue qualityMaxValue) {
		
		Session session = sessionFactory.getCurrentSession();
        session.save( qualityMaxValue );
	}

	@Override
	public void updateQuantityMaxValue(QualityMaxValue qualityMaxValue) {
		
		Session session = sessionFactory.getCurrentSession();
        session.update( qualityMaxValue );
	}

	@Override
	public void deleteQuantityMaxValue(QualityMaxValue qualityMaxValue) {
		
		Session session = sessionFactory.getCurrentSession();
        session.delete( qualityMaxValue );
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<QualityMaxValue> getAllQuanlityMaxValues() {
		
		Session session = sessionFactory.getCurrentSession();
        Criteria criteria = session.createCriteria( QualityMaxValue.class );

        return criteria.list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<QualityMaxValue> getQuanlityMaxValues(
			OrganisationUnit organisationUnit, DataSet dataSet) {
		
		Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( QualityMaxValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );

        return criteria.list();
	}

	@Override
	public QualityMaxValue getQualityMaxValue(
			OrganisationUnit organisationUnit, DataElement dataElement,
			DataSet dataSet,Date startDate ,Date endDate) {
		
		Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( QualityMaxValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );        
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );
        criteria.add( Restrictions.eq( "startDate", startDate ) );
        criteria.add( Restrictions.eq( "endDate", endDate ) );

        return (QualityMaxValue) criteria.uniqueResult();
	}

	@Override
	public Collection<QualityMaxValue> getQuanlityMaxValues(
			OrganisationUnit organisationUnit, DataElement dataElement) {
		
		Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( QualityMaxValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return criteria.list();
	}
	
	

}
