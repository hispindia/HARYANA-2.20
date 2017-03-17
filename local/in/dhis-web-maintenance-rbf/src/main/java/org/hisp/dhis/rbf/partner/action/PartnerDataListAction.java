package org.hisp.dhis.rbf.partner.action;

import com.opensymphony.xwork2.Action;

/**
 * Created by Ganesh on 15/11/14.
 */
public class PartnerDataListAction
    implements Action
{

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

    @Override
    public String execute()
        throws Exception
    {
        return SUCCESS;
    }
}
