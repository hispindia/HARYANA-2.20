package org.hisp.dhis.rbf.partner.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.rbf.api.Partner;
import org.hisp.dhis.rbf.api.PartnerService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class RemovePartnerAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    /*
    @Autowired
    private PBFDataValueService pbfDataValueService;
    */
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private PartnerService partnerService;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private PeriodService periodService;
    
    @Autowired
    private OptionService optionService;
    
    
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

    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        System.out.println( " Inside Delete partner" );
        
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        DataSet dataSet = dataSetService.getDataSet( dataSetId );
        
        DataElement dataElement = dataElementService.getDataElement( dataElementId );
        
        Date sDate = dateFormat.parse( startDate );
        Date eDate = dateFormat.parse( endDate );
        
        //Period period = periodService.getPeriod( periodId );
        
        //Period period = periodService.getPeriod( sDate, eDate, dataSet.getPeriodType() );
        
        /*
        List<Period> periodsBetweenDates = new ArrayList<Period>();
        
        periodsBetweenDates =  new ArrayList<Period>( periodService.getPeriodsBetweenDates( dataSet.getPeriodType(), sDate, eDate ) );
        
        */
        
        //periodService.getPeriod( arg0, arg1, arg2 )
        
        //Option option = optionService.getOption( optionSetId );
        
        Set<OrganisationUnit> partnerOrgUnits = new HashSet<OrganisationUnit>( partnerService.getPartnerOrganisationUnits( dataSetId, dataElementId, optionSetId, startDate, endDate ) );
        
        // Delete partner from PBF dataValue
        /*
        for ( OrganisationUnit organisationUnit : partnerOrgUnits )
        {
            //System.out.println( " Period Id " + period.getId() );
            
            if( periodsBetweenDates!= null  && periodsBetweenDates.size() > 0 )
            {
                for( Period period : periodsBetweenDates )
                {
                    if( period!= null )
                    {
                        //System.out.println( " Inside delete partner from PBF Data Value" );
                        // Delete partner in pbf datavalue
                        
                        //System.out.println( " Inside Delete partner PBF Data Value Period Id is : " + period.getIsoDate() );
                        
                        PBFDataValue pbfDataValue = pbfDataValueService.getPBFDataValue( organisationUnit, dataSet, period, dataElement );
                        
                        if ( pbfDataValue != null )
                        {
                            pbfDataValue.setOption( null );
                            pbfDataValue.setTimestamp( new Date() );

                            pbfDataValueService.updatePBFDataValue( pbfDataValue );
                        }
                    }
                }
            }
            
            //System.out.println( " orgUnit name -- " + organisationUnit.getName() );
        }
        */
        
        // Delete partner from partner
      
        //System.out.println( " partner source size "  + partnerOrgUnits.size() );
        
        for ( OrganisationUnit organisationUnit : partnerOrgUnits )
        {
            //System.out.println( " Inside Delete Partner from Partner Table" );
            
            Partner partner = partnerService.getPartner( organisationUnit, dataSet, dataElement, sDate, eDate );
            
            if ( partner != null )
            {
                partnerService.deletePartner( partner );
                
            }
        }
        
        //System.out.println( " Size of orgUnitList First -- " + orgUnitList.size() );
        
        return SUCCESS;
    }
    
}
