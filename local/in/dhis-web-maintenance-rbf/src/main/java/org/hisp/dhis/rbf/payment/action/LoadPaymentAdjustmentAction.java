package org.hisp.dhis.rbf.payment.action;

import static org.hisp.dhis.system.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.system.util.TextUtils.getCommaDelimitedString;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.velocity.tools.generic.MathTool;
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
import org.hisp.dhis.rbf.api.PBFDataValueService;
import org.hisp.dhis.rbf.api.QualityMaxValueService;
import org.hisp.dhis.rbf.api.TariffDataValueService;
import org.hisp.dhis.rbf.impl.DefaultPBFAggregationService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class LoadPaymentAdjustmentAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private DataElementCategoryService categoryService;

    @Autowired
    private DataValueService dataValueService;

    @Autowired
    private PBFDataValueService pbfDataValueService;

    @Autowired
    private TariffDataValueService tariffDataValueService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private ConstantService constantService;

    @Autowired
    private PeriodService periodService;

    @Autowired
    private LookupService lookupService;

    @Autowired
    private QualityMaxValueService qualityMaxValueService;

    @Autowired
    private DefaultPBFAggregationService defaultPBFAggregationService;

    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------
    private MathTool mathTool = new MathTool();

    public MathTool getMathTool()
    {
        return mathTool;
    }

    private String availableAmount = "";

    public String getAvailableAmount()
    {
        return availableAmount;
    }

    private Map<Integer, Double> pbfQtyMap = new HashMap<Integer, Double>();

    public Map<Integer, Double> getPbfQtyMap()
    {
        return pbfQtyMap;
    }

    private Map<Integer, Double> pbfTariffMap = new HashMap<Integer, Double>();

    public Map<Integer, Double> getPbfTariffMap()
    {
        return pbfTariffMap;
    }

    private Double overAllQualityScore = 0.0;

    public Double getOverAllQualityScore()
    {
        return overAllQualityScore;
    }

    Set<DataElement> dataElements = new HashSet<DataElement>();

    public Set<DataElement> getDataElements()
    {
        return dataElements;
    }

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

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        System.out.println( "Inside Adjustment screen" );

        if ( periodIso.equals( "-1" ) )
        {
            return SUCCESS;
        }

        OrganisationUnit selOrgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

        DataSet selDataSet = dataSetService.getDataSet( Integer.parseInt( dataSetId ) );

        Period period = PeriodType.getPeriodFromIsoString( periodIso );

        period = periodService.reloadPeriod( period );

        Set<Period> periods = new HashSet<Period>( periodService.getIntersectingPeriods( period.getStartDate(),
            period.getEndDate() ) );
        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers( Period.class, periods ) );
        String periodIdsByComma = getCommaDelimitedString( periodIds );

        dataElements.addAll( selDataSet.getDataElements() );

        Set<OrganisationUnit> pbfQtyOrgUnits = new HashSet<OrganisationUnit>();
        pbfQtyOrgUnits.addAll( organisationUnitService.getOrganisationUnitWithChildren( selOrgUnit.getId() ) );
        pbfQtyOrgUnits.retainAll( selDataSet.getSources() );
        Collection<Integer> orgUnitIds = new ArrayList<Integer>(
            getIdentifiers( OrganisationUnit.class, pbfQtyOrgUnits ) );
        String orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );

        // --------------------------------------------------------
        // Quantity Calculation
        // --------------------------------------------------------

        pbfQtyMap.putAll( pbfDataValueService.getPBFDataValues( orgUnitIdsByComma, selDataSet, periodIdsByComma ) );

        // --------------------------------------------------------
        // Quantity Tariff Calculation
        // --------------------------------------------------------
        Constant tariff_authority = constantService.getConstantByName( Lookup.TARIFF_SETTING_AUTHORITY );
        int tariff_setting_authority = 1;
        if ( tariff_authority != null )
        {
            tariff_setting_authority = (int) tariff_authority.getValue();
        }

        OrganisationUnit tariffOrgUnit = findParentOrgunitforTariff( selOrgUnit, tariff_setting_authority );

        if ( tariffOrgUnit != null )
        {
            pbfTariffMap.putAll( tariffDataValueService.getTariffDataValues( tariffOrgUnit, selDataSet, period ) );
        }

        // -----------------------------------------------------------
        // QualityScore
        // -----------------------------------------------------------

        List<Lookup> lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.DS_PAYMENT_TYPE ) );
        DataSet qualityScoreDataSet = null;
        for ( Lookup lookup : lookups )
        {
            String[] lookupType = lookup.getValue().split( ":" );
            System.out.println( lookup.getValue() + "  " + Integer.parseInt( lookupType[0] ) + "  "
                + Integer.parseInt( dataSetId ) );
            if ( Integer.parseInt( lookupType[0] ) == Integer.parseInt( dataSetId ) )
            {
                qualityScoreDataSet = dataSetService.getDataSet( Integer.parseInt( lookupType[1] ) );
                break;
            }
        }

        if ( qualityScoreDataSet != null )
        {
            overAllQualityScore = defaultPBFAggregationService.calculateOverallQualityScore( period,
                qualityScoreDataSet.getSources(), qualityScoreDataSet.getId(), tariffOrgUnit.getId() );
        }

        // -------------------------------------------------------------
        // Availbale Amount
        // -------------------------------------------------------------
        Constant paymentAmount = constantService.getConstantByName( Lookup.PAYMENT_ADJUSTMENT_AMOUNT_DE );
        DataElement dataElement = dataElementService.getDataElement( (int) paymentAmount.getValue() );
        DataElementCategoryOptionCombo optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();
        DataValue dataValue = dataValueService.getDataValue( dataElement, period, selOrgUnit, optionCombo );
        if ( dataValue != null )
        {
            availableAmount = dataValue.getValue();
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
