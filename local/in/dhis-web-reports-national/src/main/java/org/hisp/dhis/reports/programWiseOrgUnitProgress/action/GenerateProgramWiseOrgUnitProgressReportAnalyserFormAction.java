package org.hisp.dhis.reports.programWiseOrgUnitProgress.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;
import org.hisp.dhis.reports.ReportType;

import com.opensymphony.xwork2.Action;

public class GenerateProgramWiseOrgUnitProgressReportAnalyserFormAction
implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    // -------------------------------------------------------------------------
    // Getter & Setter
    // -------------------------------------------------------------------------

    private String periodTypeName;
    
    public String getPeriodTypeName()
    {
        return periodTypeName;
    }

    private String reportTypeName;

    public String getReportTypeName()
    {
        return reportTypeName;
    }  

    private List<OrganisationUnitGroup> orgUnitGroups;
    
    public List<OrganisationUnitGroup> getOrgUnitGroups()
    {
        return orgUnitGroups;
    }
    
    private List<Period> periods;

    public List<Period> getPeriods()
    {
        return periods;
    }
    
    private SimpleDateFormat simpleDateFormat;
    
    private List<String> periodNameList;

    public List<String> getPeriodNameList()
    {
        return periodNameList;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        reportTypeName = ReportType.RT_PROGRAMWISE_ORGUNITPROGRESS_REPORT;
        
        periodTypeName = MonthlyPeriodType.NAME;
        
        PeriodType periodType = periodService.getPeriodTypeByName( periodTypeName );

        periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( periodType ) );
        
        Collections.sort( periods, new PeriodComparator() );
        
        periodNameList = new ArrayList<String>();
        
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        for ( Period p1 : periods )
        {
            periodNameList.add( simpleDateFormat.format( p1.getStartDate() ) );
        }
        
        return SUCCESS;
    }
}

