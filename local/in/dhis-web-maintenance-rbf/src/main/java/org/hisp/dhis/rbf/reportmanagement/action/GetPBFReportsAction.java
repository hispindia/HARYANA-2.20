package org.hisp.dhis.rbf.reportmanagement.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.comparator.Report_inNameComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetPBFReportsAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    
    private String orgUnitId;
    
    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private String reportType;
    
    public void setReportType( String reportType )
    {
        this.reportType = reportType;
    }

    private List<Report_in> reportList;

    public List<Report_in> getReportList()
    {
        return reportList;
    }

    private String ouName;

    public String getOuName()
    {
        return ouName;
    }
    
    private String orgUnitUid;
    
    public String getOrgUnitUid()
    {
        return orgUnitUid;
    }

    private Integer ouLavel;
    
    public Integer getOuLavel()
    {
        return ouLavel;
    }
    private int selectedOrgUnitLevel;
    
    public int getSelectedOrgUnitLevel()
    {
        return selectedOrgUnitLevel;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {   
        //System.out.println( " OutSide orgUnitId  : " + orgUnitId + "---- periodType  : " + periodType + "------reportType----- " + reportType );
        
        if ( orgUnitId != null )
        {
            try
            {
                OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
                
                ouName = orgUnit.getShortName();
                ouLavel = orgUnit.getLevel();
                orgUnitUid = orgUnit.getUid();
                
                selectedOrgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( orgUnit.getId() );
                
                reportList = new ArrayList<Report_in>( reportService.getReportBySourceAndReportType( orgUnit, reportType ) );
                
                Collections.sort( reportList, new Report_inNameComparator() );
            }
            catch ( Exception e )
            {
                System.out.println( "Exception while getting Reports List : " + e.getMessage() );
            }
        }

        /*
        for( Report_in report : reportList )
        {
            System.out.println(  report.getName() );
            System.out.println(  report.getXmlTemplateName() );
            System.out.println(  report.getPeriodType().getName() );
            System.out.println(  report.getOrgunitGroup().getId() );
        }
        */
        
        return SUCCESS;
    }
}
