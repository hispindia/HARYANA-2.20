package org.hisp.dhis.coldchain.catalog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.BaseNameableObject;
import org.hisp.dhis.dataentryform.DataEntryForm;

//public class CatalogType implements Serializable
public class CatalogType extends BaseNameableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;
    
    public static String PREFIX_CATALOG_TYPE = "Vaccines";
    
    private int id;
    
    private String name;
    
    private String description;
    
    private List<CatalogTypeAttribute> catalogTypeAttributes = new ArrayList<CatalogTypeAttribute>();
	
    private DataEntryForm dataEntryForm;
    
    private String catalogTypeImage;
    
    
    /**
     * The CatalogTypeAttributeGroup associated with the CatalogType.
     */
    private Set<CatalogTypeAttributeGroup> catalogTypeAttributeGroups = new HashSet<CatalogTypeAttributeGroup>();
    

    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------
    public CatalogType()
    {

    }
    public CatalogType( String name )
    {
        this.name = name;
    }
    
    public CatalogType( String name, String description )
    {
        this.name = name;
        this.description = description;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
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

        if ( !(o instanceof CatalogType) )
        {
            return false;
        }

        final CatalogType other = (CatalogType) o;

        return name.equals( other.getName() );
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

    public DataEntryForm getDataEntryForm()
    {
        return dataEntryForm;
    }
    public void setDataEntryForm( DataEntryForm dataEntryForm )
    {
        this.dataEntryForm = dataEntryForm;
    }
    public List<CatalogTypeAttribute> getCatalogTypeAttributes()
    {
        return catalogTypeAttributes;
    }
    public void setCatalogTypeAttributes( List<CatalogTypeAttribute> catalogTypeAttributes )
    {
        this.catalogTypeAttributes = catalogTypeAttributes;
    }
    
    public String getCatalogTypeImage()
    {
        return catalogTypeImage;
    }
    public void setCatalogTypeImage( String catalogTypeImage )
    {
        this.catalogTypeImage = catalogTypeImage;
    }
    
    public Set<CatalogTypeAttributeGroup> getCatalogTypeAttributeGroups()
    {
        return catalogTypeAttributeGroups;
    }
    public void setCatalogTypeAttributeGroups( Set<CatalogTypeAttributeGroup> catalogTypeAttributeGroups )
    {
        this.catalogTypeAttributeGroups = catalogTypeAttributeGroups;
    }
    
    
}
