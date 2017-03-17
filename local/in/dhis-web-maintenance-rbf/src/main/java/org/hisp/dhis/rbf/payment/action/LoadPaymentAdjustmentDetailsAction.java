package org.hisp.dhis.rbf.payment.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.rbf.api.Lookup;
import org.hisp.dhis.rbf.api.LookupService;
import org.hisp.dhis.rbf.api.QualityMaxValue;
import org.hisp.dhis.rbf.api.QualityMaxValueService;
import org.hisp.dhis.rbf.api.TariffDataValue;
import org.hisp.dhis.rbf.api.TariffDataValueService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class LoadPaymentAdjustmentDetailsAction
    implements Action
{
    
    private final static String PAYMENT_ADJUSTMENT_AMOUNT_DE = "PAYMENT_ADJUSTMENT_AMOUNT_DE";

    private final static String TARIFF_SETTING_AUTHORITY = "TARIFF_SETTING_AUTHORITY";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private TariffDataValueService tariffDataValueService;

    public void setTariffDataValueService( TariffDataValueService tariffDataValueService )
    {
        this.tariffDataValueService = tariffDataValueService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    @Autowired
    private PeriodService periodService;

    @Autowired
    private LookupService lookupService;

    @Autowired
    private QualityMaxValueService qualityMaxValueService;

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

    private String periodIso;

    public void setPeriodIso( String periodIso )
    {
        this.periodIso = periodIso;
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

    private Map<String, String> quantityValidatedMap = new HashMap<String, String>();

    public Map<String, String> getQuantityValidatedMap()
    {
        return quantityValidatedMap;
    }

    private Map<String, String> tariffDataValueMap = new HashMap<String, String>();

    public Map<String, String> getTariffDataValueMap()
    {
        return tariffDataValueMap;
    }

    private Map<String, String> amountMap = new HashMap<String, String>();

    public Map<String, String> getAmountMap()
    {
        return amountMap;
    }

    private String amountAvailable = "";

    public String getAmountAvailable()
    {
        return amountAvailable;
    }

    private String unAdjustedAmount;

    public String getUnAdjustedAmount()
    {
        return unAdjustedAmount;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

        DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( dataSetId ) );

        Period period = PeriodType.getPeriodFromIsoString( periodIso );

        dataElements.addAll( dataSet.getDataElements() );

        Constant paymentAmount = constantService.getConstantByName( PAYMENT_ADJUSTMENT_AMOUNT_DE );

        String amountDEId = paymentAmount.getValue() + "";

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

        DataElementCategoryOptionCombo optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();

        List<Lookup> lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.DS_PAYMENT_TYPE ) );

        Double allMax = 0.0;
        Double allScore = 0.0;
        double quantityValue = 0;
        for ( Lookup lookup : lookups )
        {
            String[] lookupType = lookup.getValue().split( ":" );
            // System.out.println("lookupType[0] "+lookupType[0]+" dataSetId "+dataSetId);
            if ( Integer.parseInt( lookupType[0] ) == Integer.parseInt( dataSetId ) )
            {
                DataSet lookupdataSet = dataSetService.getDataSet( Integer.parseInt( lookupType[1] ) );
                for ( DataElement de : lookupdataSet.getDataElements() )
                {
                    List<QualityMaxValue> qualityMaxValues = new ArrayList<QualityMaxValue>();
                    OrganisationUnit parentOrgunit = findParentOrgunitforTariff( organisationUnit, tariff_setting_authority );
                    if ( parentOrgunit != null )
                    {
                        qualityMaxValues = new ArrayList<QualityMaxValue>( qualityMaxValueService.getQuanlityMaxValues(
                            parentOrgunit, de ) );
                    }
                    
                    List<Period> periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( period.getStartDate(), period.getEndDate() ) );
                    // System.out.println("Period Size: "+ periodList.size() );
                    for ( Period prd : periodList )
                    {

                        List<OrganisationUnit> orgList = new ArrayList<OrganisationUnit>(
                            organisationUnitService.getOrganisationUnitWithChildren( organisationUnit.getId() ) );
                        // System.out.println("orgList Size: "+ orgList.size()
                        // );
                        for ( OrganisationUnit ou : orgList )
                        {
                            DataValue dataValue = dataValueService.getDataValue( de, prd, ou, optionCombo );
                            for ( QualityMaxValue qualityMaxValue : qualityMaxValues )
                            {
                                // System.out.println("qualityMaxValue.getValue() "+qualityMaxValue.getValue());
                                if ( qualityMaxValue.getStartDate().getTime() <= period.getStartDate().getTime()
                                    && period.getEndDate().getTime() <= qualityMaxValue.getEndDate().getTime() )
                                {
                                    if ( dataValue != null )
                                    {
                                        allMax = allMax + qualityMaxValue.getValue();
                                        // System.out.println("dataValue.getValue() "+dataValue.getValue());
                                        allScore = allScore + Double.parseDouble( dataValue.getValue() );
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        quantityValue = Math.round( (allScore / allMax) * 100 );
        System.out.println( "quantityValue: " + quantityValue );
        // quantityValue = Math.round( quantityValue );
        double unadjusted = 0.0;
        for ( DataElement de : dataElements )
        {
            double tariffValue = 0;
            List<OrganisationUnit> orgList = new ArrayList<OrganisationUnit>();
            OrganisationUnit parentOrgunit2 = findParentOrgunitforTariff( organisationUnit, tariff_setting_authority );
            if ( parentOrgunit2 != null )
            {
                orgList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( parentOrgunit2.getId() ) );
            }
            else
            {
                orgList = new ArrayList<OrganisationUnit>(
                    organisationUnitService.getOrganisationUnitWithChildren( organisationUnit.getId() ) );
            }
            for ( OrganisationUnit ou : orgList )
            {
                List<Period> periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates(
                    period.getStartDate(), period.getEndDate() ) );
                for ( Period prd : periodList )
                {
                    TariffDataValue tariffDataValue = tariffDataValueService.getTariffDataValue( ou, de, dataSet,
                        prd.getStartDate(), prd.getEndDate() );

                    if ( tariffDataValue != null )
                    {
                        System.out.println( tariffDataValue.getValue() );
                        tariffValue = tariffValue + tariffDataValue.getValue();
                    }
                }
            }
            quantityValidatedMap.put( de.getUid(), quantityValue + "" );
            tariffDataValueMap.put( de.getUid(), tariffValue + "" );
            double amount = (quantityValue * tariffValue);
            amountMap.put( de.getUid(), amount + "" );
            unadjusted = unadjusted + amount;
        }
        unAdjustedAmount = unadjusted + "";
        
        Collections.sort( dataElements );
        
        DataElement dataElement = dataElementService.getDataElement( (int) paymentAmount.getValue() );
        
        DataValue dataValue = dataValueService.getDataValue( dataElement, period, organisationUnit, optionCombo );

        if ( dataValue != null )
        {
            amountAvailable = dataValue.getValue();
        }
        return SUCCESS;
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
}