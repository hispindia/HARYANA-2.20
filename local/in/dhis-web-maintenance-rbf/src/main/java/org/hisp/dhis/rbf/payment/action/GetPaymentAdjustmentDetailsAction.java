package org.hisp.dhis.rbf.payment.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.rbf.api.Lookup;
import org.hisp.dhis.rbf.api.LookupService;
import org.hisp.dhis.system.filter.PastAndCurrentPeriodFilter;
import org.hisp.dhis.system.util.FilterUtils;

import com.opensymphony.xwork2.Action;

public class GetPaymentAdjustmentDetailsAction
    implements Action
{

    private final static String PAYMENT_ADJUSTMENT_LEVEL_ORG_GROUP = "PAYMENT_ADJUSTMENT_LEVEL_ORG_GROUP";

    private final static String PAYMENT_ADJUSTMENT_AMOUNT_DE = "PAYMENT_ADJUSTMENT_AMOUNT_DE";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
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

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------

    private List<DataSet> dataSets = new ArrayList<DataSet>();

    public List<DataSet> getDataSets()
    {
        return dataSets;
    }

    private List<String> orgUnitList = new ArrayList<String>();

    public List<String> getOrgUnitList()
    {
        return orgUnitList;
    }

    private String amountDEId;

    public String getAmountDEId()
    {
        return amountDEId;
    }

    private List<Period> periods = new ArrayList<Period>();

    public Collection<Period> getPeriods()
    {
        return periods;
    }

    private String amountAvailable = "";

    public String getAmountAvailable()
    {
        return amountAvailable;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        Constant orgUnitGrp = constantService.getConstantByName( PAYMENT_ADJUSTMENT_LEVEL_ORG_GROUP );

        Constant paymentAmount = constantService.getConstantByName( PAYMENT_ADJUSTMENT_AMOUNT_DE );
        
        if( paymentAmount != null )
        {
            amountDEId = paymentAmount.getValue() + "";
        }
        
        List<OrganisationUnit> organisationUnitList = new ArrayList<OrganisationUnit>();
        
        if( orgUnitGrp != null )
        {
            organisationUnitList = new ArrayList<OrganisationUnit>( organisationUnitGroupService.getOrganisationUnitGroup( (int) orgUnitGrp.getValue() ).getMembers() );
            
            for ( OrganisationUnit org : organisationUnitList )
            {
                orgUnitList.add( "\"" + org.getUid() + "\"" );
            }
        }
        
        List<Lookup> lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.DS_PBF_TYPE ) );
        
        dataSets = new ArrayList<DataSet>();
        for ( Lookup lookup : lookups )
        {
            Integer dataSetId = Integer.parseInt( lookup.getValue() );

            DataSet dataSet = dataSetService.getDataSet( dataSetId );

            dataSets.add( dataSet );
        }
        
        String periodType = "Quarterly";

        CalendarPeriodType _periodType = (CalendarPeriodType) CalendarPeriodType.getPeriodTypeByName( periodType );

        Calendar cal = PeriodType.createCalendarInstance();

        periods = _periodType.generatePeriods( cal.getTime() );
        // periods = new ArrayList<Period>(
        // periodService.getPeriodsByPeriodType( periodType ) );

        FilterUtils.filter( periods, new PastAndCurrentPeriodFilter() );

        Collections.reverse( periods );
        // Collections.sort( periods );
        for ( Period period : periods )
        {
            // System.out.println("ISO Date : " + period.getIsoDate() );

            period.setName( format.formatPeriod( period ) );
        }

        return SUCCESS;
    }
}