package org.hisp.dhis.pbf.quality.dataentry;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValue;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.pbf.api.PBFDataValue;
import org.hisp.dhis.pbf.api.PBFDataValueService;
import org.hisp.dhis.pbf.api.QualityMaxValue;
import org.hisp.dhis.pbf.api.QualityMaxValueService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.system.util.ValidationUtils;
import org.hisp.dhis.user.CurrentUserService;

import com.opensymphony.xwork2.Action;

/**
 * @author Abyot Asalefew
 */
public class SaveQualityValueAction
    implements Action
{
    private static final Log log = LogFactory.getLog( SaveQualityValueAction.class );

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

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private QualityMaxValueService qualityMaxValueService;
    
    public void setQualityMaxValueService(
			QualityMaxValueService qualityMaxValueService) {
		this.qualityMaxValueService = qualityMaxValueService;
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
   
    private String dataSetId;
    
	public void setDataSetId(String dataSetId) 
	{
		this.dataSetId = dataSetId;
	}
    
	private String startDate ;
    
    public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
    
    private String endDate ;
    
	public void setEndDate(String endDate) {
		this.endDate = endDate;
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

    public String execute() throws Exception
    {
    	
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( organisationUnitId );

        if ( organisationUnit == null )
        {
            return logError( "Invalid organisation unit identifier: " + organisationUnitId );
        }
        
        DataElement dataElement = dataElementService.getDataElement( Integer.parseInt(dataElementId) );

        if ( dataElement == null )
        {
            return logError( "Invalid data element identifier: " + dataElementId );
        }
       
        DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( dataSetId ) );
        if ( dataSet == null )
        {
            return logError( "Invalid dataset identifier: " + dataSetId );
        }
        
        String storedBy = currentUserService.getCurrentUsername();

        Date now = new Date();

        if ( storedBy == null )
        {
            storedBy = "[unknown]";
        }

        if ( value != null && value.trim().length() == 0 )
        {
            value = null;
        }

        if ( value != null )
        {
            value = value.trim();
        }

        // ---------------------------------------------------------------------
        // Validate value according to type from data element
        // ---------------------------------------------------------------------

        String valid = ValidationUtils.dataValueIsValid( value, dataElement );
        
        if ( valid != null )
        {
            return logError( valid, 3 );
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        Date sDate = dateFormat.parse( startDate );
        Date eDate = dateFormat.parse( endDate );
        
        QualityMaxValue qualityMaxValue = qualityMaxValueService.getQualityMaxValue(organisationUnit, dataElement, dataSet, sDate, eDate );

        if ( qualityMaxValue == null )
        {
            if ( value != null )
            {
            	qualityMaxValue = new QualityMaxValue( );
            	qualityMaxValue.setDataSet(dataSet);
            	qualityMaxValue.setDataElement(dataElement);
            	qualityMaxValue.setOrganisationUnit(organisationUnit);
            	
            	qualityMaxValue.setValue(Double.parseDouble(value));
            	qualityMaxValue.setStartDate(sDate);
            	qualityMaxValue.setEndDate(eDate);
            	
            	qualityMaxValue.setStoredBy(storedBy);
            	qualityMaxValue.setTimestamp(now);
            	qualityMaxValueService.addQuantityMaxValue(qualityMaxValue);
                
                System.out.println("Value Added");
            }
        }
        else
        {
        	qualityMaxValue.setStoredBy(storedBy);        	
        	qualityMaxValue.setTimestamp(now);
        	
        	qualityMaxValue.setValue(Double.parseDouble(value));
        	
        	qualityMaxValueService.updateQuantityMaxValue(qualityMaxValue);
            System.out.println("Value Updated");
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
