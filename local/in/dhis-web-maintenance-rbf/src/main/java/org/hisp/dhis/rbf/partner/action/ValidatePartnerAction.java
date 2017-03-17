package org.hisp.dhis.rbf.partner.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.option.Option;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.rbf.api.PartnerService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ValidatePartnerAction implements Action
{       
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private SelectionTreeManager selectionTreeManager;
    
    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private PartnerService partnerService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private PeriodService periodService;
    
    @Autowired
    private OptionService optionService;
    
    
    // -------------------------------------------------------------------------
    // I18n
    // -------------------------------------------------------------------------

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }
        
    
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private Integer dataSetId;
    
    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }
    
    private Integer optionSetId;
    
    public void setOptionSetId( Integer optionSetId )
    {
        this.optionSetId = optionSetId;
    }
    
    private Integer dataElementId;
    
    public void setDataElementId( Integer dataElementId )
    {
        this.dataElementId = dataElementId;
    }
    
    private String message;

    public String getMessage()
    {
        return message;
    }    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        System.out.println( " Inside validate partner" );
        
        DataSet dataSet = dataSetService.getDataSet( dataSetId );
        
        if ( dataSet == null )
        {
            message = i18n.getString( "please_select_dataset" );

            return INPUT;
        }
        
        Option option = optionService.getOption( optionSetId );
        
        if ( option == null )
        {
            message = i18n.getString( "please_select_partner" );

            return INPUT;
        }                
        
        DataElement dataElement = dataElementService.getDataElement( dataElementId );
        
        if ( dataElement == null )
        {
            message = i18n.getString( "please_select_dataelement" );

            return INPUT;
        }        

        Set<OrganisationUnit> selectedOrgUnitList = new HashSet<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() );
        
        if ( selectedOrgUnitList == null  || selectedOrgUnitList.size() == 0 )
        {
            message = i18n.getString( "please_select_organisationunit" );

            return INPUT;
        }                
        
        //System.out.println( " selectedOrgUnitList " + selectedOrgUnitList.size() );
        
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
        
        for ( OrganisationUnit organisationUnit : selectedOrgUnitList )
        {
            orgUnitList.addAll( organisationUnitService.getOrganisationUnitWithChildren( organisationUnit.getId() )  );
        }
        
        //System.out.println( " Size of Children " + orgUnitList.size() );
        
        //System.out.println( " Size of Period List is  " + periodsBetweenDates.size() );
        
        Set<OrganisationUnit> dataSetSources = new HashSet<OrganisationUnit>( dataSet.getSources() );
        
        dataSetSources.retainAll( orgUnitList );
        
        //System.out.println( " Size of DataSet Source " + dataSetSources.size() );
        
        if ( dataSetSources == null  || dataSetSources.size() == 0 )
        {
            message = i18n.getString( "combination_of_dataset_and_dataelement_does_not_exists_for_the_selected_facility" );

            return INPUT;
        }         
        
        return SUCCESS;
    }
}
