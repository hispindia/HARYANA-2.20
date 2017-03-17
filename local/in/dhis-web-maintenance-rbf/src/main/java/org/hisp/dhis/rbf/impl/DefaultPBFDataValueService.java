package org.hisp.dhis.rbf.impl;

import java.util.Collection;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.rbf.api.PBFDataValue;
import org.hisp.dhis.rbf.api.PBFDataValueService;
import org.hisp.dhis.rbf.api.PBFDataValueStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultPBFDataValueService
    implements PBFDataValueService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PBFDataValueStore pbfDataValueStore;

    public void setPbfDataValueStore( PBFDataValueStore pbfDataValueStore )
    {
        this.pbfDataValueStore = pbfDataValueStore;
    }

    // -------------------------------------------------------------------------
    // PBFDataValue
    // -------------------------------------------------------------------------

    @Override
    public void addPBFDataValue( PBFDataValue pbfDataValue )
    {
        pbfDataValueStore.addPBFDataValue( pbfDataValue );
    }

    @Override
    public void updatePBFDataValue( PBFDataValue pbfDataValue )
    {
        pbfDataValueStore.updatePBFDataValue( pbfDataValue );
    }

    @Override
    public void deletePBFDataValue( PBFDataValue pbfDataValue )
    {
        pbfDataValueStore.deletePBFDataValue( pbfDataValue );
    }

    @Override
    public Collection<PBFDataValue> getPBFDataValues( OrganisationUnit organisationUnit, DataSet dataSet, Period period )
    {
        return pbfDataValueStore.getPBFDataValues( organisationUnit, dataSet, period );
    }

    @Override
    public PBFDataValue getPBFDataValue( OrganisationUnit organisationUnit, DataSet dataSet, Period period,
        DataElement dataElement )
    {
        return pbfDataValueStore.getPBFDataValue( organisationUnit, dataSet, period, dataElement );
    }
    
    @Override
    public Map<Integer, Double> getPBFDataValues( String orgUnitIds, DataSet dataSet, String periodIds )
    {
        return pbfDataValueStore.getPBFDataValues( orgUnitIds, dataSet, periodIds );
    }


}
