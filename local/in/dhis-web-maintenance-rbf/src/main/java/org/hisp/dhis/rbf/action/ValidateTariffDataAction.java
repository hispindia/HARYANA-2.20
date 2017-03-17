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
import org.hisp.dhis.rbf.api.TariffDataValue;
import org.hisp.dhis.rbf.api.TariffDataValueService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class ValidateTariffDataAction
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

    private String startDate;

    private String endDate;

    private String dataElementId;

    private String orgUnitUid;

    private Integer orgUnitGroupId;

    public void setOrgUnitGroupId( Integer orgUnitGroupId )
    {
        this.orgUnitGroupId = orgUnitGroupId;
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

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    public String getPbfType()
    {
        return pbfType;
    }

    public String getStartDate()
    {
        return startDate;
    }

    public String getEndDate()
    {
        return endDate;
    }

    public String getOrgUnitUid()
    {
        return orgUnitUid;
    }

    private String message;

    public String getMessage()
    {
        return message;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        System.out.println( startDate );
        
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        Date sDate = dateFormat.parse( startDate );
        Date eDate = dateFormat.parse( endDate );

        DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( dataElementId ) );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitUid );

        OrganisationUnitGroup orgUnitGroup = orgUnitGroupService.getOrganisationUnitGroup( orgUnitGroupId );

        DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( pbfType ) );

        List<TariffDataValue> tariffDataValues = new ArrayList<TariffDataValue>( tariffDataValueService.getTariffDataValues( orgUnitGroup, organisationUnit, dataElement ) );
        // boolean status = false;
        for ( TariffDataValue tdv : tariffDataValues )
        {
            if ( tdv.getDataSet().getId() == dataSet.getId() && tdv.getStartDate().before( sDate )
                && tdv.getEndDate().after( eDate ) )
            {
                message = "true";
                break;
            }
            else
            {
                message = "false";
            }
        }

        TariffDataValue tariffDataValue = tariffDataValueService.getTariffDataValue( organisationUnit, orgUnitGroup, dataElement, dataSet, sDate, eDate );
        
      
        
        
        if ( tariffDataValue == null )
        {
            String value = tariffDataValueService.getTariffDataValue(  orgUnitGroup.getId(), organisationUnit.getId(), dataSet.getId(), dataElement.getId(), startDate );
            
            if ( value == null  )
            {
                String enddateValue = tariffDataValueService.getTariffDataValue( orgUnitGroup.getId(), organisationUnit.getId(), dataSet.getId(), dataElement.getId(), endDate );
                
                if ( enddateValue != null  )
                {
                    message = "Data Already Exists for the period " + enddateValue + " , Please Specify Another Date";

                    return ERROR;
                }
            }
            
            else
            {
                message = "Data Already Exists for the period " + value + " , Please Specify Another Date";

                return ERROR;
            }
        }        
        
        if ( tariffDataValue != null && ( pbfType == null || tariffDataValue.getDataSet().getId() != Integer.parseInt( pbfType ) ) )
        {
            message = "Data Already Exists, Please Specify Another Date";

            return ERROR;
        }
        
        
        /*
        else
        {
            message = "Data Already Exists, Please Specify Another Date";
            
            return ERROR;
        }
        */   
        
        message = "ok";
        
        return SUCCESS;
    }
}