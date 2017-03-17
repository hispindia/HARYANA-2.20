package org.hisp.dhis.rbf.quality.dataentry;

import static org.hisp.dhis.i18n.I18nUtils.i18n;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hisp.dhis.attribute.AttributeValue;
import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.rbf.api.LookupService;
import org.hisp.dhis.rbf.api.QualityMaxValue;
import org.hisp.dhis.rbf.api.QualityMaxValueService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

public class LoadQualityMaxDetailsAction
    implements Action
{

    private final static String QUALITY_MAX_DATAELEMENT = "QUALITY_MAX_DATAELEMENT";

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

    private LookupService lookupService;

    public void setLookupService( LookupService lookupService )
    {
        this.lookupService = lookupService;
    }

    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }

    @Autowired
    private OrganisationUnitGroupService orgUnitGroupService;
    
    @Autowired
    private DataElementService dataElementService;
    
    private I18nService i18nService;

    public void setI18nService( I18nService service )
    {
        i18nService = service;
    }

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

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );

        Date sDate = dateFormat.parse( startDate );
        Date eDate = dateFormat.parse( endDate );
        Constant qualityMaxDataElement = constantService.getConstantByName( QUALITY_MAX_DATAELEMENT );

        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
        DataSet dataSet = dataSetService.getDataSet( Integer.parseInt( dataSetId ) );
        OrganisationUnitGroup orgUnitGroup = orgUnitGroupService.getOrganisationUnitGroup( Integer
            .parseInt( orgUnitGroupId ) );

        if ( organisationUnit == null || dataSet == null || orgUnitGroup == null )
        {
            return SUCCESS;
        }

        
        dataElements = new ArrayList<DataElement>();
        dataElements = new ArrayList<DataElement>( dataElementService.getAllDataElements() );
        
        List<DataElement> tempDataElementList = new ArrayList<DataElement>();
        
        List<DataElement> dataElementList = new ArrayList<DataElement>( dataSet.getDataElements() );
        for ( DataElement de : dataElementList )
        {
            Set<AttributeValue> attrValueSet = new HashSet<AttributeValue>( de.getAttributeValues() );
            for ( AttributeValue attValue : attrValueSet )
            {
                if ( attValue.getAttribute().getId() == qualityMaxDataElement.getValue() )
                {
                    tempDataElementList.add( de );
                }
            }
        }

        //dataElements.retainAll( tempDataElementList );
        
        dataElements = new ArrayList<DataElement>( geti18nDataElements( dataElementList ) );
        
        
        for ( DataElement dataElement : dataElements )
        {
            QualityMaxValue qualityMaxValue = qualityMaxValueService.getQualityMaxValue( orgUnitGroup,
                organisationUnit, dataElement, dataSet, sDate, eDate );
            if ( qualityMaxValue != null )
            {
                qualityMaxValueMap.put( dataElement.getId(), qualityMaxValue );
                //System.out.println( "In Quality Data Value" );
            }
        }
        
       
        //Collections.sort( dataElements );
        
        
        return SUCCESS;
    }
    
    public Collection<DataElement> geti18nDataElements( List<DataElement> dataElements )
    {
        return i18n( i18nService, dataElements );
    }
    
}