package org.hisp.dhis.schedule;

import java.io.Serializable;

/**
 * @author Mithilesh Kumar Thakur
 */
public class SchedulingPolicy implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * The unique identifier
     */
    private int id;
    
    
    /**
     * Name
     */
    
    private String name;
    
    /**
     * Value
     */
    
    private String value;
    
    
    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------
    
    // Default
    public SchedulingPolicy()
    {
        
    }

    public SchedulingPolicy( String name, String value )
    {
        this.name = name;
        this.value = value;
    }    
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    public int hashCode()
    {
      //int prime = 31;
      int result = 1;
      result = 31 * result + this.id;
      result = 31 * result + (this.name == null ? 0 : this.name.hashCode());
      result = 31 * result + (this.value == null ? 0 : this.value.hashCode());
      return result;
    }

    public boolean equals(Object obj)
    {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      SchedulingPolicy other = (SchedulingPolicy)obj;
      if (this.id != other.id)
        return false;
      if (this.name == null) {
        if (other.name != null)
          return false;
      } else if (!this.name.equals(other.name))
        return false;
      if (this.value == null) {
        if (other.value != null)
          return false;
      } else if (!this.value.equals(other.value))
        return false;
      return true;
    }
    
    // -------------------------------------------------------------------------
    // Logic
    // -------------------------------------------------------------------------
    
    public String toString()
    {
      return "SchedulingPolicy [id=" + this.id + ", name=" + this.name + ", value=" + this.value + "]";
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


    public String getValue()
    {
        return value;
    }


    public void setValue( String value )
    {
        this.value = value;
    }
    
}
