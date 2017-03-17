package org.hisp.dhis.coldchain.catalog;

import java.util.Set;

import org.hisp.dhis.common.BaseNameableObject;

public class CatalogTypeAttribute extends BaseNameableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;
    
    public static final String TYPE_DATE = "DATE";

    public static final String TYPE_STRING = "TEXT";

    public static final String TYPE_INT = "NUMBER";

    public static final String TYPE_BOOL = "YES/NO";

    public static final String TYPE_COMBO = "COMBO";

    private int id;
    
    private String name;
    
    private String description;

    private String valueType;
    
    private boolean mandatory;

    private Integer noChars;
    
    /*
     * True if this CatalogTypeAttribute is for display in list
     */
    private boolean display = false;
 
    private Set<CatalogTypeAttributeOption> attributeOptions;
    
    // -------------------------------------------------------------------------
    // Default Constructors
    // -------------------------------------------------------------------------

    public CatalogTypeAttribute()
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

        if ( !(o instanceof CatalogTypeAttribute) )
        {
            return false;
        }

        final CatalogTypeAttribute other = (CatalogTypeAttribute) o;

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

    public String getValueType()
    {
        return valueType;
    }

    public void setValueType( String valueType )
    {
        this.valueType = valueType;
    }

    public boolean isMandatory()
    {
        return mandatory;
    }

    public void setMandatory( boolean mandatory )
    {
        this.mandatory = mandatory;
    }

    public Integer getNoChars()
    {
        return noChars;
    }

    public void setNoChars( Integer noChars )
    {
        this.noChars = noChars;
    }

    public Set<CatalogTypeAttributeOption> getAttributeOptions()
    {
        return attributeOptions;
    }

    public void setAttributeOptions( Set<CatalogTypeAttributeOption> attributeOptions )
    {
        this.attributeOptions = attributeOptions;
    }
    
    public boolean isDisplay()
    {
        return display;
    }

    public void setDisplay( boolean display )
    {
        this.display = display;
    }
}
