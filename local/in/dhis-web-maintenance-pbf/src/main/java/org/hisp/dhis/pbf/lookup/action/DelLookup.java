package org.hisp.dhis.pbf.lookup.action;

import org.hisp.dhis.common.DeleteNotAllowedException;
import org.hisp.dhis.i18n.I18n;

import org.hisp.dhis.pbf.api.LookupService;

import com.opensymphony.xwork2.Action;

public class DelLookup
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LookupService lookupService;

    // -------------------------------------------------------------------------
    // Getters & setters
    // -------------------------------------------------------------------------

    public void setLookupService( LookupService lookupService )
    {
        this.lookupService = lookupService;
    }

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    // -------------------------------------------------------------------------
    // I18n
    // -------------------------------------------------------------------------

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
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
    // Action
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        try
        {
            lookupService.deleteLookup( lookupService.getLookup( id ) );

            message = i18n.getString( "delete_success" );
        }
        catch ( DeleteNotAllowedException ex )
        {
            if ( ex.getErrorCode().equals( DeleteNotAllowedException.ERROR_ASSOCIATED_BY_OTHER_OBJECTS ) )
            {
                message = i18n.getString( "object_not_deleted_associated_by_objects" ) + " " + ex.getMessage();

                return ERROR;
            }
        }

        return SUCCESS;
    }
}
