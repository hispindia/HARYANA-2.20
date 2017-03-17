package org.hisp.dhis.coldchain.inventory;

import java.io.Serializable;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version InventoryType_Attribute.java Jun 14, 2012 1:27:37 PM	
 */
public class InventoryType_Attribute implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -5670110591005778814L;
    
    
    /**
     * Part of composite key
     */
    private InventoryType inventoryType;

    /**
     * Part of composite key
     */
    private InventoryTypeAttribute inventoryTypeAttribute;
    
    private boolean display = false;
    
    private Integer sortOrder;
    
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    
    //Default Constructors
    
    public InventoryType_Attribute()
    {
        
    }
    
    
    public InventoryType_Attribute( InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute, boolean display )
    {
        this.inventoryType = inventoryType;
        this.inventoryTypeAttribute = inventoryTypeAttribute;
        this.display = display;
    }

    public InventoryType_Attribute( InventoryType inventoryType, InventoryTypeAttribute inventoryTypeAttribute, boolean display, Integer sortOrder)
    {
        this.inventoryType = inventoryType;
        this.inventoryTypeAttribute = inventoryTypeAttribute;
        this.display = display;
        this.sortOrder = sortOrder;
    }

    // -------------------------------------------------------------------------
    // hashCode, equals and toString
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        return inventoryType.hashCode() + inventoryTypeAttribute.hashCode();
    }

    @Override
    public boolean equals( Object object )
    {

        if ( object == null )
        {
            return false;
        }

        if ( getClass() != object.getClass() )
        {
            return false;
        }

        final InventoryType_Attribute other = ( InventoryType_Attribute ) object;

        return inventoryTypeAttribute.getId() == other.getInventoryTypeAttribute().getId()
            && inventoryType.getId() == other.getInventoryType().getId();
    }

    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public InventoryType getInventoryType()
    {
        return inventoryType;
    }

    public void setInventoryType( InventoryType inventoryType )
    {
        this.inventoryType = inventoryType;
    }

    public InventoryTypeAttribute getInventoryTypeAttribute()
    {
        return inventoryTypeAttribute;
    }

    public void setInventoryTypeAttribute( InventoryTypeAttribute inventoryTypeAttribute )
    {
        this.inventoryTypeAttribute = inventoryTypeAttribute;
    }

    public boolean isDisplay()
    {
        return display;
    }

    public void setDisplay( boolean display )
    {
        this.display = display;
    }

    public Integer getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder( Integer sortOrder )
    {
        this.sortOrder = sortOrder;
    }

}
