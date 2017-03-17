package org.hisp.dhis.rbf.aggregation.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.caseaggregation.CaseAggregationCondition;
import org.hisp.dhis.caseaggregation.CaseAggregationConditionService;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.rbf.api.Lookup;
import org.hisp.dhis.rbf.api.LookupService;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ShowUpdateAggregationQueryFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CaseAggregationConditionService aggregationConditionService;
    
    public void setAggregationConditionService( CaseAggregationConditionService aggregationConditionService )
    {
        this.aggregationConditionService = aggregationConditionService;
    }
    
    private LookupService lookupService;

    public void setLookupService(LookupService lookupService) 
    {
        this.lookupService = lookupService;
    }
        
    private DataElementService dataElementService;
    
    public void setDataElementService(DataElementService dataElementService) 
    {
        this.dataElementService = dataElementService;
    }

    // -------------------------------------------------------------------------
    // Input/Output Getters && Setters
    // -------------------------------------------------------------------------

    private Integer id;
    
    public void setId( Integer id )
    {
        this.id = id;
    }

    private CaseAggregationCondition aggregation;
    
    
    public CaseAggregationCondition getAggregation()
    {
        return aggregation;
    }

    private String description;
    
    public String getDescription()
    {
        return description;
    }

    private List<Lookup> lookups;
    
    public List<Lookup> getLookups() 
    {
        return lookups;
    }
        
    private List<DataElement> dataElementList;
    
    public List<DataElement> getDataElementList() 
    {
        return dataElementList;
    }
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.PBF_AGG_TYPE ) );
        Collections.sort( lookups, IdentifiableObjectNameComparator.INSTANCE );
        
        dataElementList = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
        Collections.sort( dataElementList, IdentifiableObjectNameComparator.INSTANCE );
        
        aggregation = aggregationConditionService.getCaseAggregationCondition( id );
        description = aggregationConditionService.getConditionDescription( aggregation.getAggregationExpression() );
        
        return SUCCESS;
    }
}

