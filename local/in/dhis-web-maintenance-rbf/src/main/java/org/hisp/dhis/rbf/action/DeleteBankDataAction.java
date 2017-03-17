package org.hisp.dhis.rbf.action;

import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.rbf.api.BankDetails;
import org.hisp.dhis.rbf.api.BankDetailsService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class DeleteBankDataAction
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

    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }
    
    /*
    private Integer dataSetId;
    
    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }
    */
    
    private String orgUnitGroupId;
    
    public void setOrgUnitGroupId( String orgUnitGroupId )
    {
        this.orgUnitGroupId = orgUnitGroupId;
    }
    
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        //DataSet dataSet = dataSetService.getDataSet( dataSetId );
        
        OrganisationUnitGroup orgUnitGroup = orgUnitGroupService.getOrganisationUnitGroup( Integer.parseInt( orgUnitGroupId ) );
        
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
        
        BankDetails bankDetail = bankDetailsService.getBankDetails( organisationUnit, orgUnitGroup );
        
        //BankDetails bankDetail = bankDetailsService.getBankDetails( organisationUnit, dataSet );

        bankDetailsService.deleteBankDetails( bankDetail );

        return SUCCESS;
    }
}