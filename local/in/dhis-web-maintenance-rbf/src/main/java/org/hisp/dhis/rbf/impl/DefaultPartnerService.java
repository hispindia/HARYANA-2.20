package org.hisp.dhis.rbf.impl;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.option.Option;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.rbf.api.Partner;
import org.hisp.dhis.rbf.api.PartnerService;
import org.hisp.dhis.rbf.api.PartnerStore;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Mithilesh Kumar Thakur
 */
@Transactional
public class DefaultPartnerService implements PartnerService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PartnerStore partnerStore;

    public void setPartnerStore( PartnerStore partnerStore )
    {
        this.partnerStore = partnerStore;
    }


    // -------------------------------------------------------------------------
    // Partner
    // -------------------------------------------------------------------------

    @Override
    public void addPartner( Partner partner )
    {
        partnerStore.addPartner( partner );;
    }

    @Override
    public void updatePartner( Partner partner )
    {
        partnerStore.updatePartner( partner );
    }

    @Override
    public void deletePartner( Partner partner )
    {
        partnerStore.deletePartner( partner );
    }
    
    @Override
    public Partner getPartner( OrganisationUnit organisationUnit, DataSet dataSet, DataElement dataElement, Date startDate, Date endDate )
    {
        return partnerStore.getPartner( organisationUnit, dataSet, dataElement, startDate, endDate );
    }    
    
    @Override
    public Collection<Partner> getAllPartner()
    {
        return partnerStore.getAllPartner();
    }

    @Override
    public Collection<Partner> getPartner( OrganisationUnit organisationUnit, DataSet dataSet )
    {
        return partnerStore.getPartner( organisationUnit, dataSet );
    }


    public Collection<String> getStartAndEndDate( Integer dataSetId, Integer dataElementId, Integer optionId )
    {
        return partnerStore.getStartAndEndDate( dataSetId, dataElementId, optionId );
    }
    
    public Map<String, Integer> getOrgUnitCountFromPartner( Integer dataSetId, Integer dataElementId, Integer optionId, String startDate, String endDate )
    {
        return partnerStore.getOrgUnitCountFromPartner( dataSetId, dataElementId, optionId, startDate, endDate );
    }    
    
    public Map<String, Integer> getOrgUnitCountFromPartner( Integer dataSetId, Integer dataElementId, Integer optionId )
    {
        return partnerStore.getOrgUnitCountFromPartner( dataSetId, dataElementId, optionId );
    }     
    
    public Set<OrganisationUnit> getPartnerOrganisationUnits( Integer dataSetId, Integer dataElementId, Integer optionId, String startDate, String endDate )
    {
        return partnerStore.getPartnerOrganisationUnits( dataSetId, dataElementId, optionId, startDate, endDate );
    } 
    
    public Map<Integer, Option> getPartners( OrganisationUnit organisationUnit, DataSet dataSet, Period period )
    {
        return partnerStore.getPartners( organisationUnit, dataSet, period );
    }     
    
}
