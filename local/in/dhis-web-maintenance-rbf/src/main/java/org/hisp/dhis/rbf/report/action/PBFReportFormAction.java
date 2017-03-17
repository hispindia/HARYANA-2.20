package org.hisp.dhis.rbf.report.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.CalendarPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.filter.PastAndCurrentPeriodFilter;
import org.hisp.dhis.system.util.FilterUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class PBFReportFormAction implements Action
{
    private final static String REPORTING_GROUP_SET = "REPORTING_GROUP_SET";
    
    private final static String TARIFF_SETTING_AUTHORITY_GROUP_SET_ID = "TARIFF_SETTING_AUTHORITY_GROUP_SET_ID";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitService organisationUnitService;
    
    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<Period> periods = new ArrayList<Period>();

    public Collection<Period> getPeriods()
    {
        return periods;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    private String periodTypeName;

    public String getPeriodTypeName()
    {
        return periodTypeName;
    }

    public void setPeriodTypeName( String periodTypeName )
    {
        this.periodTypeName = periodTypeName;
    }

    private String birtPath;
    
    public String getBirtPath()
    {
        return birtPath;
    }
    
    private List<OrganisationUnit> orgUnitList;
    
    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }
    
    private Map<String, String> orgUnitGroupNameMap = new HashMap<String, String>();
    
    public Map<String, String> getOrgUnitGroupNameMap()
    {
        return orgUnitGroupNameMap;
    }
    
    private Map<String, Integer> orgUnitGroupIdMap = new HashMap<String, Integer>();
    
    public Map<String, Integer> getOrgUnitGroupIdMap()
    {
        return orgUnitGroupIdMap;
    }
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------


    public String execute() throws Exception
    {
        birtPath = System.getenv( "DHIS2_HOME" );
        
        birtPath += File.separator + "birtreports" + File.separator + "PBFInvoice.rptdesign";
        
        //periodTypeName = QuarterlyPeriodType.NAME;
        
        periodTypeName = MonthlyPeriodType.NAME;
        
        CalendarPeriodType _periodType = (CalendarPeriodType) CalendarPeriodType.getPeriodTypeByName( periodTypeName );

        Calendar cal = PeriodType.createCalendarInstance();

        periods = _periodType.generatePeriods( cal.getTime() );

        // periods = new ArrayList<Period>(
        // periodService.getPeriodsByPeriodType( periodType ) );

        FilterUtils.filter( periods, new PastAndCurrentPeriodFilter() );

        Collections.reverse( periods );
        // Collections.sort( periods );
        for ( Period period : periods )
        {
            period.setName( format.formatPeriod( period ) );
        }
        
        orgUnitList = new ArrayList<OrganisationUnit>();
        orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnits() );
        
        Constant orgUnitGroupSetId = constantService.getConstantByName( REPORTING_GROUP_SET );
        
        OrganisationUnitGroupSet organisationUnitGroupSet = organisationUnitGroupService.getOrganisationUnitGroupSet( (int) orgUnitGroupSetId.getValue()  );
        
        
        Constant orgUnitPBFGroupSetId = constantService.getConstantByName( TARIFF_SETTING_AUTHORITY_GROUP_SET_ID );
        
        OrganisationUnitGroupSet pbfOrganisationUnitGroupSet = organisationUnitGroupService.getOrganisationUnitGroupSet( (int) orgUnitPBFGroupSetId.getValue()  );
        
        //System.out.println( "  organisationUnit List size   " + orgUnitList.size() );orgUnitGroupSetId
        
        /*
        List<OrganisationUnitGroupSet> organisationUnitGroupSetList = new ArrayList<OrganisationUnitGroupSet>();
        
        organisationUnitGroupSetList = new ArrayList<OrganisationUnitGroupSet>();
        
        organisationUnitGroupSetList = new ArrayList<OrganisationUnitGroupSet>( organisationUnitGroupService.getAllOrganisationUnitGroupSets() );
        
        //System.out.println( "  organisationUnit Group Set List size Before Remove  " + organisationUnitGroupSetList.size() );
        
        // remove the orgUnitGroupSet which has no any orgUnitGroup
        Iterator<OrganisationUnitGroupSet> allorganisationUnitGroupSetIterator = organisationUnitGroupSetList.iterator();
        while ( allorganisationUnitGroupSetIterator.hasNext() )
        {
            OrganisationUnitGroupSet organisationUnitGroupSet = allorganisationUnitGroupSetIterator.next();
            
            if ( organisationUnitGroupSet.getOrganisationUnitGroups().size() == 0  )
            {
                //System.out.println("  organisationUnitGroupSet Name   " + organisationUnitGroupSet.getName() );
                allorganisationUnitGroupSetIterator.remove();
            }
        }
        */
        
        //System.out.println( "  organisationUnit Group Set List size After Remove  " + organisationUnitGroupSetList.size() );
        
        orgUnitGroupNameMap = new HashMap<String, String>();
        for( OrganisationUnit orgUnit : orgUnitList )
        {
            for ( OrganisationUnitGroup organisationUnitGroup : organisationUnitGroupSet.getOrganisationUnitGroups() )
            {
                if( orgUnit.getGroups() != null && orgUnit.getGroups().size() > 0 )
                {
                    if( orgUnit.getGroups().contains( organisationUnitGroup ) )
                    {
                        orgUnitGroupNameMap.put( orgUnit.getUid(), organisationUnitGroup.getName() );
                        break;
                    }
                }
            }
        }
        
        
        orgUnitGroupIdMap = new HashMap<String, Integer>();
        
        for( OrganisationUnit orgUnit : orgUnitList )
        {
            for ( OrganisationUnitGroup organisationUnitGroup : pbfOrganisationUnitGroupSet.getOrganisationUnitGroups() )
            {
                if( orgUnit.getGroups() != null && orgUnit.getGroups().size() > 0 )
                {
                    if( orgUnit.getGroups().contains( organisationUnitGroup ) )
                    {
                        orgUnitGroupIdMap.put( orgUnit.getUid(), organisationUnitGroup.getId() );
                        break;
                    }
                }
            }
        }
        
        
        //System.out.println( "  organisationUnit Group Map size   " + orgUnitGroupNameMap.size() );
        
        /*
        for( String key : orgUnitGroupNameMap.keySet() )
        {
            System.out.println( " OrganisationUnit Uid   " + key  + " OrganisationUnit Group Name   " + orgUnitGroupNameMap.get( key ) );
        }
        */
        
        
        
        
        return SUCCESS;
    }

}
