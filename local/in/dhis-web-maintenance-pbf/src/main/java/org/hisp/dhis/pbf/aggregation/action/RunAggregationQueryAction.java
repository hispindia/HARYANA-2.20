package org.hisp.dhis.pbf.aggregation.action;

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
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.pbf.api.Lookup;
import org.hisp.dhis.pbf.impl.DefaultPBFAggregationService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

public class RunAggregationQueryAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    /*
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    */
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

    /*private CCEIAggregationService cceiAggregationService;

    public void setCceiAggregationService( CCEIAggregationService cceiAggregationService )
    {
        this.cceiAggregationService = cceiAggregationService;
    }
*/
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
    
    private String selectedPeriodId;
    
    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }
        
    private Date aggregationPeriod;
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Map<String, Double> aggregationResultMap = new HashMap<String, Double>();

        //Set<OrganisationUnit> orgUnitList = new HashSet<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() );

        Set<OrganisationUnit> tempOrgUnitList = new HashSet<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() );
        
        Set<OrganisationUnit> orgUnitList = new HashSet<OrganisationUnit>();
        
        for ( OrganisationUnit org : tempOrgUnitList )
        {
            orgUnitList.addAll( organisationUnitService.getOrganisationUnitWithChildren( org.getId() )  ) ;
        }
        
        /*
        List<OrganisationUnit> rootOrganisationUnits = new ArrayList<OrganisationUnit>( organisationUnitService.getRootOrganisationUnits() );

        OrganisationUnit rootOrganisationUnit = rootOrganisationUnits.get( 0 );
        */
        
        
        //Set<OrganisationUnitGroup> orgUnitGroups = new HashSet<OrganisationUnitGroup>( organisationUnitGroupService.getAllOrganisationUnitGroups() );

        /*List<OrganisationUnitGroup> ouGroups = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupService.getOrganisationUnitGroupByName( EquipmentAttributeValue.HEALTHFACILITY ) );

        OrganisationUnitGroup ouGroup = ouGroups.get( 0 );

        if ( ouGroup != null )
        {
            orgUnitList.retainAll( ouGroup.getMembers() );
        }*/

        /*
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyyMM" );
        String curMonth = simpleDateFormat.format( new Date() );
        Period period = PeriodType.getPeriodFromIsoString( curMonth );
        period = periodService.reloadPeriod( period );
        
        List<Period> periods = new ArrayList<Period>();
        periods.add( period );
*/
        
        Period period = new Period();
        
        period = PeriodType.getPeriodFromIsoString( selectedPeriodId );
        
        if( period != null )
        {
            aggregationPeriod = period.getStartDate();
        }
        
        else
        {
            aggregationPeriod = new Date();
        }
        
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
                
                Set<OrganisationUnit> orgUnits = new HashSet<OrganisationUnit>( dataSet.getSources() );
                
                orgUnits.retainAll( orgUnitList );
                
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
            
            dataElements.add( dataElement );
        }

        for( String key : aggregationResultMap.keySet() )
        {
            System.out.println( key + " -- " + aggregationResultMap.get(  key ) );
        }
        
        importStatus = defaultPBFAggregationService.importData( aggregationResultMap );

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


}
