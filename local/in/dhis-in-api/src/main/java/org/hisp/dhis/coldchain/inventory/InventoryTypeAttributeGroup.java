package org.hisp.dhis.coldchain.inventory;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.BaseNameableObject;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version InventoryTypeAttributeGroup.javaMar 5, 2013 11:47:03 AM	
 */

public class InventoryTypeAttributeGroup extends BaseNameableObject
{ 
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;
    
    private int id;
    
    private String name;
    
    private String description;
    
    private InventoryType inventoryType;
    
    private List<InventoryType_Attribute> inventoryType_Attributes = new ArrayList<InventoryType_Attribute>();
    
    private Integer sortOrder;

    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public InventoryTypeAttributeGroup()
    {
        
    }
    
    public InventoryTypeAttributeGroup( String name )
    {
        this.name = name;
    }
    
    public InventoryTypeAttributeGroup( String name, String description )
    {
        this.name = name;
        this.description = description;
    }
    
    public InventoryTypeAttributeGroup( String name, String description , InventoryType inventoryType )
    {
        this.name = name;
        this.description = description;
        this.inventoryType = inventoryType;
    }
    
    public InventoryTypeAttributeGroup( String name, String description , InventoryType inventoryType, Integer sortOrder )
    {
        this.name = name;
        this.description = description;
        this.inventoryType = inventoryType;
        this.sortOrder = sortOrder;
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

        if ( !(o instanceof InventoryTypeAttributeGroup) )
        {
            return false;
        }

        final InventoryTypeAttributeGroup other = (InventoryTypeAttributeGroup) o;

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

    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public InventoryType getInventoryType()
    {
        return inventoryType;
    }

    public void setInventoryType( InventoryType inventoryType )
    {
        this.inventoryType = inventoryType;
    }

    public List<InventoryType_Attribute> getInventoryType_Attributes()
    {
        return inventoryType_Attributes;
    }

    public void setInventoryType_Attributes( List<InventoryType_Attribute> inventoryType_Attributes )
    {
        this.inventoryType_Attributes = inventoryType_Attributes;
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
