package org.hisp.dhis.excelimport.linelisting.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.period.comparator.PeriodComparator;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */

public class LineListingExcelImportFormAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    // -------------------------------------------------------------------------
    // Constants
    // -------------------------------------------------------------------------

    private final int ALL = 0;

    public int getALL()
    {
        return ALL;
    }

   // private String raFolderName;

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    private String message;

    public void setMessage( String message )
    {
        this.message = message;
    }
    
    private List<Period> monthlyPeriods;

    public List<Period> getMonthlyPeriods()
    {
        return monthlyPeriods;
    }

    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    private PeriodType monthlyPeriodType;

    public PeriodType getMonthlyPeriodType()
    {
        return monthlyPeriodType;
    }
    
    private List<PeriodType> periodTypes;

    public List<PeriodType> getPeriodTypes()
    {
        return periodTypes;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {

        /* Monthly Periods */
        monthlyPeriodType = new MonthlyPeriodType();
        periodTypes = new ArrayList<PeriodType>();
        
        periodTypes.add( monthlyPeriodType );
        
        monthlyPeriods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( monthlyPeriodType ) );
        Iterator<Period> periodIterator = monthlyPeriods.iterator();
        while ( periodIterator.hasNext() )
        {
            Period p1 = periodIterator.next();

            if ( p1.getStartDate().compareTo( new Date() ) > 0 )
            {
                periodIterator.remove();
            }

        }
        
        Collections.sort( monthlyPeriods, new PeriodComparator() );
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        System.out.println(message);
        return SUCCESS;
    }

}

