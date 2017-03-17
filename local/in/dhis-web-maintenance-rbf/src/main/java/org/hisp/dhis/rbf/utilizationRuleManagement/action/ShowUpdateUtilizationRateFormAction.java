package org.hisp.dhis.rbf.utilizationRuleManagement.action;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.rbf.api.UtilizationRate;
import org.hisp.dhis.rbf.api.UtilizationRateService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ShowUpdateUtilizationRateFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UtilizationRateService utilizationRateService;
    
    public void setUtilizationRateService( UtilizationRateService utilizationRateService )
    {
        this.utilizationRateService = utilizationRateService;
    }
    
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------
    
    private String dataElementId;
    
    public void setDataElementId( String dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    private String startRange;
    
    public void setStartRange( String startRange )
    {
        this.startRange = startRange;
    }

    private String endRange;
    
    public void setEndRange( String endRange )
    {
        this.endRange = endRange;
    }
    
    private UtilizationRate utilizationRate;

    public UtilizationRate getUtilizationRate()
    {
        return utilizationRate;
    }
    
    private DataElement dataElement;
    
    public DataElement getDataElement()
    {
        return dataElement;
    }
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        dataElement = dataElementService.getDataElement( Integer.parseInt( dataElementId ) );
        
        if( dataElement != null )
        {
            utilizationRate = utilizationRateService.getUtilizationRate( dataElement,  Double.parseDouble( startRange ), Double.parseDouble( endRange ) );
        }
    
        return SUCCESS;
    }

}

