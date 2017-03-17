package org.hisp.dhis.reports.smsscheduling.action;

import static org.hisp.dhis.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.util.TextUtils.getCommaDelimitedString;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.indicator.Indicator;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.user.UserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class ScheduleSMSAction implements Action
{
    public static final String ADVERSE_EVENT_DATAELEMENT_GROUP_ID = "ADVERSE_EVENT_DATAELEMENT_GROUP_ID";//
    public static final String SMS_USER_GROUP_ID = "SMS_USER_GROUP_ID";//
    
    public static final String WEBSERVICE_INDICATOR_DATAELEMENT_GROUP_ID = "WEBSERVICE_INDICATOR_DATAELEMENT_GROUP_ID";
    public static final String WEBSERVICES_INDICATOR_GROUP_ID = "WEBSERVICES_INDICATOR_GROUP_ID";
    
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    @Autowired
    private OrganisationUnitService organisationUnitService;
    
    @Autowired
    private ConstantService constantService;
    
    @Autowired
    private UserGroupService userGroupService;
    
    @Autowired
    private DataElementService dataElementService;
    
    @Autowired
    private DataElementCategoryService dataElementCategoryService;
    
    @Autowired
    private PeriodService periodService;
    
    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
    private IndicatorService indicatorService;
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------
    
    private SimpleDateFormat simpleDateFormat;
    private String complateDate = "";
    private Period currentperiod;
    
    // -------------------------------------------------------------------------
    // Action
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        //get current date time with Date()
        Date date = new Date();
        System.out.println(timeFormat.format(date));
        
        String currentDate = simpleDateFormat.format( date ).split( "-" )[2];
        String currentHour = timeFormat.format(date).split( ":" )[0];
        
        System.out.println( currentDate + " --- " + currentHour );
        
        //boolean runningStatus = true;
        
        if ( currentHour.equalsIgnoreCase( "17" ) )
        {
            scheduledAdverseEventSMS();
        }

        if ( (currentDate.equalsIgnoreCase( "19" ) || currentDate.equalsIgnoreCase( "20" ) || currentDate.equalsIgnoreCase( "21" )) && currentHour.equalsIgnoreCase( "10" ) )
        {
            scheduledNonReportingFacilitySMS();
        }
        
        if ( ( currentDate.equalsIgnoreCase( "15" )|| ( currentDate.equalsIgnoreCase( "20" ))) && currentHour.equalsIgnoreCase( "17" ) )
        {
            scheduledKeyPerformanceIndicatorsSMS();
        }
        
        return SUCCESS;
    }
    
    // -------------------------------------------------------------------------
    // Support methods 
    // -------------------------------------------------------------------------
    
    // Key Performance Indicators Scheduler
    //@Scheduled(cron="*/2 * * * * MON-FRI")
    public void scheduledKeyPerformanceIndicatorsSMS() throws IOException
    {
        System.out.println(" Key Performance Indicators Scheduler Started at : " + new Date() );
        
        BulkSMSHttpInterface bulkSMSHTTPInterface = new BulkSMSHttpInterface();
        
        simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        complateDate = simpleDateFormat.format( new Date() );
        
        String isoPeriodString = complateDate.split( "-" )[0] + complateDate.split( "-" )[1];
        
        currentperiod = periodService.reloadIsoPeriod( isoPeriodString );
        
        SimpleDateFormat monthFormat = new SimpleDateFormat();
        monthFormat = new SimpleDateFormat("MMM-yyyy");
        
        
        List<String> mobileNumbers = new ArrayList<String>();
        mobileNumbers = new ArrayList<String>( getAllUsersMobileNumber() );
        
        Map<String, String> indicatorValueMap = new HashMap<String, String>();
        indicatorValueMap = new HashMap<String, String>( getIndicatorValueMap() );
        
        List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( getOrganisationUnitsForKeyPerformanceIndicator() );
        List<Indicator> indicators = new ArrayList<Indicator>( getIndicators() );
        String message = "";
        
        for( OrganisationUnit orgUnit : organisationUnits )
        {
            String value1 = indicatorValueMap.get( orgUnit.getId()+":" + 3 );
            String value2 = indicatorValueMap.get( orgUnit.getId()+":" + 4 );
            String value3 = indicatorValueMap.get( orgUnit.getId()+":" + 6 );
            
            String indicatorVlaue1 = "";
            String indicatorVlaue2 = "";
            String indicatorVlaue3 = "";
            
            if( value1 != null )
            {
                indicatorVlaue1 = value1;
            }
            
            if( value1 != null && value2 != null )
            {
                double percentatgeValue = ((double) Double.parseDouble(  value2 ) / (double) Double.parseDouble(  value1 ) ) * 100.0;
                
                percentatgeValue = Math.round( percentatgeValue * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
                
                indicatorVlaue2 = ""+percentatgeValue;
            }
            
            if( value1 != null && value3 != null )
            {
                double percentatgeValue = ((double) Double.parseDouble(  value3 ) / (double) Double.parseDouble(  value1 ) ) * 100.0;
                
                percentatgeValue = Math.round( percentatgeValue * Math.pow( 10, 2 ) ) / Math.pow( 10, 2 );
                
                indicatorVlaue3 = ""+percentatgeValue;
            }
            
            for( Indicator indicator : indicators )
            {
                for( String phoneNoAndName : mobileNumbers )
                { 
                    String phoneNo = phoneNoAndName.split( ":" )[0];
                    String userName = phoneNoAndName.split( ":" )[1];
                    
                    if( indicator.getId() == 322 && !indicatorVlaue1.equalsIgnoreCase( "" ) )
                    {
                        message = " Dear " + userName + " greeting from single reporting HMIS server. Key performance indicator of  " + orgUnit.getName() + " for " + monthFormat.format( currentperiod.getStartDate() ) + " " + indicator.getName() + " " + indicatorVlaue1;    
                        
                    }
                    else if( indicator.getId() == 35 && !indicatorVlaue2.equalsIgnoreCase( "" )  )
                    {
                        message = " Dear " + userName + " greeting from single reporting HMIS server. Key performance indicator of  " + orgUnit.getName() + " for " + monthFormat.format( currentperiod.getStartDate() ) + " " + indicator.getName() + " " + indicatorVlaue2;    
                    }
                    
                    else if( indicator.getId() == 38 && !indicatorVlaue3.equalsIgnoreCase( "" )  )
                    {
                        message = " Dear " + userName + " greeting from single reporting HMIS server. Key performance indicator of  " + orgUnit.getName() + " for " + monthFormat.format( currentperiod.getStartDate() ) + " " + indicator.getName() + " " + indicatorVlaue3;    
                    }
                    
                    //System.out.println( orgUnit.getId() + "  " + indicator.getId() + "  " + phoneNo +  " -------- > " + message );
                    bulkSMSHTTPInterface.sendMessage( message, phoneNo );
                }
            }
        }
        
        System.out.println(" Key Performance Indicators Scheduler Ended at : " + new Date() );
    }
    
    
    // Adverse EventSMS Scheduler
    public void scheduledAdverseEventSMS() throws IOException
    {
        System.out.println(" Adverse Event SMS Scheduler Started at : " + new Date() );
        
        simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        complateDate = simpleDateFormat.format( new Date() );
        
        String isoPeriodString = complateDate.split( "-" )[0] + complateDate.split( "-" )[1];
        
        currentperiod = periodService.reloadIsoPeriod( isoPeriodString );
                
        //monthFormat.format( currentperiod.getStartDate() );
        
        BulkSMSHttpInterface bulkSMSHTTPInterface = new BulkSMSHttpInterface();
        
        //System.out.println(" SMS Users List -------- > " + smsUsers );
       
        //List<String> mobileNumbers = new ArrayList<String>();
        
        //mobileNumbers = new ArrayList<String>( getAllUsersMobileNumber() );
        
        List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>( getOrganisationUnits());
        List<Integer> orgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, organisationUnits ) );
        String orgUnitsByComma = getCommaDelimitedString( orgUnitTreeIds );
        
        Map<String, List<String>> userPhoneNumberMap = new HashMap<String,  List<String>>( getPhoneNumbers( orgUnitsByComma ));
        
        //List<Period> periods = new ArrayList<Period>( getPeriods() );
        
        /*
        SimpleDateFormat monthFormat = new SimpleDateFormat();
        monthFormat = new SimpleDateFormat("MMM-yyyy");
        List<String> periodAndOrgUnitIds = new ArrayList<String>( getPeriodAndOrganisationUnitIds() );
        
        List<DataElement> dataElementList = new ArrayList<DataElement>();
        dataElementList = new ArrayList<DataElement>( getDataElements() );
        
        Map<Integer, List<Integer>> dataElementMapWithCategoryOption = new HashMap<Integer,  List<Integer>>( getDataElementMapWithCategoryOption() );
        
        Map<String, String> dataValueMap = new HashMap<String, String>();
        dataValueMap = new HashMap<String, String>( getDataValueMap() );
        */
        
        Map<String, List<String>> msgContentMap = new HashMap<String, List<String>>();
        msgContentMap = new HashMap<String, List<String>>( getMessageContent() );
        
        for( OrganisationUnit orgUnit : organisationUnits )
        {
            //String message = msgContentMap.get( ""+orgUnit.getId() );
            
            List<String> messageList = new ArrayList<String>( msgContentMap.get( ""+orgUnit.getId() ));
            //int i = 1;
            for( String message : messageList )
            {
                List<String> mobileNumbers = new ArrayList<String>( userPhoneNumberMap.get( ""+orgUnit.getId() ));
                
                //System.out.println( i + "  " +orgUnit.getId() +   " -------- > " + message );
                //bulkSMSHTTPInterface.sendMessage( message, "9560163563" );
                //i = i +1;
                
                for( String phoneNo : mobileNumbers )
                {
                    //System.out.println( i + "  " +orgUnit.getId() +  " -- " + phoneNo + " -------- > " + message );
                    bulkSMSHTTPInterface.sendMessage( message, phoneNo );
                    //i = i +1;
                }
                
                
            }
        }
        
        
        
        //System.out.println(" 1 periodAndOrgUnitIds Size is  -------- > " + periodAndOrgUnitIds.size() );
        /*
        String message = "";
        int i = 1;
        for( String peAndOgUnitId : periodAndOrgUnitIds )
        {
            String periodId = peAndOgUnitId.split( ":" )[0];
            String sourceId = peAndOgUnitId.split( ":" )[1];
            String parentId = peAndOgUnitId.split( ":" )[2];
            
            Period period = periodService.getPeriod( Integer.parseInt( periodId ) );
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( sourceId ) );
            OrganisationUnit parent = organisationUnitService.getOrganisationUnit( Integer.parseInt( parentId ) );
            
            for( DataElement dataElement : dataElementList )
            {
                List<Integer> coIds = new ArrayList<Integer>( dataElementMapWithCategoryOption.get( dataElement.getId() ));
                
                for( Integer coId : coIds )
                {
                    DataElementCategoryOption categoryOption = dataElementCategoryService.getDataElementCategoryOption( coId );
                    
                    //categoryOption.getName();
                    
                    String mapKey = dataElement.getId() + ":" + coId + ":" + periodId+ ":" + sourceId;
                    
                    String dataValue = dataValueMap.get( mapKey );
                    
                    //System.out.println( dataElement.getName() +  " -> " + categoryOption.getName() +  " -> " + dataValue  );
                    //System.out.println(  orgUnit.getShortName() );
                    
                    //System.out.println( orgUnit.getParent().getName() );
                    
                    if( dataValue != null && Integer.parseInt( dataValue ) > 0 )
                    {
                        //System.out.println( dataElement.getName() +  " -> " + categoryOption.getName() +  " -> " + dataValue +  " -> " + orgUnit.getShortName() );
                        
                        if( categoryOption.getName().equalsIgnoreCase( "default" ))
                        {
                            message = " Schedule SMS : " + dataValue + " " + dataElement.getShortName() + " identified for " + orgUnit.getShortName() + ", " + monthFormat.format( period.getStartDate() ) + ", " + parent.getName();
                        }
                        
                        else
                        {
                            message = " Schedule SMS : " + dataValue + " " + dataElement.getShortName() + " " + categoryOption.getName()  + " identified for " + orgUnit.getShortName() + ", " + monthFormat.format( period.getStartDate() )+ ", " + parent.getName();
                        }
                        
                        System.out.println( i + "--" + message );
                        
                        
                        //message = " Schedule SMS : " + dataValue + " " + dataElement.getShortName() + " identified for " + orgUnit.getShortName() + ", " + monthFormat.format( period.getStartDate() );
                        
                        for( String phoneNo : mobileNumbers )
                        {
                            //System.out.println( phoneNo +  " -------- > " + message );
                            bulkSMSHTTPInterface.sendMessage( message, phoneNo );
                        }
                        
                        i = i +1;
                    }
                   
                }
                
            }
        }
        */
        
       
        /*
        System.out.println(" 1 SMS Users List -------- > " + mobileNumbers );
        
        System.out.println(" complateDate -------- > " + complateDate + " Complate Date -------- > " + currentperiod.getId() + " -- " + currentperiod.getIsoDate() );
        
        Map<String, String> dataValueMap = new HashMap<String, String>();
        dataValueMap = new HashMap<String, String>( getDataValueMap() );
        
        System.out.println(" 3 Value Map Size is  -------- > " + dataValueMap.size() );
        
        String message = "";
        
        List<DataElement> dataElementList = new ArrayList<DataElement>();
        dataElementList = new ArrayList<DataElement>( getDataElements() );
        for( DataElement dataElement : dataElementList )
        {
            for( DataElementCategoryOption co : dataElement.getCategoryCombo().getCategoryOptions() )
            {
                System.out.println( dataElement.getName() + " -------- > "+  dataElement.getId()  +" -------- > " + co.getId()  +" -------- > " + co.getName() );
            }
            
            dataElement.getCategoryCombo().getCategoryOptions().size();
            
            if( dataValueMap.get( dataElement.getId() ) != null )
            {
                String completeValue = dataValueMap.get( dataElement.getId() );
                String periodId = completeValue.split( ":" )[0];
                String sourceId = completeValue.split( ":" )[1];
                String dataValue = completeValue.split( ":" )[2];
                String lastUpdateDate = completeValue.split( ":" )[3];
                
                Period period = periodService.getPeriod( Integer.parseInt( periodId ) );
                OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( sourceId ) );
                
                System.out.println( lastUpdateDate +  " -------- > " + complateDate );
                
                if( lastUpdateDate.equalsIgnoreCase( complateDate ) || lastUpdateDate.endsWith( complateDate ) )
                {
                    System.out.println(" Key is -------- > " + dataElement.getId() + " Value is -------- > " + dataValueMap.get( dataElement.getId() ));
                    
                    message = " Schedule SMS : " + dataValue + " " + dataElement.getShortName() + " identified for " + organisationUnit.getShortName() + ", " + monthFormat.format( period.getStartDate() );
                    
                    for( String phoneNo : mobileNumbers )
                    {
                        //System.out.println( phoneNo +  " -------- > " + message );
                        bulkSMSHTTPInterface.sendMessage( message, phoneNo );
                    }
                    
                }
            }
           
            //System.out.println(" Key is -------- > " + dataElement.getId() + " Value is -------- > " + dataValueMap.get( dataElement.getId() ));
        }
        
        // Adverse Event DataElement
        /*
        Constant adverseEventDataElementGroupConstant = constantService.getConstantByName( ADVERSE_EVENT_DATAELEMENT_GROUP_ID );
        
        DataElementGroup dataElementGroup = dataElementService.getDataElementGroup( (int) adverseEventDataElementGroupConstant.getValue() );
        List<DataElement> adverseEventDEs = new ArrayList<DataElement>( dataElementGroup.getMembers() );
        
        List<Integer> dataElementIds = new ArrayList<Integer>( getIdentifiers( DataElement.class, adverseEventDEs ) );
        String dataElementIdsByComma = getCommaDelimitedString( dataElementIds );
        */
        
        System.out.println(" Adverse Event SMS Scheduler Ended at : " + new Date() );
        
    }
    
    // Non Reporting SMS Scheduler
    public void scheduledNonReportingFacilitySMS() throws IOException
    {
        System.out.println(" NonReportingFacility SMS Scheduler Started at : " + new Date() );
        
        BulkSMSHttpInterface bulkSMSHTTPInterface = new BulkSMSHttpInterface();
        
        simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        complateDate = simpleDateFormat.format( new Date() );
        
        String isoPeriodString = complateDate.split( "-" )[0] + complateDate.split( "-" )[1];
        
        currentperiod = periodService.reloadIsoPeriod( isoPeriodString );
        
        SimpleDateFormat monthFormat = new SimpleDateFormat();
        monthFormat = new SimpleDateFormat("MMM-yyyy");
        
        String message = "";
        
        int i = 1;
        List<DataSet> dataSets = new ArrayList<DataSet>( getAssignDataSetList() );
        //System.out.println( " Data Set Lenght  " + dataSets.size() +  " -------- > " + message );
        for( DataSet dataSet : dataSets )
        {
            List<OrganisationUnit> nonReportingFacilitys = new ArrayList<OrganisationUnit>( getNonReportingFacilityList( dataSet.getId() ));
            
            //System.out.println( " Data Set Name  " + dataSet.getName() +  " -------- Non Reporting Facility Size > " + nonReportingFacilitys.size() );
            for( OrganisationUnit nonReportingFaclity : nonReportingFacilitys )
            {
                List<String> userMobileNoWithNameList = new ArrayList<String>( getUserMobileNoWithName( nonReportingFaclity.getId() ));
                
                //System.out.println( " Data Set Name  " + dataSet.getName() +  " -------- Non Reporting Facility name > " + nonReportingFaclity.getName() + " Mobile No list Size -- "  +userMobileNoWithNameList.size() );
                
                for( String phoneNoAndName : userMobileNoWithNameList )
                {
                    String phoneNo = phoneNoAndName.split( ":" )[0];
                    String userName = phoneNoAndName.split( ":" )[1];
                    
                    //System.out.println( " Data Set Name  " + dataSet.getName() +  " -------- Non Reporting Facility name > " + nonReportingFaclity.getName() + " Mobile No list Size -- "  +userMobileNoWithNameList.size() + " User Name : " + userName + " Mobile No  : " + phoneNo );
                    message = " Dear " + userName + " greeting from single reporting HMIS server. " + nonReportingFaclity.getName() + " has not entered the data of  " + dataSet.getName() + " dataset on DHIS-2 portal for "+ monthFormat.format( currentperiod.getStartDate() ) + ", Please get it done urgently";    
                    //System.out.println( i + "  " + phoneNo +  " -------- > " + message );
                    bulkSMSHTTPInterface.sendMessage( message, phoneNo );
                    i = i +1;
                }
            }
        }
        System.out.println(" NonReportingFacility SMS Scheduler Ended at : " + new Date() );
    }

    
    // get phoneNo of All users
    public List<String> getAllUsersMobileNumber()
    {
        /*
        List<String> mobileNumbers = new ArrayList<String>();
        
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getAllOrganisationUnits() );
        
        List<User> orgUnitUserList = new ArrayList<User>();
        for( OrganisationUnit orgUnit : orgUnitList )
        {
            if( orgUnit.getUsers() != null && orgUnit.getUsers().size() > 0 )
            {
                orgUnitUserList.addAll( orgUnit.getUsers() );
            }
        }
        
        
        Constant smsUserGroupConstant = constantService.getConstantByName( SMS_USER_GROUP_ID );
        
        UserGroup userGroup = userGroupService.getUserGroup( (int) smsUserGroupConstant.getValue() );
        List<User> smsUsers = new ArrayList<User>( userGroup.getMembers() );
        
        smsUsers.retainAll( orgUnitUserList );
        
        try
        {
            for( User user : smsUsers )
            {
                if( user.getPhoneNumber() != null && user.getPhoneNumber().equalsIgnoreCase( "" ) )
                {
                    mobileNumbers.add( user.getPhoneNumber()  );
                }
            }
            
            System.out.println("-------------------- > " + mobileNumbers );
            
            return mobileNumbers;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal OrganisationUnit id", e );
        }
        */
        
        
        List<String> mobileNumbers = new ArrayList<String>();
        
        Constant smsUserGroupConstant = constantService.getConstantByName( SMS_USER_GROUP_ID );
        
        int smsUserGroupId = (int) smsUserGroupConstant.getValue();
        
        try
        {
            String query = "SELECT userinfoid,surname, firstname, phonenumber FROM userinfo " +
                " WHERE  userinfoid IN ( SELECT userid FROM usergroupmembers where usergroupid = " + smsUserGroupId  + " ) ";
            
            //SELECT userinfoid,surname, firstname, phonenumber from userinfo where userinfoid IN ( SELECT userid FROM usergroupmembers where usergroupid = 91451);

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                //Integer orgUnitId = rs.getInt( 1 );
                String userName = rs.getString( 3 ) + " " + rs.getString( 2 ) ;
                
                String phoneNo = rs.getString( 4 );
                
                
                if ( phoneNo != null )
                {
                    //OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( orgUnitId );
                    mobileNumbers.add( phoneNo + ":" + userName );
                }
            }

            return mobileNumbers;
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal User Group Id ", e );
        }
        
    }        
    
    // get dataElements List
    public List<DataElement> getDataElements()
    {
        List<DataElement> dataElements = new ArrayList<DataElement>();
        
        Constant adverseEventDataElementGroupConstant = constantService.getConstantByName( ADVERSE_EVENT_DATAELEMENT_GROUP_ID );
        
        int deGroupId = (int) adverseEventDataElementGroupConstant.getValue();
        
        try
        {
            String query = "SELECT dataelementid FROM dataelementgroupmembers " +
                " WHERE  dataelementgroupid  = " + deGroupId  + " ";
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer deId = rs.getInt( 1 );
                
                if ( deId != null )
                {
                    DataElement de = dataElementService.getDataElement( deId );
                    dataElements.add( de );
                }
            }

            return dataElements;
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal De Group Id ", e );
        }
        
    }
    
    // get dataValue of All users
    public Map<String, String> getDataValueMap()
    {
        simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        String currentDate = simpleDateFormat.format( new Date() );
        
        //currentDate = "2015-10-20";
        
        Map<String, String> dataValueMap = new HashMap<String,String>();
        
        Constant adverseEventDataElementGroupConstant = constantService.getConstantByName( ADVERSE_EVENT_DATAELEMENT_GROUP_ID );
        
        int deGroupId = (int) adverseEventDataElementGroupConstant.getValue();
        
        try
        {
            String query = "SELECT dataelementid, categoryoptioncomboid, periodid, sourceid, value, storedby, lastupdated FROM datavalue " +
                " WHERE  dataelementid IN ( SELECT dataelementid FROM dataelementgroupmembers where dataelementgroupid = " + deGroupId  + " ) "
                    + " AND date(lastupdated) =  '"+ currentDate +"' ORDER BY  lastupdated ASC";
            
            //System.out.println(" query -------- > " + query );
            
            /*
            SELECT dataelementid, periodid, sourceid, value, 
            storedby, lastupdated
            FROM datavalue where dataelementid in ( SELECT dataelementid FROM dataelementgroupmembers where dataelementgroupid  = 50) order by lastupdated desc;
            
            SELECT dataelementid, periodid, sourceid, value, 
            storedby, lastupdated
            FROM datavalue where dataelementid in ( SELECT dataelementid FROM dataelementgroupmembers where dataelementgroupid  = 50) and periodid = 447985 order by lastupdated desc;
            */
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            int i = 1;
            while ( rs.next() )
            {
                Integer deId = rs.getInt( 1 );
                Integer coId = rs.getInt( 2 );
                String periodId = rs.getString( 3 );
                String sourceId = rs.getString( 4 );
                String value = rs.getString( 5 );
                //String lastUpdated = simpleDateFormat.format( rs.getDate( 6 ) );
                
                if ( value != null && Integer.parseInt( value ) > 0 )
                {
                    String mapKey = deId + ":" + coId + ":" + periodId+ ":" + sourceId;
                    
                    //String mapValue = periodId+":"+sourceId+":"+value+":"+lastUpdated;
                    
                    //System.out.println(" de is  -------- > " + deId + " Map Value is -------- > " + mapValue );
                    
                    dataValueMap.put( mapKey, value );
                    //System.out.println( i + " Value Map Size is  -------- > " + dataValueMap.size() );
                    i =i+1;
                }
                
            }
            
            //System.out.println(" 2 Value Map Size is  -------- > " + dataValueMap.size() );
            return dataValueMap;
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal De Group Id ", e );
        }
        
    }
 
    //--------------------------------------------------------------------------------
    // Get DataElement Map with categoryOption list
    //--------------------------------------------------------------------------------
    public Map<Integer, List<Integer>> getDataElementMapWithCategoryOption()
    {
        //System.out.println(" Inside query");

        Map<Integer, List<Integer>> dataElementMapWithCategoryOption = new HashMap<Integer,  List<Integer>>();

        try
        {
            String query = "SELECT  dataelement.dataelementid, dataelement.name, categories_categoryoptions.categoryoptionid, dataelementcategoryoption.name from dataelement " +
                             " INNER JOIN categorycombos_categories ON categorycombos_categories.categorycomboid = dataelement.categorycomboid " +
                             " INNER JOIN categories_categoryoptions ON categories_categoryoptions.categoryid = categorycombos_categories.categoryid " +
                             " INNER JOIN dataelementcategoryoption ON dataelementcategoryoption.categoryoptionid = categories_categoryoptions.categoryoptionid " + 
                             " ORDER BY dataelement.dataelementid"; 
             
            //System.out.println( query );
            
            /*
            SELECT  dataelement.dataelementid,dataelement.name, categories_categoryoptions.categoryoptionid, dataelementcategoryoption.name from dataelement
            INNER JOIN categorycombos_categories ON categorycombos_categories.categorycomboid = dataelement.categorycomboid
            INNER JOIN categories_categoryoptions ON categories_categoryoptions.categoryid = categorycombos_categories.categoryid
            INNER JOIN dataelementcategoryoption ON dataelementcategoryoption.categoryoptionid = categories_categoryoptions.categoryoptionid
            order by dataelement.dataelementid  
            */
          
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer dataElementId = rs.getInt( 1 );
                Integer coId  = rs.getInt( 3 );
                
                if ( dataElementId != null && coId != null )
                {
                    List<Integer> coIds = dataElementMapWithCategoryOption.get( dataElementId );
                    if ( coIds == null )
                    {
                        coIds = new  ArrayList<Integer>();
                    }
                    coIds.add( coId );
                    dataElementMapWithCategoryOption.put( dataElementId, coIds );
                }
            }
            
            return dataElementMapWithCategoryOption;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal DataElement id CO id ", e );
        }
    }    
    
    // get Period List
    public List<Period> getPeriods()
    {
        List<Period> periods = new ArrayList<Period>();
        
        simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        String currentDate = simpleDateFormat.format( new Date() );
        //currentDate = "2015-10-20";
        
        Constant adverseEventDataElementGroupConstant = constantService.getConstantByName( ADVERSE_EVENT_DATAELEMENT_GROUP_ID );
        
        int deGroupId = (int) adverseEventDataElementGroupConstant.getValue();
        
        try
        {
            String query = "SELECT periodid FROM datavalue " +
                      " WHERE  dataelementid IN ( SELECT dataelementid FROM dataelementgroupmembers where dataelementgroupid = " + deGroupId  + " ) "
                    + " AND date(lastupdated) =  '"+ currentDate +"' ORDER BY  lastupdated ASC";
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer periodId = rs.getInt( 1 );
                
                if ( periodId != null )
                {
                    Period period = periodService.getPeriod( periodId );
                    periods.add( period );
                }
            }

            return periods;
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal De Group Id ", e );
        }
        
    }   
    
    // get Source List
    public List<OrganisationUnit> getOrganisationUnits()
    {
        List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();
        
        simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        String currentDate = simpleDateFormat.format( new Date() );
        
        //currentDate = "2015-10-23";
        
        Constant adverseEventDataElementGroupConstant = constantService.getConstantByName( ADVERSE_EVENT_DATAELEMENT_GROUP_ID );
        
        int deGroupId = (int) adverseEventDataElementGroupConstant.getValue();
        
        try
        {
            String query = "SELECT distinct sourceid FROM datavalue " +
                      " WHERE  dataelementid IN ( SELECT dataelementid FROM dataelementgroupmembers where dataelementgroupid = " + deGroupId  + " ) "
                    + " AND date(lastupdated) =  '"+ currentDate +"' ";
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer sourceId = rs.getInt( 1 );
                
                if ( sourceId != null )
                {
                    OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( sourceId );
                    organisationUnits.add( orgUnit );
                }
            }

            return organisationUnits;
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal De Group Id ", e );
        }
        
    }

    // get Period ids and Source ids
    public List<String> getPeriodAndOrganisationUnitIds()
    {
        List<String> periodAndOrgUintIds = new ArrayList<String>();
        
        simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        String currentDate = simpleDateFormat.format( new Date() );
        
        //currentDate = "2015-10-20";
        
        Constant adverseEventDataElementGroupConstant = constantService.getConstantByName( ADVERSE_EVENT_DATAELEMENT_GROUP_ID );
        
        int deGroupId = (int) adverseEventDataElementGroupConstant.getValue();
        
        try
        {
            String query = " SELECT  datavalue.periodid, datavalue.sourceid, organisationunit.parentid FROM datavalue  " +
                           " INNER JOIN organisationunit ON organisationunit.organisationunitid = datavalue.sourceid " +
                           " WHERE  datavalue.dataelementid IN ( SELECT dataelementid FROM dataelementgroupmembers where dataelementgroupid = " + deGroupId  + " ) " +
                           " AND date(datavalue.lastupdated) =  '"+ currentDate +"' ORDER BY  datavalue.lastupdated ASC";
            
            
            
/*
SELECT  datavalue.periodid, datavalue.sourceid, organisationunit.parentid FROM datavalue 
INNER JOIN organisationunit ON organisationunit.organisationunitid = datavalue.sourceid
where dataelementid in ( SELECT dataelementid FROM dataelementgroupmembers where dataelementgroupid  = 50) 
and date(datavalue.lastupdated) = '2015-10-20' 
*/
            
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer periodId = rs.getInt( 1 );
                Integer sourceId = rs.getInt( 2 );
                Integer parentId = rs.getInt( 3 );
                
                if ( periodId != null && sourceId != null )
                {
                    periodAndOrgUintIds.add( periodId + ":" + sourceId + ":" + parentId );
                }
            }

            return periodAndOrgUintIds;
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal De Group Id ", e );
        }
        
    }    
    

    // get message content map
    public Map<String, List<String>> getMessageContent()
    {
        Map<String, List<String>> msgContentMap = new HashMap<String,  List<String>>();
        
        simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        String currentDate = simpleDateFormat.format( new Date() );
        
        SimpleDateFormat monthFormat = new SimpleDateFormat();
        monthFormat = new SimpleDateFormat("MMM-yyyy");
        
        //currentDate = "2015-10-23";
        
        Constant adverseEventDataElementGroupConstant = constantService.getConstantByName( ADVERSE_EVENT_DATAELEMENT_GROUP_ID );
        
        int deGroupId = (int) adverseEventDataElementGroupConstant.getValue();
        String message = "";
        
        try
        {
            String query = " SELECT  dataelement.name, dataelementcategoryoption.name, period.startdate, organisationunit.name, datavalue.value,datavalue.sourceid,datavalue.storedby FROM datavalue " +
                           " INNER JOIN period ON period.periodid = datavalue.periodid " +
                           " INNER JOIN organisationunit ON organisationunit.organisationunitid = datavalue.sourceid " +
                           " INNER JOIN dataelement ON dataelement.dataelementid = datavalue.dataelementid " +
                           " INNER JOIN dataelementcategoryoption on dataelementcategoryoption.categoryoptionid = datavalue.categoryoptioncomboid " +
                           " INNER JOIN dataelementgroupmembers on dataelementgroupmembers.dataelementid = datavalue.dataelementid " +
                           " WHERE  dataelementgroupmembers.dataelementgroupid = " + deGroupId  + 
                           " AND date(datavalue.lastupdated) =  '"+ currentDate +"' AND CAST(value AS NUMERIC) > 0 ORDER BY  datavalue.lastupdated ASC";
            
            
            
                        /*
                        SELECT  datavalue.dataelementid, dataelement.name, datavalue.periodid, datavalue.sourceid, datavalue.value,dataelementcategoryoption.name, organisationunit.name,period.startdate FROM datavalue
                        INNER JOIN period ON period.periodid = datavalue.periodid
                        INNER JOIN organisationunit ON organisationunit.organisationunitid = datavalue.sourceid
                        INNER JOIN dataelement ON dataelement.dataelementid = datavalue.dataelementid
                        INNER JOIN dataelementcategoryoption on dataelementcategoryoption.categoryoptionid=datavalue.categoryoptioncomboid
                        INNER JOIN dataelementgroupmembers on dataelementgroupmembers.dataelementid = datavalue.dataelementid
                        where dataelementgroupmembers.dataelementgroupid  = 50 and date(datavalue.lastupdated) = '2015-10-21' and CAST(value AS NUMERIC) > 0
                        */
            
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                String deName = rs.getString( 1 );
                String coName = rs.getString( 2 );
                String periodName = monthFormat.format( rs.getDate( 3 ) );
                String sourceName = rs.getString( 4 );
                String value = rs.getString( 5 );
                String sourceId = rs.getString( 6 );
                String userName = rs.getString( 7 );
                
                if ( deName != null && sourceId != null )
                {
                    if( coName.equalsIgnoreCase( "default" ))
                    {
                        message = " Dear " + userName + " greeting from single reporting HMIS server. " + sourceName + " has reported " + value + " deaths/cases of " + deName +  " on DHIS-2 portal for "+ periodName + ".Please get this data verified";
                    }
                    
                    else
                    {
                        message = " Dear " + userName + " greeting from single reporting HMIS server. " + sourceName + " has reported " + value + " deaths/cases of " + deName +  " " + coName  + " on DHIS-2 portal for " + periodName + ".Please get this data verified";
                        
                    }
                    
                    List<String> msgList = msgContentMap.get( sourceId );
                    if ( msgList == null )
                    {
                        msgList = new  ArrayList<String>();
                    }
                    
                    msgList.add( message );
                    msgContentMap.put( sourceId, msgList );
                }
                
              
            }

            return msgContentMap;
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal De Group Id ", e );
        }
        
    }    
    

    // get message content map
    public Map<String, List<String>> getPhoneNumbers( String orgUnitIdsByComma )
    {
        Map<String, List<String>> userPhoneNumberMap = new HashMap<String,  List<String>>();
       
        try
        {
            String query = " SELECT dist,name,organisationunitid,string_agg(ph,',') ph from  ( " +
                           " SELECT dist,name,asd.organisationunitid,ph from ( " +
                           " SELECT ou1.name AS dist,ou.name,os.idlevel3, ou.organisationunitid FROM organisationunit ou " +
                           " INNER JOIN _orgunitstructure os ON ou.organisationunitid=os.organisationunitid " +
                           " INNER JOIN organisationunit ou1 ON os.idlevel3=ou1.organisationunitid " +
                           " WHERE os.organisationunitid IN ( " +  orgUnitIdsByComma + " ) )asd left join ( " +
                           " SELECT um.organisationunitid,u.phonenumber ph from userinfo u " +
                           " INNER join usermembership um on u.userinfoid=um.userinfoid " +
                           " where phonenumber is  not null and phonenumber not like '' ) asd1 " + 
                           " on (asd.organisationunitid=asd1.organisationunitid or asd.idlevel3=asd1.organisationunitid) " +
                           " group by dist,name,asd.organisationunitid,ph ) fin group by dist,name,organisationunitid ";
                           
                           
            /*
            SELECT dist,name,organisationunitid,string_agg(ph,',') ph from 
            (
            select dist,name,asd.organisationunitid,ph 
            from
            (
            SELECT ou1.name AS dist,ou.name,os.idlevel3, ou.organisationunitid
            FROM organisationunit ou
            INNER JOIN _orgunitstructure os ON ou.organisationunitid=os.organisationunitid
            INNER JOIN organisationunit ou1 ON os.idlevel3=ou1.organisationunitid
            WHERE os.organisationunitid IN ( 10176 )
            )asd
            left join 
            (
            select um.organisationunitid,u.phonenumber ph
            from userinfo u
            inner join usermembership um on u.userinfoid=um.userinfoid
            where phonenumber is  not null
            and phonenumber not like ''
            )asd1
            on (asd.organisationunitid=asd1.organisationunitid or asd.idlevel3=asd1.organisationunitid)
            group by dist,name,asd.organisationunitid,ph
            )fin
            group by dist,name,organisationunitid;
            */
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                String sourceId = rs.getString( 3 );
                String phoneNo = rs.getString( 4 );
                
                if ( sourceId != null )
                {
                    List<String> phoneNoList = userPhoneNumberMap.get( sourceId );
                    
                    if ( phoneNoList == null )
                    {
                        phoneNoList = new  ArrayList<String>();
                    }
                    
                    String[] phones = phoneNo.split( "," );
                    
                    if( phones != null && phones.length > 0 )
                    {
                        phoneNoList = new ArrayList<String>(Arrays.asList( phones ));
                    }
                    
                    else
                    {
                        phoneNoList.add( phoneNo );
                    }
                    
                    userPhoneNumberMap.put( sourceId, phoneNoList );
                }
            }

            return userPhoneNumberMap;
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal De Group Id ", e );
        }
        
    }
    
    // get assign dataSetList
    public List<DataSet> getAssignDataSetList()
    {
        List<DataSet> dataSets = new ArrayList<DataSet>();
        
        try
        {
            String query = " SELECT ds.datasetid, d.name, count(ds.sourceid) from datasetsource ds " +
                           " INNER JOIN dataset d ON d.datasetid = ds.datasetid " +
                           " group by ds.datasetid,d.name having count(ds.datasetid) > 0 order by ds.datasetid ";
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            /*
            SELECT ds.datasetid, d.name, count(ds.sourceid) from datasetsource ds
            INNER JOIN dataset d ON d.datasetid = ds.datasetid
            group by ds.datasetid,d.name having count(ds.datasetid) > 0 order by ds.datasetid;
            */
            
            while ( rs.next() )
            {
                Integer dataSetId = rs.getInt( 1 );
                
                if ( dataSetId != null )
                {
                    DataSet dataSet = dataSetService.getDataSet( dataSetId );
                    dataSets.add( dataSet );
                }
            }

            return dataSets;
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal De Group Id ", e );
        }
        
    } 
    
    
    
    // get NonReporting Facility List
    public List<OrganisationUnit> getNonReportingFacilityList( Integer dataSetId )
    {
        List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();
        
        String isoPeriodString = complateDate.split( "-" )[0] + complateDate.split( "-" )[1];
        
        currentperiod = periodService.reloadIsoPeriod( isoPeriodString );
        
        try
        {
            String query = " SELECT sourceid from datasetsource where datasetid = 91455 and sourceid not in ( " +
                           " SELECT dv.sourceid from datavalue dv INNER JOIN datasetmembers dsm on dv.dataelementid = dsm.dataelementid " +
                           " WHERE dsm.datasetid = " + dataSetId + " AND dv.periodid = " + currentperiod.getId() +
                           " group by dv.sourceid having count(value) > 0 )order by sourceid ";
                           
                /*
                SELECT sourceid from datasetsource where datasetid = 91455 and sourceid not in ( 
                SELECT dv.sourceid from datavalue dv 
                inner join datasetmembers dsm on dv.dataelementid = dsm.dataelementid
                where dsm.datasetid in ( 91455 ) and  dv.periodid = 447985 group by dv.sourceid having count(value) > 0)order by sourceid;   
                */
            
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer sourceId = rs.getInt( 1 );
                
                if ( sourceId != null )
                {
                    OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( sourceId );
                    organisationUnits.add( orgUnit );
                }
            }

            return organisationUnits;
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal De Group Id ", e );
        }
        
    }    
    
 
    // get userMobileNo and userName by OrgUnit
    public List<String> getUserMobileNoWithName( Integer sourceId )
    {
        List<String> userMobileNoWithNameList = new ArrayList<String>();
       
        try
        {
            String query = " select dist,name,organisationunitid,string_agg(ph,',') ph, sname,fname  from ( " +
                           " select dist,name,asd.organisationunitid,ph , sname,fname from ( " +
                           " SELECT ou1.name AS dist,ou.name,os.idlevel3, ou.organisationunitid FROM organisationunit ou " +
                           " INNER JOIN _orgunitstructure os ON ou.organisationunitid=os.organisationunitid " +
                           " INNER JOIN organisationunit ou1 ON os.idlevel3=ou1.organisationunitid " +
                           " WHERE os.organisationunitid = " +  sourceId + " )asd left join ( " +
                           " SELECT um.organisationunitid,u.phonenumber ph, u.surname sname, u.firstname fname from userinfo u " +
                           " INNER join usermembership um on u.userinfoid=um.userinfoid " +
                           " where phonenumber is  not null and phonenumber not like '' ) asd1 " + 
                           " on (asd.organisationunitid=asd1.organisationunitid or asd.idlevel3=asd1.organisationunitid) " +
                           " group by dist,name,asd.organisationunitid,ph,sname,fname ) fin group by dist,name,organisationunitid,sname,fname ";
                           

            /*
            select dist,name,organisationunitid,string_agg(ph,',') ph, sname,fname  from 
            (
            select dist,name,asd.organisationunitid,ph , sname,fname 
            from
            (
            SELECT ou1.name AS dist,ou.name,os.idlevel3, ou.organisationunitid
            FROM organisationunit ou
            INNER JOIN _orgunitstructure os ON ou.organisationunitid=os.organisationunitid
            INNER JOIN organisationunit ou1 ON os.idlevel3=ou1.organisationunitid
            WHERE os.organisationunitid = 9541
            )asd
            left join 
            (
            select um.organisationunitid,u.phonenumber ph,u.surname sname, u.firstname fname
            from userinfo u
            inner join usermembership um on u.userinfoid=um.userinfoid
            where phonenumber is  not null
            and phonenumber not like ''
            )asd1
            on (asd.organisationunitid=asd1.organisationunitid or asd.idlevel3=asd1.organisationunitid)
            group by dist,name,asd.organisationunitid,ph,sname,fname
            )fin
            group by dist,name,organisationunitid,sname,fname ;
            */
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                String phoneNo = rs.getString( 4 );
                String userName = rs.getString( 6 ) + " " + rs.getString( 5 );
                
                if ( phoneNo != null )
                {
                    userMobileNoWithNameList.add( phoneNo + ":" + userName );
                }
            }

            return userMobileNoWithNameList;
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal De Group Id ", e );
        }
        
    }    
    
// Methods for Key Performance Indicators
    
    // get Source List for KeyPerformance Indicators
    public List<OrganisationUnit> getOrganisationUnitsForKeyPerformanceIndicator()
    {
        List<OrganisationUnit> organisationUnits = new ArrayList<OrganisationUnit>();
        
        complateDate = simpleDateFormat.format( new Date() );
        
        String isoPeriodString = complateDate.split( "-" )[0] + complateDate.split( "-" )[1];
        
        currentperiod = periodService.reloadIsoPeriod( isoPeriodString );
        
        Constant indicatorDeGroupConstant = constantService.getConstantByName( WEBSERVICE_INDICATOR_DATAELEMENT_GROUP_ID );
        
        int deGroupId = (int) indicatorDeGroupConstant.getValue();
        
        try
        {
            String query = "SELECT distinct sourceid FROM datavalue " +
                      " WHERE  dataelementid IN ( SELECT dataelementid FROM dataelementgroupmembers where dataelementgroupid = " + deGroupId  + " ) "
                    + " AND periodid =  "+ currentperiod.getId() +" and CAST(value AS NUMERIC) > 0 ";
            
            /*
            SELECT  distinct sourceid FROM datavalue 
            where dataelementid in ( 3,4,6 ) 
                        and periodid = 114779;
            */
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer sourceId = rs.getInt( 1 );
                
                if ( sourceId != null )
                {
                    OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( sourceId );
                    organisationUnits.add( orgUnit );
                }
            }

            return organisationUnits;
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal De Group Id ", e );
        }
        
    }

    // get Indicator List for KeyPerformance SMS
    public List<Indicator> getIndicators()
    {
        List<Indicator> indicators = new ArrayList<Indicator>();
        
        Constant indicatorGroupConstant = constantService.getConstantByName( WEBSERVICES_INDICATOR_GROUP_ID );
        
        int deGroupId = (int) indicatorGroupConstant.getValue();
        
        try
        {
            String query = " SELECT indicatorid, indicatorgroupid FROM indicatorgroupmembers " +
                           " WHERE  indicatorgroupid =  "+ deGroupId +" ";
            
            /*
            SELECT indicatorid, indicatorgroupid
              FROM indicatorgroupmembers where indicatorgroupid = 450817;
            */
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            while ( rs.next() )
            {
                Integer indicatorId = rs.getInt( 1 );
                
                if ( indicatorId != null )
                {
                    Indicator indicator = indicatorService.getIndicator( indicatorId );
                    indicators.add( indicator );
                }
            }

            return indicators;
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal De Group Id ", e );
        }
        
    }    
    
    // get indicator Values
    public Map<String, String> getIndicatorValueMap()
    {
        complateDate = simpleDateFormat.format( new Date() );
        
        String isoPeriodString = complateDate.split( "-" )[0] + complateDate.split( "-" )[1];
        
        currentperiod = periodService.reloadIsoPeriod( isoPeriodString );
        
        //currentDate = "2015-10-20";
        
        Map<String, String> dataValueMap = new HashMap<String,String>();
        
        Constant indicatorDeGroupConstant = constantService.getConstantByName( WEBSERVICE_INDICATOR_DATAELEMENT_GROUP_ID );
        
        int deGroupId = (int) indicatorDeGroupConstant.getValue();
        
        try
        {
            String query = " SELECT sourceid, dataelementid, value FROM datavalue " +
                           " WHERE  dataelementid IN ( SELECT dataelementid FROM dataelementgroupmembers where dataelementgroupid = " + deGroupId  + " ) " +
                           " AND periodid =  "+ currentperiod.getId() +" and CAST(value AS NUMERIC) > 0 ";
            
            //System.out.println(" query -------- > " + query );
            
            /*
            SELECT sourceid, dataelementid,value FROM datavalue 
            where dataelementid in ( 3,4,6 ) and 
            periodid = 114779;
            */
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            int i = 1;
            while ( rs.next() )
            {
                Integer sourceId = rs.getInt( 1 );
                Integer deId = rs.getInt( 2 );
                String value = rs.getString( 3 );
                
                if ( value != null && Integer.parseInt( value ) > 0 )
                {
                    String mapKey = sourceId + ":" + deId;
                    dataValueMap.put( mapKey, value );
                    //System.out.println( i + " Value Map Size is  -------- > " + dataValueMap.size() );
                    i =i+1;
                }
                
            }
            
            //System.out.println(" 2 Value Map Size is  -------- > " + dataValueMap.size() );
            return dataValueMap;
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal De Group Id ", e );
        }
        
    }    
    
    
    
    
    
    
    
    
    
    
    
    
 /*
  * SELECT dataelementid, periodid, sourceid, value, 
       storedby, lastupdated
    FROM datavalue where dataelementid in ( SELECT dataelementid FROM dataelementgroupmembers where dataelementgroupid  = 50) and  CAST(value AS numeric) > 0 order by lastupdated desc;   
  
  *SELECT dataelementid, periodid, sourceid, categoryoptioncomboid, value, 
       storedby, lastupdated, comment, followup, attributeoptioncomboid, 
       created
  FROM datavalue where dataelementid in ( SELECT dataelementid FROM dataelementgroupmembers where dataelementgroupid  = 50);
    
    
    SELECT userinfoid,surname, firstname, phonenumber from userinfo where userinfoid IN ( SELECT userid FROM usergroupmembers where usergroupid = 91451);

	
	http://192.168.0.40:20093/quic_dhis/api/dataElements.json?fields=categoryCombo[*]
  *
  *
  */
    
    
    
    
    
    
    
    
    
}

