package org.hisp.dhis.rbf.api;

import java.io.Serializable;

public class QualityScorePayment implements Serializable
{

    private int id;
    
    private Double startRange;
    
    private Double endRange;
    
    private Double addQtyPayment;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public QualityScorePayment()
    {
        
    }
    
    public QualityScorePayment( Double startRange, Double endRange, Double addQtyPayment )
    {
        
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof QualityScorePayment ) )
        {
            return false;
        }

        final QualityScorePayment other = (QualityScorePayment) o;

        return startRange.equals( other.getStartRange() ) && endRange.equals( other.getEndRange() );
    }

    @Override
    public int hashCode()
    {        
        return id;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public Double getStartRange()
    {
        return startRange;
    }

    public void setStartRange( Double startRange )
    {
        this.startRange = startRange;
    }

    public Double getEndRange()
    {
        return endRange;
    }

    public void setEndRange( Double endRange )
    {
        this.endRange = endRange;
    }

    public Double getAddQtyPayment()
    {
        return addQtyPayment;
    }

    public void setAddQtyPayment( Double addQtyPayment )
    {
        this.addQtyPayment = addQtyPayment;
    }
    
}
