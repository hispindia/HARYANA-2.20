package org.hisp.dhis.rbf.quality.dataentry;

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.Section;
import org.hisp.dhis.dataset.comparator.SectionOrderComparator;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.rbf.api.Lookup;
import org.hisp.dhis.rbf.api.LookupService;
import org.hisp.dhis.rbf.api.QualityMaxValueService;
import org.hisp.dhis.rbf.api.QualityScorePayment;
import org.hisp.dhis.rbf.api.QualityScorePaymentService;
import org.hisp.dhis.rbf.impl.DefaultPBFAggregationService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class LoadQualityScoreDetailsAction
    implements Action
{
    private final static String TARIFF_SETTING_AUTHORITY = "TARIFF_SETTING_AUTHORITY";

    private final static String QUALITY_MAX_DATAELEMENT = "QUALITY_MAX_DATAELEMENT";
    
    private final static String OVER_ALL_QUALITY_SCORE_DATAELEMENT_ID = "OVER_ALL_QUALITY_SCORE_DATAELEMENT_ID";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private QualityMaxValueService qualityMaxValueService;

    public void setQualityMaxValueService( QualityMaxValueService qualityMaxValueService )
    {
        this.qualityMaxValueService = qualityMaxValueService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private LookupService lookupService;

    public void setLookupService( LookupService lookupService )
    {
        this.lookupService = lookupService;
    }

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    @Autowired
    private OrganisationUnitGroupService orgUnitGroupService;
    
    @Autowired
    private DefaultPBFAggregationService defaultPBFAggregationService;
    
    @Autowired
    private PeriodService periodService;
    
    @Autowired
    private QualityScorePaymentService qualityScorePaymentService;

    @Autowired
    private DataElementService dataElementService;
    
    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }
    
    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------

    private String orgUnitId;

    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private String dataSetId;

    public void setDataSetId( String dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private String selectedPeriodId;

    public void setSelectedPeriodId( String selectedPeriodId )
    {
        this.selectedPeriodId = selectedPeriodId;
    }

    List<DataElement> dataElements = new ArrayList<DataElement>();

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    private Map<Integer, Double> qualityMaxValueMap = new HashMap<Integer, Double>();

    public Map<Integer, Double> getQualityMaxValueMap()
    {
        return qualityMaxValueMap;
    }

    private Map<Integer, DataValue> dataValueMap = new HashMap<Integer, DataValue>();

    public Map<Integer, DataValue> getDataValueMap()
    {
        return dataValueMap;
    }

    private String paymentMessage = "( No data avialable for ";
    
    public String getPaymentMessage()
    {
        return paymentMessage;
    }
    
    private Double totalUnadjustedAmt = 0.0;

    public Double getTotalUnadjustedAmt()
    {
        return totalUnadjustedAmt;
    }
    
    private Set<QualityScorePayment> qualityScorePayments;
    
    public Set<QualityScorePayment> getQualityScorePayments()
    {
        return qualityScorePayments;
    }
    
    private int overAllQtyDataElementId;
    
    public int getOverAllQtyDataElementId()
    {
        return overAllQtyDataElementId;
    }
    
    private boolean locked = false;
    
    public boolean isLocked()
    {
        return locked;
    }
    
    private int overHeadPaymentDataElementId;
    
    public int getOverHeadPaymentDataElementId()
    {
        return overHeadPaymentDataElementId;
    }
    
    private String overHeadPaymentDEValue = "";
    
    public String getOverHeadPaymentDEValue()
    {
        return overHeadPaymentDEValue;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

        Period period = PeriodType.getPeriodFromIsoString( selectedPeriodId );
        
        Constant tariff_authority = constantService.getConstantByName( TARIFF_SETTING_AUTHORITY );
        
        int tariff_setting_authority = 0;
        if ( tariff_authority == null )
        {
            tariff_setting_authority = 1;

        }
        else
        {
            tariff_setting_authority = (int) tariff_authority.getValue();
        }
        
        Constant qualityMaxDataElement = constantService.getConstantByName( QUALITY_MAX_DATAELEMENT );
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        
        
        Constant overAllQtyDetId = constantService.getConstantByName( OVER_ALL_QUALITY_SCORE_DATAELEMENT_ID );
        DataElement overAllDataElement = dataElementService.getDataElement( (int) overAllQtyDetId.getValue() );
        overAllQtyDataElementId = 0;
        if( overAllDataElement != null )
        {
            overAllQtyDataElementId = overAllDataElement.getId();
        }
        
        
        Lookup ohPaymentlookup =  lookupService.getLookupByName( Lookup.QUALITY_OVERHEAD_PAYMENT );
        
        if( ohPaymentlookup != null )
        {
            DataElement overHeadPaymentDataElement = dataElementService.getDataElement( Integer.parseInt( ohPaymentlookup.getValue() ) );
            overHeadPaymentDataElementId = 0;
            if( overHeadPaymentDataElement != null )
            {
                overHeadPaymentDataElementId = overHeadPaymentDataElement.getId();
            }
            
            DataElementCategoryOptionCombo optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
            DataValue dataValue = dataValueService.getDataValue( overHeadPaymentDataElement, period, organisationUnit, optionCombo );
            if ( dataValue != null )
            {
                overHeadPaymentDEValue = dataValue.getValue();
            }        
            
        }
        
        List<OrganisationUnit> orgUnitBranch = organisationUnitService.getOrganisationUnitBranch( organisationUnit.getId() );
        String orgUnitBranchIds = "-1";
        for( OrganisationUnit orgUnit : orgUnitBranch )
        {
            orgUnitBranchIds += "," + orgUnit.getId();
        }
        
        DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( dataSetId ) );
        
        locked = dataSetService.isLocked( dataSet, period, organisationUnit, null, null );
        
        DataElementCategoryOptionCombo optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();

        OrganisationUnitGroup orgUnitGroup = findPBFOrgUnitGroupforTariff( organisationUnit, dataSet.getId(), orgUnitBranchIds );
        
        
        //System.out.println(" DataSet Id ---" + dataSet.getId()  );
        //System.out.println(" orgUnitGroup Id ---" + orgUnitGroup.getId()  );
        //System.out.println(" orgUnitBranch Ids ---" + orgUnitBranchIds  );
        //System.out.println(" period Id ---" + period.getId()  );
        
        if( orgUnitGroup != null )
        {
            qualityMaxValueMap.putAll( qualityMaxValueService.getQualityMaxValues( orgUnitGroup, orgUnitBranchIds, dataSet, period ) );
        }
        
 
        
        //List<DataElement> dataElementList = new ArrayList<DataElement>( dataSet.getDataElements() );
        
        
        
        //dataElements = new ArrayList<DataElement>();
        //dataElements = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
        
        List<DataElement> dataElementList = new ArrayList<DataElement>();
        
        
        
    
        List<Section> sectionList = new ArrayList<Section>( dataSet.getSections() );
        List<DataElement> tempDEList = new ArrayList<DataElement>();
        
        if( sectionList != null && sectionList.size() > 0  )
        {
            Collections.sort(sectionList ,new SectionOrderComparator());
            
            for ( Section section : sectionList )
            {
               tempDEList.addAll( section.getDataElements() );
            }
            
            dataElementList.addAll( tempDEList );
        }
        else
        {
            dataElementList.addAll( dataSet.getDataElements() );
        }
        
        List<DataElement> tempDataElementList = new ArrayList<DataElement>();
        
        for ( DataElement de : dataElementList )
        {
            Set<AttributeValue> attrValueSet = new HashSet<AttributeValue>( de.getAttributeValues() );
            for ( AttributeValue attValue : attrValueSet )
            {
                if ( attValue.getAttribute().getId() == qualityMaxDataElement.getValue() )
                {
                    tempDataElementList.add( de );
                }
            }
        }
        
        
        //dataElements.retainAll( tempDataElementList );
        
        dataElements = new ArrayList<DataElement>( geti18nDataElements( dataElementList ) );
        
        /*
        for ( DataElement de : dataElements )
        {
            System.out.println(" de name ---" + de.getId() + "-"+ de.getDisplayName() +" Quality max value is : " + qualityMaxValueMap.get( de.getId() )  );
            
        }
        */
        
       
        for ( DataElement dataElement : dataElements )
        {
            DataValue dataValue = dataValueService.getDataValue( dataElement, period, organisationUnit, optionCombo );

            if ( dataValue != null )
            {
                dataValueMap.put( dataElement.getId(), dataValue );
            }
        }
        
        //Collections.sort( dataElements );
        
        List<Lookup> lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.DS_PAYMENT_TYPE ) );
        DataSet paymentDataSet = null;
        for ( Lookup lookup : lookups )
        {
            String[] lookupType = lookup.getValue().split( ":" );
            
            //System.out.println( lookup.getValue() +" ----- " + Integer.parseInt( lookupType[0] ) + " ---- " + Integer.parseInt( dataSetId ) );
            
            if ( Integer.parseInt( lookupType[1] ) == Integer.parseInt( dataSetId ) )
            {
                //System.out.println( "Inside if condition ----- " + Integer.parseInt( lookupType[1] ) + " ---- " + Integer.parseInt( dataSetId ) );
                paymentDataSet = dataSetService.getDataSet( Integer.parseInt(  lookupType[0] ) );
                break;
            }
        }
        
        int flag = 1;
        
        //System.out.println(" Payment dataSet Name ---" + paymentDataSet.getName()  );
        
        //System.out.println( period.getStartDate() + " -- " + period.getEndDate() );
        
        //System.out.println(" Payment dataSet Period Type---" + paymentDataSet.getPeriodType() );
        
        if( paymentDataSet != null )
        {
        
            period = periodService.reloadPeriod( period );
        
            Set<Period> periods = new HashSet<Period>( periodService.getIntersectingPeriodsByPeriodType( paymentDataSet.getPeriodType(), period.getStartDate(), period.getEndDate() ) );
        
            for( Period period1 : periods )
            {
                Double overAllAdjustedAmt = defaultPBFAggregationService.calculateOverallUnadjustedPBFAmount( period1, organisationUnit, paymentDataSet );
                
                if( overAllAdjustedAmt == null || overAllAdjustedAmt == 0 )
                {
                    paymentMessage += period1.getDisplayName() +", ";
                    flag = 2;
                }
                else
                {
                    totalUnadjustedAmt += overAllAdjustedAmt;
                }
            }
        
            if( flag == 1 )
            {
                paymentMessage = " ";
            }
            else
            {
            	paymentMessage = paymentMessage.substring(0, paymentMessage.length()-2);
            	paymentMessage += " ) ";
            }
        }
        else
        {
            paymentMessage = "Payment dataset is not linked for this quality score dataset.";
        }

        qualityScorePayments = new HashSet<QualityScorePayment>( qualityScorePaymentService.getAllQualityScorePayments() );
        
        return SUCCESS;
    }

    public OrganisationUnitGroup findPBFOrgUnitGroupforTariff( OrganisationUnit organisationUnit, Integer dataSetId, String orgUnitIds )
    {
        
        Set<Integer> orgUnitGroupIds = qualityMaxValueService.getOrgUnitGroupsByDataset( dataSetId, orgUnitIds );
        
        OrganisationUnitGroup orgUnitGroup = null;
        if( orgUnitGroupIds != null && orgUnitGroupIds.size() > 0 )
        {
             orgUnitGroup = orgUnitGroupService.getOrganisationUnitGroup( orgUnitGroupIds.iterator().next() );
        }
        else
        {        
            Constant tariff_authority = constantService.getConstantByName( TARIFF_SETTING_AUTHORITY );
                
            OrganisationUnitGroupSet orgUnitGroupSet = orgUnitGroupService.getOrganisationUnitGroupSet( (int) tariff_authority.getValue() );
                
            orgUnitGroup = organisationUnit.getGroupInGroupSet( orgUnitGroupSet );
        }
        
        return orgUnitGroup;            	
    }
    
    public OrganisationUnit findParentOrgunitforTariff( OrganisationUnit organisationUnit, Integer tariffOULevel )
    {
        Integer ouLevel = organisationUnitService.getLevelOfOrganisationUnit( organisationUnit.getId() );
        if ( tariffOULevel == ouLevel )
        {
            return organisationUnit;
        }
        else
        {
            return findParentOrgunitforTariff( organisationUnit.getParent(), tariffOULevel );
        }
    }
    
    public Collection<DataElement> geti18nDataElements( List<DataElement> dataElements )
    {
        return i18n( i18nService, dataElements );
    }
    
    
}