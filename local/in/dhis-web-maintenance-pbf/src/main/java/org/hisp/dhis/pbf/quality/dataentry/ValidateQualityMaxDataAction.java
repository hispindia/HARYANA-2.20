package org.hisp.dhis.pbf.quality.dataentry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.pbf.api.QualityMaxValue;
import org.hisp.dhis.pbf.api.QualityMaxValueService;

import com.opensymphony.xwork2.Action;

public class ValidateQualityMaxDataAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private QualityMaxValueService qualityMaxValueService;

    public void setQualityMaxValueService( QualityMaxValueService qualityMaxValueService )
    {
        this.qualityMaxValueService = qualityMaxValueService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------

    private String orgUnitId;

    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private String dataSetId;

    public void setDataSetId( String dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private String startDate;

    public void setStartDate( String startDate )
    {
        this.startDate = startDate;
    }

    private String endDate;

    public void setEndDate( String endDate )
    {
        this.endDate = endDate;
    }

    List<DataElement> dataElements = new ArrayList<DataElement>();

    public List<DataElement> getDataElements()
    {
        return dataElements;
    }

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    private Map<Integer, QualityMaxValue> qualityMaxValueMap = new HashMap<Integer, QualityMaxValue>();

    public Map<Integer, QualityMaxValue> getQualityMaxValueMap()
    {
        return qualityMaxValueMap;
    }

    private boolean message = false;

    public boolean getMessage()
    {
        return message;
    }
    
    private String maximumRange;
    
    public String getMaximumRange() 
    {
		return maximumRange;
	}

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

	public String execute()
        throws Exception
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

        Date sDate = dateFormat.parse( startDate );
        Date eDate = dateFormat.parse( endDate );
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( dataSetId ) );

        List<QualityMaxValue> qualityMaxValues = new ArrayList<QualityMaxValue>(
            qualityMaxValueService.getQuanlityMaxValues( organisationUnit, dataSet ) );
        for ( QualityMaxValue qualityMaxValue : qualityMaxValues )
        {
            if ( qualityMaxValue.getStartDate().getTime() ==  sDate.getTime() && qualityMaxValue.getEndDate().getTime() ==  eDate.getTime() )
            {
                message = message && false;
            }  
            else if (sDate.before( qualityMaxValue.getStartDate() ) && eDate.after( qualityMaxValue.getEndDate() ) )
            {            	
            	message = message || true;            	
            	//System.out.println("Start date is less and end date is greater or equal "+message);
            }
            else if (qualityMaxValue.getStartDate().getTime() >=  sDate.getTime() && sDate.getTime() <= qualityMaxValue.getEndDate().getTime() )
            {            	
            	message = message || true;
            	//System.out.println("Start date between max start date and end date "+message);
            }
            else if (qualityMaxValue.getStartDate().getTime() >=  eDate.getTime() && eDate.getTime() <= qualityMaxValue.getEndDate().getTime() )
            {            	
            	message = message || true;
            	//System.out.println("End date between max start date and end date  "+message);
            }            
            else
            {
            	message = message && false;
            }
        }
        //System.out.println("Message: "+message);
        return SUCCESS;
    }
}