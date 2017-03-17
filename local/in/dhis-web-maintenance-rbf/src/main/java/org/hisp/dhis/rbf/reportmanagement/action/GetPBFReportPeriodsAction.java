package org.hisp.dhis.rbf.reportmanagement.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.filter.PastAndCurrentPeriodFilter;
import org.hisp.dhis.system.util.FilterUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetPBFReportPeriodsAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------
    
    private String reportPeriodTypeName;
    
    public void setReportPeriodTypeName( String reportPeriodTypeName )
    {
        this.reportPeriodTypeName = reportPeriodTypeName;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------


    private List<Period> periods = new ArrayList<Period>();

    public Collection<Period> getPeriods()
    {
        return periods;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    { 
        
        CalendarPeriodType _periodType = (CalendarPeriodType) CalendarPeriodType.getPeriodTypeByName( reportPeriodTypeName );
        
        Calendar cal = PeriodType.createCalendarInstance();
        
        periods = _periodType.generatePeriods( cal.getTime() );
        
        //periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( periodType ) );
        
        FilterUtils.filter( periods, new PastAndCurrentPeriodFilter() );

        Collections.reverse( periods );
        //Collections.sort( periods );
        for ( Period period : periods )
        {
            //System.out.println("ISO Date : " + period.getIsoDate() );
            
            period.setName( format.formatPeriod( period ) );
        }
        
        return SUCCESS;
    }
}

