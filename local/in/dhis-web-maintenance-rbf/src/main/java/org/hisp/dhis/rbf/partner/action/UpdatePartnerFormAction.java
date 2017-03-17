package org.hisp.dhis.rbf.partner.action;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.option.Option;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.rbf.api.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class UpdatePartnerFormAction implements Action
{
    //private final String OPTION_SET_PARTNER = "Partner";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    
    @Autowired
    private OptionService optionService;
    
    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private PartnerService partnerService;
    
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

    private String startDate;
    
    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }
    
    public String getStartDate()
    {
        return startDate;
    }
    
    private String endDate;
    
    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    private DataSet dataSet;
    
    public DataSet getDataSet()
    {
        return dataSet;
    }

    private DataElement dataElement;
    
    public DataElement getDataElement()
    {
        return dataElement;
    }

    private Option option;
    
    public Option getOption()
    {
        return option;
    }
    
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        
        dataSet = dataSetService.getDataSet( dataSetId );
        
        dataElement = dataElementService.getDataElement( dataElementId );
        
        option = optionService.getOption( optionSetId );
        
        selectionTreeManager.setSelectedOrganisationUnits( partnerService.getPartnerOrganisationUnits( dataSetId, dataElementId, optionSetId, startDate, endDate ) );
        
        /*
        for ( Option option : options )
        {       
            option.getId();
            
            System.out.println( " Option Id -- " + option.getId() + " Option name -- " + option.getName() );
        }
        */
        
        return SUCCESS;
    }
}

