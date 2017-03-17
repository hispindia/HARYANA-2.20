package org.hisp.dhis.rbf.qualityscorepayment.action;

import org.hisp.dhis.common.DeleteNotAllowedException;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.rbf.api.QualityScorePayment;
import org.hisp.dhis.rbf.api.QualityScorePaymentService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class DeleteQualityScorePaymentAction implements Action
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
    // Getters & setters
    // -------------------------------------------------------------------------

    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }

    // -------------------------------------------------------------------------
    // I18n
    // -------------------------------------------------------------------------

    private I18n i18n;
    
    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------

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
        //System.out.println( "Inside Delete Action : "  + id );
        try
        {
            QualityScorePayment qualityScorePayment = qualityScorePaymentService.getQualityScorePayment( id );
            
            qualityScorePaymentService.deleteQualityScorePayment( qualityScorePayment );
    
            message = i18n.getString( "delete_success" );
        }
        catch ( DeleteNotAllowedException ex )
        {
            if ( ex.getErrorCode().equals( DeleteNotAllowedException.ERROR_ASSOCIATED_BY_OTHER_OBJECTS ) )
            {
                message = i18n.getString( "object_not_deleted_associated_by_objects" ) + " " + ex.getMessage();
    
                return ERROR;
            }
        }
    
        return SUCCESS;
    }
}

