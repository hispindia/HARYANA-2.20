package org.hisp.dhis.excelimport.linelisting.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

import org.amplecode.quick.StatementManager;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.excelimport.util.ReportService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.user.CurrentUserService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class LineListingExcelImportResultAction
    implements Action
{
    public static final String LL_DEATHS = "Line listing Deaths";
    public static final String LL_MATERNAL_DEATHS = "Line listing Maternal Deaths";
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    /*
    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private DataElementCategoryService dataElementCategoryService;
    
    public void setDataElementCategoryService( DataElementCategoryService dataElementCategoryService )
    {
        this.dataElementCategoryService = dataElementCategoryService;
    }    
    */
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }
    /*
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    */
    

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private String message;

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }

    private String importSheetList;

    public void setImportSheetList( String importSheetList )
    {
        this.importSheetList = importSheetList;
    }

    private String reportFileNameTB;

    public void setReportFileNameTB( String reportFileNameTB )
    {
        this.reportFileNameTB = reportFileNameTB;
    }

    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }

    private File file;

    public void setUpload( File file )
    {
        this.file = file;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    public void setUploadFileName( String fileName )
    {
        this.fileName = fileName;
    }

    private int availablePeriods;

    public void setAvailablePeriods( int availablePeriods )
    {
        this.availablePeriods = availablePeriods;
    }

    private Integer dataSetId;
    
    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }
    
    private String reportModelTB;

    public void setReportModelTB( String reportModelTB )
    {
        this.reportModelTB = reportModelTB;
    }
    
    private boolean excelValidator;

    public boolean getExcelValidator()
    {
        return excelValidator;
    }

    OrganisationUnit orgUnit;
    
    private boolean lockStatus;
    
    public boolean isLockStatus()
    {
        return lockStatus;
    }
    
    private Period selectedPeriod;

    public Period getSelectedPeriod()
    {
        return selectedPeriod;
    }

    private String storedBy;

    public String getStoredBy()
    {
        return storedBy;
    }
    
    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }
    
    private List<Integer> sheetList;

    private List<Integer> rowList;

    private List<Integer> colList;

    private List<String> deCodeType;

    private List<String> serviceType;
    
    private List<String> services;

    public List<String> getServices()
    {
        return services;
    }
    
    private List<String> importStatusMsgList = new ArrayList<String>();
    
    public List<String> getImportStatusMsgList()
    {
        return importStatusMsgList;
    }
    
    private Map<String, String> resMap;
    
    /*
    private Date sDate;

    private Date eDate;
    */
    
    private String raFolderName;
    
    private String ageCategory;
    
    private DataSet dataSet;
    
    private Integer maxRecordNo = null;
    
    private String deCodesImportXMLFileName = "";
    
    private String excelFilePath = "";
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // Initialization

        statementManager.initialise();
        raFolderName = reportService.getRAFolderName();
        
        services = new ArrayList<String>();
        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();
        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();
        
        ageCategory = "";
        
        initializeResultMap();
        
        message = "";
        importStatusMsgList = new ArrayList<String>();
        //InputStream inputStream = null;

        excelValidator = true;

        deCodesImportXMLFileName = "";
        deCodesImportXMLFileName = importSheetList + "DECodes.xml";

        String excelImportFolderName = "excelimport";

        //inputStream = new BufferedInputStream( new FileInputStream( file ) );
        
        storedBy = currentUserService.getCurrentUsername();
        String excelTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + excelImportFolderName + File.separator + "template" + File.separator + reportFileNameTB;

        System.out.println( excelTemplatePath );
        
        excelFilePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + excelImportFolderName + File.separator + "pending" + File.separator + fileName;

        file.renameTo( new File( excelFilePath ) );

        moveFile( file, new File( excelFilePath ) );

        WorkbookSettings ws = new WorkbookSettings();
        ws.setLocale( new Locale( "en", "EN" ) );
        
        String fileType = fileName.substring( fileName.indexOf( '.' ) + 1, fileName.length() );

        if ( !fileType.equalsIgnoreCase( "xls" ) )
        {
            message = "The file you are trying to import is not an excel file";
            
            //importStatusMsgList.add( message );

            return SUCCESS;
        }
        
        /*
        Workbook excelTemplateFile = Workbook.getWorkbook( new File( excelTemplatePath ) );
        
        excelValidator = validateReport( deCodesImportXMLFileName, excelImportFile, excelTemplateFile );

        if ( excelValidator == false )
        {
            message = "The file you are trying to import is not the correct format";

            return SUCCESS;
        }
        */
        
        if ( reportModelTB.equalsIgnoreCase( "STATIC" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>();
            orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList.add( orgUnit );
        }
        
        dataSet = dataSetService.getDataSet( dataSetId );
        
        //dataSet.getExpiryDays()
        
        selectedPeriod = periodService.getPeriod( availablePeriods );
        
        System.out.println ( dataSet.getName() + " Importing Start Time "  + new Date() );
        
        //sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );

        //eDate = format.parseDate( String.valueOf( selectedPeriod.getEndDate() ) );

        //lockStatus = dataSetService.isLocked( dataSet, selectedPeriod, orgUnit, null );
        lockStatus = dataSetService.isLocked( dataSet, selectedPeriod, orgUnit, null, null );

        if( lockStatus )
        {
            message = "Unable to Import : Corresponding Dataset ( "+ dataSet.getName() +" ) for the selected Excel Template is locked.";
            
            //importStatusMsgList.add( message );
            
            return SUCCESS;
        }
        
        maxRecordNo = null;
        
        maxRecordNo = reportService.getMaxRecordNoFromLLDataValue();
        
        if( maxRecordNo == null )
        {
            maxRecordNo = 0;
        }
        
        if( dataSet != null && ( deCodesImportXMLFileName != null || deCodesImportXMLFileName.equalsIgnoreCase( "" ) ) )
        {
            if( dataSet.getName().equalsIgnoreCase( LL_DEATHS ) )
            {
                importLineListingDeath();
            }
            else if( dataSet.getName().equalsIgnoreCase( LL_MATERNAL_DEATHS ) )
            {
                importLineListingMarernalDeath();
            }
            else
            {
                message = "The file you are trying to import is not the correct format";
                return SUCCESS;
            }
        }
        else
        {
            message = "The file you are trying to import is not the correct format";
            return SUCCESS;
        }
        
        /*
        System.out.println ( "Size of deCodesList  " + deCodesList.size()  );
        
        for( String deCode : deCodesList )
        {
            System.out.println ( "deCode  " + deCode  );
        }
        */
        
        //Integer colEnd = 5;
        
        //String cellContent = sheet.getCell( tempColNo, tempRowNo ).getContents();
        
        //DataElementCategoryOptionCombo defaultOptionCombo = dataElementCategoryService.getDefaultDataElementCategoryOptionCombo();
        
        //System.out.println ( "Total Ro No  " + sheet.getRows() + " Max Record No " + maxRecordNo );
        
        //System.out.println ( " Last Age Category " + ageCategory  );
        
        System.out.println ( dataSet.getName() + " Importing End Time "  + new Date() );
        
        return SUCCESS;
    }

    // Supported Methods
    
    // // Importing LineListing Maternal Death Data
    public void importLineListingMarernalDeath() throws IOException, BiffException
    {
        List<String> maternalDeathDeCodesList = new ArrayList<String>();

        maternalDeathDeCodesList.clear();

        if ( maternalDeathDeCodesList.isEmpty() )
        {
            maternalDeathDeCodesList = getDECodes( deCodesImportXMLFileName );
        }
        
        long t;
        Date d = new Date();
        t = d.getTime();
        java.sql.Date lastUpdatedDate = new java.sql.Date( t );
        
        int sheetNo = 0;
        
        Workbook excelImportFile = Workbook.getWorkbook( new File( excelFilePath ) );
        Sheet sheet = excelImportFile.getSheet( sheetNo );
        
        int recordCount = maxRecordNo + 1;
        int recordNo = 1;
        
        Integer rowStart = 3;
        Integer colStart = 1;
        for( int i = rowStart ; i < sheet.getRows() ; i++ )
        {
            List<String> llMdeValueAndRecord = new ArrayList<String>();
            String age = "";
            
            int count1 = 0;
            for( int j = colStart ; j <= maternalDeathDeCodesList.size() ; j++ )
            {
                String sType = (String) serviceType.get( count1 );
                String dataElementId = maternalDeathDeCodesList.get( count1 );
                String cellContent = sheet.getCell( j, i ).getContents().trim();
                
                if( sType.equalsIgnoreCase( "llmdeathname" ) )
                {
                    if( cellContent == null || cellContent.length() == 0 )
                    {
                        importStatusMsgList.add( "Name of Mother for Record " + recordNo  );
                    }
                    else
                    {
                        llMdeValueAndRecord.add( dataElementId + ":" + recordCount + ":" + cellContent  );
                    }
                }
                
                else if( sType.equalsIgnoreCase( "llmdeathvillage" ) )
                {
                    if( cellContent == null || cellContent.length() == 0 )
                    {
                        importStatusMsgList.add( "Village Name is missing for Record " + recordNo  );
                    }
                    else
                    {
                        llMdeValueAndRecord.add( dataElementId + ":" + recordCount + ":" + cellContent  );
                    }
                }
                else if( sType.equalsIgnoreCase( "llmdeathage" ) )
                {
                    if( cellContent == null || cellContent.length() == 0 )
                    {
                        importStatusMsgList.add( "Age at Death is missing for Record " + recordNo  );
                    }
                    else
                    {
                        age = cellContent;
                        
                        boolean motherAgeValidate = validateMaternalDeathAge( age );
                        
                        if( motherAgeValidate )
                        {
                            llMdeValueAndRecord.add( dataElementId + ":" + recordCount + ":" + age  );
                        }
                        
                        else
                        {
                            importStatusMsgList.add( "Age not in (between 15 - 50) for Record " + recordNo  );
                        }
                    }
                }
                else if( sType.equalsIgnoreCase( "llmdeathduringdeath" )  )
                {
                    if( cellContent == null || cellContent.length() == 0 )
                    {
                        importStatusMsgList.add( "Death During is missing for Record " + recordNo  );
                    }
                    
                    else
                    {
                        String duringMaternalDeath = resMap.get( cellContent );
                        if( duringMaternalDeath != null )
                        {
                            cellContent = duringMaternalDeath;
                        }
                        
                        List<String> duringDeathList = new ArrayList<String>();
                        duringDeathList = getMaternalDeathList( "DURINGDEATH" );
                        
                        if( duringDeathList.contains( cellContent ))
                        {
                            llMdeValueAndRecord.add( dataElementId + ":" + recordCount + ":" + cellContent  );
                        }
                        else
                        {
                            importStatusMsgList.add( "Selected Death During not correct for Record " + recordNo  );
                        }
                    }
                        
                }
                
                else if( sType.equalsIgnoreCase( "llmdeathdeliveryat" )  )
                {
                    if( cellContent == null || cellContent.length() == 0 )
                    {
                        importStatusMsgList.add( "Delivery At is missing for Record " + recordNo  );
                    }
                    
                    else
                    {
                        String deliveryAt = resMap.get( cellContent );
                        if( deliveryAt != null )
                        {
                            cellContent = deliveryAt;
                        }
                        
                        List<String> deliveryAtList = new ArrayList<String>();
                        deliveryAtList = getMaternalDeathList( "DELIVERYAT" );
                        
                        if( deliveryAtList.contains( cellContent ))
                        {
                            llMdeValueAndRecord.add( dataElementId + ":" + recordCount + ":" + cellContent  );
                        }
                        else
                        {
                            importStatusMsgList.add( "Selected Delivery At not correct for Record " + recordNo  );
                        }
                    }
                }                
                
                else if( sType.equalsIgnoreCase( "llmdeathdeliveryby" )  )
                {
                    if( cellContent == null || cellContent.length() == 0 )
                    {
                        importStatusMsgList.add( "Delivery By is missing for Record " + recordNo  );
                    }
                    
                    else
                    {
                        String deliveryBy = resMap.get( cellContent );
                        if( deliveryBy != null )
                        {
                            cellContent = deliveryBy;
                        }
                        
                        List<String> deliveryByList = new ArrayList<String>();
                        deliveryByList = getMaternalDeathList( "DELIVERYBY" );
                        
                        if( deliveryByList.contains( cellContent ))
                        {
                            llMdeValueAndRecord.add( dataElementId + ":" + recordCount + ":" + cellContent  );
                        }
                        else
                        {
                            importStatusMsgList.add( "Selected Delivery By not correct for Record " + recordNo  );
                        }
                    }
                }                
                
                else if( sType.equalsIgnoreCase( "llmdeathcauseofdeath" )  )
                {
                    if( cellContent == null || cellContent.length() == 0 )
                    {
                        importStatusMsgList.add( "Cause of Death is missing for Record " + recordNo  );
                    }
                    
                    else
                    {
                        String causeOfDeath = resMap.get( cellContent );
                        if( causeOfDeath != null )
                        {
                            cellContent = causeOfDeath;
                        }
                        
                        List<String> causeOfDeathList = new ArrayList<String>();
                        causeOfDeathList = getMaternalDeathList( "CAUSEOFDEATH" );
                        
                        if( causeOfDeathList.contains( cellContent ))
                        {
                            llMdeValueAndRecord.add( dataElementId + ":" + recordCount + ":" + cellContent  );
                        }
                        else
                        {
                            importStatusMsgList.add( "Selected Cause of Death not correct for Record " + recordNo  );
                        }
                    }
                }                                  
                
                else if( sType.equalsIgnoreCase( "llmdeathisaudited" )  )
                {
                    if( cellContent == null || cellContent.length() == 0 )
                    {
                        importStatusMsgList.add( "Is Audited? is missing for Record " + recordNo  );
                    }
                    
                    else
                    {
                        String isAudlited = resMap.get( cellContent );
                        if( isAudlited != null )
                        {
                            cellContent = isAudlited;
                        }
                        
                        List<String> isAudlitedList = new ArrayList<String>();
                        isAudlitedList = getMaternalDeathList( "ISAUDILTED" );
                        
                        if( isAudlitedList.contains( cellContent ))
                        {
                            llMdeValueAndRecord.add( dataElementId + ":" + recordCount + ":" + cellContent  );
                        }
                        else
                        {
                            importStatusMsgList.add( "Selected Is Audited? not correct for Record " + recordNo  );
                        }
                    }
                } 
                
                else
                {
                    break;
                }
                
                count1++;
            }
            
            if( llMdeValueAndRecord.size() != maternalDeathDeCodesList.size() )
            {
                importStatusMsgList.add( " Record - " + recordNo +". is not validated."  );
            }
            else
            {
                String insertQuery = "INSERT INTO lldatavalue (dataelementid,periodid,sourceid,categoryoptioncomboid,recordno,value,storedby,lastupdated,comment) VALUES "; 
                
                int insertFlag = 1;
                try
                {
                    int count = 1;
                    for( String llMImportParameters : llMdeValueAndRecord )
                    {
                        
                        String[] partsOfLLMImportParameters = llMImportParameters.split( ":" );
                        int dataElementId = Integer.parseInt( partsOfLLMImportParameters[0] );
                        int optionComboId = 1;
                        int tempRecordNo = Integer.parseInt( partsOfLLMImportParameters[1] );
                        String value = partsOfLLMImportParameters[2].trim();
                        
                        if ( value != null && !value.trim().equals( "" ) )
                        {
                            //insertQuery += "(" +dataElementId+","+ selectedPeriod.getId() +","+ orgUnit.getId() +","+optionComboId+","+ tempRecordNo +",'" + value + "' ,'" + storedBy + "' ,'" + lastUpdatedDate + "'  )";
                            insertQuery += "(" +dataElementId+","+ selectedPeriod.getId() +","+ orgUnit.getId() +","+optionComboId+","+ tempRecordNo +",'" + value + "' ,'" + storedBy + "' ,'" + lastUpdatedDate + "','import from excel'  )";
                            insertFlag = 2;
                        }
                        
                        if( count == llMdeValueAndRecord.size() )
                        {
                            insertQuery += ";";
                        }
                            
                        else
                        {
                            insertQuery += ",";
                        }
                        
                        count++;
                    }
                    
                    if ( insertFlag != 1 )
                    {
                        jdbcTemplate.update( insertQuery );
                    }
                }
                
                catch ( Exception e )
                {
                    message = "Exception occured while import, please check log for more details" + e.getMessage();
                }
                
                importStatusMsgList.add( " Record - " + recordNo +". is succuessfully imported."  );
                
                recordCount++;
            }
            
            recordNo++;
        }
                
        excelImportFile.close();
        statementManager.destroy();
        
    }    
    
    
    // Importing LineListing Death Data
    public void importLineListingDeath() throws IOException, BiffException
    {
        List<String> deCodesList = new ArrayList<String>();

        deCodesList.clear();

        if ( deCodesList.isEmpty() )
        {
            deCodesList = getDECodes( deCodesImportXMLFileName );
        }
        
        long t;
        Date d = new Date();
        t = d.getTime();
        java.sql.Date lastUpdatedDate = new java.sql.Date( t );
        
        int sheetNo = 0;
        
        Workbook excelImportFile = Workbook.getWorkbook( new File( excelFilePath ) );
        Sheet sheet = excelImportFile.getSheet( sheetNo );
        
        int recordCount = maxRecordNo + 1;
        int recordNo = 1;
       
        //System.out.println ( "Total Row No  " + sheet.getRows() + " Max Record No " + maxRecordNo );
        
        Integer rowStart = 3;
        Integer colStart = 1;
        
        for( int i = rowStart ; i < sheet.getRows() ; i++ )
        {
            List<String> lldeValueAndRecord = new ArrayList<String>();
            String ageType = "";
            String age = "";
            
            int count1 = 0;
            for( int j = colStart ; j <= deCodesList.size() ; j++ )
            {
                String sType = (String) serviceType.get( count1 );
                String dataElementId = deCodesList.get( count1 );
                String cellContent = sheet.getCell( j, i ).getContents().trim();
                
                //System.out.println ( i + "--" + j + "--" + cellContent + "-- DataElement Id : " + dataElementId  +  "-- Service Type : " + sType );
                
                /*
                if( cellContent != null && cellContent.length() != 0 )
                {
                    System.out.println ( i + "--" + j + "--" + cellContent + "-- DataElement Id : " + dataElementId  +  "-- Service Type : " + sType );
                    
                    lldeValueAndRecord.add( dataElementId + ":" + recordCount + ":" + cellContent  );
                    
                    
                    if( stype.equ(lldataelement-at) )
                    {
                        if( cellContent == null )
                        {
                            importStatusMsgList.add( "Age Type is missing for Row number  " + i  );
                        }
                        else
                        {
                            if( cellContent.trim().equalsIgnoreCase( "Y" ) || cellContent.trim().equalsIgnoreCase( "M" ) || cellContent.trim().equalsIgnoreCase( "D" ) )
                            {
                                ageType = cellContent;
                            }
                            else
                            {
                                importStatusMsgList.add( "Age Type is not valid, is should be either Y or M or D for Row number  " + i  );
                            }
                        }
                        
                    }
                    else if( stype.equ(lldataelement-age) )
                    {
                        age = cellContent;
                        
                        
                    }
                    else if( stype.equ(lldataelement-age) )
                    {
                        calcuate age cateory based  on age and agetype
                        agecategory
                    }
                    if( stype.equ(lldataelement-pcd) )
                    {
                        check for pcd
                    }
                    
                    llDataValueRow.add();
                    
                    
                    System.out.println ( i + "--" + j + "--" + cellContent + "-- DataElement Id : " + deCodesList.get( j ) );
                    
                }
                
                */
                
                if( sType.equalsIgnoreCase( "lldeathname" ) )
                {
                    if( cellContent == null || cellContent.length() == 0 )
                    {
                        //int importedRow =  i + 1;
                        importStatusMsgList.add( "Name is missing for Record " + recordNo  );
                    }
                    else
                    {
                        lldeValueAndRecord.add( dataElementId + ":" + recordCount + ":" + cellContent  );
                    }
                }
                
                else if( sType.equalsIgnoreCase( "lldeathvillage" ) )
                {
                    if( cellContent == null || cellContent.length() == 0 )
                    {
                        //int importedRow =  i + 1;
                        importStatusMsgList.add( "Village Name is missing for Record " + recordNo  );
                    }
                    else
                    {
                        lldeValueAndRecord.add( dataElementId + ":" + recordCount + ":" + cellContent  );
                    }
                }

                else if( sType.equalsIgnoreCase( "lldeathsex" ) )
                {
                    if( cellContent == null || cellContent.length() == 0 )
                    {
                        //int importedRow =  i + 1;
                        importStatusMsgList.add( "Sex is missing for Record " + recordNo  );
                    }
                    else
                    {
                        if( cellContent.equalsIgnoreCase( "Female" ) || cellContent.equalsIgnoreCase( "Male" ) )
                        {
                            String sex = resMap.get( cellContent );
                            if( sex != null )
                            {
                                cellContent = sex;
                            }
                            
                            lldeValueAndRecord.add( dataElementId + ":" + recordCount + ":" + cellContent  );
                        }
                        else
                        {
                            //int importedRow =  i + 1;
                            importStatusMsgList.add( "Sex is not valid, is should be either Male or Female For Record " + recordNo  );
                        }
                        
                        //lldeValueAndRecord.add( dataElementId + ":" + recordCount + ":" + cellContent  );
                    }
                }
                
                else if( sType.equalsIgnoreCase( "lldeathagetype" ) )
                {
                    if( cellContent == null || cellContent.length() == 0 )
                    {
                        //int importedRow =  i + 1;
                        importStatusMsgList.add( "Age Type is missing for Record " + recordNo  );
                    }
                    else
                    {
                        if( cellContent.trim().equalsIgnoreCase( "Years" ) || cellContent.trim().equalsIgnoreCase( "Months" ) 
                            || cellContent.trim().equalsIgnoreCase( "Weeks" ) || cellContent.trim().equalsIgnoreCase( "Days" ) 
                            || cellContent.trim().equalsIgnoreCase( "Hrs" )  )
                        {
                            
                            ageType = resMap.get( cellContent );
                            if( ageType != null )
                            {
                                cellContent = ageType;
                            }
                            
                            lldeValueAndRecord.add( dataElementId + ":" + recordCount + ":" + cellContent  );
                        }
                        else
                        {
                            //int importedRow =  i + 1;
                            importStatusMsgList.add( "Age Type is not valid, is should be either Years or Months or Weeks or Days or Hrs for Record  " + recordNo  );
                        }
                        
                    }
                }
                else if( sType.equalsIgnoreCase( "lldeathage" ) )
                {
                    if( cellContent == null || cellContent.length() == 0 )
                    {
                        importStatusMsgList.add( "Age is missing for Record " + recordNo  );
                    }
                    
                    else
                    {
                        age = cellContent;
                        
                        boolean ageValidate = validateAgeTypeAndAge( ageType, age );
                        
                        if( ageValidate )
                        {
                            lldeValueAndRecord.add( dataElementId + ":" + recordCount + ":" + age  );
                        }
                        
                        else
                        {
                            importStatusMsgList.add( "Age is not valid for Age Type  Record " + recordNo  );
                        }
                    }
                }
                                
                else if( sType.equalsIgnoreCase( "lldeathpcd" )  )
                {
                    if( cellContent == null || cellContent.length() == 0 )
                    {
                        importStatusMsgList.add( "Probable Cause of death is missing for Record " + recordNo  );
                    }
                    
                    else
                    {
                        boolean ageValidate = validateAgeTypeAndAge( ageType, age );
                        
                        if( ageValidate )
                        {
                            String causeOfDeath = resMap.get( cellContent );
                            if( causeOfDeath != null )
                            {
                                cellContent = causeOfDeath;
                            }
                            
                            List<String> probableCauseOfDeathList = new ArrayList<String>();
                            probableCauseOfDeathList = getProbableCauseOfDeath( ageType, age );
                            
                            if( probableCauseOfDeathList.contains( cellContent ))
                            {
                                lldeValueAndRecord.add( dataElementId + ":" + recordCount + ":" + cellContent  );
                            }
                            
                            else
                            {
                                importStatusMsgList.add( "Selected Probable Cause of death not correct for Record " + recordNo  );
                            }
                        }
                    }
                        
                }
                else if( sType.equalsIgnoreCase( "lldeathagecategory" )  )
                {
                    boolean ageValidate = validateAgeTypeAndAge( ageType, age );
                    
                    if( ageValidate )
                    {
                        lldeValueAndRecord.add( dataElementId + ":" + recordCount + ":" + ageCategory  );
                    }
                }
                else
                {
                    break;
                }
                
                count1++;
            }
            
            if( lldeValueAndRecord.size() != deCodesList.size() )
            {
                //System.out.println( "missing data");
                //int importedRow =  i + 1;
                importStatusMsgList.add( " Record - " + recordNo +". is not validated."  );
                
                //message += "<font color=red><strong>"+ orgUnit.getName()+ " :  Row : " + i + " missing data.<br></font></strong>";
            }
            else
            {
                //SaveRecord
                //System.out.println( " Inside Save Record Count Added " + recordCount );
                
                String insertQuery = "INSERT INTO lldatavalue (dataelementid,periodid,sourceid,categoryoptioncomboid,recordno,value,storedby,lastupdated,comment) VALUES "; 
                
                int insertFlag = 1;
                
                try
                {
                    int count = 1;
                    for( String llImportParameters : lldeValueAndRecord )
                    {
                        
                        String[] partsOfLLImportParameters = llImportParameters.split( ":" );
                        int dataElementId = Integer.parseInt( partsOfLLImportParameters[0] );
                        int optionComboId = 1;
                        int tempRecordNo = Integer.parseInt( partsOfLLImportParameters[1] );
                        String value = partsOfLLImportParameters[2].trim();
                        
                        if ( value != null && !value.trim().equals( "" ) )
                        {
                            //insertQuery += "(" +dataElementId+","+ selectedPeriod.getId() +","+ orgUnit.getId() +","+optionComboId+","+ tempRecordNo +",'"+value+"','admin' )";
                            
                            insertQuery += "(" +dataElementId+","+ selectedPeriod.getId() +","+ orgUnit.getId() +","+optionComboId+","+ tempRecordNo +",'" + value + "' ,'" + storedBy + "' ,'" + lastUpdatedDate + "','import from excel'  )";
                            insertFlag = 2;
                        }
                        
                        if( count == lldeValueAndRecord.size() )
                        {
                            insertQuery += ";";
                        }
                            
                        else
                        {
                            insertQuery += ",";
                        }
                        
                        count++;
                    }
                    
                    if ( insertFlag != 1 )
                    {
                        jdbcTemplate.update( insertQuery );
                    }
                }
                
                catch ( Exception e )
                {
                    message = "Exception occured while import, please check log for more details" + e.getMessage();
                }
                
                //System.out.println ( " query  " + insertQuery  );
                
               //System.out.println ( " Age Category " + ageCategory  );
                
                //message += " Row - " + i + ". is succuessfully imported. \n";
                
                //message += "<font color=red><strong>"+ " :  Row  : " + i +  " is succuessfully imported.<br></font></strong>";
                
                //message += "<font color=red><strong>"+ orgUnit.getName()+ " :  Row : " + i + " Imported.<br></font></strong>";
                //int importedRow =  i + 1;
                importStatusMsgList.add( " Record - " + recordNo +". is succuessfully imported."  );
                
                recordCount++;
            }
            
            recordNo++;
        }
        
        //System.out.println ( " Final Record Count  " + recordCount  );
        
        //resultStatus += "<font color=red><strong>"+ orgUnit.getName()+ " :  From : " + period.getStartDate() + " To : "  + period.getEndDate() + " Imported.<br></font></strong>";
        
        excelImportFile.close();
        statementManager.destroy();       
        
    }
    
  
    // getDECodes
    public List<String> getDECodes( String fileName )
    {

        String excelImportFolderName = "excelimport";

        List<String> deCodes = new ArrayList<String>();

        deCodes.clear();
        deCodes.clear();
        serviceType.clear();
        deCodeType.clear();
        sheetList.clear();
        rowList.clear();
        colList.clear();

        // String excelTemplatePath = System.getenv( "DHIS2_HOME" ) +
        // File.separator + raFolderName + File.separator
        // + excelImportFolderName + File.separator + reportFileNameTB;

        // String excelFilePath = System.getenv( "DHIS2_HOME" ) + File.separator
        // + raFolderName + excelImportFolderName
        // + File.separator + "pending" + File.separator + fileName;

        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName
            + File.separator + excelImportFolderName + File.separator + fileName;
        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + raFolderName + File.separator + excelImportFolderName
                    + File.separator + fileName;
            }
        }

        catch ( NullPointerException npe )
        {
            // do nothing, but we might be using this somewhere without
            // USER_HOME set, which will throw a NPE
        }

        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                // System.out.println( "There is no DECodes related XML file in
                // the user home" );
                return null;
            }

            NodeList listOfDECodes = doc.getElementsByTagName( "de-code" );
            int totalDEcodes = listOfDECodes.getLength();

            for ( int s = 0; s < totalDEcodes; s++ )
            {
                Element deCodeElement = (Element) listOfDECodes.item( s );
                NodeList textDECodeList = deCodeElement.getChildNodes();
                deCodes.add( ((Node) textDECodeList.item( 0 )).getNodeValue().trim() );
                serviceType.add( deCodeElement.getAttribute( "stype" ) );
                deCodeType.add( deCodeElement.getAttribute( "type" ) );
                sheetList.add( new Integer( deCodeElement.getAttribute( "sheetno" ) ) );
                rowList.add( new Integer( deCodeElement.getAttribute( "rowno" ) ) );
                colList.add( new Integer( deCodeElement.getAttribute( "colno" ) ) );

            }// end of for loop with s var
        }// try block end
        catch ( SAXParseException err )
        {
            System.out.println( "** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId() );
            System.out.println( " " + err.getMessage() );
        }
        catch ( SAXException e )
        {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        }
        catch ( Throwable t )
        {
            t.printStackTrace();
        }
        return deCodes;
    }// getDECodes end

    
    // validateAgeTypeAndAge
    public Boolean validateAgeTypeAndAge( String ageTypeVal, String ageValue )
    {
        Boolean isAgeTypeAndAgeValidate = false;
        
        ageCategory = "";
        
        // HOUR
        if( ageTypeVal == "HOUR" )
        {
            if( Integer.parseInt( ageValue ) > 23 || Integer.parseInt( ageValue ) < 1 )
            {
                return false;
            }
            else if( Integer.parseInt( ageValue) >= 1 && Integer.parseInt( ageValue ) <= 23 )
            {
                //var valueSaverTemp = new ValueSaver( "1030:"+recordNo, "B1DAY", '#ccffcc' );
                isAgeTypeAndAgeValidate = true;
                
                ageCategory = "B1DAY";
            }
        }
        
        // DAY
        else if( ageTypeVal == "DAY" )
        {
            if( Integer.parseInt( ageValue ) > 6 || Integer.parseInt( ageValue ) < 1 )
            {
                return false;
            }                       
            else if( Integer.parseInt( ageValue) >= 1 && Integer.parseInt( ageValue ) <= 6 )
            {
                isAgeTypeAndAgeValidate = true;
                //var valueSaverTemp = new ValueSaver( "1030:"+recordNo, "B1WEEK", '#ccffcc' );
                ageCategory = "B1WEEK";
                  
            }
            /*
            else if( Integer.parseInt( ageValue ) > 7 && Integer.parseInt( ageValue ) <= 30 )
            {
                isAgeTypeAndAgeValidate = true;
                //var valueSaverTemp = new ValueSaver( "1030:"+recordNo, "B1MONTH", '#ccffcc' );
                ageCategory = "B1MONTH";
            }
            */
            
        }
        
        // Week
        else if( ageTypeVal == "WEEK" )
        {
            if( Integer.parseInt( ageValue ) > 3 || Integer.parseInt( ageValue ) < 1 )
            {
                return false;
            }
            else if( Integer.parseInt( ageValue ) >= 1 && Integer.parseInt( ageValue ) <= 3 )
            {
                //var valueSaverTemp = new ValueSaver( "1030:"+recordNo, "B1MONTH", '#ccffcc' );
                ageCategory = "B1MONTH";
                isAgeTypeAndAgeValidate = true;                
            }
        }
        
        // Month
        else if( ageTypeVal == "MONTH" )
        {
            if( Integer.parseInt( ageValue ) > 11 || Integer.parseInt( ageValue ) < 1 )
            {
                return false;
            }
            else if( Integer.parseInt( ageValue ) >= 1 && Integer.parseInt( ageValue ) <= 11 )
            {
                //var valueSaverTemp = new ValueSaver( "1030:"+recordNo, "B1YEAR", '#ccffcc' );
                isAgeTypeAndAgeValidate = true;
                ageCategory = "B1YEAR";
            }
        }        
        else if( ageTypeVal == "YEAR" )
        {
            if( Integer.parseInt( ageValue ) > 150  )
            {
                return false;
            }
            
            else if( Integer.parseInt( ageValue ) <=  150 && Integer.parseInt( ageValue ) > 55 )
            {
                isAgeTypeAndAgeValidate = true;
                //var valueSaverTemp = new ValueSaver( "1030:"+recordNo, "O55YEAR", '#ccffcc' );
                ageCategory = "O55YEAR";
            }
            else if( Integer.parseInt( ageValue ) <= 55 && Integer.parseInt( ageValue ) >= 15 )
            {
                //var valueSaverTemp = new ValueSaver( "1030:"+recordNo, "O15YEAR", '#ccffcc' );
                isAgeTypeAndAgeValidate = true;
                ageCategory = "O15YEAR";
            }
            else if( Integer.parseInt( ageValue ) <= 14 && Integer.parseInt( ageValue ) >= 6 )
            {
                //var valueSaverTemp = new ValueSaver( "1030:"+recordNo, "O5YEAR", '#ccffcc' );
                isAgeTypeAndAgeValidate = true;
                ageCategory = "O5YEAR";
            }
            else if( Integer.parseInt( ageValue ) <= 5 && Integer.parseInt( ageValue ) >= 1 )
            {
                //var valueSaverTemp = new ValueSaver( "1030:"+recordNo, "B5YEAR", '#ccffcc' );
                isAgeTypeAndAgeValidate = true;
                ageCategory = "B5YEAR";
            }
        } 
        
        return isAgeTypeAndAgeValidate;
        
    }
    
    // getProbableCauseOfDeath
    public List<String> getProbableCauseOfDeath( String ageTypeVal, String ageValue )
    {
        List<String> probableCauseOfDeathList = new ArrayList<String>();
        
        // HOUR
        if( ageTypeVal == "HOUR" )
        {
          
            if( Integer.parseInt( ageValue) >= 1 && Integer.parseInt( ageValue ) <= 23 )
            {
                probableCauseOfDeathList.add( "WITHIN24HOURSOFBIRTH" );
            }
        }
        // DAY
        else if( ageTypeVal == "DAY" )
        {
            if( Integer.parseInt( ageValue) >= 1 && Integer.parseInt( ageValue ) <= 6 )
            {
                probableCauseOfDeathList.add( "SEPSIS" );
                probableCauseOfDeathList.add( "ASPHYXIA" );
                probableCauseOfDeathList.add( "LOWBIRTHWEIGH" );
                probableCauseOfDeathList.add( "OTHERS" );
            }
            
            /*
            else if( Integer.parseInt( ageValue ) > 7 && Integer.parseInt( ageValue ) <= 30 )
            {
                probableCauseOfDeathList.add( "SEPSIS" );
                probableCauseOfDeathList.add( "ASPHYXIA" );
                probableCauseOfDeathList.add( "LOWBIRTHWEIGH" );
                probableCauseOfDeathList.add( "OTHERS" );
            }
            */
            
        }
        // Week
        else if( ageTypeVal == "WEEK" )
        {
            if( Integer.parseInt( ageValue ) >= 1 && Integer.parseInt( ageValue ) <= 3 )
            {
                probableCauseOfDeathList.add( "SEPSIS" );
                probableCauseOfDeathList.add( "ASPHYXIA" );
                probableCauseOfDeathList.add( "LOWBIRTHWEIGH" );
                probableCauseOfDeathList.add( "OTHERS" );            
            }
        }
        
        // Month
        else if( ageTypeVal == "MONTH" )
        {
            if( Integer.parseInt( ageValue ) >= 1 && Integer.parseInt( ageValue ) <= 11 )
            {
                probableCauseOfDeathList.add( "PNEUMONIA" );
                probableCauseOfDeathList.add( "DIADIS" );
                probableCauseOfDeathList.add( "OFR" );
                probableCauseOfDeathList.add( "MEASLES" );
                probableCauseOfDeathList.add( "OTHERS" );
            }
        }        
        else if( ageTypeVal == "YEAR" )
        {
            if( Integer.parseInt( ageValue ) <=  150 && Integer.parseInt( ageValue ) > 55  )
            {
                probableCauseOfDeathList.add( "DIADIS" );
                probableCauseOfDeathList.add( "TUBER" );
                probableCauseOfDeathList.add( "RID" );
                probableCauseOfDeathList.add( "MALARIA" );
                probableCauseOfDeathList.add( "OFR" );
                probableCauseOfDeathList.add( "HIVAIDS" );
                probableCauseOfDeathList.add( "HDH" );
                probableCauseOfDeathList.add( "SND" );
                probableCauseOfDeathList.add( "AI" );
                probableCauseOfDeathList.add( "SUICIDES" );
                probableCauseOfDeathList.add( "ABS" );
                probableCauseOfDeathList.add( "OKAD" );
                probableCauseOfDeathList.add( "OKCD" );
                probableCauseOfDeathList.add( "NK" );
            }
            else if( Integer.parseInt( ageValue ) <= 55 && Integer.parseInt( ageValue ) >= 15 )
            {
                probableCauseOfDeathList.add( "DIADIS" );
                probableCauseOfDeathList.add( "TUBER" );
                probableCauseOfDeathList.add( "RID" );
                probableCauseOfDeathList.add( "MALARIA" );
                probableCauseOfDeathList.add( "OFR" );
                probableCauseOfDeathList.add( "HIVAIDS" );
                probableCauseOfDeathList.add( "HDH" );
                probableCauseOfDeathList.add( "SND" );
                probableCauseOfDeathList.add( "AI" );
                probableCauseOfDeathList.add( "SUICIDES" );
                probableCauseOfDeathList.add( "ABS" );
                probableCauseOfDeathList.add( "OKAD" );
                probableCauseOfDeathList.add( "OKCD" );
                probableCauseOfDeathList.add( "NK" );
            }
            else if( Integer.parseInt( ageValue ) <= 14 && Integer.parseInt( ageValue ) >= 6 )
            {
                probableCauseOfDeathList.add( "DIADIS" );
                probableCauseOfDeathList.add( "TUBER" );
                probableCauseOfDeathList.add( "RID" );
                probableCauseOfDeathList.add( "MALARIA" );
                probableCauseOfDeathList.add( "OFR" );
                probableCauseOfDeathList.add( "HIVAIDS" );
                probableCauseOfDeathList.add( "HDH" );
                probableCauseOfDeathList.add( "SND" );
                probableCauseOfDeathList.add( "AI" );
                probableCauseOfDeathList.add( "SUICIDES" );
                probableCauseOfDeathList.add( "ABS" );
                probableCauseOfDeathList.add( "OKAD" );
                probableCauseOfDeathList.add( "OKCD" );
                probableCauseOfDeathList.add( "NK" );
            }
            else if( Integer.parseInt( ageValue ) <= 5 && Integer.parseInt( ageValue ) >= 1 )
            {
                probableCauseOfDeathList.add( "PNEUMONIA" );
                probableCauseOfDeathList.add( "DIADIS" );
                probableCauseOfDeathList.add( "OFR" );
                probableCauseOfDeathList.add( "MEASLES" );
                probableCauseOfDeathList.add( "OTHERS" );
            }
        } 
        
        return probableCauseOfDeathList;
    }
    
    // getProbableCauseOfDeath
    public List<String> getMaternalDeathList( String listType )
    {
        List<String> maternalDeathList = new ArrayList<String>();
        
        // Death During in Line Listing Maternal Death
        if( listType == "DURINGDEATH" )
        {
            maternalDeathList.add( "FTP" );
            maternalDeathList.add( "STP" );
            maternalDeathList.add( "TTP" );
            maternalDeathList.add( "DELIVERY" );
            maternalDeathList.add( "ADW42D" );
        }
        // Delivery At in Line Listing Maternal Death
        else if( listType == "DELIVERYAT" )
        {
            maternalDeathList.add( "HOME" );
            maternalDeathList.add( "SC" );
            maternalDeathList.add( "PHC" );
            maternalDeathList.add( "CHC" );
            maternalDeathList.add( "MC" );
            maternalDeathList.add( "PVTINST" );
        }

        // Delivery By in Line Listing Maternal Death
        else if( listType == "DELIVERYBY" )
        {
            maternalDeathList.add( "UNTRAINED" );
            maternalDeathList.add( "TRAINED" );
            maternalDeathList.add( "ANM" );
            maternalDeathList.add( "NURSE" );
            maternalDeathList.add( "DOCTOR" );
            maternalDeathList.add( "OTHER" );
        }
        
        // Cause of Death in Line Listing Maternal Death
        else if( listType == "CAUSEOFDEATH" )
        {
            maternalDeathList.add( "ABORTION" );
            maternalDeathList.add( "OPL" );
            maternalDeathList.add( "FITS" );
            maternalDeathList.add( "BBCD" );
            maternalDeathList.add( "HFBD" );
            maternalDeathList.add( "MDNK" );
        }       
        
        // Is Audited? in Line Listing Maternal Death
        else if( listType == "ISAUDILTED" )
        {
            maternalDeathList.add( "Y" );
            maternalDeathList.add( "N" );
            maternalDeathList.add( "NOTKNOWN" );
        }
        
        return maternalDeathList;
    }
        
    // initializeResultMap
    public void initializeResultMap()
    {
        resMap = new HashMap<String, String>();
        
        // Sex
        resMap.put( "Male", "M" );
        resMap.put( "Female", "F" );
        
        // Age Type
        resMap.put( "Years", "YEAR" );
        resMap.put( "Months", "MONTH" );
        resMap.put( "Weeks", "WEEK" );
        resMap.put( "Days", "DAY" );
        resMap.put( "Hrs", "HOUR" );
        
        //infant Death( Up to 1 Year of age )
        resMap.put( "C01-Within 24 hrs of birth","WITHIN24HOURSOFBIRTH" );
        resMap.put( "C02-Sepsis","SEPSIS" );
        resMap.put( "C03-Asphyxia","ASPHYXIA" );
        resMap.put( "C04-Low Birth Weight (LBW) for Children upto 4 weeks of age only" ,"LOWBIRTHWEIGH" );
        resMap.put( "C05-Pneumonia", "PNEUMONIA"  );
        resMap.put( "C06-Diarrhoea", "DIADIS" );
        resMap.put( "C07-Fever related", "OFR" );
        resMap.put( "C08-Measles","MEASLES"  );
        resMap.put( "C09-Others","OTHERS" );
        
        //Adolescents and Adults
        resMap.put( "A01-Diarrhoeal diseases","DIADIS" );
        resMap.put( "A02-Tuberculosis","TUBER" );
        resMap.put( "A03-Respiratory diseases including infections (other than TB)","RID" );
        resMap.put( "A04-Malaria", "MALARIA" );
        resMap.put( "A05-Other Fever Related","OFR" );
        resMap.put( "A06-HIV/AIDS","HIVAIDS" );
        resMap.put( "A07-Heart disease/Hypertension related","HDH"  );
        resMap.put( "A08-Neurological disease including strokes","SND" );
        resMap.put( "A09-Trauma/Accidents/Burn cases","AI" );
        resMap.put( "A10-Suicide","SUICIDES" );
        resMap.put( "A11-Animal bites and stings","ABS" );
        resMap.put( "A12-Known Acute Disease","OKAD" );
        resMap.put( "A13-Known Chronic Disease","OKCD" );
        resMap.put( "A14-Causes not known","NK" );
        
        // Maternal death cause
        resMap.put( "M01-Abortion","ABORTION" );
        resMap.put( "M02-Obstructed/prolonged labour","OPL" );
        resMap.put( "M03-Severe hypertension/fits","FITS" );
        resMap.put( "M04-Bleeding","BBCD" );
        resMap.put( "M05-High fever","HFBD" );
        resMap.put( "M06-Other Causes (including causes not known)", "MDNK");
        
        // others Is Audited? Maternal death
        resMap.put( "YES","Y" );
        resMap.put( "NO","N" );
        resMap.put( "NOT KNOWN","NOTKNOWN" );
        
        // others Delivery By Maternal death
        resMap.put( "UNTRAINED","UNTRAINED" );
        resMap.put( "TRAINED","TRAINED" );
        resMap.put( "ANM", "ANM");
        resMap.put( "NURSE", "NURSE");
        resMap.put( "DOCTOR", "DOCTOR");
        resMap.put( "OTHERS", "OTHER");
        
        // others Delivery At Maternal death
        resMap.put( "HOME","HOME" );
        resMap.put( "SUBCENTER","SC" );
        resMap.put( "PHC", "PHC");
        resMap.put( "CHC", "CHC");
        resMap.put( "DH/SDH/MEDICAL COLLEGE", "MC");
        resMap.put( "PVT INST", "PVTINST");
               
        // others Death During Maternal death
        resMap.put( "FIRST TRIMESTER PREGNANCY","FTP" );
        resMap.put( "SECOND TRIMESTER PREGNANCY","STP" );
        resMap.put( "THIRD TRIMESTER PREGNANCY", "TTP");
        resMap.put( "DELIVERY", "DELIVERY");
        resMap.put( "AFTER DELIVERY WITHIN 42 DAYS", "ADW42D");       

        /*
        List<String> infantDeathCausesWithin24Hour = new ArrayList<String>();
        infantDeathCausesWithin24Hour.add( "WITHIN24HOURSOFBIRTH" );
        
        List<String> infantDeathCauses = new ArrayList<String>();
        infantDeathCauses.add( "SEPSIS" );
        infantDeathCauses.add( "ASPHYXIA" );
        infantDeathCauses.add( "LOWBIRTHWEIGH" );
        infantDeathCauses.add( "OTHERS" );
      
        List<String> deathCauses1YearTo5Year = new ArrayList<String>();
        deathCauses1YearTo5Year.add( "PNEUMONIA" );
        deathCauses1YearTo5Year.add( "DIADIS" );
        deathCauses1YearTo5Year.add( "OFR" );
        deathCauses1YearTo5Year.add( "MEASLES" );
        deathCauses1YearTo5Year.add( "OTHERS" );
        
        List<String> adultsDeathCuases = new ArrayList<String>();
        adultsDeathCuases.add( "DIADIS" );
        adultsDeathCuases.add( "TUBER" );
        adultsDeathCuases.add( "RID" );
        adultsDeathCuases.add( "MALARIA" );
        adultsDeathCuases.add( "OFR" );
        adultsDeathCuases.add( "HIVAIDS" );
        adultsDeathCuases.add( "HDH" );
        adultsDeathCuases.add( "SND" );
        adultsDeathCuases.add( "AI" );
        adultsDeathCuases.add( "SUICIDES" );
        adultsDeathCuases.add( "ABS" );
        adultsDeathCuases.add( "OKAD" );
        adultsDeathCuases.add( "OKCD" );
        adultsDeathCuases.add( "NK" );
        */
        
    }

    // validateAgeTypeAndAge
    public Boolean validateMaternalDeathAge( String ageValue )
    {
        Boolean isAgeValidate = false;
        
        if( Integer.parseInt(ageValue) >= 15 && Integer.parseInt(ageValue) <= 50 )
        {
            isAgeValidate = true;
        }
        else
        {
            return false;
        }

        return isAgeValidate;
    }
        
    
    
    
    public int moveFile( File source, File dest ) throws IOException
    {
        if ( !dest.exists() )
        {
            dest.createNewFile();
        }

        InputStream in = null;

        OutputStream out = null;

        try
        {

            in = new FileInputStream( source );

            out = new FileOutputStream( dest );

            byte[] buf = new byte[1024];

            int len;

            while ( (len = in.read( buf )) > 0 )
            {
                out.write( buf, 0, len );
            }
        }

        catch ( Exception e )
        {
            return -1;
        }

        finally
        {
            if ( in != null )
            {
                in.close();
            }
                
            if ( out != null )
            {
                out.close();
            }
                
        }
        return 1;
    }
    
}
