package org.hisp.dhis.rbf.api;

import java.util.Collection;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;

/**
 * @author Mithilesh Kumar Thakur
 */
public interface UtilizationRateStore
{
    String ID = UtilizationRateStore.class.getName();
    
    void addUtilizationRate( UtilizationRate utilizationRate );

    void updateUtilizationRate( UtilizationRate utilizationRate );

    void deleteUtilizationRate( UtilizationRate utilizationRate );
    
    UtilizationRate getUtilizationRate( DataElement dataElement, Double startRange, Double endRange );
    
    Collection<UtilizationRate> getAllUtilizationRate();
    
    Collection<UtilizationRate> getUtilizationRates( DataElement dataElement );
    
    Double getUtilizationRateTariffValue( DataElement dataElement, Double startRange, Double endRange );
    
    Map<Integer, String>getUtilizationRates();
    
}
