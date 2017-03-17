package org.hisp.dhis.rbf.api;

import java.util.Collection;

public interface QualityScorePaymentStore
{
    String ID = QualityScorePaymentStore.class.getName();

    // -------------------------------------------------------------------------
    // QualityScorePayment
    // -------------------------------------------------------------------------
    
    void addQualityScorePayment( QualityScorePayment qualityScorePayment );

    void updateQualityScorePayment( QualityScorePayment qualityScorePayment );

    void deleteQualityScorePayment( QualityScorePayment qualityScorePayment );

    QualityScorePayment getQualityScorePayment( int id );

    Collection<QualityScorePayment> getAllQualityScorePayments();
}
