package org.hisp.dhis.rbf.partner.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.option.Option;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.option.OptionSet;
import org.hisp.dhis.rbf.api.Lookup;
import org.hisp.dhis.rbf.api.LookupService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * Created by Ganesh on 14/11/14.
 */
public class PartnerFundManagement
    implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private OptionService optionService;

    @Autowired
    private DataSetService dataSetService;

    @Autowired
    private LookupService lookupService;

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<DataSet> dataSets = new ArrayList<DataSet>();

    public List<DataSet> getDataSets()
    {
        return dataSets;
    }

    private List<Option> options = new ArrayList<Option>();

    public List<Option> getOptions()
    {
        return options;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    @java.lang.Override
    public String execute()
        throws Exception
    {

        Lookup partnerOptionSetLookup = lookupService.getLookupByName( Lookup.OPTION_SET_PARTNER );

        OptionSet activitesOptionSet = optionService.getOptionSet( Integer.parseInt( partnerOptionSetLookup.getValue() ) );

        if ( activitesOptionSet != null )
        {
            options = new ArrayList<Option>( activitesOptionSet.getOptions() );
            
            dataSets = new ArrayList<DataSet>( dataSetService.getAllDataSets() );
            
            List<Lookup> lookups = new ArrayList<Lookup>( lookupService.getAllLookupsByType( Lookup.DS_PBF_TYPE ) );
            
            List<DataSet> pbfDataSets = new ArrayList<DataSet>();
            
            for( Lookup lookup : lookups )
            {
                Integer dataSetId = Integer.parseInt( lookup.getValue() );
                
                DataSet dataSet = dataSetService.getDataSet( dataSetId );
                if( dataSet != null )
                {
                    pbfDataSets.add(dataSet);
                }
            }
            
            dataSets.retainAll( pbfDataSets );
            Collections.sort(dataSets);
            
        }

        return SUCCESS;
    }
}
