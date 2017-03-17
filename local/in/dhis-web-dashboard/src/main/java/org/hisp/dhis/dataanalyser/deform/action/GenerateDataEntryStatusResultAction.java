package org.hisp.dhis.dataanalyser.deform.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.dataanalyser.util.DashBoardService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GenerateDataEntryStatusResultAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private DataSetService dataSetService;
    
    private DashBoardService dashBoardService;
    
    public void setDashBoardService( DashBoardService dashBoardService )
    {
        this.dashBoardService = dashBoardService;
    }

    // -------------------------------------------------------------------------
    // input / output
    // -------------------------------------------------------------------------
    
    private Integer selectedDataSetId;
    
    public void setSelectedDataSetId( Integer selectedDataSetId )
    {
        this.selectedDataSetId = selectedDataSetId;
    }
    
    private List<DataElement> dataElementList = new ArrayList<DataElement>();
  
    public List<DataElement> getDataElementList()
    {
        return dataElementList;
    }

    private Set<DataElement> dataElements = new HashSet<DataElement>();
    
    public Set<DataElement> getDataElements()
    {
        return dataElements;
    }
    
    private DataSet dataSet;
    
    public DataSet getDataSet()
    {
        return dataSet;
    }
    
    private List<DataElement> deList = new ArrayList<DataElement>();
    
    public List<DataElement> getDeList()
    {
        return deList;
    }
    
    private int dataSetMemberCount;
    
    public int getDataSetMemberCount()
    {
        return dataSetMemberCount;
    }

    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        if( selectedDataSetId != null || selectedDataSetId != -1 )
        {
            dataSet = dataSetService.getDataSet( selectedDataSetId );
            
            dataElements = new HashSet<DataElement>( dashBoardService.getDataElementsInDataEntryForm( dataSet ));
            
            dataElementList = new ArrayList<DataElement>( dataElements );
            
            deList = new ArrayList<DataElement>( dataSet.getDataElements() );
            
            deList.removeAll( dataElementList );
        }
        
        //dataSet.getDataElements().size();
        
        Collections.sort( dataElementList, new IdentifiableObjectNameComparator() );
        
        Collections.sort( deList, new IdentifiableObjectNameComparator() );
        
        dataSetMemberCount = 0;
        for ( DataElement de : dataSet.getDataElements() )
        {
            dataSetMemberCount += de.getCategoryCombo().getOptionCombos().size();
        }
        
        return SUCCESS;
    }

}
