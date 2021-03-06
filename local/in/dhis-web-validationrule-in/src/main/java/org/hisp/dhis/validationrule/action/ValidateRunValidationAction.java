package org.hisp.dhis.validationrule.action;

/*
 * Copyright (c) 2004-2007, University of Oslo
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

import java.util.Collection;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.oust.manager.SelectionTreeManager;

import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Margrethe Store
 * @version $Id: ValidateRunValidationAction.java 5730 2008-09-20 14:32:22Z brajesh $
 */
public class ValidateRunValidationAction 
    extends ActionSupport
{
    private static final Log LOG = LogFactory.getLog( ValidateRunValidationAction.class );
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------     
    
    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    private SelectionTreeManager selectionTreeManager;

    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------    
    
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
    
    // -------------------------------------------------------------------------
    // Output
    // -------------------------------------------------------------------------
    
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
        try
        {           
            Collection<OrganisationUnit> selectedOrganisationUnits = selectionTreeManager.getSelectedOrganisationUnits();
    
            if ( selectedOrganisationUnits.isEmpty() )
            {
                message = i18n.getString( "specify_organisationunit" );
                
                return INPUT;
            }
        }
        catch ( Exception e )
        {
            LOG.error( e.getMessage(), e );

            throw e;
        }

        Date sDate;

        if ( startDate == null || startDate.trim().length() == 0 )
        {
            message = i18n.getString( "specify_a_start_date" );

            return INPUT;
        }
        else
        {
            sDate = format.parseDate( startDate.trim() );

            if ( sDate == null )
            {
                message = i18n.getString( "enter_a_valid_start_date" );

                return INPUT;
            }
        }

        if ( endDate == null || endDate.trim().length() == 0 )
        {
            message = i18n.getString( "specify_an_ending_date" );

            return INPUT;
        }
        else
        {
            Date eDate = format.parseDate( endDate.trim() );

            if ( eDate == null )
            {
                message = i18n.getString( "enter_a_valid_ending_date" );

                return INPUT;
            }

            if ( eDate.before( sDate ) )
            {
                message = i18n.getString( "end_date_cannot_be_before_start_date" );

                return INPUT;
            }
        }
       
        message = i18n.getString( "everything_is_ok" );

        return SUCCESS;
    }
}
