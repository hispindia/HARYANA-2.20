package org.hisp.dhis.rbf.utilizationRuleManagement.action;

import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.rbf.api.UtilizationRate;
import org.hisp.dhis.rbf.api.UtilizationRateService;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class UpdateUtilizationRateAction implements Action
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
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
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

    private String tariff;
    
    public void setTariff( String tariff )
    {
        this.tariff = tariff;
    }
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------
    

    public String execute() throws Exception
    {
        DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( dataElementId ) );
        
        if( dataElement != null )
        {
            UtilizationRate utilizationRate = utilizationRateService.getUtilizationRate( dataElement,  Double.parseDouble( startRange ), Double.parseDouble( endRange ) );
            
            if( utilizationRate != null )
            {
                //System.out.println( dataElement.getId() + " : " + Double.parseDouble( startRange ) + " : " + Double.parseDouble( endRange ) );
                
                utilizationRate.setDataElement( dataElement );
                utilizationRate.setStartRange( Double.parseDouble( startRange ) );
                utilizationRate.setEndRange(  Double.parseDouble( endRange )  );
                utilizationRate.setTariff( Double.parseDouble( tariff ) );
                
                String storedBy = currentUserService.getCurrentUsername();

                Date now = new Date();

                if ( storedBy == null )
                {
                    storedBy = "[unknown]";
                }

                utilizationRate.setStoredBy( storedBy );
                utilizationRate.setTimestamp( now );
                
                utilizationRateService.updateUtilizationRate( utilizationRate );
            }
            
        }
    
        return SUCCESS;
    }

}
