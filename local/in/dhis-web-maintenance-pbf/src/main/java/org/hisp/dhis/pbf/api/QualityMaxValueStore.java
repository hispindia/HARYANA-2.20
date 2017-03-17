package org.hisp.dhis.pbf.api;

import java.util.Collection;
import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.organisationunit.OrganisationUnit;


public interface QualityMaxValueStore
{
	String ID = QualityMaxValueStore.class.getName();
	
	void addQuantityMaxValue( QualityMaxValue qualityMaxValue );
    
    void updateQuantityMaxValue( QualityMaxValue qualityMaxValue );
    
    void deleteQuantityMaxValue( QualityMaxValue qualityMaxValue );
    
    Collection<QualityMaxValue> getAllQuanlityMaxValues();
    
    Collection<QualityMaxValue> getQuanlityMaxValues( OrganisationUnit organisationUnit, DataSet dataSet );
    
    Collection<QualityMaxValue> getQuanlityMaxValues( OrganisationUnit organisationUnit, DataElement dataElement );
    
    QualityMaxValue getQualityMaxValue( OrganisationUnit organisationUnit, DataElement dataElement, DataSet dataSet, Date startDate ,Date endDate);
}
