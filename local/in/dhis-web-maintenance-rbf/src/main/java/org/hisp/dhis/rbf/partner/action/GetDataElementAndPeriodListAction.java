package org.hisp.dhis.rbf.partner.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetDataElementAndPeriodListAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private PeriodService periodService;
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private List<DataElement> dataElements = new ArrayList<DataElement>();
    
    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    private List<Period> periods = new ArrayList<Period>();

    public List<Period> getPeriods()
    {
        return periods;
    }
    
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        DataSet dataSet = dataSetService.getDataSet( dataSetId );
        
        dataElements = new ArrayList<DataElement>( dataSet.getDataElements() );
        
        Collections.sort( dataElements );
        
        String periodTypeName = dataSet.getPeriodType().getName();
        
        PeriodType periodType = periodService.getPeriodTypeByName( periodTypeName );
        
        periods = new ArrayList<Period>( periodService.getPeriodsByPeriodType( periodType ) );  
        
        Iterator<Period> periodIterator = periods.iterator();
        while( periodIterator.hasNext() )
        {
            Period p1 = periodIterator.next();
            
            if ( p1.getStartDate().compareTo( new Date() ) > 0 )
            {
                periodIterator.remove( );
            }
            
        }

        for ( Period period : periods )
        {
            period.setName( format.formatPeriod( period ) );
        }
        
        Collections.reverse( periods );
       
        /*
        for ( DataElement de : dataElements )
        {
            System.out.println( " DataElement name -- " + de.getName() );
        }
        
        for ( Period period : periods )
        {
            System.out.println( " Period name -- " + period.getName() );
        }
        */
        
        
        //System.out.println( " Size of orgUnitList First -- " + orgUnitList.size() );
        
        
        return SUCCESS;
    }


}
