package org.hisp.dhis.rbf.utilizationRuleManagement.action;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ShowAddUtilizationRateFormAction implements Action
{
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------   
    
    private String dataElementId;
    
    public String getDataElementId()
    {
        return dataElementId;
    }

    public void setDataElementId( String dataElementId )
    {
        this.dataElementId = dataElementId;
    }
    
    private DataElement dataElement;
    
    public DataElement getDataElement()
    {
        return dataElement;
    }
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        dataElement = dataElementService.getDataElement( Integer.parseInt( dataElementId ) );
        
        return SUCCESS;
    }

}
