package org.hisp.dhis.pbf.impl;

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.pbf.api.QualityMaxValue;
import org.hisp.dhis.pbf.api.QualityMaxValueService;
import org.hisp.dhis.pbf.api.QualityMaxValueStore;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DefaultQualityMaxValueService implements QualityMaxValueService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private QualityMaxValueStore qualityMaxValueStore;

    public void setQualityMaxValueStore(QualityMaxValueStore qualityMaxValueStore) {
		this.qualityMaxValueStore = qualityMaxValueStore;
	}

    // -------------------------------------------------------------------------
    // QualityMaxValue
    // -------------------------------------------------------------------------
    
	@Override
	public void addQuantityMaxValue(QualityMaxValue qualityMaxValue) {
		
		qualityMaxValueStore.addQuantityMaxValue(qualityMaxValue);
	}

	@Override
	public void updateQuantityMaxValue(QualityMaxValue qualityMaxValue) {
		
		qualityMaxValueStore.updateQuantityMaxValue(qualityMaxValue);
	}

	@Override
	public void deleteQuantityMaxValue(QualityMaxValue qualityMaxValue) {
		
		qualityMaxValueStore.deleteQuantityMaxValue(qualityMaxValue);
	}

	@Override
	public Collection<QualityMaxValue> getAllQuanlityMaxValues() {
		
		return qualityMaxValueStore.getAllQuanlityMaxValues();
	}

	@Override
	public Collection<QualityMaxValue> getQuanlityMaxValues(
			OrganisationUnit organisationUnit, DataSet dataSet) {
		
		return qualityMaxValueStore.getQuanlityMaxValues(organisationUnit, dataSet);
	}

	@Override
	public QualityMaxValue getQualityMaxValue(
			OrganisationUnit organisationUnit, DataElement dataElement,
			DataSet dataSet ,Date startDate ,Date endDate) {
		
		return qualityMaxValueStore.getQualityMaxValue(organisationUnit, dataElement, dataSet ,startDate, endDate);
	}

	@Override
	public Collection<QualityMaxValue> getQuanlityMaxValues(
			OrganisationUnit organisationUnit, DataElement dataElement) {
		
		return qualityMaxValueStore.getQuanlityMaxValues(organisationUnit, dataElement);
	}

}
