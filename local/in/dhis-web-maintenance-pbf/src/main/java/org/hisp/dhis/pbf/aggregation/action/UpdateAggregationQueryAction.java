package org.hisp.dhis.pbf.aggregation.action;

import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.pbf.api.Lookup;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class UpdateAggregationQueryAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CaseAggregationConditionService aggregationConditionService;

    public void setAggregationConditionService( CaseAggregationConditionService aggregationConditionService )
    {
        this.aggregationConditionService = aggregationConditionService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private DataElementCategoryService dataElementCategoryService;

    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }

    
    // -------------------------------------------------------------------------
    // Input/ Output
    // -------------------------------------------------------------------------

    /*
    private String name;
    
    public void setName( String name )
    {
        this.name = name;
    }
    */
    
    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }
    
    private String aggType;
    
    public void setAggType( String aggType )
    {
        this.aggType = aggType;
    }

    private Integer dataElementId;

    public void setDataElementId( Integer dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    private Integer pbfDataSetId;
    
    public void setPbfDataSetId( Integer pbfDataSetId )
    {
        this.pbfDataSetId = pbfDataSetId;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        DataElement dataElement = dataElementService.getDataElement( dataElementId );
        
        /*
        if( name == null &&  name.equalsIgnoreCase( "" ))
        {
            name = dataElement.getName();
        }
        
        */
        
        /**
         * TODO support for category option combo
         */
        
        CaseAggregationCondition expression = aggregationConditionService.getCaseAggregationCondition( id );
        
        if( expression != null )
        {
            if( aggType.equals( Lookup.PBF_AGG_TYPE_OVERALL_QUALITY_SCORE ) || aggType.equals( Lookup.PBF_AGG_TYPE_OVERALL_UNADJUSTED_PBF_AMOUNT ) )
            {
                String query = ""+pbfDataSetId;            
                
                expression.setName( dataElement.getName() );
                expression.setOperator( aggType );
                expression.setAggregationExpression( query );
                expression.setAggregationDataElement( dataElement );
                expression.setOptionCombo( dataElementCategoryService.getDefaultDataElementCategoryOptionCombo() );
                
                aggregationConditionService.updateCaseAggregationCondition( expression );
                
            }
        }
 
        return SUCCESS;
    }
}
