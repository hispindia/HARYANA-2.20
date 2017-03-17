package org.hisp.dhis.pbf.aggregation.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.pbf.api.Lookup;
import org.hisp.dhis.pbf.api.LookupService;

import com.opensymphony.xwork2.Action;

public class GetAggregationParameterAction
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

    private DataSetService dataSetService;
    
    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    // -------------------------------------------------------------------------
    // Input/ Output
    // -------------------------------------------------------------------------
    private String aggTypeId;

    public void setAggTypeId( String aggTypeId )
    {
        this.aggTypeId = aggTypeId;
    }
    
    public String getAggTypeId()
    {
        return aggTypeId;
    }

    private List<DataSet> pbfTypeDataSets = new ArrayList<DataSet>();
    
    public List<DataSet> getPbfTypeDataSets()
    {
        return pbfTypeDataSets;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        if ( aggTypeId.equals( Lookup.PBF_AGG_TYPE_OVERALL_QUALITY_SCORE ) )
        {
            List<Lookup> lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.DS_QUALITY_TYPE ) );
            
            for( Lookup lookup : lookups )
            {
                DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( lookup.getValue() ) );
                
                pbfTypeDataSets.add( dataSet );
            }
        }
        else if ( aggTypeId.equals( Lookup.PBF_AGG_TYPE_OVERALL_UNADJUSTED_PBF_AMOUNT ) )
        {
            List<Lookup> lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.DS_PBF_TYPE ) );
            
            for( Lookup lookup : lookups )
            {
                DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( lookup.getValue() ) );
                
                pbfTypeDataSets.add( dataSet );
            }
        }
        
        return SUCCESS;
    }
}
