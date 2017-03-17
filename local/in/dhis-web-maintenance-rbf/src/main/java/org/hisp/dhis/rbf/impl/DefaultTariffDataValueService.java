package org.hisp.dhis.rbf.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.rbf.api.TariffDataValue;
import org.hisp.dhis.rbf.api.TariffDataValueService;
import org.hisp.dhis.rbf.api.TariffDataValueStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultTariffDataValueService implements TariffDataValueService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TariffDataValueStore tariffDataValueStore;

    public void setTariffDataValueStore( TariffDataValueStore tariffDataValueStore )
    {
        this.tariffDataValueStore = tariffDataValueStore;
    }

    // -------------------------------------------------------------------------
    // TariffDataValue
    // -------------------------------------------------------------------------

    @Override
    public void addTariffDataValue( TariffDataValue tariffDataValue )
    {
        tariffDataValueStore.addTariffDataValue( tariffDataValue );;
    }

    @Override
    public void updateTariffDataValue( TariffDataValue tariffDataValue )
    {
        tariffDataValueStore.updateTariffDataValue( tariffDataValue );
    }

    @Override
    public void deleteTariffDataValue( TariffDataValue tariffDataValue )
    {
        tariffDataValueStore.deleteTariffDataValue( tariffDataValue );
    }

    @Override
    public TariffDataValue getTariffDataValue( OrganisationUnit organisationUnit, DataElement dataElement, DataSet dataSet, Date startDate, Date endDate )
    {
        return tariffDataValueStore.getTariffDataValue( organisationUnit, dataElement, dataSet, startDate, endDate );
    }

    @Override
    public TariffDataValue getTariffDataValue( OrganisationUnitGroup orgUnitGroup, DataElement dataElement, DataSet dataSet, Date startDate, Date endDate )
    {
        return tariffDataValueStore.getTariffDataValue( orgUnitGroup, dataElement, dataSet, startDate, endDate );
    }

    @Override
    public Collection<TariffDataValue> getAllTariffDataValues()
    {
        return tariffDataValueStore.getAllTariffDataValues();
    }

    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataSet dataSet )
    {
        return tariffDataValueStore.getTariffDataValues( organisationUnit, dataSet );
    }

    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnitGroup orgUnitGroup, DataSet dataSet )
    {
        return tariffDataValueStore.getTariffDataValues( orgUnitGroup, dataSet );
    }

    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataElement dataElement )
    {
        return tariffDataValueStore.getTariffDataValues( organisationUnit, dataElement );
    }

    @Override
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnitGroup orgUnitGroup, DataElement dataElement )
    {
        return tariffDataValueStore.getTariffDataValues( orgUnitGroup, dataElement );
    }

    public Map<Integer, Double> getTariffDataValues( OrganisationUnit organisationUnit, DataSet dataSet, Period period )
    {
        return tariffDataValueStore.getTariffDataValues( organisationUnit, dataSet, period );
    }

    public Map<Integer, Double> getTariffDataValues( OrganisationUnitGroup orgUnitGroup, DataSet dataSet, Period period )
    {
        return tariffDataValueStore.getTariffDataValues( orgUnitGroup, dataSet, period );
    }

    public String getTariffDataValue( Integer orgunitgroupId, Integer dataSetId, Integer dataElementId, String date )
    {
        return tariffDataValueStore.getTariffDataValue( orgunitgroupId, dataSetId, dataElementId, date );
    }
    
    public TariffDataValue getTariffDataValue( OrganisationUnit organisationUnit, OrganisationUnitGroup orgUnitGroup, DataElement dataElement, DataSet dataSet, Date startDate, Date endDate )
    {
        return tariffDataValueStore.getTariffDataValue( organisationUnit, orgUnitGroup, dataElement, dataSet, startDate, endDate );
    }
    
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, OrganisationUnitGroup orgUnitGroup, DataSet dataSet )
    {
        return tariffDataValueStore.getTariffDataValues( organisationUnit, orgUnitGroup, dataSet );
    }
    
    public Collection<TariffDataValue> getTariffDataValues( OrganisationUnitGroup orgUnitGroup, OrganisationUnit organisationUnit, DataElement dataElement )
    {
        return tariffDataValueStore.getTariffDataValues( orgUnitGroup, organisationUnit, dataElement );
    }
    
    public Map<Integer, Double> getTariffDataValues( OrganisationUnitGroup orgUnitGroup, String orgUnitBranchIds, DataSet dataSet, Period period )
    {
        return tariffDataValueStore.getTariffDataValues( orgUnitGroup, orgUnitBranchIds, dataSet, period );
    }
    
    public String getTariffDataValue( Integer orgunitgroupId, Integer organisationUnitId, Integer dataSetId, Integer dataElementId, String date )
    {
        return tariffDataValueStore.getTariffDataValue( orgunitgroupId, organisationUnitId, dataSetId, dataElementId, date );
    }
    
    public Set<Integer> getOrgUnitGroupsByDataset( Integer dataSetId, String orgUnitIds )
    {
        return tariffDataValueStore.getOrgUnitGroupsByDataset( dataSetId, orgUnitIds );
    }
}
