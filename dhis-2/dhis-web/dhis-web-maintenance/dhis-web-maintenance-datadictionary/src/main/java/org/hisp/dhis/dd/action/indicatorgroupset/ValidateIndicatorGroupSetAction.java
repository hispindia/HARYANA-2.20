package org.hisp.dhis.dd.action.indicatorgroupset;



import java.util.ArrayList;
import java.util.List;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.indicator.IndicatorGroupSet;
import org.hisp.dhis.indicator.IndicatorService;
import org.springframework.beans.factory.annotation.Autowired;
import com.opensymphony.xwork2.Action;

public class ValidateIndicatorGroupSetAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private IndicatorService indicatorService;


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
          
            List<IndicatorGroupSet> indicatorGroupSetList = new ArrayList<IndicatorGroupSet>( indicatorService.getIndicatorGroupSetByName( name ) );
            
            if( indicatorGroupSetList != null && indicatorGroupSetList.size() > 0 )
            {
                IndicatorGroupSet match =  indicatorGroupSetList.get( 0 );
                
                if ( match != null && (id == null || match.getId() != id) )
                {
                    message = i18n.getString( "name_in_use" );

                    return ERROR;
                }
            }
        }
        
   
        
        // ---------------------------------------------------------------------
        // Validation success
        // ---------------------------------------------------------------------

        message = i18n.getString( "everything_is_ok" );

        return SUCCESS;
    }
}
