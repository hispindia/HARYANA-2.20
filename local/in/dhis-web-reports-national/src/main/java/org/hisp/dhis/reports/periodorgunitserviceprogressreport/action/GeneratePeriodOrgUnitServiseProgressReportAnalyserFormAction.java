package org.hisp.dhis.reports.periodorgunitserviceprogressreport.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportType;

import com.opensymphony.xwork2.Action;

public class GeneratePeriodOrgUnitServiseProgressReportAnalyserFormAction implements Action
{
    
    public static final String NAME_TYPE = "Type";
    public static final String NAME_OWNERSHIP = "Ownership";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private Collection<PeriodType> periodTypes;

    public Collection<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }
    
    private String reportTypeName;

    public String getReportTypeName()
    {
        return reportTypeName;
    }
    
    private List<OrganisationUnitGroup> orgUnitGroupMembers;
    
    public List<OrganisationUnitGroup> getOrgUnitGroupMembers()
    {
        return orgUnitGroupMembers;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        reportTypeName = ReportType.RT_PERIOD_ORGUNIT_SERVICE_PROGRESS_REPORT;

        periodTypes = periodService.getAllPeriodTypes();

        Iterator<PeriodType> periodTypeIterator = periodTypes.iterator();
        while ( periodTypeIterator.hasNext() )
        {
            PeriodType type = periodTypeIterator.next();
            if (type.getName().equalsIgnoreCase("Daily") || type.getName().equalsIgnoreCase("Monthly") || type.getName().equalsIgnoreCase("quarterly") || type.getName().equalsIgnoreCase("yearly"))
            {
            }
            else
            {
                periodTypeIterator.remove();
            }
        }
        
	List<OrganisationUnitGroupSet> organisationUnitGroupSet1List = new ArrayList<OrganisationUnitGroupSet>( organisationUnitGroupService.getOrganisationUnitGroupSetByName( NAME_TYPE ) );
        OrganisationUnitGroupSet organisationUnitGroupSet1 = organisationUnitGroupSet1List.get( 0 );

        //OrganisationUnitGroupSet organisationUnitGroupSet1 = organisationUnitGroupService.getOrganisationUnitGroupSetByName( OrganisationUnitGroupSetPopulator.NAME_TYPE );
        
        orgUnitGroupMembers = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupSet1.getOrganisationUnitGroups() );
        
        List<OrganisationUnitGroupSet> organisationUnitGroupSet2List = new ArrayList<OrganisationUnitGroupSet>( organisationUnitGroupService.getOrganisationUnitGroupSetByName( NAME_OWNERSHIP ) );
		OrganisationUnitGroupSet organisationUnitGroupSet2 = organisationUnitGroupSet2List.get( 0 );

        //OrganisationUnitGroupSet organisationUnitGroupSet2 = organisationUnitGroupService.getOrganisationUnitGroupSetByName( OrganisationUnitGroupSetPopulator.NAME_OWNERSHIP );
        
        orgUnitGroupMembers.addAll( new ArrayList<OrganisationUnitGroup>( organisationUnitGroupSet2.getOrganisationUnitGroups() ) );
        
        
        return SUCCESS;
    }

}


