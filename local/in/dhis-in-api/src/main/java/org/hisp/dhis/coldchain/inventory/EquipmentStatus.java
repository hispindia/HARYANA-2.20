package org.hisp.dhis.coldchain.inventory;

import java.io.Serializable;
import java.util.Date;

public class EquipmentStatus
    implements Serializable
{
    public static final String STATUS_WORKING = "WORKING";

    public static final String STATUS_NOTWORKING = "NOTWORKING";

    public static final String STATUS_REPAIR = "REPAIR";

    public static final String WORKING_STATUS = "WORKING_STATUS";

    public static final String STATUS_NOT_WORKING = "Not working";

    public static final String STATUS_WORKING_WELL = "Working well";

    public static final String STATUS_WORKING_NEEDS_MAINTENANCE = "Working but needs maintenance";

    public static final String STATUS_IN_USE = "In Use";

    public static final String STATUS_NOT_IN_USE = "Not in use";

    public static final String STATUS_IN_STORE = "In store for allocation";

    public static final String STATUS_UNKNOWN = "UNKNOWN";

    private int id;

    private EquipmentInstance equipmentInstance;

    private Date reportingDate;

    private Date updationDate;

    private String status;

    private String description;

    private String storedBy;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public EquipmentStatus()
    {

    }

    public EquipmentStatus( EquipmentInstance equipmentInstance, Date reportingDate, Date updationDate, String status )
    {
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

        if ( !(o instanceof EquipmentStatus) )
        {
            return false;
        }

        final EquipmentStatus other = (EquipmentStatus) o;

        return equipmentInstance.equals( other.getEquipmentInstance() );

    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + equipmentInstance.hashCode();

        return result;
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

    public EquipmentInstance getEquipmentInstance()
    {
        return equipmentInstance;
    }

    public void setEquipmentInstance( EquipmentInstance equipmentInstance )
    {
        this.equipmentInstance = equipmentInstance;
    }

    public Date getReportingDate()
    {
        return reportingDate;
    }

    public void setReportingDate( Date reportingDate )
    {
        this.reportingDate = reportingDate;
    }

    public Date getUpdationDate()
    {
        return updationDate;
    }

    public void setUpdationDate( Date updationDate )
    {
        this.updationDate = updationDate;
    }

    public String getStatus()
    {
        return status;
    }

    public void setStatus( String status )
    {
        this.status = status;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public String getStoredBy()
    {
        return storedBy;
    }

    public void setStoredBy( String storedBy )
    {
        this.storedBy = storedBy;
    }

}
