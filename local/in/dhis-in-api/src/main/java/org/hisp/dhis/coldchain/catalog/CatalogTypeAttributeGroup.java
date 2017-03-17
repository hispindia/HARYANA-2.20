package org.hisp.dhis.coldchain.catalog;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.common.BaseNameableObject;

/**
 * @author Mithilesh Kumar Thakur
 *
 * @version CatalogTypeAttributeGroup.javaOct 9, 2012 2:12:38 PM	
 */
//public class CatalogTypeAttributeGroup implements Serializable
public class CatalogTypeAttributeGroup extends BaseNameableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;
    
    private int id;
    
    private String name;
    
    private String description;
    
    private CatalogType catalogType;
    
    private List<CatalogTypeAttribute> catalogTypeAttributes = new ArrayList<CatalogTypeAttribute>();
    
    private Integer sortOrder;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public CatalogTypeAttributeGroup()
    {
        
    }
    
    public CatalogTypeAttributeGroup( String name )
    {
        this.name = name;
    }
    
    public CatalogTypeAttributeGroup( String name, String description )
    {
        this.name = name;
        this.description = description;
    }
    
    public CatalogTypeAttributeGroup( String name, String description , CatalogType catalogType )
    {
        this.name = name;
        this.description = description;
        this.catalogType = catalogType;
    }
    
    public CatalogTypeAttributeGroup( String name, String description , CatalogType catalogType, Integer sortOrder )
    {
        this.name = name;
        this.description = description;
        this.catalogType = catalogType;
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

        if ( !(o instanceof CatalogTypeAttributeGroup) )
        {
            return false;
        }

        final CatalogTypeAttributeGroup other = (CatalogTypeAttributeGroup) o;

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

    public CatalogType getCatalogType()
    {
        return catalogType;
    }

    public void setCatalogType( CatalogType catalogType )
    {
        this.catalogType = catalogType;
    }

    public List<CatalogTypeAttribute> getCatalogTypeAttributes()
    {
        return catalogTypeAttributes;
    }

    public void setCatalogTypeAttributes( List<CatalogTypeAttribute> catalogTypeAttributes )
    {
        this.catalogTypeAttributes = catalogTypeAttributes;
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
