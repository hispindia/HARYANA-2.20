package org.hisp.dhis.rbf.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodStore;
import org.hisp.dhis.rbf.api.PBFDataValue;
import org.hisp.dhis.rbf.api.PBFDataValueStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class HibernatePBFDataValueStore implements PBFDataValueStore
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private PeriodStore periodStore;

    public void setPeriodStore( PeriodStore periodStore )
    {
        this.periodStore = periodStore;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    // -------------------------------------------------------------------------
    // PBFDataValue
    // -------------------------------------------------------------------------

    @Override
    public void addPBFDataValue( PBFDataValue pbfDataValue ) 
    {
	pbfDataValue.setPeriod( periodStore.reloadForceAddPeriod( pbfDataValue.getPeriod() ) );

        Session session = sessionFactory.getCurrentSession();

        session.save( pbfDataValue );
    }

    @Override
    public void updatePBFDataValue( PBFDataValue pbfDataValue ) 
    {
	pbfDataValue.setPeriod( periodStore.reloadForceAddPeriod( pbfDataValue.getPeriod() ) );

        Session session = sessionFactory.getCurrentSession();

        session.update( pbfDataValue );
    }

    @Override
    public void deletePBFDataValue( PBFDataValue pbfDataValue ) 
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( pbfDataValue );
    }

    @Override
    public PBFDataValue getPBFDataValue( OrganisationUnit organisationUnit, DataSet dataSet, Period period, DataElement dataElement ) 
    {
	Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return null;
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( PBFDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return ( PBFDataValue ) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<PBFDataValue> getPBFDataValues( OrganisationUnit organisationUnit, DataSet dataSet, Period period ) 
    {
        Period storedPeriod = periodStore.reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return Collections.emptySet();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( PBFDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );

        return criteria.list();
    }

    public Map<Integer, Double> getPBFDataValues( String orgUnitIds, DataSet dataSet, String periodIds )
    {
        Map<Integer, Double> pbfDataValueMap = new HashMap<Integer, Double>();
        
        try
        {
            String query = "SELECT dataelementid, SUM( CAST( qtyvalidated AS NUMERIC) ) FROM pbfdatavalue " +                                
                                " WHERE " + 
                                    " organisationunitid IN (" + orgUnitIds + ") AND " +
                                    " datasetid = "+ dataSet.getId() + " AND " +
                                    " periodid IN (" + periodIds + ") " +
                                    " GROUP BY dataelementid";
                
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer dataElementId = rs.getInt( 1 );
                Double value = rs.getDouble( 2 );
                pbfDataValueMap.put( dataElementId, value );
            }
        }
        catch( Exception e )
        {
            System.out.println("In getTariffDataValues Exception :"+ e.getMessage() );
        }
        
        return pbfDataValueMap;
    }
}
