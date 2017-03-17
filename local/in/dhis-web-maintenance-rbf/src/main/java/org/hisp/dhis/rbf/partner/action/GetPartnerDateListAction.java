package org.hisp.dhis.rbf.partner.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.rbf.api.PartnerService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GetPartnerDateListAction implements Action
{

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private PartnerService partnerService;

    // -------------------------------------------------------------------------
    // Input & Output Getter and Setter
    // -------------------------------------------------------------------------

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }
    
    public Integer getDataSetId()
    {
        return dataSetId;
    }

    private Integer optionSetId;

    public void setOptionSetId( Integer optionSetId )
    {
        this.optionSetId = optionSetId;
    }

    public Integer getOptionSetId()
    {
        return optionSetId;
    }
    
    private Integer dataElementId;

    public void setDataElementId( Integer dataElementId )
    {
        this.dataElementId = dataElementId;
    }

    public Integer getDataElementId()
    {
        return dataElementId;
    }
    
    private List<String> partnerDateList = new ArrayList<String>();
    
    public List<String> getPartnerDateList()
    {
        return partnerDateList;
    }
    
    private Map<String, Integer> partnerOrgUnitCountMap = new HashMap<String, Integer>();
    
    public Map<String, Integer> getPartnerOrgUnitCountMap()
    {
        return partnerOrgUnitCountMap;
    }
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        partnerDateList = new ArrayList<String>();
        partnerOrgUnitCountMap = new HashMap<String, Integer>();
        
        if( dataSetId != null && optionSetId != null && dataElementId != null )
        {
            partnerDateList = new ArrayList<String>( partnerService.getStartAndEndDate( dataSetId, dataElementId, optionSetId ) );
            
            partnerOrgUnitCountMap = new HashMap<String, Integer>( partnerService.getOrgUnitCountFromPartner( dataSetId, dataElementId, optionSetId ) );
            
            //this.paging = createPaging( partnerDateList.size() );
            //partnerDateList = getBlockElement( partnerDateList, paging.getStartPos(), paging.getPageSize() );
            
        }
        
        return SUCCESS;
    }
}

