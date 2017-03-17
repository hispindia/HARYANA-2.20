package org.hisp.dhis.spis;

import java.io.Serializable;

import org.hisp.dhis.common.BaseIdentifiableObject;
import org.hisp.dhis.trackedentity.TrackedEntityAttribute;

/**
 * @author Mithilesh Kumar Thakur
 */

public class CriteriaValue
    extends BaseIdentifiableObject
    implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private TrackedEntityAttribute tea;

    private Criteria criteria;

    private String operator;

    private String validationValue;

    
    
    // -------------------------------------------------------------------------
    // Contructors
    // -------------------------------------------------------------------------
    
    public CriteriaValue()
    {

    }
    
    public CriteriaValue( Criteria criteria, String operator, String validationValue )
    {
        this.criteria = criteria;
        this.operator = operator;
        this.validationValue = validationValue;
    }
    
    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        
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

        if ( !(o instanceof CriteriaValue) )
        {
            return false;
        }

        final CriteriaValue other = (CriteriaValue) o;

        return criteria.equals( other.getCriteria() );
    }

    
    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------
    
    public Criteria getCriteria()
    {
        return criteria;
    }

    public void setCriteria( Criteria criteria )
    {
        this.criteria = criteria;
    }

    public TrackedEntityAttribute getTea()
    {
        return tea;
    }

    public void setTea( TrackedEntityAttribute tea )
    {
        this.tea = tea;
    }

    
    public String getOperator()
    {
        return operator;
    }

    public void setOperator( String operator )
    {
        this.operator = operator;
    }

    public String getValidationValue()
    {
        return validationValue;
    }

    public void setValidationValue( String validationValue )
    {
        this.validationValue = validationValue;
    }

}
