package org.hisp.dhis.rbf.utilizationRuleManagement.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.rbf.api.UtilizationRate;
import org.hisp.dhis.rbf.api.UtilizationRateService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetUtilizationRateListAction implements Action
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

    private List<UtilizationRate> utilizationRateList = new ArrayList<UtilizationRate>();

    public List<UtilizationRate> getUtilizationRateList()
    {
        return utilizationRateList;
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
        
        utilizationRateList = new ArrayList<UtilizationRate>();
        
        if( dataElement != null )
        {
            utilizationRateList = new ArrayList<UtilizationRate>( utilizationRateService.getUtilizationRates( dataElement ) );
             
        }
    
        return SUCCESS;
    }

}
