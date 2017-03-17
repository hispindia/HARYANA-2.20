package org.hisp.dhis.coldchain.catalog;

import java.io.Serializable;

public class CatalogTypeAttributeOption implements Serializable
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 9052641474847384010L;
    
    private int id;
    
    private String name;

    private CatalogTypeAttribute catalogTypeAttribute;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public CatalogTypeAttributeOption()
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

        if ( !(o instanceof CatalogTypeAttributeOption) )
        {
            return false;
        }

        final CatalogTypeAttributeOption other = (CatalogTypeAttributeOption) o;

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

    public CatalogTypeAttribute getCatalogTypeAttribute()
    {
        return catalogTypeAttribute;
    }

    public void setCatalogTypeAttribute( CatalogTypeAttribute catalogTypeAttribute )
    {
        this.catalogTypeAttribute = catalogTypeAttribute;
    }

    
    
}
