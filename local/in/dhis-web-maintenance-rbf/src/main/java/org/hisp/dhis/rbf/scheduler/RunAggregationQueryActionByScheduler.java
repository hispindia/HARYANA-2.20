package org.hisp.dhis.rbf.scheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionService;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.rbf.api.Lookup;
import org.hisp.dhis.rbf.impl.DefaultPBFAggregationService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class RunAggregationQueryActionByScheduler implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private CaseAggregationConditionService aggregationConditionService;

    public void setAggregationConditionService( CaseAggregationConditionService aggregationConditionService )
    {
        this.aggregationConditionService = aggregationConditionService;
    }

    private DefaultPBFAggregationService defaultPBFAggregationService;
    
    public void setDefaultPBFAggregationService( DefaultPBFAggregationService defaultPBFAggregationService )
    {
        this.defaultPBFAggregationService = defaultPBFAggregationService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private List<DataElement> dataElements = new ArrayList<DataElement>();

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    private String importStatus = "";

    public String getImportStatus()
    {
        return importStatus;
    }
    

    private Date aggregationPeriod;
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        System.out.println(" Aggregation Job Scheduler Started at : " + new Date() );
        
        Map<String, Double> aggregationResultMap = new HashMap<String, Double>();

        Set<OrganisationUnit> orgUnitList = new HashSet<OrganisationUnit>();
        
        orgUnitList.addAll( organisationUnitService.getAllOrganisationUnits() );
        
        Period period = new Period();
        
        aggregationPeriod = new Date();
        
        // find first day and last day of previous month
        
        /*
        Calendar calprevious = Calendar.getInstance();
        calprevious.add(Calendar.MONTH, -1);
        calprevious.set(Calendar.DATE, 1);
        Date firstDateOfPreviousMonth = calprevious.getTime();
        
        calprevious.set(Calendar.DATE, calprevious.getActualMaximum(Calendar.DATE)); // changed calendar to cal

        Date lastDateOfPreviousMonth = calprevious.getTime();
        
        System.out.println( "First Date Of Previous Month -- " + firstDateOfPreviousMonth + " Last Date Of Previous Month -- " + lastDateOfPreviousMonth );
        */
        
        // find first day and last day of current month
        /*
        Calendar calCurrent = Calendar.getInstance();
        calCurrent.add(Calendar.MONTH, 0);
        calCurrent.set(Calendar.DATE, 1);
        Date firstDateOfCurrentMonth = calCurrent.getTime();
        
        calCurrent.set(Calendar.DATE, calCurrent.getActualMaximum(Calendar.DATE)); // changed calendar to cal

        Date lastDateOfCurrentMonth = calCurrent.getTime();
        
        System.out.println( "First Date Of Current Month -- " + firstDateOfCurrentMonth + " Last Date Of Current Month -- " + lastDateOfCurrentMonth );
        */
        
        
        //System.out.println( " Aggregation Period -- " + aggregationPeriod );
        
        Constant tariff_authority = constantService.getConstantByName( "TARIFF_SETTING_AUTHORITY" );
        int tariff_setting_authority = 0;
        if ( tariff_authority == null )
        {
            tariff_setting_authority = 3;
        }
        else
        {
            tariff_setting_authority = (int) tariff_authority.getValue();
        }

        Set<CaseAggregationCondition> conditions = new HashSet<CaseAggregationCondition>( aggregationConditionService.getAllCaseAggregationCondition() );
        for ( CaseAggregationCondition condition : conditions )
        {
            DataElement dataElement = condition.getAggregationDataElement();
                        
            if ( condition.getOperator().equals( Lookup.PBF_AGG_TYPE_OVERALL_QUALITY_SCORE ) )
            {
                Integer dataSetId = Integer.parseInt( condition.getAggregationExpression() );
                
                DataSet dataSet = dataSetService.getDataSet( dataSetId );
                
                //Set<OrganisationUnit> orgUnits = new HashSet<OrganisationUnit>( dataSet.getSources() );
                
                Set<OrganisationUnit> orgUnits = new HashSet<OrganisationUnit>( getDataSetSources( dataSet ) );
                
                System.out.println( " Size of  orgUnit List -- " + orgUnitList.size() );
                
                orgUnits.retainAll( orgUnitList );
                
                //System.out.println( " Size of OrgList Source -- " + orgUnits.size() );
                System.out.println( " Size of DataSet Source -- " + orgUnits.size() );
                
                List<Period> periods = new ArrayList<Period>();
                
                //periods.add( getCurrentPeriod( dataSet.getPeriodType(), new Date() ) );
                
                periods.add( getCurrentPeriod( dataSet.getPeriodType(), aggregationPeriod ) );

                aggregationResultMap.putAll( defaultPBFAggregationService.calculateOverallQualityScore( periods, dataElement, orgUnits, dataSetId, tariff_setting_authority ) );
            }
            else if( condition.getOperator().equals( Lookup.PBF_AGG_TYPE_OVERALL_UNADJUSTED_PBF_AMOUNT ) )
            {
                Integer dataSetId = Integer.parseInt( condition.getAggregationExpression() );
                
                DataSet dataSet = dataSetService.getDataSet( dataSetId );
                
                Set<OrganisationUnit> orgUnits = new HashSet<OrganisationUnit>( dataSet.getSources() );
                
                orgUnits.retainAll( orgUnitList );
                
                List<Period> periods = new ArrayList<Period>();
                
                //periods.add( getCurrentPeriod( dataSet.getPeriodType(), new Date() ) );
                
                periods.add( getCurrentPeriod( dataSet.getPeriodType(), aggregationPeriod ) );

                aggregationResultMap.putAll( defaultPBFAggregationService.calculateOverallUnadjustedPBFAmount( periods, dataElement, orgUnits, dataSetId ) );
            }
            else if( condition.getOperator().equals( Lookup.PBF_AGG_TYPE_QUANTITY_VALIDATED ) )
            {
                Set<OrganisationUnit> orgUnits = new HashSet<OrganisationUnit>();
                
                orgUnits.addAll( orgUnitList );
                
                List<Period> periods = new ArrayList<Period>();
                
                //periods.add( getCurrentPeriod( dataSet.getPeriodType(), new Date() ) );
                
                periods.add( period );

                aggregationResultMap.putAll( defaultPBFAggregationService.calculateQuantityValidated( periods, orgUnits ) );
                
            }
            
            dataElements.add( dataElement );
        }

        for( String key : aggregationResultMap.keySet() )
        {
            System.out.println( key + " -- " + aggregationResultMap.get(  key ) );
        }
        
        importStatus = defaultPBFAggregationService.importData( aggregationResultMap );
        
        System.out.println(" Aggregation Job Scheduler Status : " + importStatus );
        
        System.out.println(" Aggregation Job Scheduler Ended at : " + new Date() );
        
        return SUCCESS;
    }
    
    
    public Period getCurrentPeriod( PeriodType periodType, Date currentDate )
    {
        Period period = new Period();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime( currentDate );

        int currentMonth = calendar.get( Calendar.MONTH );

        if ( periodType.getName().equalsIgnoreCase( "quarterly" ) )
        {
            if ( currentMonth >= 0 && currentMonth <= 2 )
            {
                period = PeriodType.getPeriodFromIsoString( calendar.get( Calendar.YEAR ) + "Q1" );
            }
            else if ( currentMonth >= 3 && currentMonth <= 5 )
            {
                period = PeriodType.getPeriodFromIsoString( calendar.get( Calendar.YEAR ) + "Q2" );
            }
            else if ( currentMonth >= 6 && currentMonth <= 8 )
            {
                period = PeriodType.getPeriodFromIsoString( calendar.get( Calendar.YEAR ) + "Q3" );
            }
            else
            {
                period = PeriodType.getPeriodFromIsoString( calendar.get( Calendar.YEAR ) + "Q4" );
            }
        }
        else if ( periodType.getName().equalsIgnoreCase( "yearly" ) )
        {
            period = PeriodType.getPeriodFromIsoString( calendar.get( Calendar.YEAR )+"" );
        }
        else if ( periodType.getName().equalsIgnoreCase( "monthly" ) )
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyyMM" );
            period = PeriodType.getPeriodFromIsoString( simpleDateFormat.format( currentDate ) );
        }
        
        period = periodService.reloadPeriod( period );
        
        //System.out.println( periodType.getName() + " -- " + period.getStartDateString() );

        return period;
    }


    // get dataSet Source List
    public List<OrganisationUnit> getDataSetSources( DataSet dataSet )
    {
        List<OrganisationUnit> dataSetSourceList = new ArrayList<OrganisationUnit>();
        
        try
        {
            String query = "SELECT distinct(sourceid) FROM datasetsource " +
                            " WHERE  datasetid  = " + dataSet.getId()  + " ";
          
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer orgUnitId = rs.getInt( 1 );
                
                if ( orgUnitId != null )
                {
                    OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
                    dataSetSourceList.add( orgUnit );
                }
            }

            return dataSetSourceList;
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataSet Id or Source Id ", e );
        }
        
    }    
    
    
    //SELECT distinct(sourceid)FROM datasetsource where datasetid = 1432;
    
}
