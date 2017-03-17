package org.hisp.dhis.rbf.qualityscorepayment.action;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.paging.ActionPagingSupport;
import org.hisp.dhis.rbf.api.Lookup;
import org.hisp.dhis.rbf.api.LookupService;
import org.hisp.dhis.rbf.api.QualityScorePayment;
import org.hisp.dhis.rbf.api.QualityScorePaymentService;

/**
 * @author Mithilesh Kumar Thakur
 */

public class GetQualityScorePaymentList extends ActionPagingSupport<QualityScorePayment>
{

    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private QualityScorePaymentService qualityScorePaymentService;
    
    public void setQualityScorePaymentService( QualityScorePaymentService qualityScorePaymentService )
    {
        this.qualityScorePaymentService = qualityScorePaymentService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private List<QualityScorePayment> qualityScorePayments;
    
    public List<QualityScorePayment> getQualityScorePayments()
    {
        return qualityScorePayments;
    }

    private String key;
    
    public String getKey()
    {
        return key;
    }
    
    public void setKey( String key )
    {
        this.key = key;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------


    public String execute() throws Exception
    {
        qualityScorePayments = new ArrayList<QualityScorePayment>( qualityScorePaymentService.getAllQualityScorePayments() );
       
        /*
        for( QualityScorePayment qualityScorePayment : qualityScorePayments )
        {
            System.out.println( qualityScorePayment );
        }
        */
        
        
        
        /*
        if ( isNotBlank( key ) )
        {
            qualityScorePaymentService.searchLookupByName( qualityScorePayments, key );
        }
    
        this.paging = createPaging( qualityScorePayments.size() );
        qualityScorePayments = getBlockElement( qualityScorePayments, paging.getStartPos(), paging.getPageSize() );
        */
        
        //Collections.sort( qualityScorePayments );
    
        return SUCCESS;
    
    }
}
