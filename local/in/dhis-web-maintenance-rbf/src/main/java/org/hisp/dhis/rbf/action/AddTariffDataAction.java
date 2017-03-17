package org.hisp.dhis.rbf.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.rbf.api.TariffDataValue;
import org.hisp.dhis.rbf.api.TariffDataValueService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class AddTariffDataAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private TariffDataValueService tariffDataValueService;

    public void setTariffDataValueService( TariffDataValueService tariffDataValueService )
    {
        this.tariffDataValueService = tariffDataValueService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    @Autowired
    private OrganisationUnitGroupService orgUnitGroupService;
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String pbfType;

    private String tariff;

    private String startDate;

    private String endDate;

    private String dataElementId;

    private String orgUnitUid;
    
    private String target;
    
    private String targetPercentage;     
    
    private Integer orgUnitGroupId;
    
    public void setOrgUnitGroupId(Integer orgUnitGroupId)
    {
	this.orgUnitGroupId = orgUnitGroupId;
    }

    public void setTarget(String target) 
    {
	this.target = target;
    }

    public void setTargetPercentage(String targetPercentage) 
    {
	this.targetPercentage = targetPercentage;
    }

    public void setDataElementId( String dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    public void setOrgUnitUid( String orgUnitUid )
    {
        this.orgUnitUid = orgUnitUid;
    }

    public void setPbfType( String pbfType )
    {
        this.pbfType = pbfType;
    }

    public void setTariff( String tariff )
    {
        this.tariff = tariff;
    }

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
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

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitUid );
        
        OrganisationUnitGroup orgUnitGroup = orgUnitGroupService.getOrganisationUnitGroup( orgUnitGroupId );

        DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( pbfType ) );
        
        TariffDataValue tariffDataValue = tariffDataValueService.getTariffDataValue( organisationUnit, orgUnitGroup, dataElement, dataSet, sDate, eDate );

        if ( tariffDataValue == null )
        {
            tariffDataValue = new TariffDataValue();
            
            tariffDataValue.setValue( Double.parseDouble( tariff ) );
            if(!target.equals(""))
            {
            	tariffDataValue.setTarget(Integer.parseInt(target));
            }
            if(!targetPercentage.equals(""))
            {
            	tariffDataValue.setTargetPercentage(Double.parseDouble(targetPercentage));
            }            
            tariffDataValue.setStartDate( sDate );
            tariffDataValue.setEndDate( eDate );
            tariffDataValue.setTimestamp( new Date() );
            tariffDataValue.setStoredBy( currentUserService.getCurrentUsername() );
            tariffDataValue.setDataElement( dataElement );
            tariffDataValue.setDataSet( dataSet );
            tariffDataValue.setOrganisationUnit( organisationUnit );
            tariffDataValue.setOrgUnitGroup( orgUnitGroup );
            tariffDataValue.setOrgUnitLevel( organisationUnit.getOrganisationUnitLevel() );
            
            tariffDataValueService.addTariffDataValue( tariffDataValue );
            //System.out.println("Tariff Data Added");
        }
        else
        {
            tariffDataValue.setValue( Double.parseDouble( tariff ) );
            if(!target.equals(""))
            {
            	tariffDataValue.setTarget(Integer.parseInt(target));
            }
            if(!targetPercentage.equals(""))
            {
            	tariffDataValue.setTargetPercentage(Double.parseDouble(targetPercentage));
            }   
            tariffDataValue.setTimestamp( new Date() );
            tariffDataValue.setStoredBy( currentUserService.getCurrentUsername() );
            
            tariffDataValueService.updateTariffDataValue( tariffDataValue );
            //System.out.println("Tariff Data Updated");
        }
        
        return SUCCESS;
    }
}