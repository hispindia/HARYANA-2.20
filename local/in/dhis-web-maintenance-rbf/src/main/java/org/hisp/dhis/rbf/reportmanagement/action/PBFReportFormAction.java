package org.hisp.dhis.rbf.reportmanagement.action;

import org.hisp.dhis.reports.ReportType;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class PBFReportFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------


    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------
    
    private String reportTypeName;

    public String getReportTypeName()
    {
        return reportTypeName;
    }
 
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        
        reportTypeName = ReportType.RT_BIRT;
        
        return SUCCESS;
    }
}