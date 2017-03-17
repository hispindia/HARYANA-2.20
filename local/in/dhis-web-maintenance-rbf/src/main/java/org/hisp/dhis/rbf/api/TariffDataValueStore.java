package org.hisp.dhis.rbf.api;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;

public interface TariffDataValueStore
{
    String ID = TariffDataValueStore.class.getName();
    
    void addTariffDataValue( TariffDataValue tariffDataValue );
    
    void updateTariffDataValue( TariffDataValue tariffDataValue );
    
    void deleteTariffDataValue( TariffDataValue tariffDataValue );
        
    TariffDataValue getTariffDataValue( OrganisationUnitGroup orgUnitGroup, DataElement dataElement, DataSet dataSet, Date startDate, Date endDate );
    
    TariffDataValue getTariffDataValue( OrganisationUnit organisationUnit, DataElement dataElement, DataSet dataSet, Date startDate, Date endDate );
    
    Collection<TariffDataValue> getAllTariffDataValues();
    
    Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataSet dataSet );
    
    Collection<TariffDataValue> getTariffDataValues( OrganisationUnitGroup orgUnitGroup, DataSet dataSet );
    
    Map<Integer, Double> getTariffDataValues( OrganisationUnit organisationUnit, DataSet dataSet, Period period );
    
    Map<Integer, Double> getTariffDataValues( OrganisationUnitGroup orgUnitGroup, DataSet dataSet, Period period );
    
    Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, DataElement dataElement );
    
    Collection<TariffDataValue> getTariffDataValues( OrganisationUnitGroup orgUnitGroup, DataElement dataElement );
    
    String getTariffDataValue( Integer orgunitgroupId, Integer dataSetId, Integer dataElementId, String date );
    
    TariffDataValue getTariffDataValue( OrganisationUnit organisationUnit, OrganisationUnitGroup orgUnitGroup, DataElement dataElement, DataSet dataSet, Date startDate, Date endDate );
    
    Collection<TariffDataValue> getTariffDataValues( OrganisationUnit organisationUnit, OrganisationUnitGroup orgUnitGroup, DataSet dataSet );
    
    Collection<TariffDataValue> getTariffDataValues( OrganisationUnitGroup orgUnitGroup, OrganisationUnit organisationUnit, DataElement dataElement );
    
    Map<Integer, Double> getTariffDataValues( OrganisationUnitGroup orgUnitGroup, String orgUnitBranchIds, DataSet dataSet, Period period );
    
    String getTariffDataValue( Integer orgunitgroupId, Integer organisationUnitId, Integer dataSetId, Integer dataElementId, String date );
    
    Set<Integer> getOrgUnitGroupsByDataset( Integer dataSetId, String orgUnitIds );
    
}
