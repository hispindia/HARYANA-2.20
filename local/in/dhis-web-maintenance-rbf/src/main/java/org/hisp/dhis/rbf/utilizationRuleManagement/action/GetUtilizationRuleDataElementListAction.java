package org.hisp.dhis.rbf.utilizationRuleManagement.action;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetUtilizationRuleDataElementListAction implements Action
{
    private final static String UTILIZATION_RULE_DATAELEMENT_ATTRIBUTE = "UTILIZATION_RULE_DATAELEMENT_ATTRIBUTE";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private DataElementService dataElementtService;
    
    public void setDataElementtService( DataElementService dataElementtService )
    {
        this.dataElementtService = dataElementtService;
    }
    
    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------

    private List<DataElement> dataElements = new ArrayList<DataElement>();

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

 
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        Constant utilizationRuleAttribute = constantService.getConstantByName( UTILIZATION_RULE_DATAELEMENT_ATTRIBUTE );
        
        List<DataElement> dataElementList = new ArrayList<DataElement>( dataElementtService.getAllDataElements() );
        for ( DataElement de : dataElementList )
        {
            Set<AttributeValue> attrValueSet = new HashSet<AttributeValue>( de.getAttributeValues() );
            for ( AttributeValue attValue : attrValueSet )
            {
                if ( attValue.getAttribute().getId() == utilizationRuleAttribute.getValue() && attValue.getValue().equalsIgnoreCase( "true" ) )
                {
                    dataElements.add( de );
                }
            }
        }
        
        return SUCCESS;
    }
}