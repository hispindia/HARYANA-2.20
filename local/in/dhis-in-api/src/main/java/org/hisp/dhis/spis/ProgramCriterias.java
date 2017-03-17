package org.hisp.dhis.spis;

import java.io.Serializable;

import org.hisp.dhis.common.DxfNamespaces;
import org.hisp.dhis.common.view.DetailedView;
import org.hisp.dhis.common.view.ExportView;
import org.hisp.dhis.program.Program;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @author Mithilesh Kumar Thakur
 */
@JacksonXmlRootElement( localName = "programCriterias", namespace = DxfNamespaces.DXF_2_0 )
public class ProgramCriterias
    implements Serializable
{

    /**
	 * 
	 */
    private static final long serialVersionUID = -7427511530535477462L;


    private Criteria criteria;
    
    private Program program;


    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------
    
    public ProgramCriterias()
    {
        
    }

    public ProgramCriterias( Criteria criteria, Program program )
    {
        this.criteria = criteria;
        this.program = program;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        
        result = result * prime + program.hashCode();
        result = result * prime + criteria.hashCode();

        return result;
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

        if ( !(o instanceof ProgramCriterias) )
        {
            return false;
        }

        final ProgramCriterias other = (ProgramCriterias) o;

        return program.equals( other.getProgram() ) && criteria.equals( other.getCriteria() );
    }


    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Criteria getCriteria()
    {
        return criteria;
    }

    public void setCriteria( Criteria criteria )
    {
        this.criteria = criteria;
    }

    
    @JsonProperty
    @JsonView( { DetailedView.class, ExportView.class } )
    @JacksonXmlProperty( namespace = DxfNamespaces.DXF_2_0 )
    public Program getProgram()
    {
        return program;
    }

    public void setProgram( Program program )
    {
        this.program = program;
    }

}