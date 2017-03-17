package org.hisp.dhis.rbf.dataentry;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.rbf.quality.dataentry.SaveDataValueAction;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class SaveValueInDataValueAction implements Action
{
    private static final Log log = LogFactory.getLog( SaveDataValueAction.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }
    
    private DataElementCategoryService categoryService;

    public void setCategoryService( DataElementCategoryService categoryService )
    {
        this.categoryService = categoryService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private String value;

    public void setValue( String value )
    {
        this.value = value;
    }
    
    private String dataElementId;

    public void setDataElementId( String dataElementId )
    {
        this.dataElementId = dataElementId;
    }
    
    private String organisationUnitId;

    public void setOrganisationUnitId( String organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }
   
    private String periodIso;

    public void setPeriodIso(String periodIso) 
    {
        this.periodIso = periodIso;
    }
    
    private String overAllScoreValue;
    
    public void setOverAllScoreValue( String overAllScoreValue )
    {
        this.overAllScoreValue = overAllScoreValue;
    }
    
    private String overAllScoreDeId;
    
    public void setOverAllScoreDeId( String overAllScoreDeId )
    {
        this.overAllScoreDeId = overAllScoreDeId;
    }
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
    private int statusCode = 0;

    public int getStatusCode()
    {
        return statusCode;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        Period period = PeriodType.getPeriodFromIsoString(periodIso);
        
        if ( period == null )
        {
            return logError( "Illegal period identifier: " + periodIso );
        }

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        if ( organisationUnit == null )
        {
            return logError( "Invalid organisation unit identifier: " + organisationUnitId );
        }

        DataElement dataElement = dataElementService.getDataElement( Integer.parseInt( dataElementId ) );

        if ( dataElement == null )
        {
            return logError( "Invalid data element identifier: " + dataElementId );
        }

        DataElementCategoryOptionCombo optionCombo = categoryService.getDefaultDataElementCategoryOptionCombo();

        String storedBy = currentUserService.getCurrentUsername();

        Date now = new Date();

        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }

        if( value == null )
        {
            value = "";
        }
        
        if ( value != null )
        {
            value = value.trim();
        }

        // ---------------------------------------------------------------------
        // Update data
        // ---------------------------------------------------------------------

        DataValue dataValue = dataValueService.getDataValue( dataElement, period, organisationUnit, optionCombo);
       
        if ( dataValue == null )
        {
            if ( value != null && (!value.trim().equals( "" ) || !value.equalsIgnoreCase( "-1" ))  )
            {
                dataValue = new DataValue();
                
                dataValue.setPeriod(period);
                dataValue.setDataElement(dataElement);
                dataValue.setSource(organisationUnit);
                dataValue.setCategoryOptionCombo(optionCombo);
                
                dataValue.setValue( value.trim() );
                dataValue.setLastUpdated( now );
                dataValue.setStoredBy( storedBy );
                
                dataValueService.addDataValue( dataValue );
            }
            
            System.out.println( " Value Addedd in dataValue Table de: "+ dataElement.getId() + " org unit Id : " + organisationUnit.getId() + "  Period Id : " + period.getId() + "  Value : " + value );
        }
        else
        {
            if( !(value.trim().equalsIgnoreCase( dataValue.getValue() ) ) )
            {
                dataValue.setValue( value.trim() );
                dataValue.setLastUpdated( now );
                dataValue.setStoredBy( storedBy );                
                dataValueService.updateDataValue( dataValue );
            }
            
            System.out.println( " Value Updated in dataValue Table de: " + dataElement.getId() + " org unit Id : " + organisationUnit.getId() + "  Period Id : " + period.getId() + "  Value : " + value );  
            
        }
        
        
        
        
        
        
        // for saving Over All Score
        
        /*
        if ( overAllScoreValue != null && overAllScoreDeId != null )
        {
            overAllScoreValue = overAllScoreValue.trim();
            
            DataElement overAllScoreDataElement = dataElementService.getDataElement( Integer.parseInt( overAllScoreDeId ) );

            if ( overAllScoreDataElement == null )
            {
                return logError( "Invalid dataelement identifier: " + overAllScoreDeId );
            }
            
            DataValue overAllScoreDataValue = dataValueService.getDataValue( overAllScoreDataElement, period, organisationUnit, optionCombo );
            
            if ( overAllScoreDataValue == null )
            {
                if ( overAllScoreValue != null && (!overAllScoreValue.trim().equals( "" ) )  )
                {
                    overAllScoreDataValue = new DataValue();
                    
                    overAllScoreDataValue.setPeriod( period );
                    overAllScoreDataValue.setDataElement( overAllScoreDataElement );
                    overAllScoreDataValue.setSource(organisationUnit);
                    overAllScoreDataValue.setCategoryOptionCombo( optionCombo );
                    
                    overAllScoreDataValue.setValue( overAllScoreValue.trim() );
                    //overAllScoreDataValue.setTimestamp( now );
                    overAllScoreDataValue.setLastUpdated( now );
                    
                    overAllScoreDataValue.setStoredBy( storedBy );
                    
                    dataValueService.addDataValue( overAllScoreDataValue );
                }
            }
            else
            {
                if( !(overAllScoreValue.trim().equalsIgnoreCase( overAllScoreDataValue.getValue() ) ) )
                {
                    overAllScoreDataValue.setValue( overAllScoreValue.trim() );
                    //overAllScoreDataValue.setTimestamp( now );
                    overAllScoreDataValue.setLastUpdated( now );
                    overAllScoreDataValue.setStoredBy( storedBy );                
                    dataValueService.updateDataValue( overAllScoreDataValue );
              }            
            }
            
        }
        */
        
        
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private String logError( String message )
    {
        return logError( message, 1 );
    }

    private String logError( String message, int statusCode )
    {
        log.info( message );

        this.statusCode = statusCode;

        return SUCCESS;
    }
}
