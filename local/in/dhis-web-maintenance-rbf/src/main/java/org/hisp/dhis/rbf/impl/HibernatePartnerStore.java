package org.hisp.dhis.rbf.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.option.Option;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.rbf.api.Partner;
import org.hisp.dhis.rbf.api.PartnerStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */
@Transactional
public class HibernatePartnerStore
    implements PartnerStore
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

    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private OptionService optionService;
    // -------------------------------------------------------------------------
    // Partner
    // -------------------------------------------------------------------------

    @Override
    public void addPartner( Partner partner )
    {
        Session session = sessionFactory.getCurrentSession();

        session.save( partner );
    }

    @Override
    public void updatePartner( Partner partner )
    {
        Session session = sessionFactory.getCurrentSession();

        session.update( partner );
    }

    @Override
    public void deletePartner( Partner partner )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( partner );
    }

    @Override
    public Partner getPartner( OrganisationUnit organisationUnit, DataSet dataSet, DataElement dataElement,
        Date startDate, Date endDate )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Partner.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "startDate", startDate ) );
        criteria.add( Restrictions.eq( "endDate", endDate ) );

        return (Partner) criteria.uniqueResult();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<Partner> getAllPartner()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Partner.class );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    @Override
    public Collection<Partner> getPartner( OrganisationUnit organisationUnit, DataSet dataSet )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( Partner.class );
        criteria.add( Restrictions.eq( "organisationUnit", organisationUnit ) );
        criteria.add( Restrictions.eq( "dataSet", dataSet ) );

        return criteria.list();
    }

    @Override
    public Collection<String> getStartAndEndDate( Integer dataSetId, Integer dataElementId, Integer optionId )
    {
        List<String> dateList = new ArrayList<String>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

        try
        {
            String query = "SELECT startdate, enddate FROM ("
                + "  SELECT Distinct (startdate , enddate),startdate , enddate FROM partner WHERE" + " datasetid = "
                + dataSetId + " AND " + " dataelementid = " + dataElementId + " AND " + " optionid = " + optionId
                + ") asd";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                String startDate = simpleDateFormat.format( rs.getDate( 1 ) );
                String endDate = simpleDateFormat.format( rs.getDate( 2 ) );

                if ( startDate != null && endDate != null )
                {
                    String date = startDate + ":" + endDate;

                    dateList.add( date );
                }
            }
        }
        catch ( Exception ex )
        {
            System.out.println( " In Partner Data Exception :" + ex.getMessage() );
            ex.printStackTrace();
        }
        return dateList;

    }

    // get OrgUnit Count FromPartner
    public Map<String, Integer> getOrgUnitCountFromPartner( Integer dataSetId, Integer dataElementId, Integer optionId,
        String startDate, String endDate )
    {
        Map<String, Integer> partnerOrgUnitCountMap = new HashMap<String, Integer>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        // String curPeriod = simpleDateFormat.format( period.getEndDate() );

        try
        {
            String query = "SELECT datasetid, dataelementid, optionid, startdate, enddate ,COUNT( organisationunitid ) FROM partner "
                + " WHERE "
                + " datasetid = "
                + dataSetId
                + " AND "
                + " dataelementid = "
                + dataElementId
                + " AND "
                + " optionid = "
                + optionId
                + " AND "
                + " startdate >= '"
                + startDate
                + "' AND  enddate <= '"
                + endDate
                + "' GROUP BY datasetid, dataelementid, startdate, enddate, optionid ";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer dataSetID = rs.getInt( 1 );
                Integer dataElementID = rs.getInt( 2 );
                Integer optionID = rs.getInt( 3 );
                String sDate = simpleDateFormat.format( rs.getDate( 4 ) );
                String eDate = simpleDateFormat.format( rs.getDate( 5 ) );
                Integer orgUnitCount = rs.getInt( 6 );

                if ( orgUnitCount != null && orgUnitCount > 0 )
                {

                    String key = dataSetID + ":" + dataElementID + ":" + optionID + ":" + sDate + ":" + eDate;
                    partnerOrgUnitCountMap.put( key, orgUnitCount );
                }
            }
        }
        catch ( Exception e )
        {
            System.out.println( " In Partner Data Exception :" + e.getMessage() );
            e.printStackTrace();
        }

        return partnerOrgUnitCountMap;
    }

    // get OrgUnit Count FromPartner
    public Map<String, Integer> getOrgUnitCountFromPartner( Integer dataSetId, Integer dataElementId, Integer optionId )
    {
        Map<String, Integer> partnerOrgUnitCountMap = new HashMap<String, Integer>();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        // String curPeriod = simpleDateFormat.format( period.getEndDate() );

        try
        {
            String query = "SELECT datasetid, dataelementid, optionid, startdate, enddate ,COUNT( organisationunitid ) FROM partner "
                + " WHERE "
                + " datasetid = "
                + dataSetId
                + " AND "
                + " dataelementid = "
                + dataElementId
                + " AND "
                + " optionid = " + optionId + " GROUP BY datasetid, dataelementid, startdate, enddate, optionid ";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer dataSetID = rs.getInt( 1 );
                Integer dataElementID = rs.getInt( 2 );
                Integer optionID = rs.getInt( 3 );
                String sDate = simpleDateFormat.format( rs.getDate( 4 ) );
                String eDate = simpleDateFormat.format( rs.getDate( 5 ) );
                Integer orgUnitCount = rs.getInt( 6 );

                if ( orgUnitCount != null && orgUnitCount > 0 )
                {

                    String key = dataSetID + ":" + dataElementID + ":" + optionID + ":" + sDate + ":" + eDate;
                    partnerOrgUnitCountMap.put( key, orgUnitCount );
                }
            }
        }
        catch ( Exception e )
        {
            System.out.println( " In Partner Data Exception :" + e.getMessage() );
            e.printStackTrace();
        }

        return partnerOrgUnitCountMap;
    }

    public Set<OrganisationUnit> getPartnerOrganisationUnits( Integer dataSetId, Integer dataElementId,
        Integer optionId, String startDate, String endDate )
    {
        Set<OrganisationUnit> organisationUnits = new HashSet<OrganisationUnit>();

        try
        {

            String query = "SELECT organisationunitid FROM partner " + " WHERE " + " datasetid = " + dataSetId
                + " AND " + " dataelementid = " + dataElementId + " AND " + " optionid = " + optionId + " AND "
                + " startdate >= '" + startDate + "' AND  enddate <= '" + endDate + "' ";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer organisationUnitId = rs.getInt( 1 );

                if ( organisationUnitId != null )
                {
                    OrganisationUnit organisationUnit = organisationUnitService
                        .getOrganisationUnit( organisationUnitId );
                    organisationUnits.add( organisationUnit );
                }
            }
        }

        catch ( Exception ex )
        {
            System.out.println( " In Partner Data Exception :" + ex.getMessage() );
            ex.printStackTrace();
        }

        return organisationUnits;

    }
    
    // getPartners
    
    public Map<Integer, Option> getPartners( OrganisationUnit organisationUnit, DataSet dataSet, Period period )
    {
        Map<Integer, Option> partnerMap = new HashMap<Integer, Option>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        String curPeriodStartDate = simpleDateFormat.format( period.getStartDate() );
        
        String curPeriodEndDate = simpleDateFormat.format( period.getEndDate() );
        
        try
        {
            String query = "SELECT dataelementid, optionid FROM partner " +
                " WHERE " +
                    " organisationunitid = " + organisationUnit.getId() + " AND " +
                    " datasetid = " + dataSet.getId() + " AND " +
                    " startdate BETWEEN '" + curPeriodStartDate + "' AND '" + curPeriodEndDate + "' AND " +  
                    " enddate BETWEEN '" + curPeriodStartDate + "' AND '" + curPeriodEndDate + "' ";
            
            
            /*
            String query = "SELECT dataelementid, optionid FROM partner " +
                            " WHERE " +
                                " organisationunitid = " + organisationUnit.getId() + " AND " +
                                " datasetid = " + dataSet.getId() + " AND " +
                                " startdate >= '" + curPeriodStartDate + "' AND "+ 
                                " enddate <= '" + curPeriodEndDate +"'";
            
            
                        
            String query = "SELECT dataelementid, optionid FROM partner " +
                " WHERE " +
                    " organisationunitid = " + organisationUnit.getId() + " AND " +
                    " datasetid = " + dataSet.getId() + " AND " +
                    " startdate <= '" + curPeriodEndDate + "' AND "+ 
                    " enddate >= '" + curPeriodEndDate +"'";
            
            
            */
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer dataElementId = rs.getInt( 1 );
                Integer optionId = rs.getInt( 2 );
                
                if ( optionId != null )
                {
                    Option option = optionService.getOption( optionId );
                    partnerMap.put( dataElementId, option );
                    
                }
            }
        }
        catch( Exception e )
        {
            System.out.println("In getPartner Exception :"+ e.getMessage() );
        }
        
        return partnerMap;
    }
}
