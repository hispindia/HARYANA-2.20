package org.hisp.dhis.rbf.impl;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.rbf.api.TariffDataValue;
import org.hisp.dhis.rbf.api.TariffDataValueStore;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class HibernateTariffDataValueStore implements TariffDataValueStore
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
    // TariffDataValue
    // -------------------------------------------------------------------------
    
    @Override
    public void addTariffDataValue( TariffDataValue tariffDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( tariffDataValue );
    }

    @Override
    public void updateTariffDataValue( TariffDataValue tariffDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( tariffDataValue );
    }

    @Override
    public void deleteTariffDataValue( TariffDataValue tariffDataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( tariffDataValue );
    }

    @Override
    public TariffDataValue getTariffDataValue( OrganisationUnit organisationUnit, DataElement dataElement, DataSet dataSet, Date startDate, Date endDate )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );        
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );
        criteria.add( Restrictions.eq( "startDate", startDate ) );
        criteria.add( Restrictions.eq( "endDate", endDate ) );

        return (TariffDataValue) criteria.uniqueResult();
    }
    
    @Override
    public TariffDataValue getTariffDataValue( OrganisationUnit organisationUnit, OrganisationUnitGroup orgUnitGroup, DataElement dataElement, DataSet dataSet, Date startDate, Date endDate )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "orgUnitGroup", orgUnitGroup ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );
        criteria.add( Restrictions.eq( "startDate", startDate ) );
        criteria.add( Restrictions.eq( "endDate", endDate ) );

        return (TariffDataValue) criteria.uniqueResult();
    }

    @Override
    public TariffDataValue getTariffDataValue( OrganisationUnitGroup orgUnitGroup, DataElement dataElement, DataSet dataSet, Date startDate, Date endDate )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "orgUnitGroup", orgUnitGroup ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );        
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );
        criteria.add( Restrictions.eq( "startDate", startDate ) );
        criteria.add( Restrictions.eq( "endDate", endDate ) );

        return (TariffDataValue) criteria.uniqueResult();
    }

    @Override
    public Collection<TariffDataValue> getAllTariffDataValues()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );

        return criteria.list();
    }

    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataSet dataSet )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );

        return criteria.list();
    }

    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, OrganisationUnitGroup orgUnitGroup, DataSet dataSet )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "orgUnitGroup", orgUnitGroup ) );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );

        return criteria.list();
    }

    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnitGroup orgUnitGroup, DataSet dataSet )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "orgUnitGroup", orgUnitGroup ) );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );

        return criteria.list();
    }

    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.addOrder(Order.asc("dataSet"));

        return criteria.list();
    }
    
    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnitGroup orgUnitGroup, OrganisationUnit organisationUnit, DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "orgUnitGroup", orgUnitGroup ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.addOrder(Order.asc("dataSet"));

        return criteria.list();
    }

    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnitGroup orgUnitGroup, DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( TariffDataValue.class );
        criteria.add( Restrictions.eq( "orgUnitGroup", orgUnitGroup ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.addOrder(Order.asc("dataSet"));

        return criteria.list();
    }

    public Map<Integer, Double> getTariffDataValues( OrganisationUnit organisationUnit, DataSet dataSet, Period period )
    {
        Map<Integer, Double> tariffDataValueMap = new HashMap<Integer, Double>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String curPeriod = simpleDateFormat.format( period.getEndDate() );
        
        try
        {
            String query = "SELECT dataelementid, value FROM tariffdatavalue " +
                            " WHERE " +
                                " organisationunitid = " + organisationUnit.getId() + " AND " +
                                " datasetid = " + dataSet.getId() + " AND " +
                                " startdate <= '" + curPeriod + "' AND "+ 
                                " enddate >= '" + curPeriod +"'";
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer dataElementId = rs.getInt( 1 );
                Double value = rs.getDouble( 2 );
                tariffDataValueMap.put( dataElementId, value );
            }
        }
        catch( Exception e )
        {
            System.out.println("In getTariffDataValues Exception :"+ e.getMessage() );
        }
        
        return tariffDataValueMap;
    }
    
    public Map<Integer, Double> getTariffDataValues( OrganisationUnitGroup orgUnitGroup, DataSet dataSet, Period period )
    {
        Map<Integer, Double> tariffDataValueMap = new HashMap<Integer, Double>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String curPeriod = simpleDateFormat.format( period.getEndDate() );
        
        try
        {
            String query = "SELECT dataelementid, value FROM tariffdatavalue " +
                            " WHERE " +
                                " orgunitgroupid = " + orgUnitGroup.getId() + " AND " +
                                " datasetid = " + dataSet.getId() + " AND " +
                                " startdate <= '" + curPeriod + "' AND "+ 
                                " enddate >= '" + curPeriod +"'";
            
            //System.out.println("Query: " + query );
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer dataElementId = rs.getInt( 1 );
                Double value = rs.getDouble( 2 );
                tariffDataValueMap.put( dataElementId, value );
                //System.out.println( dataElementId + " : " + value );
            }
        }
        catch( Exception e )
        {
            System.out.println("In getTariffDataValues Exception :"+ e.getMessage() );
        }
        
        return tariffDataValueMap;
    }
    
    public Map<Integer, Double> getTariffDataValues( OrganisationUnitGroup orgUnitGroup, String orgUnitBranchIds, DataSet dataSet, Period period )
    {
        Map<Integer, Double> tariffDataValueMap = new HashMap<Integer, Double>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String curPeriod = simpleDateFormat.format( period.getEndDate() );
        
        
        
        try
        {
            /*String query = "SELECT dataelementid, value FROM tariffdatavalue " +
                            " WHERE " +
                                " orgunitgroupid = " + orgUnitGroup.getId() + " AND " +
                                " organisationunitid = " + organisationUnit.getId() + " AND " +
                                " datasetid = " + dataSet.getId() + " AND " +
                                " startdate <= '" + curPeriod + "' AND "+ 
                                " enddate >= '" + curPeriod +"'";
            */
            
            String query = "select td.dataelementid,td.value from "+
                            "( " +
                                "select max(asd.orgunitlevelid) as level,asd.dataelementid,asd.orgunitgroupid,datasetid " +
                                " from " +
                                    "( "+
                                        " select td.orgunitgroupid,td.organisationunitid,td.datasetid,td.dataelementid,td.orgunitlevelid,td.value " +
                                            " from tariffdatavalue td "+
                                            " where '" + curPeriod + "'  between date(td.startdate) and date(td.enddate) " +
                                                " and orgunitgroupid in ( " + orgUnitGroup.getId() + ") " +
                                                " and datasetid in ( " +dataSet.getId() + ") "+
                                                " and organisationunitid in ("+ orgUnitBranchIds +") "+
                                                " )asd "+
                                                " group by asd.dataelementid,asd.orgunitgroupid,datasetid " +
                                                " )sag1 " +
                                                " inner join tariffdatavalue td on td.dataelementid=sag1.dataelementid " +
                                                " where td.orgunitgroupid=sag1.orgunitgroupid " + 
                                                " and td.datasetid=sag1.datasetid " +
                                                " and sag1.level=td.orgunitlevelid ";
                                                //" and td.organisationunitid in ("+ orgUnitBranchIds +") ";
            
            System.out.println(" tariff Query: " + query );
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer dataElementId = rs.getInt( 1 );
                Double value = rs.getDouble( 2 );
                tariffDataValueMap.put( dataElementId, value );
                //System.out.println( dataElementId + " : " + value );
            }
        }
        catch( Exception e )
        {
            System.out.println("In getTariffDataValues Exception :"+ e.getMessage() );
        }
        
        return tariffDataValueMap;
    }
    
    public Set<Integer> getOrgUnitGroupsByDataset( Integer dataSetId, String orgUnitIds )
    {
        Set<Integer> orgUnitGroupIds = new HashSet<Integer>();
        
        try
        {
            String query = "select orgunitgroupid from tariffdatavalue " + 
                            " WHERE " +
                                " datasetid = " + dataSetId + " AND " +
                                " organisationunitid IN (" + orgUnitIds + ")";
            
            //System.out.println( query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while( rs.next() )
            {
                orgUnitGroupIds.add( rs.getInt( 1 ) );
            }
        }
        catch( Exception e )
        {
            System.out.println("In getOrgUnitGroupsByDataset Exception :"+ e.getMessage() );            
        }
        
        return orgUnitGroupIds;
    }
    public String getTariffDataValue( Integer orgunitgroupId, Integer dataSetId, Integer dataElementId, String date )
    {
        String value = null;
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        try
        {
            String query = "select startdate, enddate from tariffdatavalue " + 
                " WHERE " +
                " orgunitgroupid = " + orgunitgroupId + " AND " +
                " datasetid = " + dataSetId + " AND " +
                " dataelementid = " + dataElementId + " AND " +
                " startdate <= '" + date + "' AND "+ 
                " enddate >= '" + date +"'";
            
            //System.out.println( " query is --: " + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            if ( rs.next() )
            {
                Date sDate = rs.getDate( 1 );
                
                Date eDate = rs.getDate( 2 );
                
                if ( sDate != null && eDate != null  )
                {
                    value  = simpleDateFormat.format( sDate ) + " To " + simpleDateFormat.format( eDate ) ;
                }
            }

        }
        catch ( Exception e )
        {
            System.out.println("In getTariffDataValues Exception :"+ e.getMessage() );
        }
        
        return value; 
    }
    
    public String getTariffDataValue( Integer orgunitgroupId, Integer organisationUnitId, Integer dataSetId, Integer dataElementId, String date )
    {
        String value = null;
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        try
        {
            String query = "select startdate, enddate from tariffdatavalue " + 
                " WHERE " +
                " orgunitgroupid = " + orgunitgroupId + " AND " +
                " organisationunitid = " + organisationUnitId + " AND " +
                " datasetid = " + dataSetId + " AND " +
                " dataelementid = " + dataElementId + " AND " +
                " startdate <= '" + date + "' AND "+ 
                " enddate >= '" + date +"'";
            
            //System.out.println( " query is --: " + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            if ( rs.next() )
            {
                Date sDate = rs.getDate( 1 );
                
                Date eDate = rs.getDate( 2 );
                
                if ( sDate != null && eDate != null  )
                {
                    value  = simpleDateFormat.format( sDate ) + " To " + simpleDateFormat.format( eDate ) ;
                }
            }

        }
        catch ( Exception e )
        {
            System.out.println("In getTariffDataValues Exception :"+ e.getMessage() );
        }
        
        return value; 
    }
   
}
