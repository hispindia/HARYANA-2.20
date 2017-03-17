package org.hisp.dhis.rbf.impl;

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

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.rbf.api.Lookup;
import org.hisp.dhis.rbf.api.LookupService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultPBFAggregationService
{
    private final static String TARIFF_SETTING_AUTHORITY = "TARIFF_SETTING_AUTHORITY";
    
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

    /*
    private PeriodService periodService;
    
    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    */
    
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

    @Autowired
    private OrganisationUnitGroupService orgUnitGroupService;

    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private LookupService lookupService;
    
    // -------------------------------------------------------------------------
    //
    // -------------------------------------------------------------------------
    public Map<String, Double> getQualityMaxValues( OrganisationUnitGroup orgUnitGroup, Integer orgUnitId, String orgUnitBranchIds, DataSet dataSet, Period period )
    {
        Map<String, Double> qualityMaxValueMap = new HashMap<String, Double>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String curPeriod = simpleDateFormat.format( period.getEndDate() );
        
        try
        {                       
            String query = "select SUM( td.value ) from "+
                            "( " +
                                "select max(asd.level) as level,asd.dataelementid,asd.orgunitgroupid,datasetid " +
                                " from " +
                                    "( "+
                                        " select td.orgunitgroupid,td.organisationunitid,td.datasetid,td.dataelementid,os.level,td.value " +
                                            " from qualitymaxvalue td inner join _orgunitstructure os on os.organisationunitid = td.organisationunitid "+
                                            " where '" + curPeriod + "'  between date(td.startdate) and date(td.enddate) " +
                                                " and orgunitgroupid in ( " + orgUnitGroup.getId() + ") " +
                                                " and datasetid in ( " +dataSet.getId() + ") "+
                                                " )asd "+
                                                " group by asd.dataelementid,asd.orgunitgroupid,datasetid " +
                                                " )sag1 " +
                                                " inner join qualitymaxvalue td on td.dataelementid=sag1.dataelementid " +
                                                " where td.orgunitgroupid=sag1.orgunitgroupid " + 
                                                " and td.datasetid=sag1.datasetid " +
                                                " and td.organisationunitid in ("+ orgUnitBranchIds +") ";
            
            //System.out.println("Query: " + query );
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            while ( rs.next() )
            {
                //Integer dataElementId = rs.getInt( 1 );
                Double value = rs.getDouble( 1 );
                qualityMaxValueMap.put( orgUnitId+":"+period.getId(), value );
                //System.out.println( dataElementId + " : " + value );
            }
        }
        catch( Exception e )
        {
            System.out.println("In getQualityMaxValues Exception :"+ e.getMessage() );
        }
    
        return qualityMaxValueMap;
    }

    
    public Map<String, Double> calculateOverallQualityScore( List<Period> periods, DataElement dataElement, Set<OrganisationUnit> orgUnits, Integer dataSetId, int settingLevel )
    {
        System.out.println(" In side calculateOverallQualityScore method " );
        
        Map<String, Double> aggregationResultMap = new HashMap<String, Double>();
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        try
        {
            Map<String, Double> maxScoreResultMap = new HashMap<String, Double>();
            for( OrganisationUnit orgUnit : orgUnits )
            {
                //OrganisationUnitGroup orgUnitGroup = findPBFOrgUnitGroupforTariff( orgUnit );
                
                Integer orgUnitGroupId = getOrgUnitGroupIdforTariff( orgUnit );
                
                List<OrganisationUnit> orgUnitBranch = organisationUnitService.getOrganisationUnitBranch( orgUnit.getId() );
                String orgUnitBranchIds = "-1";
                for( OrganisationUnit ou : orgUnitBranch )
                {
                    orgUnitBranchIds += "," + ou.getId();
                }
                
                for( Period period : periods )
                {
                    String curPeriod = simpleDateFormat.format( period.getEndDate() );
                    
                    String query = "select SUM( td.value ) from "+
                        "( " +
                            "select max(asd.level) as level,asd.dataelementid,asd.orgunitgroupid,datasetid " +
                            " from " +
                                "( "+
                                    " select td.orgunitgroupid,td.organisationunitid,td.datasetid,td.dataelementid,os.level,td.value " +
                                        " from qualitymaxvalue td inner join _orgunitstructure os on os.organisationunitid = td.organisationunitid "+
                                        " where '" + curPeriod + "'  between date(td.startdate) and date(td.enddate) " +
                                            " and orgunitgroupid in ( " + orgUnitGroupId + ") " +
                                            " and datasetid in ( " + dataSetId + ") "+
                                            " )asd "+
                                            " group by asd.dataelementid,asd.orgunitgroupid,datasetid " +
                                            " )sag1 " +
                                            " inner join qualitymaxvalue td on td.dataelementid=sag1.dataelementid " +
                                            " where td.orgunitgroupid=sag1.orgunitgroupid " + 
                                            " and td.datasetid=sag1.datasetid " +
                                            " and td.organisationunitid in ("+ orgUnitBranchIds +") ";
                    
                    SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
                    while ( rs.next() )
                    {
                        //Integer orgUnitId = rs.getInt( 1 );
                        //Integer deId = rs.getInt( 3 );
                        Double value = rs.getDouble( 1 );
                        maxScoreResultMap.put( orgUnit.getId()+":"+period.getId(), value );
                    }

                    /*
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
                    */                
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
                                    " AND dv.value != null GROUP BY dv.sourceid, dv.periodid";
            
            
            //System.out.println( "Query is --- " + query );
            
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
                        
                        aggregationResultMap.put( orgUnitId+ ":" +dataElement.getId() +":" +dataElementCategoryService.getDefaultDataElementCategoryOptionCombo().getId() + ":" +periodId, overAllQualityScore );                            
                    }
                }
                catch( Exception e )
                {
                    
                }
            }                
        }
        catch( Exception e )
        {
            System.out.println("Exception EEEEEE---TTTTTT: " + e.getMessage() );
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
                aggregationResultMap.put( orgUnitId+":"+dataElement.getId()+ ":" +dataElementCategoryService.getDefaultDataElementCategoryOptionCombo().getId() + ":"+periodId, countValue );
            }
        }
        catch( Exception e )
        {
            System.out.println("Exception :"+ e.getMessage() );
        }
        
        return aggregationResultMap;
    }

    public Map<String, Double> calculateQuantityValidated( List<Period> periods, Set<OrganisationUnit> orgUnits )
    {
        Map<String, Double> aggregationResultMap = new HashMap<String, Double>();
        
        Lookup catQtyReportedLookup = lookupService.getLookupByName( Lookup.CATEGORY_QUANTITY_REPORTED );
        
        Lookup catQtyValidatedLookup = lookupService.getLookupByName( Lookup.CATEGORY_QUANTITY_VALIDATED );
        
        Lookup catQtyExternalVerificationLookup = lookupService.getLookupByName( Lookup.CATEGORY_QUANTITY_EXTERNAL_VERIFICATION );
        
        try
        {   
            /*
            String query = "SELECT organisationunitid, dataelementid, periodid, qtyvalidated FROM pbfdatavalue " +
                            " WHERE " + 
                                " periodid IN ( "+ Lookup.PERIODID_BY_COMMA +" ) AND "+
                                " organisationunitid IN ( " + Lookup.ORGUNITID_BY_COMMA + " ) ";
            
            */
            
            String query = "SELECT organisationunitid, dataelementid, periodid, qtyreported, qtyvalidated, qtyexternalverification FROM pbfdatavalue " +
                " WHERE " + 
                    " periodid IN ( "+ Lookup.PERIODID_BY_COMMA +" ) AND "+
                    " organisationunitid IN ( " + Lookup.ORGUNITID_BY_COMMA + " ) ";
            
            
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
                Integer dataElementId = rs.getInt( 2 );
                Integer periodId = rs.getInt( 3 );
                
                Double qtyReported = rs.getDouble( 4 );
                Double qtyValidated = rs.getDouble( 5 );
                Double qtyExternalVerification = rs.getDouble( 6 );
                
                if( qtyReported != null && catQtyReportedLookup != null  )
                {
                    //aggregationResultMap.put( orgUnitId+":"+dataElementId+":"+periodId, qtyValidated );
                    aggregationResultMap.put( orgUnitId+":"+dataElementId +":" +catQtyReportedLookup.getValue() + ":"+periodId, qtyReported );
                }
                
                if( qtyValidated != null && catQtyValidatedLookup != null )
                {
                    //aggregationResultMap.put( orgUnitId+":"+dataElementId+":"+periodId, qtyValidated );
                    aggregationResultMap.put( orgUnitId+":"+dataElementId +":" +catQtyValidatedLookup.getValue() + ":"+periodId, qtyValidated );
                }
                
                if( qtyExternalVerification != null && catQtyExternalVerificationLookup != null )
                {
                    //aggregationResultMap.put( orgUnitId+":"+dataElementId+":"+periodId, qtyValidated );
                    aggregationResultMap.put( orgUnitId+":"+dataElementId +":" +catQtyExternalVerificationLookup.getValue() + ":"+periodId, qtyExternalVerification );
                }
                
            }
        }
        catch( Exception e )
        {
            System.out.println("Exception :"+ e.getMessage() );
        }
        
        return aggregationResultMap;
    }

    public Double calculateOverallUnadjustedPBFAmount( Period period, OrganisationUnit orgUnit, DataSet dataSet )
    {
        Double overAllAdjustedAmt = null;
        
        try
        {
            String query = "SELECT SUM( ( qtyvalidated * tariffamount ) ) FROM pbfdatavalue " +
                            " WHERE " + 
                                " periodid = "+ period.getId() +" AND "+
                                " datasetid = "+ dataSet.getId() + " AND " +
                                " organisationunitid = " + orgUnit.getId();
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            if( rs.next() )
            {
                overAllAdjustedAmt = rs.getDouble( 1 );
            }
        }
        catch( Exception e )
        {
            System.out.println("Exception in calculateOverallUnadjustedPBFAmount :"+ e.getMessage() );
        }
        
        return overAllAdjustedAmt;
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
                //Integer deId = rs.getInt( 1 );
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
                Integer deCOCId = Integer.parseInt( oneRow[2] );
                
                //Integer periodId = period.getId();
                Integer periodId = Integer.parseInt( oneRow[3] );
                
                //Integer deCOCId = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo().getId();
                
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

            importStatus = "Successfully populated aggregated data : "; // for the period + period.getStartDateString();
            importStatus += "<br/> Total new records : " + insertCount;
            importStatus += "<br/> Total updated records : " + updateCount;

        }
        catch ( Exception e )
        {
            importStatus = "Exception occured while import, please check log for more details" + e.getMessage();
        }

        return importStatus;
    }
/*
    public OrganisationUnitGroup findPBFOrgUnitGroupforTariff( OrganisationUnit organisationUnit )
    {
        System.out.println(" In side findPBFOrgUnitGroupforTariff method " );
        
        Constant tariff_authority = constantService.getConstantByName( TARIFF_SETTING_AUTHORITY );
        
        OrganisationUnitGroupSet orgUnitGroupSet = orgUnitGroupService.getOrganisationUnitGroupSet( (int) tariff_authority.getValue() );
        
        OrganisationUnitGroup orgUnitGroup = organisationUnit.getGroupInGroupSet( orgUnitGroupSet );
        
        return orgUnitGroup;
        
    }
 */
    
    
    public Integer getOrgUnitGroupIdforTariff( OrganisationUnit organisationUnit )
    {
        //System.out.println(" In side findPBFOrgUnitGroupforTariff method " );
        
        Constant tariff_authority = constantService.getConstantByName( TARIFF_SETTING_AUTHORITY );
        
        OrganisationUnitGroupSet orgUnitGroupSet = orgUnitGroupService.getOrganisationUnitGroupSet( (int) tariff_authority.getValue() );
        
        Integer orgUnitGroupId = null;
        
        try
        {
            String query = "SELECT orgunitgroupid from orgunitgroupsetmembers where orgunitgroupsetid = " + orgUnitGroupSet.getId() + " AND "
                + " orgunitgroupid in ( select orgunitgroupid from orgunitgroupmembers where organisationunitid = " + organisationUnit.getId() + ")";

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            if ( rs.next() )
            {
                orgUnitGroupId = rs.getInt( 1 );
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
        return orgUnitGroupId;
        
        
        //"SELECT orgunitgroupid from orgunitgroupsetmembers where orgunitgroupsetid = 1372 and orgunitgroupid in ( select orgunitgroupid from orgunitgroupmembers where organisationunitid = 1230 )";

        
    }    
    
    
    
    

}
