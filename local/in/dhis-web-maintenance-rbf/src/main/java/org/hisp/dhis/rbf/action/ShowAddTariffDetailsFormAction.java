package org.hisp.dhis.rbf.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.rbf.api.Lookup;
import org.hisp.dhis.rbf.api.LookupService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ShowAddTariffDetailsFormAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
   
    @Autowired
    private LookupService lookupService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private OrganisationUnitGroupService orgUnitGroupService;

    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------
    
    private Integer orgUnitGroupId;

    public void setOrgUnitGroupId( Integer orgUnitGroupId )
    {
        this.orgUnitGroupId = orgUnitGroupId;
    }

    private List<DataSet> dataSets = new ArrayList<DataSet>();

    public List<DataSet> getDataSets()
    {
        return dataSets;
    }


    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {

        OrganisationUnitGroup orgUnitGroup = orgUnitGroupService.getOrganisationUnitGroup( orgUnitGroupId );

        //System.out.println( tariffList.size() + " : " + orgUnitGroup.getId() + " : " + organisationUnit.getId() + " : " + selecteddataElement.getId() );
        
        List<Lookup> lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.DS_PBF_TYPE ) );

        for ( Lookup lookup : lookups )
        {
            Integer dataSetId = Integer.parseInt( lookup.getValue() );

            DataSet dataSet = dataSetService.getDataSet( dataSetId );

            dataSets.add( dataSet );
        }
        
        /*
        Set<OrganisationUnit> groupMember = new TreeSet<OrganisationUnit>( orgUnitGroup.getMembers() );
        
        Set<DataSet> tempDataSets = new TreeSet<DataSet>();
        
        for( OrganisationUnit orgUnit : groupMember )
        {
            tempDataSets.addAll( orgUnit.getDataSets() );
        }
        
        dataSets.retainAll( tempDataSets );
        */
        
        
        dataSets.retainAll( dataSetService.getDataSetsBySources( orgUnitGroup.getMembers() ) );
        
        //dataSets.retainAll( orgUnitGroup.getDataSets() );
        
        /*
        System.out.println( "Lookup DataSet Size : " + dataSets.size() );
        
        for( DataSet dataSet : dataSets )
        {
            System.out.println(" Lookup dataSet ---" + dataSet.getId() +" -- " + dataSet.getName() );
        }
        
        System.out.println( " OrgUnit DataSet Size : " + orgUnitGroup.getDataSets().size() );
        
        for( DataSet dataSet : orgUnitGroup.getDataSets() )
        {
            System.out.println(" Group dataSet ---" + dataSet.getId() +" -- " + dataSet.getName() );
        }
        
        System.out.println( "Final DataSet Size : " + dataSets.size() );
        
        for( DataSet dataSet : dataSets )
        {
            System.out.println(" Final dataSet ---" + dataSet.getId() +" -- " + dataSet.getName() );
        }      
        */
        
        return SUCCESS;
    }
}
