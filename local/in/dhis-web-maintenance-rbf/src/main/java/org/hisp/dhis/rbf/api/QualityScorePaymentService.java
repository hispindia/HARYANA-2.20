package org.hisp.dhis.rbf.api;

import java.util.Collection;

public interface QualityScorePaymentService
{

    String ID = QualityScorePaymentService.class.getName();

    // -------------------------------------------------------------------------
    // QualityScorePayment
    // -------------------------------------------------------------------------
    
    void addQualityScorePayment( QualityScorePayment qualityScorePayment );

    void updateQualityScorePayment( QualityScorePayment qualityScorePayment );

    void deleteQualityScorePayment( QualityScorePayment qualityScorePayment );

    QualityScorePayment getQualityScorePayment( int id );

    Collection<QualityScorePayment> getAllQualityScorePayments();
}
