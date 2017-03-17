package org.hisp.dhis.rbf.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.rbf.api.UtilizationRate;
import org.hisp.dhis.rbf.api.UtilizationRateStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

/**
 * @author Mithilesh Kumar Thakur
 */
public class HibernateUtilizationRateStore implements UtilizationRateStore
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
    // UtilizationRateStore Methods implements
    // -------------------------------------------------------------------------    
    
    public void addUtilizationRate( UtilizationRate utilizationRate )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( utilizationRate );
    }

    public void updateUtilizationRate( UtilizationRate utilizationRate )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( utilizationRate );
    }

    public void deleteUtilizationRate( UtilizationRate utilizationRate )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( utilizationRate );
    }
    
   
    public UtilizationRate getUtilizationRate( DataElement dataElement, Double startRange, Double endRange )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( UtilizationRate.class );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "startRange", startRange ) );
        criteria.add( Restrictions.eq( "endRange", endRange ) );

        return (UtilizationRate) criteria.uniqueResult();
    }   
    
   
    @SuppressWarnings( "unchecked" )
    public Collection<UtilizationRate> getAllUtilizationRate()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( UtilizationRate.class );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<UtilizationRate> getUtilizationRates( DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( UtilizationRate.class );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        
        return criteria.list();
    }
    
    public Double getUtilizationRateTariffValue( DataElement dataElement, Double startRange, Double endRange )
    {
        Double tariffValue = null;
        
        try
        {
            String query = "SELECT tariff FROM utilizationrate WHERE dataelementid = " + dataElement.getId() + " AND startrange = "  + startRange + " AND "
                + " endrange = " + endRange;

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            if ( rs.next() )
            {
                tariffValue = rs.getDouble( 1 );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
        return tariffValue;
    }    

    
    public Map<Integer, String>getUtilizationRates()
    {
        Map<Integer, String> utilizationRatesMap = new HashMap<Integer, String>();

        try
        {                       
            
            String query = "SELECT dataelementid, startrange, endrange, tariff FROM utilizationrate";
            
            //System.out.println("Query: " + query );
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer dataElementId = rs.getInt( 1 );
                Double startRange = rs.getDouble( 2 );
                Double endRange = rs.getDouble( 3 );
                Double tariff = rs.getDouble( 4 );
                
                String value = startRange + ":" + endRange + ":" + tariff;
                String tempVal = utilizationRatesMap.get( dataElementId );
                if( tempVal != null )
                {
                    value = tempVal +"#" + value;
                }                    
                utilizationRatesMap.put( dataElementId, value );
                //System.out.println( dataElementId + " : " + value );
            }
        }
        catch( Exception e )
        {
            System.out.println("In getUtilizationRates Exception :"+ e.getMessage() );
        }
        
        return utilizationRatesMap;
}    
    
    
    
    
    
    
}
