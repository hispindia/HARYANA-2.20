package org.hisp.dhis.rbf.qualityscorepayment.action;

import org.hisp.dhis.rbf.api.QualityScorePayment;
import org.hisp.dhis.rbf.api.QualityScorePaymentService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class UpdateQualityScorePaymentAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private QualityScorePaymentService qualityScorePaymentService;
    
    public void setQualityScorePaymentService( QualityScorePaymentService qualityScorePaymentService )
    {
        this.qualityScorePaymentService = qualityScorePaymentService;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------
    
    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
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

    private String addQualityPayment;
    
    public void setAddQualityPayment( String addQualityPayment )
    {
        this.addQualityPayment = addQualityPayment;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        
        QualityScorePayment qualityScorePayment = qualityScorePaymentService.getQualityScorePayment( id );
        
        if( qualityScorePayment != null )
        {
            qualityScorePayment.setStartRange( Double.parseDouble( startRange ) );
            qualityScorePayment.setEndRange(  Double.parseDouble( endRange )  );
            qualityScorePayment.setAddQtyPayment( Double.parseDouble( addQualityPayment ) );
           
            qualityScorePaymentService.updateQualityScorePayment( qualityScorePayment );
        }
        
    
        return SUCCESS;
    }

}

