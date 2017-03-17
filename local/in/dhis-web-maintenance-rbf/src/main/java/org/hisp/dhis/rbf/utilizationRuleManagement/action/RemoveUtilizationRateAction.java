package org.hisp.dhis.rbf.utilizationRuleManagement.action;

import org.hisp.dhis.common.DeleteNotAllowedException;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.rbf.api.UtilizationRate;
import org.hisp.dhis.rbf.api.UtilizationRateService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class RemoveUtilizationRateAction implements Action
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

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
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
        DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( dataElementId ) );
        
        if( dataElement != null )
        {
            try
            {
                UtilizationRate utilizationRate = utilizationRateService.getUtilizationRate( dataElement,  Double.parseDouble( startRange ), Double.parseDouble( endRange ) );
                
                if( utilizationRate != null )
                {
                    utilizationRateService.deleteUtilizationRate( utilizationRate );
                }
            }
            catch ( DeleteNotAllowedException ex )
            {
                if ( ex.getErrorCode().equals( DeleteNotAllowedException.ERROR_ASSOCIATED_BY_OTHER_OBJECTS ) )
                {
                    message = i18n.getString( "object_not_deleted_associated_by_objects" ) + " " + ex.getMessage();
                }
                
                return ERROR;
            }
  
        }
        
        return SUCCESS;
    }
}
