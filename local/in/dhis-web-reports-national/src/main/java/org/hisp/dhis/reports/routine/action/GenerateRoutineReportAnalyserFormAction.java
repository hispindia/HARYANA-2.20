package org.hisp.dhis.reports.routine.action;

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

public class GenerateRoutineReportAnalyserFormAction
    implements Action
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
    
    private List<OrganisationUnitGroup> orgUnitGroupMembers;
    
    public List<OrganisationUnitGroup> getOrgUnitGroupMembers()
    {
        return orgUnitGroupMembers;
    }
    
    private String reportTypeName;

    public String getReportTypeName()
    {
        return reportTypeName;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        reportTypeName = ReportType.RT_ROUTINE;

        periodTypes = periodService.getAllPeriodTypes();

        Iterator<PeriodType> periodTypeIterator = periodTypes.iterator();
        while ( periodTypeIterator.hasNext() )
        {
            PeriodType type = periodTypeIterator.next();
            
            if( type.getName().equalsIgnoreCase("Monthly") || type.getName().equalsIgnoreCase("quarterly") || type.getName().equalsIgnoreCase("yearly") || type.getName().equalsIgnoreCase("weekly") || type.getName().equalsIgnoreCase("Daily") || type.getName().equalsIgnoreCase( "Forteen" ) )
            {
            }
            else
            {
                periodTypeIterator.remove();
            }
        }
        
	//List<OrganisationUnitGroupSet> organisationUnitGroupSet1List = new ArrayList<OrganisationUnitGroupSet>( organisationUnitGroupService.getOrganisationUnitGroupSetByName( OrganisationUnitGroupSetPopulator.NAME_TYPE ) );
	List<OrganisationUnitGroupSet> organisationUnitGroupSet1List = new ArrayList<OrganisationUnitGroupSet>( organisationUnitGroupService.getOrganisationUnitGroupSetByName( NAME_TYPE ) );
	OrganisationUnitGroupSet organisationUnitGroupSet1 = organisationUnitGroupSet1List.get( 0 );
        
        orgUnitGroupMembers = new ArrayList<OrganisationUnitGroup>( organisationUnitGroupSet1.getOrganisationUnitGroups() );
        
        //List<OrganisationUnitGroupSet> organisationUnitGroupSet2List = new ArrayList<OrganisationUnitGroupSet>( organisationUnitGroupService.getOrganisationUnitGroupSetByName( OrganisationUnitGroupSetPopulator.NAME_OWNERSHIP ) );
        List<OrganisationUnitGroupSet> organisationUnitGroupSet2List = new ArrayList<OrganisationUnitGroupSet>( organisationUnitGroupService.getOrganisationUnitGroupSetByName( NAME_OWNERSHIP ) );
        OrganisationUnitGroupSet organisationUnitGroupSet2 = organisationUnitGroupSet2List.get( 0 );
        
        orgUnitGroupMembers.addAll( new ArrayList<OrganisationUnitGroup>( organisationUnitGroupSet2.getOrganisationUnitGroups() ) );
        
        return SUCCESS;
    }

}
