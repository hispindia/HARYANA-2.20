package org.hisp.dhis.rbf.partner.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.option.Option;
import org.hisp.dhis.option.OptionService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.oust.manager.SelectionTreeManager;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.rbf.api.Partner;
import org.hisp.dhis.rbf.api.PartnerService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class SavePartnerDetailsResultAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private SelectionTreeManager selectionTreeManager;
    
    public void setSelectionTreeManager( SelectionTreeManager selectionTreeManager )
    {
        this.selectionTreeManager = selectionTreeManager;
    }
    /*
    private PBFDataValueService pbfDataValueService;
    
    public void setPbfDataValueService(PBFDataValueService pbfDataValueService) 
    {
        this.pbfDataValueService = pbfDataValueService;
    }
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
    
    /*
    private Integer periodId;
    
    public void setPeriodId( Integer periodId )
    {
        this.periodId = periodId;
    }
    */
    
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
        System.out.println( " Inside save partner" );
        
        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        DataSet dataSet = dataSetService.getDataSet( dataSetId );
        
        DataElement dataElement = dataElementService.getDataElement( dataElementId );
        
        Date sDate = dateFormat.parse( startDate );
        Date eDate = dateFormat.parse( endDate );
        
        //Period period = periodService.getPeriod( periodId );
        
        //Period period = periodService.getPeriod( sDate, eDate, dataSet.getPeriodType() );
        //periodService.getPeriod( arg0, arg1, arg2 )
        
        
        //List<Period> periodsBetweenDates = new ArrayList<Period>();
        
        //periodsBetweenDates =  new ArrayList<Period>( periodService.getPeriodsBetweenDates( dataSet.getPeriodType(), sDate, eDate ) );
        
        Option option = optionService.getOption( optionSetId );
        
        /*
        System.out.println( " Option name -- " + option.getName() );
        System.out.println( " dataSet name -- " + dataSet.getName() );
        System.out.println( " dataElement name -- " + dataElement.getName() );
        System.out.println( " period name -- " + period.getName() );
        */
        
        Set<OrganisationUnit> selectedOrgUnitList = new HashSet<OrganisationUnit>( selectionTreeManager.getReloadedSelectedOrganisationUnits() );
        
        //System.out.println( " selectedOrgUnitList " + selectedOrgUnitList.size() );
        
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>();
        
        for ( OrganisationUnit organisationUnit : selectedOrgUnitList )
        {
            orgUnitList.addAll( organisationUnitService.getOrganisationUnitWithChildren( organisationUnit.getId() )  );
        }
        
        //System.out.println( " Size of Children " + orgUnitList.size() );
        
        //System.out.println( " Size of Period List is  " + periodsBetweenDates.size() );
        
        
        Set<OrganisationUnit> dataSetSources = new HashSet<OrganisationUnit>( dataSet.getSources() );
        
        dataSetSources.retainAll( orgUnitList );
        
        /*
        for ( OrganisationUnit organisationUnit : dataSetSources )
        {
            if( periodsBetweenDates!= null  && periodsBetweenDates.size() > 0 )
            {
                for( Period period : periodsBetweenDates )
                {
                    if( period != null )
                    {
                        //System.out.println( " Inside add partner PBF Data Value Period Id is : " + period.getIsoDate() );
                        // save partner in pbf datavalue
                        PBFDataValue pbfDataValue = pbfDataValueService.getPBFDataValue( organisationUnit, dataSet, period, dataElement );
                        
                        if( pbfDataValue == null )
                        {
                            pbfDataValue = new PBFDataValue();
                            
                            pbfDataValue.setDataSet( dataSet );
                            pbfDataValue.setDataElement( dataElement );
                            pbfDataValue.setPeriod( period );
                            pbfDataValue.setOrganisationUnit( organisationUnit );
                            
                            pbfDataValue.setOption( option );
                            pbfDataValue.setTimestamp( new Date() );
                            
                            
                            pbfDataValueService.addPBFDataValue( pbfDataValue );
                        }

                        else
                        {
                            //System.out.println( " Inside update partner PBF Data Value Period Id is : " + period.getIsoDate() );
                            
                            pbfDataValue.setOption( option );
                            pbfDataValue.setTimestamp( new Date() );

                            pbfDataValueService.updatePBFDataValue( pbfDataValue );
                        }
                    }
                    
                }
            }
            
            //System.out.println( " orgUnit name -- " + organisationUnit.getName() );
        }
        
        */
        
        // save partnet in partner
        
        //System.out.println( " Data Set source size "  + dataSetSources.size() );
        
        for ( OrganisationUnit organisationUnit : dataSetSources )
        {
            //System.out.println( " Inside save partner Partner Table"  + dataSetSources.size() );
            Partner partner = partnerService.getPartner( organisationUnit, dataSet, dataElement, sDate, eDate );
            
            if ( partner == null )
            {
                partner = new Partner();
                
                partner.setOrganisationUnit( organisationUnit );
                partner.setDataSet( dataSet );
                partner.setDataElement( dataElement );
                partner.setOption( option );
                
                partner.setStartDate( sDate );
                partner.setEndDate( eDate );
                partner.setTimestamp( new Date() );
                partner.setStoredBy( currentUserService.getCurrentUsername() );
                
                partnerService.addPartner( partner );
                
            }
            
            else
            {
                partner.setOrganisationUnit( organisationUnit );
                partner.setDataSet( dataSet );
                partner.setDataElement( dataElement );
                partner.setOption( option );
                
                partner.setOption( option );
                
                partner.setStartDate( sDate );
                partner.setEndDate( eDate );
                partner.setTimestamp( new Date() );
                partner.setStoredBy( currentUserService.getCurrentUsername() );
                
                partnerService.updatePartner( partner );
            }
            
        }
        
        //System.out.println( " Size of orgUnitList First -- " + orgUnitList.size() );
        
        return SUCCESS;
    }
    
}
