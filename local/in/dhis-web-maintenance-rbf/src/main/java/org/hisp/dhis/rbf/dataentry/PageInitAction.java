package org.hisp.dhis.rbf.dataentry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.ouwt.manager.OrganisationUnitSelectionManager;
import org.hisp.dhis.rbf.api.Lookup;
import org.hisp.dhis.rbf.api.LookupService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class PageInitAction implements Action
{
    private final static String UTILIZATION_RATE_DATAELEMENT_ID = "UTILIZATION_RATE_DATAELEMENT_ID";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private OrganisationUnitSelectionManager selectionManager;

    public void setSelectionManager( OrganisationUnitSelectionManager selectionManager )
    {
        this.selectionManager = selectionManager;
    }
    
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
    
    @Autowired
    private ConstantService constantService;
    
    // -------------------------------------------------------------------------
    // Input/output
    // -------------------------------------------------------------------------

    private OrganisationUnit organisationUnit;

    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }    
    
    private String orgUnitId;
    
    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }
    
    private List<DataSet> dataSets = new ArrayList<DataSet>();
    
    public List<DataSet> getDataSets()
    {
        return dataSets;
    }
    
    private String utilizationRateDataElementId;
    
    public String getUtilizationRateDataElementId()
    {
        return utilizationRateDataElementId;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------


    public String execute() throws Exception
    {
        //selectionManager.clearSelectedOrganisationUnits();
        
        organisationUnit = selectionManager.getSelectedOrganisationUnit();
        
        if( organisationUnit == null )
        {
            System.out.println("Organisationunit is null");
            
        }
        
        else
        {
            System.out.println("Organisationunit is not null ---" + organisationUnit.getId() );
            dataSets = new ArrayList<DataSet>( organisationUnit.getDataSets() );
        }
        
        if( organisationUnit == null && orgUnitId != null )
        {
            organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
            dataSets = new ArrayList<DataSet>( organisationUnit.getDataSets() );
           
        }
        
        //dataSets = new ArrayList<DataSet>( organisationUnit.getDataSets() );
        
        List<Lookup> lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.DS_PBF_TYPE ) );
        
        List<DataSet> pbfDataSets = new ArrayList<DataSet>();
        
        for( Lookup lookup : lookups )
        {
            Integer dataSetId = Integer.parseInt( lookup.getValue() );
            
            DataSet dataSet = dataSetService.getDataSet( dataSetId );
            if( dataSet != null )
            {
                pbfDataSets.add(dataSet);
           
            }
        }
        
        dataSets.retainAll( pbfDataSets );
        Collections.sort(dataSets);
        
        /*
        for( DataSet dataSet : dataSets )
        {
            System.out.println(" dataSet ---" + dataSet.getId() +" -- " + dataSet.getName() );
        }
        */
        
        Constant utilizationRateDeId = constantService.getConstantByName( UTILIZATION_RATE_DATAELEMENT_ID );
            
        utilizationRateDataElementId = utilizationRateDeId.getValue()+"";
        
        return SUCCESS;
    }
}


