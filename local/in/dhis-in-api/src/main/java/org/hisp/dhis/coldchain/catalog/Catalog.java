package org.hisp.dhis.coldchain.catalog;

import org.hisp.dhis.common.BaseNameableObject;
//public class Catalog implements Serializable
public class Catalog extends BaseNameableObject
{
    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = -6551567526188061690L;
    
    public static final String DEFAULT_CCEMFOLDER = "CCEMIMAGES";
    public static String PREFIX_CATALOG_NAME = "catalogname";
    
    public static final String NAME_FACILITY_TYPE = "Facility Type";
    
    
    private int id;
    
    private String name;
    
    private String description;
    
    private String catalogImage;
    
    //private Blob image;

    private CatalogType catalogType;
    
   // private File image;
    
    //private byte[] image;
    

    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------
    public Catalog()
    {
        
    }

    public Catalog( String name, CatalogType catalogType )
    {
        this.name = name;
        this.catalogType = catalogType;
    }
    
    public Catalog( String name, String description, CatalogType catalogType )
    {
        this.name = name;
        this.description = description;
        this.catalogType = catalogType;
    }
    
    /*
    public Catalog( Blob image ) 
    {
        this.image = image;
    }
    
   
    public Catalog( byte[] image ) 
    {
        this.image = image;
    }
    */

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

        if ( !(o instanceof Catalog) )
        {
            return false;
        }

        final Catalog other = (Catalog) o;

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

    public CatalogType getCatalogType()
    {
        return catalogType;
    }

    public void setCatalogType( CatalogType catalogType )
    {
        this.catalogType = catalogType;
    }
    
    public String getCatalogImage()
    {
        return catalogImage;
    }

    public void setCatalogImage( String catalogImage )
    {
        this.catalogImage = catalogImage;
    }
    /*
    public File getImage()
    {
        return image;
    }

    public void setImage( File image )
    {
        this.image = image;
    }
    
    public void setBinaryStream( Blob image )
    {
        this.image = image;
    }
    
    public Blob getBinaryStream()
    {
        return image;
    }
    
    public Blob getImage()
    {
        return image;
    }

    public void setImage( Blob image )
    {
        this.image = image;
    }
    
    
    public byte[] getImage()
    {
        return image;
    }

    public void setImage( byte[] image )
    {
        this.image = image;
    }
    */

    
    
    
}
