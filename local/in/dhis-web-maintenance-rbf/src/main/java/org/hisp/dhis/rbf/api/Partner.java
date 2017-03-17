package org.hisp.dhis.rbf.api;

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.option.Option;
import org.hisp.dhis.organisationunit.OrganisationUnit;

/**
 * @author Mithilesh Kumar Thakur
 */
public class Partner implements Serializable
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private OrganisationUnit organisationUnit;
    
    private DataSet dataSet;
    
    private DataElement dataElement;
    
    private Date startDate;

    private Date endDate;
    
    // for Partner value
    private Option option;
    
    private String storedBy;

    private Date timestamp;
    
    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------
    public Partner()
    {

    }

    public Partner( OrganisationUnit organisationUnit, DataSet dataSet, DataElement dataElement, Date startDate, Date endDate )
    {
        this.organisationUnit = organisationUnit;
        this.dataSet = dataSet;
        this.dataElement = dataElement;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Partner( OrganisationUnit organisationUnit, DataSet dataSet, DataElement dataElement, Date startDate, Date endDate, Option option )
    {
        this.organisationUnit = organisationUnit;
        this.dataSet = dataSet;
        this.dataElement = dataElement;
        this.startDate = startDate;
        this.endDate = endDate;
        this.option = option;
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

        if ( !(o instanceof Partner) )
        {
            return false;
        }

        final Partner other = (Partner) o;

        return organisationUnit.equals( other.getOrganisationUnit() ) && dataSet.equals( other.getDataSet() ) && dataElement.equals( other.getDataElement() );
        
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        
        result = result * prime + organisationUnit.hashCode();
        result = result * prime + dataSet.hashCode();
        result = result * prime + dataElement.hashCode();
       
        return result;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------    
    
    public OrganisationUnit getOrganisationUnit()
    {
        return organisationUnit;
    }

    public void setOrganisationUnit( OrganisationUnit organisationUnit )
    {
        this.organisationUnit = organisationUnit;
    }

    public DataSet getDataSet()
    {
        return dataSet;
    }

    public void setDataSet( DataSet dataSet )
    {
        this.dataSet = dataSet;
    }

    public DataElement getDataElement()
    {
        return dataElement;
    }

    public void setDataElement( DataElement dataElement )
    {
        this.dataElement = dataElement;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate( Date startDate )
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate( Date endDate )
    {
        this.endDate = endDate;
    }

    public Option getOption()
    {
        return option;
    }

    public void setOption( Option option )
    {
        this.option = option;
    }

    public String getStoredBy()
    {
        return storedBy;
    }

    public void setStoredBy( String storedBy )
    {
        this.storedBy = storedBy;
    }

    public Date getTimestamp()
    {
        return timestamp;
    }    
    
    public void setTimestamp( Date timestamp )
    {
        this.timestamp = timestamp;
    }    
}
