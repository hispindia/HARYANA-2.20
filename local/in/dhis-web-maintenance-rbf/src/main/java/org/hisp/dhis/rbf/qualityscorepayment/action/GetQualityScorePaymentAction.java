package org.hisp.dhis.rbf.qualityscorepayment.action;

import org.hisp.dhis.rbf.api.QualityScorePayment;
import org.hisp.dhis.rbf.api.QualityScorePaymentService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetQualityScorePaymentAction implements Action
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
    // Input
    // -------------------------------------------------------------------------

    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

    private QualityScorePayment qualityscorepayment; 
    
    public QualityScorePayment getQualityscorepayment()
    {
        return qualityscorepayment;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
    
        qualityscorepayment = qualityScorePaymentService.getQualityScorePayment( id );
        /*
        System.out.println( qualityscorepayment.getStartRange() );
        System.out.println( qualityscorepayment.getEndRange() );
        System.out.println( qualityscorepayment.getAddQtyPayment() );
        */
        
        return SUCCESS;
    }

}

