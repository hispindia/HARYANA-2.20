package org.hisp.dhis.rbf.quality.dataentry;

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
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.rbf.api.QualityMaxValue;
import org.hisp.dhis.rbf.api.QualityMaxValueService;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    private OrganisationUnitGroupService orgUnitGroupService;

    // -------------------------------------------------------------------------
    // Input / Output
    // -------------------------------------------------------------------------

    private String orgUnitId;

    public void setOrgUnitId( String orgUnitId )
    {
        this.orgUnitId = orgUnitId;
    }

    private String orgUnitGroupId;

    public void setOrgUnitGroupId( String orgUnitGroupId )
    {
        this.orgUnitGroupId = orgUnitGroupId;
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
        OrganisationUnitGroup orgUnitGroup = orgUnitGroupService.getOrganisationUnitGroup( Integer.parseInt( orgUnitGroupId ) );

        List<QualityMaxValue> qualityMaxValues = new ArrayList<QualityMaxValue>( qualityMaxValueService.getQuanlityMaxValues( orgUnitGroup, organisationUnit, dataSet ) );
        
        
        String startDateValue = qualityMaxValueService.getQuanlityMaxValueStartDateEndDate(  orgUnitGroup.getId(), organisationUnit.getId(), dataSet.getId(), startDate );
        
        String endDateValue = qualityMaxValueService.getQuanlityMaxValueStartDateEndDate( orgUnitGroup.getId(), organisationUnit.getId(), dataSet.getId(), endDate );
        
        //System.out.println(  startDateValue + " -- " + endDateValue );
        
        for ( QualityMaxValue qualityMaxValue : qualityMaxValues )
        {
            if ( qualityMaxValue.getStartDate().getTime() == sDate.getTime() && qualityMaxValue.getEndDate().getTime() == eDate.getTime() )
            {
                message = message && false;
                 
                //System.out.println( " 0 . Start date and end date are equal " + message );
            }
            
            else if ( startDateValue != null )
            {
                message = message || true;
                //System.out.println( " 1 . Start Date Value " + startDateValue +" -- "+ message );
            }
            
            else if ( endDateValue != null )
            {
                message = message || true;
                //System.out.println( " 2 . End Date Value " + endDateValue +" -- "+ message );
            }            
            
            /*
            else if ( sDate.before( qualityMaxValue.getStartDate() ) && eDate.after( qualityMaxValue.getEndDate() ) )
            {
                message = message || true;
                System.out.println(" 1 . Start date is less and end date is greater or equal " + message);
            }
            
            else if ( qualityMaxValue.getStartDate().getTime() >= sDate.getTime() && sDate.getTime() <= qualityMaxValue.getEndDate().getTime() )
            {
                message = message || true;
                System.out.println(" 2 . Start date between max start date and end date " + message);
            }
            
            else if ( qualityMaxValue.getStartDate().getTime() >= eDate.getTime() && eDate.getTime() <= qualityMaxValue.getEndDate().getTime() )
            {
                message = message || true;
                System.out.println(" 3 . End date between max start date and end date  " + message);
            }
            
            else if ( sDate.getTime() >= qualityMaxValue.getStartDate().getTime() && eDate.getTime() < qualityMaxValue.getEndDate().getTime() )
            {
                message = message || true;
                System.out.println(" 4 . Start date is greater or equal and end date is less " + message);
            }
            
            else if ( sDate.getTime() > qualityMaxValue.getStartDate().getTime() && eDate.getTime() <= qualityMaxValue.getEndDate().getTime() )
            {
                message = message || true;
                System.out.println(" 5 . Start date is greater and end date is less or equal " + message);
            }
            
            else
            {
                message = message && false;
                
                System.out.println( " 6 . Start date " + sDate + " and end date " + sDate +" -- "+  message );
            }
            */
            
        }
        
        // System.out.println("Message: "+message);
        return SUCCESS;
    }
}