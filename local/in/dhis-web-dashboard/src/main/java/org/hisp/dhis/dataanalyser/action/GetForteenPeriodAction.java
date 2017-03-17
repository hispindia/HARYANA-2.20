package org.hisp.dhis.dataanalyser.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetForteenPeriodAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    } 
    
    private I18nFormat format;
    
    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private String yearList;
    
    public void setYearList( String yearList )
    {
        this.yearList = yearList;
    }

    private String forteenPeriodTypeName;
    
    public void setForteenPeriodTypeName( String forteenPeriodTypeName )
    {
        this.forteenPeriodTypeName = forteenPeriodTypeName;
    }

    private List<String> forteenPeriodList;
    
    public List<String> getForteenPeriodList()
    {
        return forteenPeriodList;
    }

    private List<Period> periods;
    
    public List<Period> getPeriods()
    {
        return periods;
    }
   
    String[] yearListArray;
   
    private SimpleDateFormat simpleDateFormat1;
    private SimpleDateFormat simpleDateFormat2;
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------


    public String execute() throws Exception
    {
        simpleDateFormat1 = new SimpleDateFormat( "yyyy-MM-dd" );
        simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        
        forteenPeriodList = new ArrayList<String>();
        
        //forteenPeriodTypeName = ForteenPeriodType.NAME;
        
        PeriodType periodType = periodService.getPeriodTypeByName( forteenPeriodTypeName );

        yearListArray = yearList.split( ";" ) ;

        for ( int i = 0 ; i < yearListArray.length ; i++ )
        {
            //System.out.println( yearListArray[i] );
            String selYear = yearListArray[i].split( "-" )[0];
            
            String tempStartDate = selYear+"-01-01";
            String tempEndDate = selYear+"-12-31";
            
            Date startDate = format.parseDate( tempStartDate );
            Date endDate   = format.parseDate( tempEndDate );
            
            periods = new ArrayList<Period>( periodService.getPeriodsBetweenDates( periodType, startDate, endDate ) );
            
            for ( Period period : periods )
            {
                String forteenPeriodName = simpleDateFormat1.format( period.getStartDate() ) + "To" + simpleDateFormat2.format( period.getEndDate() );
                forteenPeriodList.add( forteenPeriodName );
            }
            
        }

        return SUCCESS;   
    }
}

