package org.hisp.dhis.rbf.payment.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.rbf.api.Lookup;
import org.hisp.dhis.rbf.api.LookupService;
import org.hisp.dhis.system.filter.PastAndCurrentPeriodFilter;
import org.hisp.dhis.system.util.FilterUtils;

import com.opensymphony.xwork2.Action;

public class GetOrganisationUnitForPaymentAction
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------
    private String message;

    public String getMessage()
    {
        return message;
    }

    private String orgUnitId;

    public String getOrgUnitId()
    {
        return orgUnitId;
    }

    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private List<DataSet> dataSets = new ArrayList<DataSet>();

    public List<DataSet> getDataSets()
    {
        return dataSets;
    }

    private List<Period> periods = new ArrayList<Period>();

    public Collection<Period> getPeriods()
    {
        return periods;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );

        /*
         * List<OrganisationUnit> organisationUnitList = new
         * ArrayList<OrganisationUnit>(
         * organisationUnitService.getOrganisationUnitWithChildren
         * (organisationUnit.getId()) ) ; for (OrganisationUnit org :
         * organisationUnitList) { if(!dataSets.containsAll(org.getDataSets()))
         * { dataSets.addAll(org.getDataSets()); } }
         */
        List<Lookup> lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.DS_PBF_TYPE ) );
        
        dataSets = new ArrayList<DataSet>();
        for ( Lookup lookup : lookups )
        {
            Integer dataSetId = Integer.parseInt( lookup.getValue() );

            DataSet dataSet = dataSetService.getDataSet( dataSetId );

            dataSets.add( dataSet );
        }
        
        Collections.sort( dataSets );
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

        System.out.println( dataSets.size() );
        if ( dataSets.size() > 0 )
        {
            message = organisationUnit.getName();
            return SUCCESS;

        }
        else
        {
            message = organisationUnit.getName();

            return INPUT;
        }

    }

}
