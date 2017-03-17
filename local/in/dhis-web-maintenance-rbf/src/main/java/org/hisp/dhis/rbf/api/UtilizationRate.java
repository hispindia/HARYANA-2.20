package org.hisp.dhis.rbf.api;

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;

/**
 * @author Mithilesh Kumar Thakur
 */
public class UtilizationRate implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private DataElement dataElement;
    
    private Double startRange;
    
    private Double endRange;
    
    private Double tariff;
    
    private String storedBy;

    private Date timestamp;

    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public UtilizationRate()
    {
        
    }

    public UtilizationRate( DataElement dataElement, Double startRange, Double endRange, Double tariff )
    {
        this.dataElement = dataElement;
        this.startRange = startRange;
        this.endRange = endRange;
        this.tariff = tariff;
    }
    
    public UtilizationRate( DataElement dataElement, Double startRange, Double endRange, Double tariff, String storedBy, Date timestamp )
    {
        this.dataElement = dataElement;
        this.startRange = startRange;
        this.endRange = endRange;
        this.tariff = tariff;
        this.storedBy = storedBy;
        this.timestamp = timestamp;
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

        if ( !(o instanceof UtilizationRate) )
        {
            return false;
        }

        final UtilizationRate other = (UtilizationRate) o;

        return dataElement.equals( other.getDataElement() );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + dataElement.hashCode();

        return result;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
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

    public Double getTariff()
    {
        return tariff;
    }

    public void setTariff( Double tariff )
    {
        this.tariff = tariff;
    }

    public String getStoredBy()
    {
        return storedBy;
    }

    public void setStoredBy( String storedBy )
    {
        this.storedBy = storedBy;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }

}

