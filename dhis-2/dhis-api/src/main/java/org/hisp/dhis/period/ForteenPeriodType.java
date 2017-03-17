package org.hisp.dhis.period;

/*
 * Copyright (c) 2004-2012, University of Oslo
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hisp.dhis.calendar.DateTimeUnit;

/**
 * PeriodType for weekly Periods. A valid weekly Period has startDate set to
 * FRIDAY and endDate set to THURSDAY the same week, assuming FRIDAY is the first
 * day and THURSDAY is the last day of the week.
 * 
 * @author Chau Thu Tran
 * @version $Id: ForteenPeriodType.java  2012-08-07 20:10:19Z $
 */
public class ForteenPeriodType
    extends ForteenCalendarPeriodType
{
    /**
         * 
         */
    private static final long serialVersionUID = -8108178850132758311L;

        /**
     * Determines if a de-serialized file is compatible with this class.
     */
        
    private static final String ISO_FORMAT = "yyyyMMdd";

    /**
     * The name of the WeeklyPeriodType, which is "Forteen".
     */
    
    private static final String ISO8601_DURATION = "P15D";
    
    public static final String NAME = "Forteen";

    public static final int FREQUENCY_ORDER = 15;

    // -------------------------------------------------------------------------
    // PeriodType functionality
    // -------------------------------------------------------------------------

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public Period createPeriod()
    {
        return createPeriod( createCalendarInstance() );
    }

    @Override
    public Period createPeriod( Date date )
    {
        return createPeriod( createCalendarInstance( date ) );
    }

    @Override
    public Period createPeriod( Calendar cal )
    {   
        Date startDate = null;

        if( cal.get( Calendar.DAY_OF_MONTH ) <=15 )
        {
                 cal.set( Calendar.DAY_OF_MONTH, 1 );
                 startDate  = cal.getTime();
                 cal.add( Calendar.DATE, 14 );
        }
        else
        {
                 cal.set( Calendar.DAY_OF_MONTH, 16 );
                 startDate  = cal.getTime();
                 cal.set( Calendar.DAY_OF_MONTH, cal.getActualMaximum( Calendar.DAY_OF_MONTH ) );
        }
        
        return new Period( this, startDate, cal.getTime() );
    }

    @Override
    public int getFrequencyOrder()
    {
        return FREQUENCY_ORDER;
    }

    // -------------------------------------------------------------------------
    // CalendarPeriodType functionality
    // -------------------------------------------------------------------------

    @Override
    public Period getNextPeriod( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        int interval = cal.get( Calendar.DATE ) % 15;
        
        if( interval == 0)
        {
                 cal.set( Calendar.DAY_OF_MONTH, 16 );
                 cal.set( Calendar.DAY_OF_MONTH, cal.getActualMaximum( Calendar.DAY_OF_MONTH ) ); 
        }
        else
        {
                cal.set( Calendar.DAY_OF_MONTH, 1 );
                cal.add( Calendar.MONTH, 1 );
        }
        
        return createPeriod( cal );
    }

    @Override
    public Period getPreviousPeriod( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        int interval = cal.get( Calendar.DATE ) % 15;
        
        if( interval == 0)
        {
                cal.set( Calendar.DAY_OF_MONTH, 16 );
                cal.add( Calendar.MONTH, -1 );
        }
        else
        {
                cal.set( Calendar.DAY_OF_MONTH, 1 );
                cal.add( Calendar.DAY_OF_MONTH, 15 );
        }
        
        return createPeriod( cal );
    }
    
    @Override
    public List<Period> generatePeriods( Date date )
    {
        return generatePeriods( createCalendarInstance( date ) );
    }
    
    /**
     * Generates weekly Periods for the whole year in which the given Period's
     * startDate exists.
     */
    @Override
    public List<Period> generatePeriods( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        cal.set( Calendar.DAY_OF_YEAR, 1 );
        
        int year = cal.get( Calendar.YEAR );
        ArrayList<Period> periods = new ArrayList<Period>();

        while ( cal.get( Calendar.YEAR ) == year )
        {
            periods.add( createPeriod( cal ) );
            cal.add( Calendar.DAY_OF_YEAR, 15 );
        }

        return generatePeriods( cal );
    }

    /**
     * Generates the last 52 weeks where the last one is the week which the 
     * given date is inside.
     */
    public List<Period> generateRollingPeriods( Date date )
    {
        Calendar cal = createCalendarInstance( date );
        cal.set( Calendar.DAY_OF_MONTH, 1 );
        ArrayList<Period> periods = new ArrayList<Period>();
        
        return periods;
    }
    
    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private List<Period> generatePeriods( Calendar cal )
    {
        cal.set( Calendar.DAY_OF_MONTH, 1 );
         
         int year = cal.get( Calendar.YEAR );
         ArrayList<Period> periods = new ArrayList<Period>();

         while ( cal.get( Calendar.YEAR ) == year )
         {
             periods.add( createPeriod( cal ) );
             cal.add( Calendar.DAY_OF_MONTH, 15 );
         }

         return generatePeriods( cal );
    }

    @Override
    public String getIsoDate( Period period )
    {
        Calendar cal = createCalendarInstance( period.getStartDate() );
        int year = cal.get( Calendar.YEAR);
        int day = cal.get( Calendar.DAY_OF_MONTH);

        String periodString = year + "D" + day;
        return periodString;
    }

    @Override
    public Period createPeriod( String isoDate )
    {
        Calendar cal = Calendar.getInstance();
        int day = cal.get( Calendar.DAY_OF_MONTH);
        cal.set( Calendar.DAY_OF_MONTH, day );
        
        return createPeriod( cal.getTime() );
    }

    /**
     * n refers to week number, can be [1-53].
     */
    @Override
    public String getIsoFormat()
    {
        return ISO_FORMAT;
    }

    @Override
    public Date getRewindedDate( Date date, Integer rewindedPeriods )
    {
        // TODO Auto-generated method stub
        //return null;
        
        date = date != null ? date : new Date();        
        rewindedPeriods = rewindedPeriods != null ? rewindedPeriods : 1;

        Calendar cal = createCalendarInstance( date );        
        cal.add( Calendar.DAY_OF_YEAR, (rewindedPeriods * -15 ) );

        return cal.getTime();
    }

    @Override
    public String getIso8601Duration()
    {
        // TODO Auto-generated method stub
        //return null;
        
        return ISO8601_DURATION;
        
    }

    @Override
    public Period createPeriod( DateTimeUnit dateTimeUnit, org.hisp.dhis.calendar.Calendar calendar )
    {
        // TODO Auto-generated method stub
        //return null;
        
        Calendar cal = Calendar.getInstance();
        int day = cal.get( Calendar.DAY_OF_MONTH);
        cal.set( Calendar.DAY_OF_MONTH, day );
        
        return createPeriod( cal.getTime() );
    }

    @Override
    public String getIsoDate( DateTimeUnit dateTimeUnit, org.hisp.dhis.calendar.Calendar calendar )
    {
        // TODO Auto-generated method stub
        //return null;
        
        //return String.format( "%d%02d%02d", dateTimeUnit.getYear(), dateTimeUnit.getMonth(), dateTimeUnit.getDay() );
        String periodString = dateTimeUnit.getYear() + "D" + dateTimeUnit.getDay();
        return periodString;
        
    }
}
