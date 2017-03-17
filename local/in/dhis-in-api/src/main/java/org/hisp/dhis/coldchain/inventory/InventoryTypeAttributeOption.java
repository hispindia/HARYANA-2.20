package org.hisp.dhis.coldchain.inventory;

import java.io.Serializable;

public class InventoryTypeAttributeOption implements Serializable
{
    private int id;
    
    private String name;

    private InventoryTypeAttribute inventoryTypeAttribute;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public InventoryTypeAttributeOption()
    {
        
    }
    
    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------
    
    @Override
    public int hashCode()
    {
        return name.hashCode();
    }

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

        if ( !(o instanceof InventoryTypeAttributeOption) )
        {
            return false;
        }

        final InventoryTypeAttributeOption other = (InventoryTypeAttributeOption) o;

        return name.equals( other.getName() );
    }

    @Override
    public String toString()
    {
        return "[" + name + "]";
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

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public InventoryTypeAttribute getInventoryTypeAttribute()
    {
        return inventoryTypeAttribute;
    }

    public void setInventoryTypeAttribute( InventoryTypeAttribute inventoryTypeAttribute )
    {
        this.inventoryTypeAttribute = inventoryTypeAttribute;
    }

}
