package org.hisp.dhis.rbf.quality.dataentry;

/*
 * Copyright (c) 2004-2012, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of the HISP project nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Samta bajpai
 */
public class SaveDataValueAction
    implements Action
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
    
    private String overHeadPaymentDeId;
    
    public void setOverHeadPaymentDeId( String overHeadPaymentDeId )
    {
        this.overHeadPaymentDeId = overHeadPaymentDeId;
    }
    
    private String overHeadPaymentValue;
    
    public void setOverHeadPaymentValue( String overHeadPaymentValue )
    {
        this.overHeadPaymentValue = overHeadPaymentValue;
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
                //dataValue.setTimestamp( now );
                dataValue.setLastUpdated( now );
                dataValue.setStoredBy( storedBy );
                
                dataValueService.addDataValue( dataValue );
            }
        }
        else
        {   /*    
            if( !(value.trim().equalsIgnoreCase( dataValue.getValue() ) ) )
            {
                dataValue.setValue( value.trim() );
                //dataValue.setTimestamp( now );
                dataValue.setLastUpdated( now );
                dataValue.setStoredBy( storedBy );                
                dataValueService.updateDataValue( dataValue );
            }
            */
            
            dataValue.setValue( value );
            dataValue.setLastUpdated( now );
            dataValue.setStoredBy( storedBy );

            dataValueService.updateDataValue( dataValue );  
        }
        
        // for saving Over All Score
        
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
        
        // Save Quality overhead payment in dataValue
        if ( overHeadPaymentValue != null && overHeadPaymentDeId != null )
        {
            overHeadPaymentValue = overHeadPaymentValue.trim();
            
            DataElement overHeadPaymentDataElement = dataElementService.getDataElement( Integer.parseInt( overHeadPaymentDeId ) );

            if ( overHeadPaymentDataElement == null )
            {
                return logError( "Invalid dataelement identifier: " + overHeadPaymentDeId );
            }
            
            DataValue overHeadPaymentDataValue = dataValueService.getDataValue( overHeadPaymentDataElement, period, organisationUnit, optionCombo );
            
            if ( overHeadPaymentDataValue == null )
            {
                if ( overHeadPaymentValue != null && (!overHeadPaymentValue.trim().equals( "" ) )  )
                {
                    overHeadPaymentDataValue = new DataValue();
                    
                    overHeadPaymentDataValue.setPeriod( period );
                    overHeadPaymentDataValue.setDataElement( overHeadPaymentDataElement );
                    overHeadPaymentDataValue.setSource(organisationUnit);
                    overHeadPaymentDataValue.setCategoryOptionCombo( optionCombo );
                    
                    overHeadPaymentDataValue.setValue( overHeadPaymentValue.trim() );
                    overHeadPaymentDataValue.setLastUpdated( now );
                    
                    overHeadPaymentDataValue.setStoredBy( storedBy );
                    
                    dataValueService.addDataValue( overHeadPaymentDataValue );
                }
            }
            else
            {
                if( !( overHeadPaymentValue.trim().equalsIgnoreCase( overHeadPaymentDataValue.getValue() ) ) )
                {
                    overHeadPaymentDataValue.setValue( overHeadPaymentValue.trim() );
                    overHeadPaymentDataValue.setLastUpdated( now );
                    overHeadPaymentDataValue.setStoredBy( storedBy );                
                    dataValueService.updateDataValue( overHeadPaymentDataValue );
              }            
            }
            
        }
        
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
