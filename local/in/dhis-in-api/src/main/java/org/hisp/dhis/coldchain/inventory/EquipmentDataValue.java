package org.hisp.dhis.coldchain.inventory;

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.period.Period;

public class EquipmentDataValue  implements Serializable
{
    private EquipmentInstance equipmentInstance;

    private DataElement dataElement;
    
    private Period period;
    
    private String value;

    private String storedBy;

    private Date timestamp;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public EquipmentDataValue()
    {
    }

    public EquipmentDataValue( EquipmentInstance equipmentInstance, DataElement dataElement, Period period )
    {
        this.dataElement = dataElement;
        this.period = period;
        this.equipmentInstance = equipmentInstance;
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

        if ( !(o instanceof EquipmentDataValue) )
        {
            return false;
        }

        final EquipmentDataValue other = (EquipmentDataValue) o;

        return dataElement.equals( other.getDataElement() ) && period.equals( other.getPeriod() ) && equipmentInstance.equals( other.getEquipmentInstance() );
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + period.hashCode();
        result = result * prime + dataElement.hashCode();
        result = result * prime + equipmentInstance.hashCode();

        return result;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public EquipmentInstance getEquipmentInstance()
    {
        return equipmentInstance;
    }

    public void setEquipmentInstance( EquipmentInstance equipmentInstance )
    {
        this.equipmentInstance = equipmentInstance;
    }

    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }

    public Period getPeriod()
    {
        return period;
    }

    public void setPeriod( Period period )
    {
        this.period = period;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue( String value )
    {
        this.value = value;
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
