package org.hisp.dhis.den.impl;

/*
 * Copyright (c) 2004-2007, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.den.api.LLDataSets;
import org.hisp.dhis.den.api.LLDataValue;
import org.hisp.dhis.den.api.LLDataValueStore;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.system.database.DatabaseInfo;
import org.hisp.dhis.system.database.DatabaseInfoProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class HibernateLLDataValueStore
    implements LLDataValueStore
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }    

    private SessionFactory sessionFactory;

    public void setSessionFactory( SessionFactory sessionFactory )
    {
        this.sessionFactory = sessionFactory;
    }

    private PeriodService periodService;

    public void setperiodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }
    
    private DatabaseInfoProvider databaseInfoProvider;

    public void setDatabaseInfoProvider( DatabaseInfoProvider databaseInfoProvider )
    {
        this.databaseInfoProvider = databaseInfoProvider;
    }
    // -------------------------------------------------------------------------
    // Support methods for reloading periods
    // -------------------------------------------------------------------------

    private final Period reloadPeriod( Period period )
    {
        Session session = sessionFactory.getCurrentSession();

        if ( session.contains( period ) )
        {
            return period; // Already in session, no reload needed
        }

        return periodService.getPeriod( period.getStartDate(), period.getEndDate(), period.getPeriodType() );
    }

    private final Period reloadPeriodForceAdd( Period period )
    {
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            periodService.addPeriod( period );

            return period;
        }

        return storedPeriod;
    }

    // -------------------------------------------------------------------------
    // Basic DataValue
    // -------------------------------------------------------------------------

    public void addDataValue( LLDataValue dataValue )
    {
        dataValue.setPeriod( reloadPeriodForceAdd( dataValue.getPeriod() ) );

        Session session = sessionFactory.getCurrentSession();

        session.save( dataValue );
    }

    public void updateDataValue( LLDataValue dataValue )
    {
        dataValue.setPeriod( reloadPeriodForceAdd( dataValue.getPeriod() ) );

        Session session = sessionFactory.getCurrentSession();

        session.update( dataValue );
    }

    public void deleteDataValue( LLDataValue dataValue )
    {
        Session session = sessionFactory.getCurrentSession();

        session.delete( dataValue );
    }

    public int deleteDataValuesBySource( OrganisationUnit source )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete LLDataValue where source = :source" );
        query.setEntity( "source", source );

        return query.executeUpdate();
    }

    public int deleteDataValuesByDataElement( DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Query query = session.createQuery( "delete LLDataValue where dataElement = :dataElement" );
        query.setEntity( "dataElement", dataElement );

        return query.executeUpdate();
    }

    public LLDataValue getDataValue( OrganisationUnit source, DataElement dataElement, Period period,
        DataElementCategoryOptionCombo optionCombo, int recordNo )
    {
        Session session = sessionFactory.getCurrentSession();

        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return null;
        }

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.eq( "optionCombo", optionCombo ) );
        criteria.add( Restrictions.eq( "recordNo", recordNo ) );

        return (LLDataValue) criteria.uniqueResult();

    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( OrganisationUnit source, DataElement dataElement, Period period )
    {
        Session session = sessionFactory.getCurrentSession();

        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return null;
        }

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( OrganisationUnit source, DataElement dataElement, Period period,
        DataElementCategoryOptionCombo optionCombo )
    {
        Session session = sessionFactory.getCurrentSession();

        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return null;
        }

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.eq( "optionCombo", optionCombo ) );

        return criteria.list();
    }

    // -------------------------------------------------------------------------
    // Collections of DataValues
    // -------------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getAllDataValues()
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( OrganisationUnit source, Period period )
    {
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return Collections.emptySet();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( OrganisationUnit source, DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( Collection<OrganisationUnit> sources, DataElement dataElement )
    {
        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.in( "source", sources ) );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( OrganisationUnit source, Period period, Collection<DataElement> dataElements )
    {
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return Collections.emptySet();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.in( "dataElement", dataElements ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( OrganisationUnit source, Period period, Collection<DataElement> dataElements,
        Collection<DataElementCategoryOptionCombo> optionCombos )
    {
        Period storedPeriod = reloadPeriod( period );

        if ( storedPeriod == null )
        {
            return Collections.emptySet();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "source", source ) );
        criteria.add( Restrictions.eq( "period", storedPeriod ) );
        criteria.add( Restrictions.in( "dataElement", dataElements ) );
        criteria.add( Restrictions.in( "optionCombo", optionCombos ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( DataElement dataElement, Collection<Period> periods,
        Collection<OrganisationUnit> sources )
    {
        Collection<Period> storedPeriods = new ArrayList<Period>();

        for ( Period period : periods )
        {
            Period storedPeriod = reloadPeriod( period );

            if ( storedPeriod != null )
            {
                storedPeriods.add( storedPeriod );
            }
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.in( "period", storedPeriods ) );
        criteria.add( Restrictions.in( "source", sources ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( DataElement dataElement, DataElementCategoryOptionCombo optionCombo,
        Collection<Period> periods, Collection<OrganisationUnit> sources )
    {
        Collection<Period> storedPeriods = new ArrayList<Period>();

        for ( Period period : periods )
        {
            Period storedPeriod = reloadPeriod( period );

            if ( storedPeriod != null )
            {
                storedPeriods.add( storedPeriod );
            }
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );
        criteria.add( Restrictions.eq( "optionCombo", optionCombo ) );
        criteria.add( Restrictions.in( "period", storedPeriods ) );
        criteria.add( Restrictions.in( "source", sources ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( Collection<DataElement> dataElements, Collection<Period> periods,
        Collection<OrganisationUnit> sources, int firstResult, int maxResults )
    {
        Collection<Period> storedPeriods = new ArrayList<Period>();

        for ( Period period : periods )
        {
            Period storedPeriod = reloadPeriod( period );

            if ( storedPeriod != null )
            {
                storedPeriods.add( storedPeriod );
            }
        }

        if ( storedPeriods.size() == 0 )
        {
            return Collections.emptySet();
        }

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );

        criteria.add( Restrictions.in( "dataElement", dataElements ) );
        criteria.add( Restrictions.in( "period", storedPeriods ) );
        criteria.add( Restrictions.in( "source", sources ) );

        if ( maxResults != 0 )
        {
            criteria.addOrder( Order.asc( "dataElement" ) );
            criteria.addOrder( Order.asc( "period" ) );
            criteria.addOrder( Order.asc( "source" ) );

            criteria.setFirstResult( firstResult );
            criteria.setMaxResults( maxResults );
        }

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( Collection<DataElementCategoryOptionCombo> optionCombos )
    {

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.in( "optionCombo", optionCombos ) );

        return criteria.list();
    }

    @SuppressWarnings( "unchecked" )
    public Collection<LLDataValue> getDataValues( DataElement dataElement )
    {

        Session session = sessionFactory.getCurrentSession();

        Criteria criteria = session.createCriteria( LLDataValue.class );
        criteria.add( Restrictions.eq( "dataElement", dataElement ) );

        return criteria.list();
    }

    /*
    
    public void saveLLdataValue( String query )
    {
        //Connection con = sessionFactory.getCurrentSession().connection();

        PreparedStatement pst = null;

        try
        {
            Connection con = jdbcTemplate.getDataSource().getConnection();
            
            pst = con.prepareStatement( query );

            pst.executeUpdate();
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception while inserting : " + e.getMessage() );
        }
        finally
        {
            try
            {
                if ( pst != null )
                    pst.close();
            }
            catch ( Exception e )
            {
                System.out.println( "Exception while closing DB Connections : " + e.getMessage() );
            }
        }
    }*/

    
    public int getMaxRecordNo()
    {
        /*
         * Session session = sessionFactory.getCurrentSession();
         * 
         * // Criteria criteria = session.createCriteria( LLDataValue.class );
         * 
         * String sql_query = "from LLDataValue order by recordNo";
         * 
         * Query query = session.createQuery( sql_query );
         * 
         * List<LLDataValue> list = new ArrayList<LLDataValue>( query.list() );
         * 
         * if ( list == null || query.list().isEmpty() ) return 0;
         * 
         * Integer maxCount = (Integer) list.get( list.size() - 1
         * ).getRecordNo();
         * 
         * return maxCount.intValue();
         */

        //Connection con = sessionFactory.getCurrentSession().connection();

        //PreparedStatement pst = null;

        //ResultSet rs = null;

        String query = "SELECT MAX(recordno) FROM lldatavalue";

        try
        {
            //Connection con = jdbcTemplate.getDataSource().getConnection();
            
            //pst = con.prepareStatement( query );

            //rs = pst.executeQuery();
            SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query );

            if ( sqlResultSet.next() )
            {
                return sqlResultSet.getInt( 1 );
            }
            else
            {
                return 0;
            }

        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception while deleting : " + e.getMessage() );

            return 0;
        }
        finally
        {
            try
            {
                //if ( pst != null )
                 //   pst.close();

                //if ( rs != null )
                //    rs.close();
            }
            catch ( Exception e )
            {
                System.out.println( "Exception while closing DB Connections : " + e.getMessage() );
            }
        }

    }

    
    public Map<String, String> processLineListBirths( OrganisationUnit organisationUnit, Period periodL )
    {
        Map<String, String> deValueMap = new HashMap<String, String>();
        int ouId = organisationUnit.getId();

        Period storedPeriod = reloadPeriod( periodL );
        int pId = storedPeriod.getId();

        // Connection con = (new DBConnection()).openConnection();
        // Connection con = dbConnection.openConnection();
        //Connection con = sessionFactory.getCurrentSession().connection();

        int[] aggDeIds = { LLDataSets.LLB_BIRTHS, LLDataSets.LLB_BIRTHS_MALE, LLDataSets.LLB_BIRTHS_FEMALE,
            LLDataSets.LLB_WEIGHED_MALE, LLDataSets.LLB_WEIGHED_FEMALE, LLDataSets.LLB_WEIGHED_LESS1800_MALE,
            LLDataSets.LLB_WEIGHED_LESS1800_FEMALE, LLDataSets.LLB_WEIGHED_LESS2500_MALE,
            LLDataSets.LLB_WEIGHED_LESS2500_FEMALE, LLDataSets.LLB_BREASTFED_MALE, LLDataSets.LLB_BREASTFED_FEMALE };
        String[] queries = new String[11];

        // Total Live Birth
        queries[0] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLB_CHILD_NAME;
        // Total Live Birth Male
        queries[1] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLB_SEX + "  AND value = 'M'";
        // Total Live Birth Female
        queries[2] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLB_SEX + "  AND value = 'F'";

        // Live Birth Weighed Male
        queries[3] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = "
            + ouId
            + " AND periodid = "
            + pId
            + " AND dataelementid = "
            + LLDataSets.LLB_WIEGH
            + " AND (value NOT LIKE 'NK' OR value IS not null) AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLB_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Live Birth Weighed Female
        queries[4] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = "
            + ouId
            + " AND periodid = "
            + pId
            + " AND dataelementid = "
            + LLDataSets.LLB_WIEGH
            + " AND (value NOT LIKE 'nk' OR value IS not null) AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLB_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        // Live Birth Weighed Lessthan 1800 Male
        queries[5] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLB_WIEGH
            + " AND value < 1800 AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLB_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Live Birth Weighed Lessthan 1800 Female
        queries[6] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLB_WIEGH
            + " AND value < 1800 AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLB_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        // Live Birth Weighed Lessthan 2500 Male
        queries[7] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = "
            + ouId
            + " AND periodid = "
            + pId
            + " AND dataelementid = "
            + LLDataSets.LLB_WIEGH
            + " AND value >= 1800 AND value < 2500 AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLB_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Live Birth Weighed Lessthan 2500 Female
        queries[8] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = "
            + ouId
            + " AND periodid = "
            + pId
            + " AND dataelementid = "
            + LLDataSets.LLB_WIEGH
            + " AND value >= 1800 AND value < 2500 AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLB_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        // Live Birth Breastfeeding in FirstHour Male
        queries[9] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLB_BREASTFED
            + " AND value LIKE 'Y' AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLB_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Live Birth Breastfeeding in FirstHour Female
        queries[10] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLB_BREASTFED
            + " AND value LIKE 'Y' AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLB_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        try
        {
            //Connection con = jdbcTemplate.getDataSource().getConnection();
            
            for ( int i = 0; i < aggDeIds.length; i++ )
            {
                DataElement de = dataElementService.getDataElement( aggDeIds[i] );
                DataElementCategoryOptionCombo oc = de.getCategoryCombo().getOptionCombos().iterator().next();
                if ( de != null && oc != null )
                {
                    //PreparedStatement pst = con.prepareStatement( queries[i] );
                    //System.out.println( queries[i] );
                    //ResultSet rs = pst.executeQuery();
                    SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( queries[i] );
                    try
                    {
                        if ( sqlResultSet.next() )
                        {
                            deValueMap.put( de.getId() + ":" + oc.getId(), "" + sqlResultSet.getInt( 1 ) );
                            //System.out.println( "Value for " + de.getId() + " is " + sqlResultSet.getInt( 1 ) );
                        }
                        else
                        {
                            deValueMap.put( de.getId() + ":" + oc.getId(), "0" );
                            //System.out.println( "No Value for " + de.getId() );
                        }
                    }
                    catch ( Exception e )
                    {
                        System.out.println( "Exception : " + e.getMessage() );
                    }
                    finally
                    {
                        //if ( pst != null )
                         //   pst.close();
                        //if ( rs != null )
                        //    rs.close();
                    }
                }
            }
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
        }
        finally
        {
            try
            {
                // if ( con != null )
                // con.close();
            }
            catch ( Exception e )
            {
                System.out.println( "Exception while closing DB Connections : " + e.getMessage() );
            }
        }

        return deValueMap;
    }

    
    public Map<String, String> processLineListDeaths( OrganisationUnit organisationUnit, Period periodL )
    {
        Map<String, String> deValueMap = new HashMap<String, String>();
        int ouId = organisationUnit.getId();

        Period storedPeriod = reloadPeriod( periodL );
        int pId = storedPeriod.getId();

        // Connection con = (new DBConnection()).openConnection();
        // Connection con = dbConnection.openConnection();
        //Connection con = sessionFactory.getCurrentSession().connection();

        int[] aggDeIds = { LLDataSets.LLD_DEATH_OVER05Y, LLDataSets.LLD_DEATH_OVER55Y_MALE,
            LLDataSets.LLD_DEATH_OVER55Y_FEMALE, LLDataSets.LLD_DEATH_OVER15Y_MALE,
            LLDataSets.LLD_DEATH_OVER15Y_FEMALE, LLDataSets.LLD_DEATH_OVER05Y_MALE,
            LLDataSets.LLD_DEATH_OVER05Y_FEMALE, LLDataSets.LLD_DEATH_BELOW5Y, LLDataSets.LLD_DEATH_BELOW5Y_MALE,
            LLDataSets.LLD_DEATH_BELOW5Y_FEMALE, LLDataSets.LLD_DEATH_BELOW1Y_MALE,
            LLDataSets.LLD_DEATH_BELOW1Y_FEMALE, LLDataSets.LLD_DEATH_BELOW1M_MALE,
            LLDataSets.LLD_DEATH_BELOW1M_FEMALE, LLDataSets.LLD_DEATH_BELOW1W_MALE,
            LLDataSets.LLD_DEATH_BELOW1W_FEMALE, LLDataSets.LLD_DEATH_BELOW1D_MALE,
            LLDataSets.LLD_DEATH_BELOW1D_FEMALE,

            LLDataSets.LLD_CAUSE_DE1, LLDataSets.LLD_CAUSE_DE1, LLDataSets.LLD_CAUSE_DE2, LLDataSets.LLD_CAUSE_DE2,
            LLDataSets.LLD_CAUSE_DE3, LLDataSets.LLD_CAUSE_DE3, LLDataSets.LLD_CAUSE_DE4, LLDataSets.LLD_CAUSE_DE4,
            LLDataSets.LLD_CAUSE_DE5, LLDataSets.LLD_CAUSE_DE5, LLDataSets.LLD_CAUSE_DE6, LLDataSets.LLD_CAUSE_DE6,
            LLDataSets.LLD_CAUSE_DE7, LLDataSets.LLD_CAUSE_DE7, LLDataSets.LLD_CAUSE_DE8, LLDataSets.LLD_CAUSE_DE8,
            LLDataSets.LLD_CAUSE_DE9, LLDataSets.LLD_CAUSE_DE9, LLDataSets.LLD_CAUSE_DE10, LLDataSets.LLD_CAUSE_DE10,
            LLDataSets.LLD_CAUSE_DE11, LLDataSets.LLD_CAUSE_DE11, LLDataSets.LLD_CAUSE_DE12, LLDataSets.LLD_CAUSE_DE12,

            LLDataSets.LLD_CAUSE_DE13, LLDataSets.LLD_CAUSE_DE13, LLDataSets.LLD_CAUSE_DE14, LLDataSets.LLD_CAUSE_DE14,
            LLDataSets.LLD_CAUSE_DE15, LLDataSets.LLD_CAUSE_DE15, LLDataSets.LLD_CAUSE_DE16, LLDataSets.LLD_CAUSE_DE16,
            LLDataSets.LLD_CAUSE_DE17, LLDataSets.LLD_CAUSE_DE17, LLDataSets.LLD_CAUSE_DE18, LLDataSets.LLD_CAUSE_DE19,
            LLDataSets.LLD_CAUSE_DE19, LLDataSets.LLD_CAUSE_DE20, LLDataSets.LLD_CAUSE_DE20, LLDataSets.LLD_CAUSE_DE21,
            LLDataSets.LLD_CAUSE_DE21, LLDataSets.LLD_CAUSE_DE22, LLDataSets.LLD_CAUSE_DE22, LLDataSets.LLD_CAUSE_DE23,
            LLDataSets.LLD_CAUSE_DE23, LLDataSets.LLD_CAUSE_DE24, LLDataSets.LLD_CAUSE_DE24, LLDataSets.LLD_CAUSE_DE25,
            LLDataSets.LLD_CAUSE_DE25, LLDataSets.LLD_CAUSE_DE26, LLDataSets.LLD_CAUSE_DE26, LLDataSets.LLD_CAUSE_DE27,
            LLDataSets.LLD_CAUSE_DE27, LLDataSets.LLD_CAUSE_DE28, LLDataSets.LLD_CAUSE_DE28, LLDataSets.LLD_CAUSE_DE29,
            LLDataSets.LLD_CAUSE_DE29, LLDataSets.LLD_CAUSE_DE30, LLDataSets.LLD_CAUSE_DE30, LLDataSets.LLD_CAUSE_DE31,
            LLDataSets.LLD_CAUSE_DE31, LLDataSets.LLD_CAUSE_DE32, LLDataSets.LLD_CAUSE_DE32, LLDataSets.LLD_CAUSE_DE33,
            LLDataSets.LLD_CAUSE_DE33, LLDataSets.LLD_CAUSE_DE34, LLDataSets.LLD_CAUSE_DE34, LLDataSets.LLD_CAUSE_DE35,
            LLDataSets.LLD_CAUSE_DE35, LLDataSets.LLD_CAUSE_DE36, LLDataSets.LLD_CAUSE_DE36, LLDataSets.LLD_CAUSE_DE37,
            LLDataSets.LLD_CAUSE_DE37, LLDataSets.LLD_CAUSE_DE40, LLDataSets.LLD_CAUSE_DE40, LLDataSets.LLD_CAUSE_DE41,
            LLDataSets.LLD_CAUSE_DE41, LLDataSets.LLD_CAUSE_DE42, LLDataSets.LLD_CAUSE_DE42, LLDataSets.LLD_CAUSE_DE43,
            LLDataSets.LLD_CAUSE_DE43, LLDataSets.LLD_CAUSE_DE44, LLDataSets.LLD_CAUSE_DE44, LLDataSets.LLD_CAUSE_DE45,
            LLDataSets.LLD_CAUSE_DE45, LLDataSets.LLD_CAUSE_DE46, LLDataSets.LLD_CAUSE_DE46, LLDataSets.LLD_CAUSE_DE47,
            LLDataSets.LLD_CAUSE_DE47, LLDataSets.LLD_CAUSE_DE48, LLDataSets.LLD_CAUSE_DE48, LLDataSets.LLD_CAUSE_DE49,
            LLDataSets.LLD_CAUSE_DE49, LLDataSets.LLD_CAUSE_DE50, LLDataSets.LLD_CAUSE_DE50, LLDataSets.LLD_CAUSE_DE51,
            LLDataSets.LLD_CAUSE_DE51, LLDataSets.LLD_CAUSE_DE54, LLDataSets.LLD_CAUSE_DE54, LLDataSets.LLD_CAUSE_DE55,
            LLDataSets.LLD_CAUSE_DE55, LLDataSets.LLD_CAUSE_DE56, LLDataSets.LLD_CAUSE_DE56, LLDataSets.LLD_CAUSE_DE57,
            LLDataSets.LLD_CAUSE_DE57, LLDataSets.LLD_CAUSE_DE58, LLDataSets.LLD_CAUSE_DE58, LLDataSets.LLD_CAUSE_DE59,
            LLDataSets.LLD_CAUSE_DE59, LLDataSets.LLD_CAUSE_DE60, LLDataSets.LLD_CAUSE_DE60, LLDataSets.LLD_CAUSE_DE61,
            LLDataSets.LLD_CAUSE_DE61, LLDataSets.LLD_CAUSE_DE62, LLDataSets.LLD_CAUSE_DE62, LLDataSets.LLD_CAUSE_DE63,
            LLDataSets.LLD_CAUSE_DE63, LLDataSets.LLD_CAUSE_DE64, LLDataSets.LLD_CAUSE_DE64, LLDataSets.LLD_CAUSE_DE65,
            LLDataSets.LLD_CAUSE_DE65, LLDataSets.LLD_CAUSE_DE66, LLDataSets.LLD_CAUSE_DE66, LLDataSets.LLD_CAUSE_DE67,
            LLDataSets.LLD_CAUSE_DE67, LLDataSets.LLD_CAUSE_DE68, LLDataSets.LLD_CAUSE_DE68, LLDataSets.LLD_CAUSE_DE69,
            LLDataSets.LLD_CAUSE_DE69, LLDataSets.LLD_CAUSE_DE70, LLDataSets.LLD_CAUSE_DE70, LLDataSets.LLD_CAUSE_DE71,
            LLDataSets.LLD_CAUSE_DE71, LLDataSets.LLD_CAUSE_DE72, LLDataSets.LLD_CAUSE_DE72, LLDataSets.LLD_CAUSE_DE73,
            LLDataSets.LLD_CAUSE_DE73, LLDataSets.LLD_CAUSE_DE74, LLDataSets.LLD_CAUSE_DE74, LLDataSets.LLD_CAUSE_DE75,
            LLDataSets.LLD_CAUSE_DE75, LLDataSets.LLD_CAUSE_DE76, LLDataSets.LLD_CAUSE_DE76, LLDataSets.LLD_CAUSE_DE77,
            LLDataSets.LLD_CAUSE_DE77, LLDataSets.LLD_CAUSE_DE78, LLDataSets.LLD_CAUSE_DE78, LLDataSets.LLD_CAUSE_DE79,
            LLDataSets.LLD_CAUSE_DE79, LLDataSets.LLD_CAUSE_DE80, LLDataSets.LLD_CAUSE_DE80, LLDataSets.LLD_CAUSE_DE81,
            LLDataSets.LLD_CAUSE_DE81, LLDataSets.LLD_CAUSE_DE82, LLDataSets.LLD_CAUSE_DE82, LLDataSets.LLD_CAUSE_DE83,
            LLDataSets.LLD_CAUSE_DE83, LLDataSets.LLD_CAUSE_DE84, LLDataSets.LLD_CAUSE_DE84,
            
            
            //for haryana
            
            
            //Pneumonia
            LLDataSets.LLD_CAUSE_DE85, LLDataSets.LLD_CAUSE_DE85,
            LLDataSets.LLD_CAUSE_DE86, LLDataSets.LLD_CAUSE_DE86,
            LLDataSets.LLD_CAUSE_DE87, LLDataSets.LLD_CAUSE_DE87,
            //LLDataSets.LLD_CAUSE_DE88, LLDataSets.LLD_CAUSE_DE88,
            //LLDataSets.LLD_CAUSE_DE89, LLDataSets.LLD_CAUSE_DE89,
            
            //Fever related
            LLDataSets.LLD_CAUSE_DE90, LLDataSets.LLD_CAUSE_DE90,
            LLDataSets.LLD_CAUSE_DE91, LLDataSets.LLD_CAUSE_DE91,
            LLDataSets.LLD_CAUSE_DE92, LLDataSets.LLD_CAUSE_DE92,
            //LLDataSets.LLD_CAUSE_DE93, LLDataSets.LLD_CAUSE_DE93,
            //LLDataSets.LLD_CAUSE_DE94, LLDataSets.LLD_CAUSE_DE94,
        
            //Meconium aspiration syndrome
            LLDataSets.LLD_CAUSE_DE95, LLDataSets.LLD_CAUSE_DE95,
            LLDataSets.LLD_CAUSE_DE96, LLDataSets.LLD_CAUSE_DE96,
            LLDataSets.LLD_CAUSE_DE97, LLDataSets.LLD_CAUSE_DE97,
            LLDataSets.LLD_CAUSE_DE98, LLDataSets.LLD_CAUSE_DE98,
            LLDataSets.LLD_CAUSE_DE99, LLDataSets.LLD_CAUSE_DE99,
            
            
            //Meningitis
            LLDataSets.LLD_CAUSE_DE100, LLDataSets.LLD_CAUSE_DE100,
            LLDataSets.LLD_CAUSE_DE101, LLDataSets.LLD_CAUSE_DE101,
            LLDataSets.LLD_CAUSE_DE102, LLDataSets.LLD_CAUSE_DE102,
            LLDataSets.LLD_CAUSE_DE103, LLDataSets.LLD_CAUSE_DE103,
            LLDataSets.LLD_CAUSE_DE104, LLDataSets.LLD_CAUSE_DE104,
            
            
            //Major Congenital Malformation
            LLDataSets.LLD_CAUSE_DE105, LLDataSets.LLD_CAUSE_DE105,
            LLDataSets.LLD_CAUSE_DE106, LLDataSets.LLD_CAUSE_DE106,
            LLDataSets.LLD_CAUSE_DE107, LLDataSets.LLD_CAUSE_DE107,
            LLDataSets.LLD_CAUSE_DE108, LLDataSets.LLD_CAUSE_DE108,
            LLDataSets.LLD_CAUSE_DE109, LLDataSets.LLD_CAUSE_DE109,
            
            //Prematurity
            LLDataSets.LLD_CAUSE_DE110, LLDataSets.LLD_CAUSE_DE110,
            LLDataSets.LLD_CAUSE_DE111, LLDataSets.LLD_CAUSE_DE111,
            LLDataSets.LLD_CAUSE_DE112, LLDataSets.LLD_CAUSE_DE112,
            LLDataSets.LLD_CAUSE_DE113, LLDataSets.LLD_CAUSE_DE113,
            LLDataSets.LLD_CAUSE_DE114, LLDataSets.LLD_CAUSE_DE114,
        
            //Hypothermia
            LLDataSets.LLD_CAUSE_DE115, LLDataSets.LLD_CAUSE_DE115,
            LLDataSets.LLD_CAUSE_DE116, LLDataSets.LLD_CAUSE_DE116,
            LLDataSets.LLD_CAUSE_DE117, LLDataSets.LLD_CAUSE_DE117,
            LLDataSets.LLD_CAUSE_DE118, LLDataSets.LLD_CAUSE_DE118,
            LLDataSets.LLD_CAUSE_DE119, LLDataSets.LLD_CAUSE_DE119,
            
            //Diptheria
            LLDataSets.LLD_CAUSE_DE120, LLDataSets.LLD_CAUSE_DE120,
            LLDataSets.LLD_CAUSE_DE121, LLDataSets.LLD_CAUSE_DE121,
            LLDataSets.LLD_CAUSE_DE122, LLDataSets.LLD_CAUSE_DE122,
            LLDataSets.LLD_CAUSE_DE123, LLDataSets.LLD_CAUSE_DE123,
            LLDataSets.LLD_CAUSE_DE124, LLDataSets.LLD_CAUSE_DE124,
            
            //Childhood Tuberculosis
            LLDataSets.LLD_CAUSE_DE125, LLDataSets.LLD_CAUSE_DE125,
            LLDataSets.LLD_CAUSE_DE126, LLDataSets.LLD_CAUSE_DE126,
            LLDataSets.LLD_CAUSE_DE127, LLDataSets.LLD_CAUSE_DE127,
            LLDataSets.LLD_CAUSE_DE128, LLDataSets.LLD_CAUSE_DE128,
            LLDataSets.LLD_CAUSE_DE129, LLDataSets.LLD_CAUSE_DE129,
            
            
            //Dysentry
            LLDataSets.LLD_CAUSE_DE130, LLDataSets.LLD_CAUSE_DE130,
            LLDataSets.LLD_CAUSE_DE131, LLDataSets.LLD_CAUSE_DE131,
            LLDataSets.LLD_CAUSE_DE132, LLDataSets.LLD_CAUSE_DE132,
            LLDataSets.LLD_CAUSE_DE133, LLDataSets.LLD_CAUSE_DE133,
            LLDataSets.LLD_CAUSE_DE134, LLDataSets.LLD_CAUSE_DE134,
            
            //Pertusis
            LLDataSets.LLD_CAUSE_DE135, LLDataSets.LLD_CAUSE_DE135,
            LLDataSets.LLD_CAUSE_DE136, LLDataSets.LLD_CAUSE_DE136,
            LLDataSets.LLD_CAUSE_DE137, LLDataSets.LLD_CAUSE_DE137,
            LLDataSets.LLD_CAUSE_DE138, LLDataSets.LLD_CAUSE_DE138,
            LLDataSets.LLD_CAUSE_DE139, LLDataSets.LLD_CAUSE_DE139,
            
            //Polio
            LLDataSets.LLD_CAUSE_DE140, LLDataSets.LLD_CAUSE_DE140,
            LLDataSets.LLD_CAUSE_DE141, LLDataSets.LLD_CAUSE_DE141,
            LLDataSets.LLD_CAUSE_DE142, LLDataSets.LLD_CAUSE_DE142,
            LLDataSets.LLD_CAUSE_DE143, LLDataSets.LLD_CAUSE_DE143,
            LLDataSets.LLD_CAUSE_DE144, LLDataSets.LLD_CAUSE_DE144,
            
            //Tetanus Neonatorum
            LLDataSets.LLD_CAUSE_DE145, LLDataSets.LLD_CAUSE_DE145,
            LLDataSets.LLD_CAUSE_DE146, LLDataSets.LLD_CAUSE_DE146,
            LLDataSets.LLD_CAUSE_DE147, LLDataSets.LLD_CAUSE_DE147,
            LLDataSets.LLD_CAUSE_DE148, LLDataSets.LLD_CAUSE_DE148,
            LLDataSets.LLD_CAUSE_DE149, LLDataSets.LLD_CAUSE_DE149,
            
            //Tetanus (Others)
            LLDataSets.LLD_CAUSE_DE150, LLDataSets.LLD_CAUSE_DE150,
            LLDataSets.LLD_CAUSE_DE151, LLDataSets.LLD_CAUSE_DE151,
            LLDataSets.LLD_CAUSE_DE152, LLDataSets.LLD_CAUSE_DE152,
            LLDataSets.LLD_CAUSE_DE153, LLDataSets.LLD_CAUSE_DE153,
            LLDataSets.LLD_CAUSE_DE154, LLDataSets.LLD_CAUSE_DE154,
            
            
            //Acute Flaccide paralysis
            LLDataSets.LLD_CAUSE_DE155, LLDataSets.LLD_CAUSE_DE155,
            LLDataSets.LLD_CAUSE_DE156, LLDataSets.LLD_CAUSE_DE156,
            LLDataSets.LLD_CAUSE_DE157, LLDataSets.LLD_CAUSE_DE157,
            LLDataSets.LLD_CAUSE_DE158, LLDataSets.LLD_CAUSE_DE158,
            LLDataSets.LLD_CAUSE_DE159, LLDataSets.LLD_CAUSE_DE159,
            
            //Respiratory Infections (other than TB)
            LLDataSets.LLD_CAUSE_DE160, LLDataSets.LLD_CAUSE_DE160,
            LLDataSets.LLD_CAUSE_DE161, LLDataSets.LLD_CAUSE_DE161,
            LLDataSets.LLD_CAUSE_DE162, LLDataSets.LLD_CAUSE_DE162,
            LLDataSets.LLD_CAUSE_DE163, LLDataSets.LLD_CAUSE_DE163,
            LLDataSets.LLD_CAUSE_DE164, LLDataSets.LLD_CAUSE_DE164,
            
            
            
        
        
        };

        int[] aggDeOptComIds = { LLDataSets.LLD_OPTIONCOMBO_DEFAULT, LLDataSets.LLD_OPTIONCOMBO_DEFAULT,
            LLDataSets.LLD_OPTIONCOMBO_DEFAULT, LLDataSets.LLD_OPTIONCOMBO_DEFAULT, LLDataSets.LLD_OPTIONCOMBO_DEFAULT,
            LLDataSets.LLD_OPTIONCOMBO_DEFAULT, LLDataSets.LLD_OPTIONCOMBO_DEFAULT, LLDataSets.LLD_OPTIONCOMBO_DEFAULT,
            LLDataSets.LLD_OPTIONCOMBO_DEFAULT, LLDataSets.LLD_OPTIONCOMBO_DEFAULT, LLDataSets.LLD_OPTIONCOMBO_DEFAULT,
            LLDataSets.LLD_OPTIONCOMBO_DEFAULT, LLDataSets.LLD_OPTIONCOMBO_DEFAULT, LLDataSets.LLD_OPTIONCOMBO_DEFAULT,
            LLDataSets.LLD_OPTIONCOMBO_DEFAULT, LLDataSets.LLD_OPTIONCOMBO_DEFAULT, LLDataSets.LLD_OPTIONCOMBO_DEFAULT,
            LLDataSets.LLD_OPTIONCOMBO_DEFAULT,

            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE, 
            
            
           
            
            // for haryana
            
            //Pneumonia
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            
            
            // Fever/Other Fever related
            
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            
            // Meconium aspiration syndrome
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            
            // Meningitis
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            
            // Major Congenital Malformation
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            
            // Prematurity
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            
            // Hypothermia
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            
            
            // Diptheria
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            
            
            // Childhood Tuberculosis
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            
            
            //Dysentry
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            
            
            // Pertusis
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            
            // Polio
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            
            
            // Tetanus Neonatorum
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            
            // Tetanus (Others)
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            
            //Acute Flaccide paralysis
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            

            
            // Respiratory Infections (other than TB)
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            LLDataSets.LLD_CAUSE_OPTIONCOMBO_FEMALE,LLDataSets.LLD_CAUSE_OPTIONCOMBO_MALE,
            
        };

        String[] queries = new String[aggDeIds.length];

        // Death Above 5 Years Total

        queries[0] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ( 'O5YEAR', 'O15YEAR', 'O55YEAR')";

        // Death over 55 Years

        // Death Over 55 Years Male
        queries[1] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value LIKE 'O55YEAR' AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Death Over 55 Years Female
        queries[2] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value LIKE 'O55YEAR' AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        // Death 15 - 55 Years

        // Death Over 15 Year Male
        queries[3] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value LIKE 'O15YEAR' AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Death Over 15 Year Female
        queries[4] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value LIKE 'O15YEAR' AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        // Death 5 - 14 Years

        // Death 5 - 14 Years Male
        queries[5] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value LIKE 'O5YEAR' AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Death 5 - 14 Years Female
        queries[6] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value LIKE 'O5YEAR' AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        // Death Below 5 Year
        queries[7] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY + " AND value IN ('B5YEAR')";
        // Death Below 5 Year Male
        queries[8] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Death Below 5 Year Female
        queries[9] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        // Death Below 1 Year Male
        queries[10] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Death Below 1 Year Female
        queries[11] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        // Death Below 1 Month Male
        queries[12] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Death Below 1 Month Female
        queries[13] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        // Death Below 1 Week Male
        queries[14] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Death Below 1 Week Female
        queries[15] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        // Death Below 1 Day Male
        queries[16] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + ")";
        // Death Below 1 Day Female
        queries[17] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + ")";

        // 1121 Birth Asphyxia under one month Male
        queries[18] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_ASPHYXIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1121 Birth Asphyxia under one month Female
        queries[19] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_ASPHYXIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1122 : Sepsis under one month Male
        queries[20] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SEPSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1122 : Sepsis under one month Female
        queries[21] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SEPSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1123 : Low Birth Weight under one month Male
        queries[22] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '"
            + LLDataSets.LLD_LOW_BIRTH_WEIGH + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = "
            + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1123 : Low Birth Weight under one month Female
        queries[23] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '"
            + LLDataSets.LLD_LOW_BIRTH_WEIGH + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = "
            + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1124 : Immunization reactions under one month Male
        queries[24] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_IMMREAC
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1124 : Immunization reactions under one month Female
        queries[25] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_IMMREAC
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1125 : Others under one month Male
        queries[26] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1125 : Others under one month Female
        queries[27] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1126 : Not known under one month Male
        queries[28] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_NOT_KNOWN
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1126 : Not known under one month Female
        queries[29] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_NOT_KNOWN
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1127 : Pneumonia 1 year to 5 year Male
        queries[30] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PNEUMONIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1127 : Pneumonia 1 year to 5 year Female
        queries[31] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PNEUMONIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1128 : Diarrhoeal disease 1 year to 5 year Male
        queries[32] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIADIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1128 : Diarrhoeal disease 1 year to 5 year Female
        queries[33] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIADIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1129 : Measles 1 year to 5 year Male
        queries[34] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MEASLES
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1129 : Measles 1 year to 5 year Female
        queries[35] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MEASLES
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1130 : Other Fever related 1 year to 5 year Male
        queries[36] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1130 : Other Fever related 1 year to 5 year Female
        queries[37] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1131 : Others 1 year to 5 year Male
        queries[38] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1131 : Others 1 year to 5 year Female
        queries[39] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1132 : Not known 1 year to 5 year Male
        queries[40] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_NOT_KNOWN
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1132 : Not known 1 year to 5 year Female
        queries[41] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_NOT_KNOWN
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1133 : Diarrhoeal disease 5-14 years Male
        queries[42] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIADIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1133 : Diarrhoeal disease 5-14 years Female
        queries[43] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIADIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1134 : Tuberculosis 5-14 years Male
        queries[44] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TUBER
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1134 : Tuberculosis 5-14 years Female
        queries[45] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TUBER
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1135 : Malaria 5-14 years Male
        queries[46] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MALARIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1135 : Malaria 5-14 years Female
        queries[47] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MALARIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1136 : HIV/AIDS 5-14 years Male
        queries[48] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HIVAIDS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1136 : HIV/AIDS 5-14 years Female
        queries[49] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HIVAIDS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1137 : Other Fever related 5-14 years Male
        queries[50] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1137 : Other Fever related 5-14 years Female
        queries[51] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1138 : Pregnancy related death( maternal mortality) 15-55 years
        queries[52] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PRD
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1139 : Sterilisation related deaths 15-55 years Male
        queries[53] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = "
            + ouId
            + " AND periodid = "
            + pId
            + " AND dataelementid = "
            + LLDataSets.LLD_DEATH_CAUSE
            + " AND value LIKE '"
            + LLDataSets.LLD_SRD
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = "
            + ouId
            + " AND periodid = "
            + pId
            + " AND dataelementid = "
            + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR','O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1139 : Sterilisation related deaths 15-55 years Female
        queries[54] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = "
            + ouId
            + " AND periodid = "
            + pId
            + " AND dataelementid = "
            + LLDataSets.LLD_DEATH_CAUSE
            + " AND value LIKE '"
            + LLDataSets.LLD_SRD
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = "
            + ouId
            + " AND periodid = "
            + pId
            + " AND dataelementid = "
            + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR', 'O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1140 : Accidents or injuries 5-14 years Male
        queries[55] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_AI
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1140 : Accidents or injuries 5-14 years Female
        queries[56] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_AI
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1141 : Suicides 5-14 years Male
        queries[57] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SUICIDES
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1141 : Suicides 5-14 years Female
        queries[58] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SUICIDES
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1142 : Animal Bites or stings 5-14 years Male
        queries[59] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_ABS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1142 : Animal Bites or stings 5-14 years Female
        queries[60] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_ABS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1143 : Other known Acute disease 5-14 years Male
        queries[61] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OKAD
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1143 : Other known Acute disease 5-14 years Female
        queries[62] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OKAD
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1144 : Other known Chronic disease 5-14 years Male
        queries[63] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OKCD
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1144 : Other known Chronic disease 5-14 years Female
        queries[64] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OKCD
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1145 : Cause Not Known, 5-14 years Male
        queries[65] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value IN ( '" + LLDataSets.LLD_NOT_KNOWN
            + "' , '" + LLDataSets.LLD_OTHERS
            + "' ) AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1145 : Cause Not Known, 5-14 years Female
        queries[66] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value IN ( '" + LLDataSets.LLD_NOT_KNOWN
            + "' , '" + LLDataSets.LLD_OTHERS
            + "' ) AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1146 : Respiratory Infections and Disease 5-14 years Male
        queries[67] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_RID
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1146 : Respiratory Infections and Disease 5-14 years Female
        queries[68] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_RID
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1147 : Heart disease and hypertension 5-14 years Male
        queries[69] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HDH
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1147 : Heart disease and hypertension 5-14 years Female
        queries[70] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HDH
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1148 : Stroke and Neurological disease 5-14 years Male
        queries[71] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SND
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1148 : Stroke and Neurological disease 5-14 years Female
        queries[72] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SND
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1199 : Malaria 15-55 years Male
        queries[73] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MALARIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1199 : Malaria 15-55 years Female
        queries[74] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MALARIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1200 : Malaria Above 55 years Male
        queries[75] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MALARIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1200 : Malaria Above 55 years Female
        queries[76] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MALARIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1201 : Tuberculosis 15-55 years Male
        queries[77] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TUBER
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1201 : Tuberculosis 15-55 years Female
        queries[78] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TUBER
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1202 : Tuberculosis Above 55 years Male
        queries[79] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TUBER
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1202 : Tuberculosis Above 55 years Female
        queries[80] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TUBER
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1203 : Malaria 1 year to 5 years Male
        queries[81] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MALARIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1203 : Malaria 1 year to 5 years Female
        queries[82] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MALARIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1204 : Tuberculosis 1 year to 5 year Male
        queries[83] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TUBER
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1204 : Tuberculosis 1 year to 5 year Female
        queries[84] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TUBER
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1205 : Diarrhoeal disease 15-55 years Male
        queries[85] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIADIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1205 : Diarrhoeal disease 15-55 years Female
        queries[86] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIADIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1206 : HIV/AIDS 15-55 years Male
        queries[87] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HIVAIDS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1206 : HIV/AIDS 15-55 years Female
        queries[88] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HIVAIDS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1207 : Other Fever related 15-55 years Male
        queries[89] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1207 : Other Fever related 15-55 years Female
        queries[90] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1208 : Accidents or injuries 15-55 years Male
        queries[91] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_AI
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1208 : Accidents or injuries 15-55 years Female
        queries[92] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_AI
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1209 : Suicides 15-55 years Male
        queries[93] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SUICIDES
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1209 : Suicides 15-55 years Female
        queries[94] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SUICIDES
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1210 : Animal Bites or stings 15-55 years Male
        queries[95] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_ABS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1210 : Animal Bites or stings 15-55 years Female
        queries[96] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_ABS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1211 : Other known Acute disease 15-55 years Male
        queries[97] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OKAD
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1211 : Other known Acute disease 15-55 years Female
        queries[98] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OKAD
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1212 : Other known Chronic disease 15-55 years Male
        queries[99] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OKCD
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1212 : Other known Chronic disease 15-55 years Female
        queries[100] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OKCD
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1213 : Cause Not Known, 15-55 years Male
        queries[101] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value IN ( '" + LLDataSets.LLD_NOT_KNOWN
            + "' , '" + LLDataSets.LLD_OTHERS
            + "' ) AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1213 : Cause Not Known, 15-55 years Female
        queries[102] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value IN ( '" + LLDataSets.LLD_NOT_KNOWN
            + "' , '" + LLDataSets.LLD_OTHERS
            + "' ) AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1214 : Respiratory Infections and Disease 15-55 years Male
        queries[103] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_RID
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1214 : Respiratory Infections and Disease 15-55 years Female
        queries[104] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_RID
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1215 : Heart disease and hypertension 15-55 years Male
        queries[105] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HDH
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1215 : Heart disease and hypertension 15-55 years Female
        queries[106] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HDH
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1216 : Stroke and Neurological disease 15-55 years Male
        queries[107] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SND
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1216 : Stroke and Neurological disease 15-55 years Female
        queries[108] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SND
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O15YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1217 : Diarrhoeal disease Above 55 years Male
        queries[109] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIADIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1217 : Diarrhoeal disease Above 55 years Female
        queries[110] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIADIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1218 : HIV/AIDS Above 55 years Male
        queries[111] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HIVAIDS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1218 : HIV/AIDS Above 55 years Female
        queries[112] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HIVAIDS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1219 : Other Fever related Above 55 years Male
        queries[113] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1219 : Other Fever related Above 55 years Female
        queries[114] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1220 : Accidents or injuries Above 55 years Male
        queries[115] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_AI
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1220 : Accidents or injuries Above 55 years Female
        queries[116] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_AI
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1221 : Suicides Above 55 years Male
        queries[117] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SUICIDES
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1221 : Suicides Above 55 years Female
        queries[118] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SUICIDES
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1222 : Animal Bites or stings Above 55 years Male
        queries[119] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_ABS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1222 : Animal Bites or stings Above 55 years Female
        queries[120] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_ABS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1223 : Other known Acute disease Above 55 years Male
        queries[121] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OKAD
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1223 : Other known Acute disease Above 55 years Female
        queries[122] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OKAD
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1224 : Other known Chronic disease Above 55 years Male
        queries[123] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OKCD
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1224 : Other known Chronic disease Above 55 years Female
        queries[124] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OKCD
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1225 : Cause Not Known, Above 55 years Male
        queries[125] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value IN ( '" + LLDataSets.LLD_NOT_KNOWN
            + "', '" + LLDataSets.LLD_OTHERS
            + "') AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1225 : Cause Not Known, Above 55 years Female
        queries[126] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value IN ( '" + LLDataSets.LLD_NOT_KNOWN
            + "', '" + LLDataSets.LLD_OTHERS
            + "') AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1226 : Respiratory Infections and Disease Above 55 years Male
        queries[127] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_RID
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1226 : Respiratory Infections and Disease Above 55 years Female
        queries[128] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_RID
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1227 : Heart disease and hypertension Above 55 years Male
        queries[129] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HDH
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1227 : Heart disease and hypertension Above 55 years Female
        queries[130] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HDH
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1228 : Stroke and Neurological disease Above 55 years Male
        queries[131] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SND
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1228 : Stroke and Neurological disease Above 55 years Female
        queries[132] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SND
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('O55YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1229 : Immunization reactions 1 year to 5 year Male
        queries[133] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_IMMREAC
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1229 : Immunization reactions 1 yaer to 5 year Female
        queries[134] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_IMMREAC
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1230 Birth Asphyxia under one day Male
        queries[135] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_ASPHYXIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1230 Birth Asphyxia under one day Female
        queries[136] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_ASPHYXIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1231 : Sepsis under one day Male
        queries[137] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SEPSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1231 : Sepsis under one day Female
        queries[138] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SEPSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1232 : Low Birth Weight under one day Male
        queries[139] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '"
            + LLDataSets.LLD_LOW_BIRTH_WEIGH + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = "
            + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1232 : Low Birth Weight under one day Female
        queries[140] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '"
            + LLDataSets.LLD_LOW_BIRTH_WEIGH + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = "
            + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1233 : Immunization reactions under one day Male
        queries[141] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_IMMREAC
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1233 : Immunization reactions under one day Female
        queries[142] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_IMMREAC
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1234 : Others under one day Male
        queries[143] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1234 : Others under one day Female
        queries[144] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1235 : Not known under one day Male
        queries[145] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_NOT_KNOWN
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1235 : Not known under one day Female
        queries[146] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_NOT_KNOWN
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1236 Birth Asphyxia under one week Male
        queries[147] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_ASPHYXIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1236 Birth Asphyxia under one week Female
        queries[148] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_ASPHYXIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1237 : Sepsis under one week Male
        queries[149] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SEPSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1237 : Sepsis under one week Female
        queries[150] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_SEPSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1238 : Low Birth Weight under one week Male
        queries[151] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '"
            + LLDataSets.LLD_LOW_BIRTH_WEIGH + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = "
            + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1238 : Low Birth Weight under one week Female
        queries[152] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '"
            + LLDataSets.LLD_LOW_BIRTH_WEIGH + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = "
            + ouId + " AND periodid = " + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1239 : Immunization reactions under one week Male
        queries[153] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_IMMREAC
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1239 : Immunization reactions under one week Female
        queries[154] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_IMMREAC
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1240 : Others under one week Male
        queries[155] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1240 : Others under one week Female
        queries[156] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1241 : Not known under one week Male
        queries[157] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_NOT_KNOWN
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1241 : Not known under one week Female
        queries[158] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_NOT_KNOWN
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        // 1242 : Pneumonia 1 month to 5 year Male
        queries[159] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PNEUMONIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1242 : Pneumonia 1 month to 5 year Female
        queries[160] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PNEUMONIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1243 : Diarrhoeal disease 1 month to 5 year Male
        queries[161] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIADIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1243 : Diarrhoeal disease 1 month to 5 year Female
        queries[162] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIADIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1244 : Measles 1 month to 5 year Male
        queries[163] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MEASLES
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1244 : Measles 1 month to 5 year Female
        queries[164] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MEASLES
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1245 : Tuberculosis Above 55 years Male
        queries[165] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TUBER
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1245 : Tuberculosis Above 55 years Female
        queries[166] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TUBER
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1246 : Malaria 1 year to 5 years Male
        queries[167] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MALARIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1246 : Malaria 1 year to 5 years Female
        queries[168] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MALARIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1247 : Immunization reactions under one week Male
        queries[169] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_IMMREAC
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1247 : Immunization reactions under one week Female
        queries[170] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_IMMREAC
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1248 : Other Fever related 1 month to 5 year Male
        queries[171] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1248 : Other Fever related 1 month to 5 year Female
        queries[172] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1249 : Others 1 month to 5 year Male
        queries[173] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1249 : Others 1 month to 5 year Female
        queries[174] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1250 : Not known 1 month to 5 year Male
        queries[175] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_NOT_KNOWN
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        // 1250 : Not known 1 month to 5 year Female
        queries[176] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_NOT_KNOWN
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        
       
        // for haryana
        
       
        //  : Fever/other Fever  related below 1 day Female
        queries[177] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Fever/other Fever related below 1 day Male
        queries[178] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Fever/other Fever related 1 day to 1 week Female
        queries[179] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Fever/other Fever related 1 day to 1 week Male
        queries[180] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Fever/other Fever related 1 week to 1 month Female
        queries[181] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Fever/other Fever related 1 week to 1 month Male
        queries[182] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_OFR
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        
        
        
        
        
        //  : PNEUMONIA Fever  related below 1 day Female
        queries[183] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PNEUMONIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : PNEUMONIA Fever related below 1 day Male
        queries[184] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PNEUMONIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : PNEUMONIA Fever related 1 day to 1 week Female
        queries[185] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PNEUMONIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : PNEUMONIA Fever related 1 day to 1 week Male
        queries[186] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PNEUMONIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : PNEUMONIA Fever related 1 week to 1 month Female
        queries[187] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PNEUMONIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : PNEUMONIA Fever related 1 week to 1 month Male
        queries[188] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PNEUMONIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";

        
        
        
        
        
        
        //  : Meconium aspiration syndrome below 1 day Female
        queries[189] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MAS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Meconium aspiration syndrome below 1 day Male
        queries[190] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MAS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Meconium aspiration syndrome 1 day to 1 week Female
        queries[191] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MAS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Meconium aspiration syndrome 1 day to 1 week Male
        queries[192] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MAS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Meconium aspiration syndrome 1 week to 1 month Female
        queries[193] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MAS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Meconium aspiration syndrome 1 week to 1 month Male
        queries[194] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MAS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Meconium aspiration syndrome 1 month to 1 year Female
        queries[195] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MAS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Meconium aspiration syndrome 1 month to 1 year Male
        queries[196] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MAS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        
        //  : Meconium aspiration syndrome 1 year to 5 year Female
        queries[197] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MAS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Meconium aspiration syndrome 1 year to 5 year Male
        queries[198] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MAS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";           

        
       
        //  :Meningitis below 1 day Female
        queries[199] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MENINGITIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Meningitis below 1 day Male
        queries[200] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MENINGITIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Meningitis 1 day to 1 week Female
        queries[201] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MENINGITIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Meningitis 1 day to 1 week Male
        queries[202] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MENINGITIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Meningitis 1 week to 1 month Female
        queries[203] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MENINGITIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Meningitis 1 week to 1 month Male
        queries[204] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MENINGITIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Meningitis 1 month to 1 year Female
        queries[205] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MENINGITIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Meningitis 1 month to 1 year Male
        queries[206] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MENINGITIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        
        //  : Meningitis 1 year to 5 year Female
        queries[207] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MENINGITIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Meningitis 1 year to 5 year Male
        queries[208] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MENINGITIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";           
        
        
        
        
        //  :Major Congenital Malformation below 1 day Female
        queries[209] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MCM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Major Congenital Malformation below 1 day Male
        queries[210] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MCM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Major Congenital Malformation 1 day to 1 week Female
        queries[211] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MCM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Major Congenital Malformation 1 day to 1 week Male
        queries[212] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MCM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Major Congenital Malformation 1 week to 1 month Female
        queries[213] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MCM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Major Congenital Malformation 1 week to 1 month Male
        queries[214] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MCM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Major Congenital Malformation 1 month to 1 year Female
        queries[215] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MCM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Major Congenital Malformation 1 month to 1 year Male
        queries[216] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MCM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        
        //  : Major Congenital Malformation 1 year to 5 year Female
        queries[217] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MCM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Major Congenital Malformation 1 year to 5 year Male
        queries[218] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_MCM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";             
        
        
        //  : Prematurity below 1 day Female
        queries[219] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PREMATURITY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Prematurity below 1 day Male
        queries[220] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PREMATURITY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Prematurity 1 day to 1 week Female
        queries[221] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PREMATURITY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Prematurity 1 day to 1 week Male
        queries[222] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PREMATURITY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Prematurity 1 week to 1 month Female
        queries[223] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PREMATURITY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Prematurity 1 week to 1 month Male
        queries[224] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PREMATURITY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Prematurity 1 month to 1 year Female
        queries[225] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PREMATURITY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  :Prematurity 1 month to 1 year Male
        queries[226] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PREMATURITY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        
        //  : Prematurity 1 year to 5 year Female
        queries[227] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PREMATURITY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Prematurity 1 year to 5 year Male
        queries[228] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PREMATURITY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";             
                
        
        //  : Hypothermia below 1 day Female
        queries[229] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HYPOTHERMIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Hypothermia below 1 day Male
        queries[230] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HYPOTHERMIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Hypothermia 1 day to 1 week Female
        queries[231] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HYPOTHERMIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Hypothermia 1 day to 1 week Male
        queries[232] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HYPOTHERMIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Hypothermia 1 week to 1 month Female
        queries[233] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HYPOTHERMIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Hypothermia 1 week to 1 month Male
        queries[234] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HYPOTHERMIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Hypothermia 1 month to 1 year Female
        queries[235] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HYPOTHERMIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Hypothermia 1 month to 1 year Male
        queries[236] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HYPOTHERMIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        
        //  : Hypothermia 1 year to 5 year Female
        queries[237] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HYPOTHERMIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Hypothermia 1 year to 5 year Male
        queries[238] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_HYPOTHERMIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";            
        
        
        //  : Diptheria below 1 day Female
        queries[239] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIPTHERIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Diptheria below 1 day Male
        queries[240] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIPTHERIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Diptheria 1 day to 1 week Female
        queries[241] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIPTHERIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Diptheria 1 day to 1 week Male
        queries[242] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIPTHERIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Diptheria 1 week to 1 month Female
        queries[243] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIPTHERIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Diptheria 1 week to 1 month Male
        queries[244] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIPTHERIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Diptheria 1 month to 1 year Female
        queries[245] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIPTHERIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Diptheria 1 month to 1 year Male
        queries[246] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIPTHERIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        
        //  : Diptheria 1 year to 5 year Female
        queries[247] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIPTHERIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Diptheria 1 year to 5 year Male
        queries[248] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DIPTHERIA
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";            
               
        
        //  : Childhood Tuberculosis below 1 day Female
        queries[249] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_CHILD_TUBERCULOSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Childhood Tuberculosis below 1 day Male
        queries[250] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_CHILD_TUBERCULOSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Childhood Tuberculosis 1 day to 1 week Female
        queries[251] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_CHILD_TUBERCULOSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Childhood Tuberculosis 1 day to 1 week Male
        queries[252] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_CHILD_TUBERCULOSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Childhood Tuberculosis 1 week to 1 month Female
        queries[253] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_CHILD_TUBERCULOSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Childhood Tuberculosis 1 week to 1 month Male
        queries[254] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_CHILD_TUBERCULOSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Childhood Tuberculosis 1 month to 1 year Female
        queries[255] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_CHILD_TUBERCULOSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Childhood Tuberculosis 1 month to 1 year Male
        queries[256] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_CHILD_TUBERCULOSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        
        //  : Childhood Tuberculosis 1 year to 5 year Female
        queries[257] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_CHILD_TUBERCULOSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Childhood Tuberculosis 1 year to 5 year Male
        queries[258] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_CHILD_TUBERCULOSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";            
                
        
        //  : Dysentry below 1 day Female
        queries[259] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DYSENTRY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Dysentry below 1 day Male
        queries[260] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DYSENTRY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Dysentry 1 day to 1 week Female
        queries[261] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DYSENTRY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Dysentry 1 day to 1 week Male
        queries[262] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DYSENTRY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Dysentry 1 week to 1 month Female
        queries[263] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DYSENTRY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Dysentry 1 week to 1 month Male
        queries[264] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DYSENTRY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Dysentry 1 month to 1 year Female
        queries[265] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DYSENTRY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Dysentry 1 month to 1 year Male
        queries[266] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DYSENTRY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        
        //  : Dysentry 1 year to 5 year Female
        queries[267] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DYSENTRY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Dysentry 1 year to 5 year Male
        queries[268] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_DYSENTRY
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";          
        
        //  : Pertusis below 1 day Female
        queries[269] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PERTUSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Pertusis below 1 day Male
        queries[270] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PERTUSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Pertusis 1 day to 1 week Female
        queries[271] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PERTUSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Pertusis 1 day to 1 week Male
        queries[272] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PERTUSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Pertusis 1 week to 1 month Female
        queries[273] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PERTUSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Pertusis 1 week to 1 month Male
        queries[274] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PERTUSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Pertusis 1 month to 1 year Female
        queries[275] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PERTUSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Pertusis 1 month to 1 year Male
        queries[276] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PERTUSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        
        //  : Pertusis 1 year to 5 year Female
        queries[277] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PERTUSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Pertusis 1 year to 5 year Male
        queries[278] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_PERTUSIS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";           
                
        //  : Polio below 1 day Female
        queries[279] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_POLIO
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Polio below 1 day Male
        queries[280] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_POLIO
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Polio 1 day to 1 week Female
        queries[281] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_POLIO
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Polio 1 day to 1 week Male
        queries[282] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_POLIO
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Polio 1 week to 1 month Female
        queries[283] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_POLIO
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Polio 1 week to 1 month Male
        queries[284] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_POLIO
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Polio 1 month to 1 year Female
        queries[285] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_POLIO
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Polio 1 month to 1 year Male
        queries[286] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_POLIO
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        
        //  : Polio 1 year to 5 year Female
        queries[287] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_POLIO
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Polio 1 year to 5 year Male
        queries[288] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_POLIO
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";   
        
        //  : Tetanus Neonatorum below 1 day Female
        queries[289] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_NEONATORUM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Tetanus Neonatorum below 1 day Male
        queries[290] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_NEONATORUM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Tetanus Neonatorum 1 day to 1 week Female
        queries[291] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_NEONATORUM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Tetanus Neonatorum 1 day to 1 week Male
        queries[292] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_NEONATORUM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Tetanus Neonatorum 1 week to 1 month Female
        queries[293] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_NEONATORUM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Tetanus Neonatorum 1 week to 1 month Male
        queries[294] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_NEONATORUM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Tetanus Neonatorum 1 month to 1 year Female
        queries[295] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_NEONATORUM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Tetanus Neonatorum 1 month to 1 year Male
        queries[296] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_NEONATORUM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Tetanus Neonatorum 1 year to 5 year Female
        queries[297] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_NEONATORUM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Tetanus Neonatorum 1 year to 5 year Male
        queries[298] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_NEONATORUM
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";   
        
       
        
        
        
        //  : Tetanus (Others) below 1 day Female
        queries[299] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Tetanus (Others) below 1 day Male
        queries[300] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Tetanus (Others) 1 day to 1 week Female
        queries[301] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Tetanus (Others) 1 day to 1 week Male
        queries[302] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Tetanus (Others) 1 week to 1 month Female
        queries[303] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Tetanus (Others) 1 week to 1 month Male
        queries[304] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Tetanus (Others) 1 month to 1 year Female
        queries[305] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Tetanus (Others) 1 month to 1 year Male
        queries[306] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        
        //  : Tetanus (Others) 1 year to 5 year Female
        queries[307] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Tetanus (Others) 1 year to 5 year Male
        queries[308] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_TETANUS_OTHERS
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";           
        
        
        
       
        
        //  : Acute Flaccide paralysis  below 1 day Female
        queries[309] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_AFP
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Acute Flaccide paralysis  below 1 day Male
        queries[310] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_AFP
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Acute Flaccide paralysis  1 day to 1 week Female
        queries[311] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_AFP
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Acute Flaccide paralysis  1 day to 1 week Male
        queries[312] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_AFP
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Acute Flaccide paralysis  1 week to 1 month Female
        queries[313] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_AFP
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Acute Flaccide paralysis  1 week to 1 month Male
        queries[314] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_AFP
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Acute Flaccide paralysis  1 month to 1 year Female
        queries[315] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_AFP
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Acute Flaccide paralysis  1 month to 1 year Male
        queries[316] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_AFP
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        
        //  : Acute Flaccide paralysis  1 year to 5 year Female
        queries[317] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_AFP
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Acute Flaccide paralysis  1 year to 5 year Male
        queries[318] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_AFP
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";         
        
        
        
        
        //  : Respiratory Infections (other than TB) below 1 day  Female
        queries[319] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_RI_OTHER_THAN_TB
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Respiratory Infections (other than TB) below 1 day Male
        queries[320] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_RI_OTHER_THAN_TB
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1DAY') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Respiratory Infections (other than TB) 1 day to 1 week Female
        queries[321] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_RI_OTHER_THAN_TB
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Respiratory Infections (other than TB)  1 day to 1 week Male
        queries[322] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_RI_OTHER_THAN_TB
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1WEEK') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Respiratory Infections (other than TB) 1 week to 1 month Female
        queries[323] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_RI_OTHER_THAN_TB
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Respiratory Infections (other than TB) 1 week to 1 month Male
        queries[324] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_RI_OTHER_THAN_TB
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1MONTH') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Respiratory Infections (other than TB) 1 month to 1 year Female
        queries[325] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_RI_OTHER_THAN_TB
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Respiratory Infections (other than TB) 1 month to 1 year Male
        queries[326] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_RI_OTHER_THAN_TB
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B1YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        
        //  : Respiratory Infections (other than TB) 1 year to 5 year Female
        queries[327] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_RI_OTHER_THAN_TB
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'F' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
        
        //  : Respiratory Infections (other than TB)1 year to 5 year Male
        queries[328] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLD_DEATH_CAUSE + " AND value LIKE '" + LLDataSets.LLD_RI_OTHER_THAN_TB
            + "' AND recordno IN ( SELECT recordno FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = "
            + pId + " AND dataelementid = " + LLDataSets.LLD_AGE_CATEGORY
            + " AND value IN ('B5YEAR') AND recordno IN (SELECT recordno FROM lldatavalue WHERE dataelementid = "
            + LLDataSets.LLD_SEX + " AND value = 'M' AND sourceid = " + ouId + " AND periodid = " + pId + "))";
                
        
        
        
        
        try
        {
            //Connection con = jdbcTemplate.getDataSource().getConnection();
            
            for ( int i = 0; i < aggDeIds.length; i++ )
            {
                DataElement de = dataElementService.getDataElement( aggDeIds[i] );
                DataElementCategoryOptionCombo oc = dataElementCategoryService
                    .getDataElementCategoryOptionCombo( aggDeOptComIds[i] );
                if ( de != null && oc != null )
                {
                    //PreparedStatement pst = con.prepareStatement( queries[i] );
                    //ResultSet rs = pst.executeQuery();
                    SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( queries[i] );
                    try
                    {
                        if ( sqlResultSet.next() )
                        {
                            deValueMap.put( de.getId() + ":" + oc.getId(), "" + sqlResultSet.getInt( 1 ) );
                            //System.out.println( "Value for " + de.getId() + " is " + sqlResultSet.getInt( 1 ) );
                        }
                        else
                        {
                            deValueMap.put( de.getId() + ":" + oc.getId(), "0" );
                            //System.out.println( "No Value for " + de.getId() );
                        }
                    }
                    catch ( Exception e )
                    {
                        System.out.println( "Exception : " + e.getMessage() );
                    }
                    finally
                    {
                        //if ( pst != null )
                        //    pst.close();
                        //if ( rs != null )
                        //    rs.close();
                    }
                }
            }
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
        }
        finally
        {
            try
            {
                // if ( con != null )
                // con.close();
            }
            catch ( Exception e )
            {
                System.out.println( "Exception while closing DB Connections : " + e.getMessage() );
            }
        }
        
        //System.out.println( aggDeIds.length + " -- "+ aggDeOptComIds.length + " -- " + queries.length );
        
        return deValueMap;
    }

    public Map<String, String> processLineListMaternalDeaths( OrganisationUnit organisationUnit, Period periodL )
    {
        DatabaseInfo dataBaseInfo = databaseInfoProvider.getDatabaseInfo();
        
        Map<String, String> deValueMap = new HashMap<String, String>();
        int ouId = organisationUnit.getId();

        Period storedPeriod = reloadPeriod( periodL );
        int pId = storedPeriod.getId();

        // Connection con = (new DBConnection()).openConnection();
        // Connection con = dbConnection.openConnection();
        //Connection con = sessionFactory.getCurrentSession().connection();

        int[] aggDeIds = { LLDataSets.LLMD_DURING_PREGNANCY, LLDataSets.LLMD_DURING_FIRST_TRIM,
            LLDataSets.LLMD_DURING_SECOND_TRIM, LLDataSets.LLMD_DURING_THIRD_TRIM, LLDataSets.LLMD_DURING_DELIVERY,
            LLDataSets.LLMD_AFTER_DEL_WITHIN_42DAYS, LLDataSets.LLMD_AGE_BELOW16, LLDataSets.LLMD_AGE_16TO19,
            LLDataSets.LLMD_AGE_19TO35, LLDataSets.LLMD_AGE_ABOVE35, LLDataSets.LLMD_AT_HOME, LLDataSets.LLMD_AT_SC,
            LLDataSets.LLMD_AT_PHC, LLDataSets.LLMD_AT_CHC, LLDataSets.LLMD_AT_MC, LLDataSets.LLMD_AT_PVTINST, LLDataSets.LLMD_BY_UNTRAINED,
            LLDataSets.LLMD_BY_TRAINED, LLDataSets.LLMD_BY_ANM, LLDataSets.LLMD_BY_NURSE, LLDataSets.LLMD_BY_DOCTOR,
            LLDataSets.LLMD_CAUSE_ABORTION, LLDataSets.LLMD_CAUSE_OPL, LLDataSets.LLMD_CAUSE_FITS,
            LLDataSets.LLMD_CAUSE_SH, LLDataSets.LLMD_CAUSE_BBCD, LLDataSets.LLMD_CAUSE_BACD,
            LLDataSets.LLMD_CAUSE_HFBD, LLDataSets.LLMD_CAUSE_HFAD, LLDataSets.LLMD_CAUSE_NK, LLDataSets.LLMD_CAUSE_MDNK, LLDataSets.LLMD_CAUSE_ANAEMIA,
            LLDataSets.LLMD_CAUSE_SEVERE_ANAEMIA, LLDataSets.LLMD_CAUSE_MODERATE_ANAEMIA

        };
        String[] queries = new String[aggDeIds.length];

        // Metarnal Death During Pregnancy
        queries[0] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DURATION_OF_PREGNANCY + "  AND value IN ('FTP', 'STP', 'TTP')";
        // Metarnal Death During First Trimester
        queries[1] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DURATION_OF_PREGNANCY + "  AND value = 'FTP'";
        // Metarnal Death During Second Trimester
        queries[2] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DURATION_OF_PREGNANCY + "  AND value = 'STP'";
        // Metarnal Death During Third Trimester
        queries[3] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DURATION_OF_PREGNANCY + "  AND value = 'TTP'";
        // Metarnal Death During Delivery
        queries[4] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DURATION_OF_PREGNANCY + "  AND value = 'DELIVERY'";
        // Metarnal Death after delivery within 42days
        queries[5] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DURATION_OF_PREGNANCY + "  AND value = 'ADW42D'";

        // Metarnal Death At age below 16
        if ( dataBaseInfo.getType().equalsIgnoreCase( "mysql" ) )
        {
            queries[6] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
                + " AND dataelementid = " + LLDataSets.LLMD_AGE_AT_DEATH + "  AND value < 16";
        }
        else if( dataBaseInfo.getType().equalsIgnoreCase( "postgresql" ) )
        {
            queries[6] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
                + " AND dataelementid = " + LLDataSets.LLMD_AGE_AT_DEATH + "  AND CAST( value as numeric ) < 16";
        }
        // Metarnal Death At age 16 to 19
        if ( dataBaseInfo.getType().equalsIgnoreCase( "mysql" ) )
        {
            queries[7] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
                + " AND dataelementid = " + LLDataSets.LLMD_AGE_AT_DEATH + "  AND value >= 16 and value <= 19";
        }
        else if( dataBaseInfo.getType().equalsIgnoreCase( "postgresql" ) )
        {
            queries[7] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
                + " AND dataelementid = " + LLDataSets.LLMD_AGE_AT_DEATH + "  AND CAST( value as numeric ) >= 16 and CAST( value as numeric ) <= 19";
        }
        
        // Metarnal Death At age 20 to 35
        if ( dataBaseInfo.getType().equalsIgnoreCase( "mysql" ) )
        {
            queries[8] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
                + " AND dataelementid = " + LLDataSets.LLMD_AGE_AT_DEATH + "  AND value >= 20 and value <= 35 ";
        }
        else if( dataBaseInfo.getType().equalsIgnoreCase( "postgresql" ) )
        {
            queries[8] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
                + " AND dataelementid = " + LLDataSets.LLMD_AGE_AT_DEATH + "  AND CAST( value as numeric ) >= 20 and CAST( value as numeric ) <= 35 ";
        }
        
        // Metarnal Death At age above 35
        if ( dataBaseInfo.getType().equalsIgnoreCase( "mysql" ) )
        {
            queries[9] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
                + " AND dataelementid = " + LLDataSets.LLMD_AGE_AT_DEATH + "  AND value > 35";
        }
        else if( dataBaseInfo.getType().equalsIgnoreCase( "postgresql" ) )
        {
            queries[9] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
                + " AND dataelementid = " + LLDataSets.LLMD_AGE_AT_DEATH + "  AND CAST( value as numeric ) > 35";
        }
        
        // Metarnal Death At Home
        queries[10] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DELIVERY_AT + "  AND value = 'HOME'";
        // Metarnal Death At SC
        queries[11] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DELIVERY_AT + "  AND value = 'SC'";
        // Metarnal Death At PHC
        queries[12] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DELIVERY_AT + "  AND value = 'PHC'";
        // Metarnal Death At CHC
        queries[13] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DELIVERY_AT + "  AND value = 'CHC'";
        // Metarnal Death At Medical College
        queries[14] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DELIVERY_AT + "  AND value = 'MC'";
        
        // Metarnal Death At PVT INST
        queries[15] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DELIVERY_AT + "  AND value = 'PVTINST'";
        
        // Metarnal Death Assisted by Untrained
        queries[16] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_NATURE_OF_ASSISTANCE + "  AND value = 'UNTRAINED'";
        // Metarnal Death Assisted by Trained
        queries[17] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_NATURE_OF_ASSISTANCE + "  AND value = 'TRAINED'";
        // Metarnal Death Assisted by ANM
        queries[18] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_NATURE_OF_ASSISTANCE + "  AND value = 'ANM'";
        // Metarnal Death Assisted by Nurse
        queries[19] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_NATURE_OF_ASSISTANCE + "  AND value = 'NURSE'";
        // Metarnal Death Assisted by Doctor
        queries[20] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_NATURE_OF_ASSISTANCE + "  AND value = 'DOCTOR'";

        // Metarnal Death Cause Abortion
        queries[21] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'ABORTION'";
        // Metarnal Death Cause Obsturcted
        queries[22] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'OPL'";
        // Metarnal Death Cause Fits
        queries[23] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'FITS'";
        // Metarnal Death Cause Severe Hypertension
        queries[24] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'SH'";
        // Metarnal Death Cause Bleeding before Child Delivery
        queries[25] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'BBCD'";
        // Metarnal Death Cause Bleeding after Child Delivery
        queries[26] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'BACD'";
        // Metarnal Death Cause High fever before Delivery
        queries[27] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'HFBD'";
        // Metarnal Death Cause High fever after Delivery
        queries[28] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'HFAD'";
        // Metarnal Death Cause not known
        queries[29] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'NK'";
        // Metarnal Death Other Causes (including cause not known)
        queries[30] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'MDNK'";
        // Metarnal Death Other Causes (including cause not known)
        queries[31] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'ANAEMIA'";
        // Metarnal Death Other Causes (including cause not known)
        queries[32] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'SEVEREANAEMIA'";
        // Metarnal Death Other Causes (including cause not known)
        queries[33] = "SELECT COUNT(*) FROM lldatavalue WHERE sourceid = " + ouId + " AND periodid = " + pId
            + " AND dataelementid = " + LLDataSets.LLMD_DEATH_CAUSE + "  AND value = 'MODERATEANAEMIA'";
        try
        {
            //Connection con = jdbcTemplate.getDataSource().getConnection();
            
            for ( int i = 0; i < aggDeIds.length; i++ )
            {
                DataElement de = dataElementService.getDataElement( aggDeIds[i] );
                DataElementCategoryOptionCombo oc = de.getCategoryCombo().getOptionCombos().iterator().next();
                if ( de != null && oc != null )
                {
                    //PreparedStatement pst = con.prepareStatement( queries[i] );
                    //System.out.println( queries[i] );
                    //ResultSet rs = pst.executeQuery();
                    SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( queries[i] );
                    try
                    {
                        if ( sqlResultSet.next() )
                        {
                            deValueMap.put( de.getId() + ":" + oc.getId(), "" + sqlResultSet.getInt( 1 ) );
                            //System.out.println( "Value for " + de.getId() + " is " + sqlResultSet.getInt( 1 ) );
                        }
                        else
                        {
                            deValueMap.put( de.getId() + ":" + oc.getId(), "0" );
                            //System.out.println( "No Value for " + de.getId() );
                        }
                    }
                    catch ( Exception e )
                    {
                        System.out.println( "Exception : " + e.getMessage() );
                    }
                    finally
                    {
                        
                        //if ( pst != null )
                        //    pst.close();
                        //if ( rs != null )
                        //    rs.close();
                    }
                }
            }
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
        }
        finally
        {
            try
            {
                // if ( con != null )
                // con.close();
            }
            catch ( Exception e )
            {
                System.out.println( "Exception while closing DB Connections : " + e.getMessage() );
            }
        }

        return deValueMap;
    }
   
    public void removeLLRecord( int recordNo )
    {
        String query = "DELETE from lldatavalue WHERE recordno = " + recordNo;

        try
        {
           jdbcTemplate.update( query );
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception while deleting : " + e.getMessage() );
        }
    }
}
