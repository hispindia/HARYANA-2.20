package org.hisp.dhis.rbf.impl;

import java.util.Collection;

import org.hisp.dhis.rbf.api.QualityScorePayment;
import org.hisp.dhis.rbf.api.QualityScorePaymentService;
import org.hisp.dhis.rbf.api.QualityScorePaymentStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultQualityScorePaymentService implements QualityScorePaymentService
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private QualityScorePaymentStore qualityScorePaymentStore;

    public void setQualityScorePaymentStore( QualityScorePaymentStore qualityScorePaymentStore )
    {
        this.qualityScorePaymentStore = qualityScorePaymentStore;
    }

    // -------------------------------------------------------------------------
    // Implementation
    // -------------------------------------------------------------------------

    @Override
    public void addQualityScorePayment( QualityScorePayment qualityScorePayment )
    {
        qualityScorePaymentStore.addQualityScorePayment( qualityScorePayment );
    }

    @Override
    public void updateQualityScorePayment( QualityScorePayment qualityScorePayment )
    {
        qualityScorePaymentStore.updateQualityScorePayment( qualityScorePayment );
    }

    @Override
    public void deleteQualityScorePayment( QualityScorePayment qualityScorePayment )
    {
        qualityScorePaymentStore.deleteQualityScorePayment( qualityScorePayment );
        
    }

    @Override
    public QualityScorePayment getQualityScorePayment( int id )
    {
        return qualityScorePaymentStore.getQualityScorePayment( id );
    }

    @Override
    public Collection<QualityScorePayment> getAllQualityScorePayments()
    {
        return qualityScorePaymentStore.getAllQualityScorePayments();
    }

}
