package org.hisp.dhis.dd.action.indicatorgroup;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.indicator.IndicatorGroup;
import org.hisp.dhis.indicator.IndicatorService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ValidateIndicatorGroupAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private I18n i18n;

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private Integer id;

    public void setId( Integer id )
    {
        this.id = id;
    }

    private String name;

    public void setName( String name )
    {
        this.name = name;
    }

    /*
    private String shortName;

    public void setShortName( String shortName )
    {
        this.shortName = shortName;
    }

    private String code;

    public void setCode( String code )
    {
        this.code = code;
    }
    */
    
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

    @Override
    public String execute()
    {
        if ( name != null )
        {
            List<IndicatorGroup> indicatorGroupList = new ArrayList<IndicatorGroup>( indicatorService.getIndicatorGroupByName( name ) );
            
            if( indicatorGroupList != null && indicatorGroupList.size() > 0 )
            {
                IndicatorGroup match =  indicatorGroupList.get( 0 );
                
                if ( match != null && (id == null || match.getId() != id) )
                {
                    message = i18n.getString( "name_in_use" );

                    return ERROR;
                }
            }
        }
        
        /*
        if ( shortName != null )
        {
            
            IndicatorGroup match = (IndicatorGroup) indicatorService.getIndicatorGroupByName( name );
            
            if ( match != null && (id == null || match.getId() != id) )
            {
                message = i18n.getString( "short_name_in_use" );

                return ERROR;
            }
        }

        if ( code != null && !code.trim().isEmpty() )
        {
            IndicatorGroup match = (IndicatorGroup) indicatorService.getIndicatorGroupByName( name );

            if ( match != null && (id == null || match.getId() != id) )
            {
                message = i18n.getString( "code_in_use" );

                return ERROR;
            }
        }
        */
        
        // ---------------------------------------------------------------------
        // Validation success
        // ---------------------------------------------------------------------

        message = i18n.getString( "everything_is_ok" );

        return SUCCESS;
    }
}
