package org.hisp.dhis.rbf.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.rbf.api.Lookup;
import org.hisp.dhis.rbf.api.LookupService;
import org.hisp.dhis.rbf.api.TariffDataValue;
import org.hisp.dhis.rbf.api.TariffDataValueService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ShowUpdateTariffDetailsFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    @Autowired
    private TariffDataValueService tariffDataValueService;

    @Autowired
    private DataElementService dataElementService;

    @Autowired
    private OrganisationUnitService organisationUnitService;

    @Autowired
    private DataSetService dataSetService;
 
    @Autowired
    private OrganisationUnitGroupService orgUnitGroupService;
    
    @Autowired
    private LookupService lookupService;
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer organisationUnitId;
    
    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }

    private Integer orgUnitGroupId;
    
    public void setOrgUnitGroupId(Integer orgUnitGroupId)
    {
        this.orgUnitGroupId = orgUnitGroupId;
    }
    
    private String dataElementId;

    public void setDataElementId( String dataElementId )
    {
        this.dataElementId = dataElementId;
    }
    
    private Integer dataSetId;
    
    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private String startDate;
    
    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }
    
    private String endDate;
    
    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }
    
    private TariffDataValue tariffDataValue;

    public TariffDataValue getTariffDataValue()
    {
        return tariffDataValue;
    }
    
    private List<DataSet> dataSets = new ArrayList<DataSet>();

    public List<DataSet> getDataSets()
    {
        return dataSets;
    }
    
    
    private String tariffStartDate;
    
    public String getTariffStartDate()
    {
        return tariffStartDate;
    }
    
    private String tariffEndDate;
    
    public String getTariffEndDate()
    {
        return tariffEndDate;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        Date sDate = dateFormat.parse( startDate );
        Date eDate = dateFormat.parse( endDate );

        DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( dataElementId ) );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );
        
        OrganisationUnitGroup orgUnitGroup = orgUnitGroupService.getOrganisationUnitGroup( orgUnitGroupId );

        DataSet dataSet = dataSetService.getDataSet( dataSetId );
        
        tariffDataValue = new TariffDataValue();
        
        tariffDataValue = tariffDataValueService.getTariffDataValue( organisationUnit, orgUnitGroup, dataElement, dataSet, sDate, eDate );
        
        tariffStartDate = dateFormat.format( tariffDataValue.getStartDate() );
        
        tariffEndDate = dateFormat.format( tariffDataValue.getEndDate() );
        
        List<Lookup> lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.DS_PBF_TYPE ) );

        for ( Lookup lookup : lookups )
        {
            Integer dataSetId = Integer.parseInt( lookup.getValue() );

            DataSet pbfDataSet = dataSetService.getDataSet( dataSetId );

            dataSets.add( pbfDataSet );
        }
        
        /*
        Set<OrganisationUnit> groupMember = new TreeSet<OrganisationUnit>( orgUnitGroup.getMembers() );
        
        Set<DataSet> tempDataSets = new TreeSet<DataSet>();
        
        for( OrganisationUnit orgUnit : groupMember )
        {
            tempDataSets.addAll( orgUnit.getDataSets() );
        }
        
        dataSets.retainAll( tempDataSets );
        */
        
        dataSets.retainAll( dataSetService.getDataSetsBySources( orgUnitGroup.getMembers() ) );
        
        //dataSets.retainAll( orgUnitGroup.getDataSets() );
        
        return SUCCESS;
    }
}