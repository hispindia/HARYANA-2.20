package org.hisp.dhis.rbf.quality.dataentry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.rbf.api.QualityMaxValueService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class LoadQualityMaxScoreAction implements Action
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
    
    /*
    private List<QualityMaxValue> qualityMaxValues = new ArrayList<QualityMaxValue>();
    
    public List<QualityMaxValue> getQualityMaxValues()
    {
        return qualityMaxValues;
    }
    */
    private List<String> qualityMaxValues = new ArrayList<String>();
    
    public List<String> getQualityMaxValues()
    {
        return qualityMaxValues;
    }

    private SimpleDateFormat dateFormat;
    
    public SimpleDateFormat getDateFormat()
    {
        return dateFormat;
    }

    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------


    public String execute()
        throws Exception
    {
        
        dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        
        DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( dataSetId ) );
        
        OrganisationUnitGroup orgUnitGroup = orgUnitGroupService.getOrganisationUnitGroup( Integer.parseInt( orgUnitGroupId ) );

        if ( organisationUnit == null || dataSet == null || orgUnitGroup == null )
        {
            return SUCCESS;
        }

        qualityMaxValues =  new ArrayList<String>();
        
        qualityMaxValues =  new ArrayList<String>( qualityMaxValueService.getDistinctStartDateEndDateFromQualityMaxScore( orgUnitGroup, organisationUnit, dataSet ) );
        
        
        for ( String qualityMaxValue : qualityMaxValues )
        {
            //System.out.println( qualityMaxValue  );
        }
        
        
        return SUCCESS;
    }
}