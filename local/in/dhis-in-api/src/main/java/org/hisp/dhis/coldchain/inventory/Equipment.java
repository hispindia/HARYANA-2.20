package org.hisp.dhis.coldchain.inventory;

import java.io.Serializable;

public class Equipment implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;
    
    public static String PREFIX_CATALOG_NAME = "catalogname";
    
    public static String PREFIX_ORGANISATIONUNIT_NAME = "orgunitname";
    
    public static final String HEALTHFACILITY = "Health Facility";
    
    private EquipmentInstance equipmentInstance;
    
    private InventoryTypeAttribute inventoryTypeAttribute;
    
    private String value;
    
    private InventoryTypeAttributeOption inventoryTypeAttributeOption;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    public Equipment()
    {
        
    }
    public Equipment( EquipmentInstance equipmentInstance, InventoryTypeAttribute inventoryTypeAttribute, String value )
    {
        this.equipmentInstance = equipmentInstance;
        this.inventoryTypeAttribute = inventoryTypeAttribute;
        this.value = value;
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

        if ( !(o instanceof Equipment) )
        {
            return false;
        }

        final Equipment other = (Equipment) o;

        return equipmentInstance.equals( other.getEquipmentInstance() ) && inventoryTypeAttribute.equals( other.getInventoryTypeAttribute() );

    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + equipmentInstance.hashCode();
        result = result * prime + inventoryTypeAttribute.hashCode();

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
    public InventoryTypeAttribute getInventoryTypeAttribute()
    {
        return inventoryTypeAttribute;
    }
    public void setInventoryTypeAttribute( InventoryTypeAttribute inventoryTypeAttribute )
    {
        this.inventoryTypeAttribute = inventoryTypeAttribute;
    }
    public String getValue()
    {
        return value;
    }
    public void setValue( String value )
    {
        this.value = value;
    }
    public InventoryTypeAttributeOption getInventoryTypeAttributeOption()
    {
        return inventoryTypeAttributeOption;
    }
    public void setInventoryTypeAttributeOption( InventoryTypeAttributeOption inventoryTypeAttributeOption )
    {
        this.inventoryTypeAttributeOption = inventoryTypeAttributeOption;
    }
}
