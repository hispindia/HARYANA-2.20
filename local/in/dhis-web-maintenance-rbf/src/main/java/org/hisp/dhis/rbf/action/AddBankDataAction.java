package org.hisp.dhis.rbf.action;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.rbf.api.BankDetails;
import org.hisp.dhis.rbf.api.BankDetailsService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class AddBankDataAction
    implements Action
{
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

    /*
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    */
    
    @Autowired
    private OrganisationUnitGroupService orgUnitGroupService;
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String orgUnitUid;

    public void setOrgUnitUid( String orgUnitUid )
    {
        this.orgUnitUid = orgUnitUid;
    }

    private String orgUnitGroupId;
    
    public void setOrgUnitGroupId( String orgUnitGroupId )
    {
        this.orgUnitGroupId = orgUnitGroupId;
    }
    
    /*
    public void setDataSetId( String dataSetId )
    {
        this.dataSetId = dataSetId;

    }
    */
    
    private String accountNumber;

    public void setAccountNumber( String accountNumber )
    {
        this.accountNumber = accountNumber;
    }

    private String accountName;

    public void setAccountName( String accountName )
    {
        this.accountName = accountName;
    }

    private String bank;

    public void setBank( String bank )
    {
        this.bank = bank;
    }

    private String branchName;

    public void setBranchName( String branchName )
    {
        this.branchName = branchName;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitUid );

        //DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( dataSetId ) );
        
        OrganisationUnitGroup orgUnitGroup = orgUnitGroupService.getOrganisationUnitGroup( Integer.parseInt( orgUnitGroupId ) );
        
        //BankDetails bankDetails = bankDetailsService.getBankDetails( organisationUnit, dataSet );
        BankDetails bankDetails = bankDetailsService.getBankDetails( organisationUnit, orgUnitGroup );

        if ( bankDetails == null )
        {
            bankDetails = new BankDetails();
            //bankDetails.setDataSet( dataSet );
            bankDetails.setOrganisationUnitGroup( orgUnitGroup );
            bankDetails.setOrganisationUnit( organisationUnit );
            bankDetails.setAccountName( accountName );
            bankDetails.setAccountNumber( accountNumber );
            bankDetails.setBank( bank );
            bankDetails.setBranchName( branchName );
            bankDetailsService.addBankDetails( bankDetails );
        }
        else
        {
            bankDetails.setAccountName( accountName );
            bankDetails.setAccountNumber( accountNumber );
            bankDetails.setBank( bank );
            bankDetails.setBranchName( branchName );
            bankDetailsService.updateBankDetails( bankDetails );
        }

        return SUCCESS;
    }
}