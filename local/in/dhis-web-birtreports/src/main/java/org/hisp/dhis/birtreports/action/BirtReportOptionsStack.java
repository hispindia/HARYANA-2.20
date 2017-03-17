package org.hisp.dhis.birtreports.action;

/**
 * User: gaurav
 * Date: 12/7/13
 * Time: 3:06 PM
 */
public class BirtReportOptionsStack {


    private String reportClass;

    private String reportName;

    private String reportDesignFileName;

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportDesignFileName() {
        return reportDesignFileName;
    }

    public void setReportDesignFileName(String reportDesignFileName) {
        this.reportDesignFileName = reportDesignFileName;
    }

    public String getReportClass() {
        return reportClass;
    }

    public void setReportClass(String reportClass) {
        this.reportClass = reportClass;
    }
}
