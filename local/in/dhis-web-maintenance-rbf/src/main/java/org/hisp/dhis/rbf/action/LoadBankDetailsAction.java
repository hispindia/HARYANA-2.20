package org.hisp.dhis.rbf.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupSet;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.rbf.api.BankDetails;
import org.hisp.dhis.rbf.api.BankDetailsService;
import org.hisp.dhis.rbf.api.Lookup;
import org.hisp.dhis.rbf.api.LookupService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class LoadBankDetailsAction
    implements Action
{
    private final static String BANK_DETAILS_GROUPSET_ID = "BANK_DETAILS_GROUPSET_ID"; // 1618
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private BankDetailsService bankDetailsService;

    public void setBankDetailsService( BankDetailsService bankDetailsService )
    {
        this.bankDetailsService = bankDetailsService;
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
    
    /*
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    */
    
    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    @Autowired
    private OrganisationUnitGroupService orgUnitGroupService;
    
    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------

    private String orgUnitUid;

    public void setOrgUnitUid( String orgUnitUid )
    {
        this.orgUnitUid = orgUnitUid;
    }
    
    /*
    private List<DataSet> dataSets = new ArrayList<DataSet>();

    public List<DataSet> getDataSets()
    {
        return dataSets;
    }
    */
    
    private List<OrganisationUnitGroup> organisationUnitGroups = new ArrayList<OrganisationUnitGroup>();
    
    public List<OrganisationUnitGroup> getOrganisationUnitGroups()
    {
        return organisationUnitGroups;
    }

    private List<String> banks = new ArrayList<String>();

    public List<String> getBanks()
    {
        return banks;
    }

    private List<BankDetails> bankDetailsList = new ArrayList<BankDetails>();

    public List<BankDetails> getBankDetailsList()
    {
        return bankDetailsList;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitUid );

        bankDetailsList.addAll( bankDetailsService.getBankDetails( organisationUnit ) );

        //dataSets.clear();
        organisationUnitGroups.clear();
        
        Constant bankDetailsGroupSetId = constantService.getConstantByName( BANK_DETAILS_GROUPSET_ID );
        
        OrganisationUnitGroupSet orgUnitGroupSet = orgUnitGroupService.getOrganisationUnitGroupSet( (int) bankDetailsGroupSetId.getValue() );
        
        
        if( orgUnitGroupSet != null )
        {
            organisationUnitGroups = new ArrayList<OrganisationUnitGroup>( orgUnitGroupSet.getOrganisationUnitGroups() );            
            
        }    
        
        //List<DataSet> bankDetailDataSets = new ArrayList<DataSet>();
        
        List<OrganisationUnitGroup> bankDetailOrganisationUnitGroups = new ArrayList<OrganisationUnitGroup>();
        
        /*
        List<Lookup> lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.DS_PBF_TYPE ) );
        for ( Lookup lookup : lookups )
        {
            Integer dataSetId = Integer.parseInt( lookup.getValue() );

            DataSet dataSet = dataSetService.getDataSet( dataSetId );
            
            if( dataSet != null )
            {
                dataSets.add( dataSet );
            }
            
        }
        */
      
        for ( BankDetails bd : bankDetailsList )
        {
            //bankDetailDataSets.add( bd.getDataSet() );
            bankDetailOrganisationUnitGroups.add( bd.getOrganisationUnitGroup() );
            
        }
        
        
        // dataSets.removeAll(bankDetailDataSets);

        //lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.BANK ) );
        
        List<Lookup> lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.BANK ) );
        
        for ( Lookup lookup : lookups )
        {
            banks.add( lookup.getValue() );
        }

        //System.out.println( "Data Set Size :--" + dataSets.size() );
        System.out.println( "OrganisationUnitGroups Size :--" + organisationUnitGroups.size() );

        for ( OrganisationUnitGroup ogGroup : organisationUnitGroups )
        {
            System.out.println( ogGroup.getName() );
        }

        //Collections.sort( dataSets );
        
        Collections.sort( organisationUnitGroups, new IdentifiableObjectNameComparator() );
        
        
        return SUCCESS;
    }
}
