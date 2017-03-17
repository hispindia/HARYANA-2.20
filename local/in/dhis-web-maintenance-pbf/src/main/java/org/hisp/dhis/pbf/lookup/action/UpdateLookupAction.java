package org.hisp.dhis.pbf.lookup.action;

import org.hisp.dhis.pbf.api.Lookup;
import org.hisp.dhis.pbf.api.LookupService;

import com.opensymphony.xwork2.Action;

public class UpdateLookupAction
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

    private int lookupId;

    public void setLookupId( int lookupId )
    {
        this.lookupId = lookupId;
    }

    public String execute()
        throws Exception
    {

        Lookup lookup = lookupService.getLookup( lookupId );

        lookup.setName( name );
        lookup.setCode( code );
        lookup.setDescription( description );
        lookup.setType( type );
        lookup.setValue( value );

        lookupService.updateLookup( lookup );

        return SUCCESS;
    }

}
