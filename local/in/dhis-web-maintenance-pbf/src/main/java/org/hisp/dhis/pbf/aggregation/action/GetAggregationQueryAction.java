package org.hisp.dhis.pbf.aggregation.action;

import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetAggregationQueryAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependency
    // -------------------------------------------------------------------------

    private CaseAggregationConditionService aggregationConditionService;
    
    public void setAggregationConditionService( CaseAggregationConditionService aggregationConditionService )
    {
        this.aggregationConditionService = aggregationConditionService;
    }

    // -------------------------------------------------------------------------
    // Input && Output Getters && Setters
    // -------------------------------------------------------------------------

    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }

    private CaseAggregationCondition caseAggregation;
    
    public String getDescription()
    {
        return description;
    }

    private String description;
    
    public CaseAggregationCondition getCaseAggregation()
    {
        return caseAggregation;
    }


    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        caseAggregation = aggregationConditionService.getCaseAggregationCondition( id );

        description = aggregationConditionService.getConditionDescription( caseAggregation.getAggregationExpression() );
        
        return SUCCESS;
    }
}


