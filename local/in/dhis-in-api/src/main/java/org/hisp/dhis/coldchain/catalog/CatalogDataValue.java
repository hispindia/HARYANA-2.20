package org.hisp.dhis.coldchain.catalog;

import java.io.Serializable;

public class CatalogDataValue implements Serializable
{

    private Catalog catalog;
    
    private CatalogTypeAttribute catalogTypeAttribute;
    
    private String value;
    
    private CatalogTypeAttributeOption catalogTypeAttributeOption;
    
    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------
    public CatalogDataValue( )
    {
        
    }
    public CatalogDataValue( Catalog catalog, CatalogTypeAttribute catalogTypeAttribute, String value )
    {
        this.catalog = catalog;
        this.catalogTypeAttribute = catalogTypeAttribute;
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

        if ( !(o instanceof CatalogDataValue) )
        {
            return false;
        }

        final CatalogDataValue other = (CatalogDataValue) o;

        return catalogTypeAttribute.equals( other.getCatalogTypeAttribute() ) && catalog.equals( other.getCatalog() );

    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + catalogTypeAttribute.hashCode();
        result = result * prime + catalog.hashCode();

        return result;
    }
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public Catalog getCatalog()
    {
        return catalog;
    }
    public void setCatalog( Catalog catalog )
    {
        this.catalog = catalog;
    }
    public CatalogTypeAttribute getCatalogTypeAttribute()
    {
        return catalogTypeAttribute;
    }
    public void setCatalogTypeAttribute( CatalogTypeAttribute catalogTypeAttribute )
    {
        this.catalogTypeAttribute = catalogTypeAttribute;
    }
    public String getValue()
    {
        return value;
    }
    public void setValue( String value )
    {
        this.value = value;
    }
    public CatalogTypeAttributeOption getCatalogTypeAttributeOption()
    {
        return catalogTypeAttributeOption;
    }
    public void setCatalogTypeAttributeOption( CatalogTypeAttributeOption catalogTypeAttributeOption )
    {
        this.catalogTypeAttributeOption = catalogTypeAttributeOption;
    }
    
}
