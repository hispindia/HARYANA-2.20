package org.hisp.dhis.pbf.impl;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.pbf.api.Lookup;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultPBFAggregationService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private PeriodService periodService;
    
    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataElementCategoryService dataElementCategoryService;
    
    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------

    public Map<String, Double> calculateOverallQualityScore( List<Period> periods, DataElement dataElement, Set<OrganisationUnit> orgUnits, Integer dataSetId, int settingLevel )
    {
        Map<String, Double> aggregationResultMap = new HashMap<String, Double>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        try
        {
            Map<String, Double> maxScoreResultMap = new HashMap<String, Double>();
            for( Period period : periods )
            {
                String query = "SELECT os.organisationunitid, qmv.organisationunitid, qmv.startdate, qmv.enddate, SUM( qmv.value ) FROM qualitymaxvalue qmv "+ 
                                    " INNER JOIN _orgunitstructure os on qmv.organisationunitid = os.idlevel"+settingLevel+" "+ 
                                    " INNER JOIN datasetmembers dsm on dsm.dataelementid = qmv.dataelementid " +
                                    " WHERE " +
                                        " qmv.startdate <='"+ simpleDateFormat.format( period.getStartDate() ) +"' AND "+
                                        " qmv.enddate >='"+ simpleDateFormat.format( period.getEndDate() ) +"' AND " +
                                        " dsm.datasetid = " + dataSetId +" " +
                                        " GROUP BY os.organisationunitid, qmv.organisationunitid, qmv.startdate, qmv.enddate";

                //System.out.println( query );
                
                SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                while ( rs.next() )
                {
                    Integer orgUnitId = rs.getInt( 1 );
                    //Integer deId = rs.getInt( 3 );
                    Double value = rs.getDouble( 5 );
                    maxScoreResultMap.put( orgUnitId+":"+period.getId(), value );
                }                
            }
            
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnits ) );
            String orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
            
            Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers( Period.class, periods ) );
            String periodsByComma = getCommaDelimitedString( periodIds );

            String query = "SELECT dv.sourceid, dv.periodid, SUM( CAST ( value AS NUMERIC ) ) FROM datavalue dv "+ 
                                " INNER JOIN datasetmembers dsm on dsm.dataelementid = dv.dataelementid " +
                                " WHERE " +
                                    " dv.periodid IN (" + periodsByComma + ") AND "+
                                    " dv.sourceid IN ("+ orgUnitIdsByComma +") AND " +
                                    " dsm.datasetid = " + dataSetId +" " +
                                    " GROUP BY dv.sourceid, dv.periodid";
            
            //System.out.println( query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                Integer periodId = rs.getInt( 2 );
                Double value = rs.getDouble( 3 );
                
                try
                {
                    Double maxScore = maxScoreResultMap.get( orgUnitId+":"+periodId );
                    if( maxScore != null && maxScore != 0.0 )
                    {
                        Double overAllQualityScore = ( value / maxScore ) * 100.0;
                        
                        aggregationResultMap.put( orgUnitId+":"+dataElement.getId()+":"+periodId, overAllQualityScore );                            
                    }
                }
                catch( Exception e )
                {
                    
                }
            }                
        }
        catch( Exception e )
        {
            System.out.println("Exception :"+ e.getMessage() );
        }
        
        return aggregationResultMap;
    }
    
    public Map<String, Double> calculateOverallUnadjustedPBFAmount( List<Period> periods, DataElement dataElement, Set<OrganisationUnit> orgUnits, Integer dataSetId )
    {
        Map<String, Double> aggregationResultMap = new HashMap<String, Double>();
        
        try
        {
            String query = "SELECT organisationunitid, periodid, SUM( ( qtyvalidated * tariffamount ) ) FROM pbfdatavalue " +
                            " WHERE " + 
                                " periodid IN ( "+ Lookup.PERIODID_BY_COMMA +" ) AND "+
                                " datasetid = "+ dataSetId + " AND " +
                                " organisationunitid IN ( " + Lookup.ORGUNITID_BY_COMMA + " ) " +
                             " GROUP BY organisationunitid, periodid ";        
            
            //System.out.println( "Query Before Replace : --" +  orgUnits.size() + " -- "+  query  );
            
            if( periods != null && periods.size() > 0 )
            {
                Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers( Period.class, periods ) );
                String periodsByComma = getCommaDelimitedString( periodIds );
                query = query.replace( Lookup.PERIODID_BY_COMMA, periodsByComma );
            }
            else
            {
                query = query.replace( Lookup.PERIODID_BY_COMMA, "-1" );
            }
            
            if( orgUnits != null && orgUnits.size() > 0 )
            {
                Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnits ) );
                String orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
                query = query.replace( Lookup.ORGUNITID_BY_COMMA, orgUnitIdsByComma );
            }
            else
            {
                query = query.replace( Lookup.ORGUNITID_BY_COMMA, "-1" );
            }
            
            //System.out.println( "Query After Replace : --" +  query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                Integer periodId = rs.getInt( 2 );
                Double countValue = rs.getDouble( 3 );
                aggregationResultMap.put( orgUnitId+":"+dataElement.getId()+":"+periodId, countValue );
            }
        }
        catch( Exception e )
        {
            System.out.println("Exception :"+ e.getMessage() );
        }
        
        return aggregationResultMap;
    }
    
    
    public Double calculateOverallQualityScore( Period period, Set<OrganisationUnit> orgUnits, Integer dataSetId, int maxScoreOrgUnitId )
    {
        Double overallQualityScore = 0.0;
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        try
        {
            Double maxScore = 0.0;
            String query = "SELECT dataelementid, (SUM(value) * "+orgUnits.size() +") FROM qualitymaxvalue " +
                                " WHERE " +
                                    " startdate <='"+ simpleDateFormat.format( period.getStartDate() ) +"' AND "+
                                    " enddate >='"+ simpleDateFormat.format( period.getEndDate() ) +"' AND " +
                                    " organisationunitid = "+ maxScoreOrgUnitId +" AND "+
                                    " datasetid = " + dataSetId +" " +
                                    " GROUP BY dataelementid";                                

            //System.out.println( query );
                
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                Integer deId = rs.getInt( 1 );
                Double value = rs.getDouble( 2 );
                maxScore += value;
            }                
            
            Collection<Integer> orgUnitIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, orgUnits ) );
            String orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );
            
            query = "SELECT SUM( CAST ( value AS NUMERIC ) ) FROM datavalue dv "+ 
                                " INNER JOIN datasetmembers dsm on dsm.dataelementid = dv.dataelementid " +
                                " WHERE " +
                                    " dv.periodid = " + period.getId() + " AND "+
                                    " dv.sourceid IN ("+ orgUnitIdsByComma +") AND " +
                                    " dsm.datasetid = " + dataSetId;
            
            //System.out.println( query );
            Double qualityScore = 0.0;
            SqlRowSet rs1 = jdbcTemplate.queryForRowSet( query );
            if ( rs1.next() )
            {
                qualityScore = rs1.getDouble( 1 );
            }
            
            try
            {
                if( maxScore != 0.0 )
                {
                    overallQualityScore = ( qualityScore / maxScore ) * 100.0;
                }
            }
            catch( Exception e )
            {
                
            }
        }
        catch( Exception e )
        {
            System.out.println("Exception : "+ e.getMessage() );
        }
        
        return overallQualityScore;
    }

 
    public String importData( Map<String, Double> aggregationResultMap )
    {
        String importStatus = "";

        Integer updateCount = 0;
        Integer insertCount = 0;

        String storedBy = currentUserService.getCurrentUsername();
        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }

        long t;
        Date d = new Date();
        t = d.getTime();
        java.sql.Date lastUpdatedDate = new java.sql.Date( t );

        String query = "";
        int insertFlag = 1;
        String insertQuery = "INSERT INTO datavalue ( dataelementid, periodid, sourceid, categoryoptioncomboid, value, storedby, lastupdated, attributeoptioncomboid ) VALUES ";

        try
        {
            int count = 1;
            for ( String cellKey : aggregationResultMap.keySet() )
            {
                // Orgunit
                String[] oneRow = cellKey.split( ":" );
                Integer orgUnitId = Integer.parseInt( oneRow[0] );
                Integer deId = Integer.parseInt( oneRow[1] );
                //Integer periodId = period.getId();
                Integer periodId = Integer.parseInt( oneRow[2] );
                
                Integer deCOCId = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo().getId();
                String value = aggregationResultMap.get( cellKey ) + "";

                query = "SELECT value FROM datavalue WHERE dataelementid = " + deId + " AND categoryoptioncomboid = " + deCOCId + " AND periodid = " + periodId + " AND sourceid = " + orgUnitId;
                SqlRowSet sqlResultSet1 = jdbcTemplate.queryForRowSet( query );
                if ( sqlResultSet1 != null && sqlResultSet1.next() )
                {
                    String updateQuery = "UPDATE datavalue SET value = '" + value + "', storedby = '" + storedBy + "',lastupdated='" + lastUpdatedDate + "' WHERE dataelementid = " + deId + " AND periodid = "
                        + periodId + " AND sourceid = " + orgUnitId + " AND categoryoptioncomboid = " + deCOCId;

                    jdbcTemplate.update( updateQuery );
                    updateCount++;
                }
                else
                {
                    if ( value != null && !value.trim().equals( "" ) )
                    {
                        insertQuery += "( " + deId + ", " + periodId + ", " + orgUnitId + ", " + deCOCId + ", '" + value + "', '" + storedBy + "', '" + lastUpdatedDate + "'," + deCOCId + "), ";
                        insertFlag = 2;
                        insertCount++;
                    }
                }

                if ( count == 1000 )
                {
                    count = 1;

                    if ( insertFlag != 1 )
                    {
                        insertQuery = insertQuery.substring( 0, insertQuery.length() - 2 );
                        jdbcTemplate.update( insertQuery );
                    }

                    insertFlag = 1;

                    insertQuery = "INSERT INTO datavalue ( dataelementid, periodid, sourceid, categoryoptioncomboid, value, storedby, lastupdated, attributeoptioncomboid ) VALUES ";
                }

                count++;
            }

            if ( insertFlag != 1 )
            {
                insertQuery = insertQuery.substring( 0, insertQuery.length() - 2 );
                jdbcTemplate.update( insertQuery );
            }

            importStatus = "Successfully populated aggregated data for the period : "; //+ period.getStartDateString();
            importStatus += "<br/> Total new records : " + insertCount;
            importStatus += "<br/> Total updated records : " + updateCount;

        }
        catch ( Exception e )
        {
            importStatus = "Exception occured while import, please check log for more details" + e.getMessage();
        }

        return importStatus;
    }

}
