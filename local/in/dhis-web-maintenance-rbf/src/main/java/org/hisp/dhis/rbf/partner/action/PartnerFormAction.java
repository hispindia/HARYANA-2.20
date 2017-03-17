package org.hisp.dhis.rbf.partner.action;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.option.Option;
import org.hisp.dhis.option.OptionService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class PartnerFormAction implements Action
{
    //private final String OPTION_SET_PARTNER = "Partner";
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    @Autowired
    private OptionService optionService;
    
    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private DataElementService dataElementService;
    
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private Integer optionSetId;

    public void setOptionSetId( Integer optionSetId )
    {
        this.optionSetId = optionSetId;
    }

    private Integer dataElementId;

    public void setDataElementId( Integer dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    private DataSet dataSet;
    
    public DataSet getDataSet()
    {
        return dataSet;
    }

    private DataElement dataElement;
    
    public DataElement getDataElement()
    {
        return dataElement;
    }

    private Option option;
    
    public Option getOption()
    {
        return option;
    }
    
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        /*
        Lookup partnerOptionSetLookup = lookupService.getLookupByName( Lookup.OPTION_SET_PARTNER );
        
        OptionSet activitesOptionSet = optionService.getOptionSet( Integer.parseInt( partnerOptionSetLookup.getValue() ) );
        
        if( activitesOptionSet != null )
        {
            options = new ArrayList<Option>( activitesOptionSet.getOptions() );
        }
        
        dataSets = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
        */
        
        
        dataSet = dataSetService.getDataSet( dataSetId );
        
        dataElement = dataElementService.getDataElement( dataElementId );
        
        option = optionService.getOption( optionSetId );
        
        
        /*
        for ( Option option : options )
        {       
            option.getId();
            
            System.out.println( " Option Id -- " + option.getId() + " Option name -- " + option.getName() );
        }
        */
        
        return SUCCESS;
    }


 





}
