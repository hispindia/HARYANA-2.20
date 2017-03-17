package org.hisp.dhis.pbf.lookup.action;

import org.hisp.dhis.pbf.api.Lookup;
import org.hisp.dhis.pbf.api.LookupService;

import com.opensymphony.xwork2.Action;

public class EditLookupFormAction
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

    private Integer lookupId;

    public void setLookupId( Integer lookupId )
    {
        this.lookupId = lookupId;
    }

    private String descrription;

    public String getDescrription()
    {
        return descrription;
    }

    private String code;

    public String getCode()
    {
        return code;
    }

    private String type;

    public String getType()
    {
        return type;
    }

    private String value;

    public String getValue()
    {
        return value;
    }

    private Lookup lookup;

    public Lookup getLookup()
    {
        return lookup;
    }

    // -------------------------------------------------------------------------
    // Execute
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {

        if ( lookupId != null )
        {
            lookup = lookupService.getLookup( lookupId );
            code = lookup.getCode();
            descrription = lookup.getDescription();
            type = lookup.getType();
            value = lookup.getValue();
        }

        return SUCCESS;
    }

}
