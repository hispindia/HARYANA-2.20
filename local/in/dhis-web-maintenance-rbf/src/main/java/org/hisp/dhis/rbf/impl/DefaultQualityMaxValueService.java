package org.hisp.dhis.rbf.impl;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.rbf.api.QualityMaxValue;
import org.hisp.dhis.rbf.api.QualityMaxValueService;
import org.hisp.dhis.rbf.api.QualityMaxValueStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultQualityMaxValueService
    implements QualityMaxValueService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private QualityMaxValueStore qualityMaxValueStore;

    public void setQualityMaxValueStore( QualityMaxValueStore qualityMaxValueStore )
    {
        this.qualityMaxValueStore = qualityMaxValueStore;
    }

    // -------------------------------------------------------------------------
    // QualityMaxValue
    // -------------------------------------------------------------------------

    @Override
    public void addQuantityMaxValue( QualityMaxValue qualityMaxValue )
    {

        qualityMaxValueStore.addQuantityMaxValue( qualityMaxValue );
    }

    @Override
    public void updateQuantityMaxValue( QualityMaxValue qualityMaxValue )
    {

        qualityMaxValueStore.updateQuantityMaxValue( qualityMaxValue );
    }

    @Override
    public void deleteQuantityMaxValue( QualityMaxValue qualityMaxValue )
    {

        qualityMaxValueStore.deleteQuantityMaxValue( qualityMaxValue );
    }

    @Override
    public Collection<QualityMaxValue> getAllQuanlityMaxValues()
    {

        return qualityMaxValueStore.getAllQuanlityMaxValues();
    }

    @Override
    public Collection<QualityMaxValue> getQuanlityMaxValues( OrganisationUnit organisationUnit, DataSet dataSet )
    {

        return qualityMaxValueStore.getQuanlityMaxValues( organisationUnit, dataSet );
    }

    @Override
    public QualityMaxValue getQualityMaxValue( OrganisationUnit organisationUnit, DataElement dataElement,
        DataSet dataSet, Date startDate, Date endDate )
    {

        return qualityMaxValueStore.getQualityMaxValue( organisationUnit, dataElement, dataSet, startDate, endDate );
    }

    @Override
    public Collection<QualityMaxValue> getQuanlityMaxValues( OrganisationUnit organisationUnit, DataElement dataElement )
    {
        return qualityMaxValueStore.getQuanlityMaxValues( organisationUnit, dataElement );
    }

    public Collection<QualityMaxValue> getDistinctQualityMaxScore( OrganisationUnitGroup orgUnitGroup , OrganisationUnit organisationUnit, DataSet dataSet )
    {
        return qualityMaxValueStore.getDistinctQualityMaxScore( orgUnitGroup, organisationUnit, dataSet );
    }    
    
    public Collection<QualityMaxValue> getQuanlityMaxValues( OrganisationUnitGroup orgUnitGroup, OrganisationUnit organisationUnit, DataElement dataElement)
    {
    	return qualityMaxValueStore.getQuanlityMaxValues( orgUnitGroup, organisationUnit, dataElement);
    }
    
    public QualityMaxValue getQualityMaxValue( OrganisationUnitGroup orgUnitGroup, OrganisationUnit organisationUnit, DataElement dataElement, DataSet dataSet,Date startDate ,Date endDate )
    {
    	return qualityMaxValueStore.getQualityMaxValue( orgUnitGroup, organisationUnit, dataElement, dataSet, startDate, endDate );
    }
    
    public Collection<QualityMaxValue> getQuanlityMaxValues( OrganisationUnitGroup orgUnitGroup, OrganisationUnit organisationUnit, DataSet dataSet) 
    {
    	return qualityMaxValueStore.getQuanlityMaxValues( orgUnitGroup, organisationUnit, dataSet );
    }
    
    public Map<Integer, Double> getQualityMaxValues( OrganisationUnitGroup orgUnitGroup, String orgUnitBranchIds, DataSet dataSet, Period period )
    {
    	return qualityMaxValueStore.getQualityMaxValues( orgUnitGroup, orgUnitBranchIds, dataSet, period );
    }
    
    public List<String>  getDistinctStartDateEndDateFromQualityMaxScore( OrganisationUnitGroup orgUnitGroup , OrganisationUnit organisationUnit, DataSet dataSet )
    {
        return qualityMaxValueStore.getDistinctStartDateEndDateFromQualityMaxScore( orgUnitGroup, organisationUnit, dataSet );
    }    
    
    public Set<Integer> getOrgUnitGroupsByDataset( Integer dataSetId, String orgUnitIds )
    {
        return qualityMaxValueStore.getOrgUnitGroupsByDataset( dataSetId, orgUnitIds );
    }
    
    public String getQuanlityMaxValueStartDateEndDate( Integer orgunitgroupId, Integer organisationUnitId, Integer dataSetId, String date )
    {
        return qualityMaxValueStore.getQuanlityMaxValueStartDateEndDate( orgunitgroupId, organisationUnitId, dataSetId, date );
    }   
    
    
}
