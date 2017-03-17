package org.hisp.dhis.rbf.aggregation.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.rbf.api.Lookup;
import org.hisp.dhis.rbf.api.LookupService;

import com.opensymphony.xwork2.Action;

public class AddAggregationQueryFormAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    private LookupService lookupService;

    public void setLookupService( LookupService lookupService )
    {
        this.lookupService = lookupService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // Input/ Output
    // -------------------------------------------------------------------------
    List<Lookup> lookups;

    public List<Lookup> getLookups()
    {
        return lookups;
    }

    List<DataElement> dataElementList;

    public List<DataElement> getDataElementList()
    {
        return dataElementList;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.PBF_AGG_TYPE ) );

        List<DataElement> dataElements = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
        
        dataElementList = new ArrayList<DataElement>(); 
        
        Lookup lookup = lookupService.getLookupByName( "IS_PBF_AGGREGATED_DE_ATTRIBUTE_ID" );
        
        if( lookup != null )
        {
            System.out.println( " In side Lookup " );
            
            /*
            Iterator<DataElement> iterator = dataElementList.iterator();
            while( iterator.hasNext() )
            {
                DataElement dataElement = iterator.next();            
                
                Set<AttributeValue> dataElementAttributeValues = dataElement.getAttributeValues();
                if( dataElementAttributeValues != null && dataElementAttributeValues.size() > 0 )
                {
                    for( AttributeValue deAttributeValue : dataElementAttributeValues )
                    {
                        if(  deAttributeValue.getAttribute().getId() == Integer.parseInt( lookup.getValue() ) &&  !deAttributeValue.getValue().equalsIgnoreCase( "true" )  )
                        {
                            iterator.remove();
                        }
                    }
                }
            }
            */
            
            for ( DataElement de : dataElements )
            {
                Set<AttributeValue> attrValueSet = new HashSet<AttributeValue>( de.getAttributeValues() );
                for ( AttributeValue attValue : attrValueSet )
                {
                    if ( attValue.getAttribute().getId() == Integer.parseInt( lookup.getValue() ) && attValue.getValue().equalsIgnoreCase( "true" ) )
                    {
                        dataElementList.add( de );
                    }
                }
            }           
            
        }
        
        else
        {
            dataElementList = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
        }
        
        
        return SUCCESS;
    }
}
