package org.hisp.dhis.pbf.lookup.action;

import org.hisp.dhis.pbf.api.Lookup;
import org.hisp.dhis.pbf.api.LookupService;

import com.opensymphony.xwork2.Action;

public class AddLookupAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LookupService lookupService;

    public void setLookupService( LookupService lookupService )
    {
        this.lookupService = lookupService;
    }

    // -------------------------------------------------------------------------
    // Input & output
    // -------------------------------------------------------------------------

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    private String code;

    public void setCode( String code )
    {
        this.code = code;
    }

    private String description;

    public void setDescription( String description )
    {
        this.description = description;
    }

    private String type;

    public void setType( String type )
    {
        this.type = type;
    }

    private String value;

    public void setValue( String value )
    {
        this.value = value;
    }

    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {

        Lookup lookup = new Lookup();

        lookup.setCode( code );
        lookup.setDescription( description );
        lookup.setName( name );
        lookup.setType( type );
        lookup.setValue( value );

        lookupService.addLookup( lookup );

        return SUCCESS;
    }

}
