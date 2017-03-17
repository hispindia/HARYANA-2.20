package org.hisp.dhis.spis;

import java.io.Serializable;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Mithilesh Kumar Thakur
 */

@JacksonXmlRootElement( localName = "criteria", namespace = DxfNamespaces.DXF_2_0 )
public class Criteria
    extends BaseIdentifiableObject
    implements Serializable

{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * Name of Criteria Required.
     */
    
    private String name;
    
    /**
     * Description of Criteria
     */
    
    private String description;

  
    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------
    
    public Criteria()
    {

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

        if ( !(o instanceof Criteria) )
        {
            return false;
        }

        final Criteria other = (Criteria) o;

        return name.equals( other.getName() );
    }
    
    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------    
    
    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public String getDescription()
    {
        return description;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

}
