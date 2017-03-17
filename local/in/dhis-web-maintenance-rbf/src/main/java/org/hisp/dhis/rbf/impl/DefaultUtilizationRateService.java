package org.hisp.dhis.rbf.impl;

import java.util.Collection;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.rbf.api.UtilizationRate;
import org.hisp.dhis.rbf.api.UtilizationRateService;
import org.hisp.dhis.rbf.api.UtilizationRateStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */
@Transactional
public class DefaultUtilizationRateService implements UtilizationRateService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UtilizationRateStore utilizationRateStore;

    public void setUtilizationRateStore( UtilizationRateStore utilizationRateStore )
    {
        this.utilizationRateStore = utilizationRateStore;
    }

    // -------------------------------------------------------------------------
    // UtilizationRateService Methods implements
    // -------------------------------------------------------------------------
    
    @Override
    public void addUtilizationRate( UtilizationRate utilizationRate )
    {
        utilizationRateStore.addUtilizationRate( utilizationRate );
    }

    @Override
    public  void updateUtilizationRate( UtilizationRate utilizationRate )
    {
        utilizationRateStore.updateUtilizationRate( utilizationRate );
    }
    
    @Override
    public void deleteUtilizationRate( UtilizationRate utilizationRate )
    {
        utilizationRateStore.deleteUtilizationRate( utilizationRate );
    }
    
    @Override
    public UtilizationRate getUtilizationRate( DataElement dataElement, Double startRange, Double endRange )
    {
        return utilizationRateStore.getUtilizationRate( dataElement, startRange, endRange );
    }
    
    @Override
    public Collection<UtilizationRate> getAllUtilizationRate()
    {
        return utilizationRateStore.getAllUtilizationRate();
    }

    @Override
    public Collection<UtilizationRate> getUtilizationRates( DataElement dataElement )
    {
        return utilizationRateStore.getUtilizationRates( dataElement );
    }

    public Double getUtilizationRateTariffValue( DataElement dataElement, Double startRange, Double endRange )
    {
        return utilizationRateStore.getUtilizationRateTariffValue( dataElement, startRange, endRange );
    }
        
    public Map<Integer, String>getUtilizationRates()
    {
        return utilizationRateStore.getUtilizationRates();
    }
}
