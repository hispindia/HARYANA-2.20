package org.hisp.dhis.pbf.lookup.action;

import org.hisp.dhis.i18n.I18n;

import org.hisp.dhis.pbf.api.Lookup;
import org.hisp.dhis.pbf.api.LookupService;

import com.opensymphony.xwork2.Action;

public class ValidateLookupAction
    implements Action
{

    private Integer lookupId;

    public void setLookupId( Integer lookupId )
    {
        this.lookupId = lookupId;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LookupService lookupService;

    public void setLookupService( LookupService lookupService )
    {
        this.lookupService = lookupService;
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
    // Execution
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {

        // ---------------------------------------------------------------------
        // Code
        // ---------------------------------------------------------------------

        name = name.trim();

        Lookup match = lookupService.getLookupByName( name );

        if ( match != null && (lookupId == null || match.getId() != lookupId.intValue()) )
        {

            message = i18n.getString( "name_in_use" );

            return INPUT;
        }

        message = i18n.getString( "everything_is_ok" );

        return SUCCESS;
    }

}
