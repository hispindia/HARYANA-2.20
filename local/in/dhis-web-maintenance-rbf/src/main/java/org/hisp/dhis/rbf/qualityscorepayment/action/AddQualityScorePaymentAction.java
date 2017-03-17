package org.hisp.dhis.rbf.qualityscorepayment.action;

import org.hisp.dhis.rbf.api.QualityScorePayment;
import org.hisp.dhis.rbf.api.QualityScorePaymentService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class AddQualityScorePaymentAction implements Action
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
    
        QualityScorePayment qualityScorePayment = new QualityScorePayment();
        
        qualityScorePayment.setStartRange( Double.parseDouble( startRange ) );
        qualityScorePayment.setEndRange(  Double.parseDouble( endRange )  );
        qualityScorePayment.setAddQtyPayment( Double.parseDouble( addQualityPayment ) );
       
        qualityScorePaymentService.addQualityScorePayment( qualityScorePayment );
    
        return SUCCESS;
    }

}
