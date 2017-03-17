package org.hisp.dhis.reports.linelisting.action;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Workbook;
import jxl.write.WritableWorkbook;

import org.apache.velocity.tools.generic.MathTool;
import org.hisp.dhis.aggregation.AggregationService;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.datavalue.DataValueService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.indicator.IndicatorService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
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
public class GenerateLinelistingReportAnalyserResultAction_1
    implements Action
{
    private static final String NULL_REPLACEMENT = "0";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private CurrentUserService currentUserService;

    public void setCurrentUserService( CurrentUserService currentUserService )
    {
        this.currentUserService = currentUserService;
    }

    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    private PeriodService periodService;

    public void setPeriodService( PeriodService periodService )
    {
        this.periodService = periodService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }

    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }

    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }

    private AggregationService aggregationService;

    public void setAggregationService( AggregationService aggregationService )
    {
        this.aggregationService = aggregationService;
    }

    private IndicatorService indicatorService;

    public void setIndicatorService( IndicatorService indicatorService )
    {
        this.indicatorService = indicatorService;
    }

    private DataValueService dataValueService;

    public void setDataValueService( DataValueService dataValueService )
    {
        this.dataValueService = dataValueService;
    }

    private DataElementCategoryService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(
        DataElementCategoryService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }

    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------

    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------
    private InputStream inputStream;

    public InputStream getInputStream()
    {
        return inputStream;
    }

    private String fileName;

    public String getFileName()
    {
        return fileName;
    }

    private MathTool mathTool;

    public MathTool getMathTool()
    {
        return mathTool;
    }

    private List<OrganisationUnit> orgUnitList;

    public List<OrganisationUnit> getOrgUnitList()
    {
        return orgUnitList;
    }

    private Period selectedPeriod;

    public Period getSelectedPeriod()
    {
        return selectedPeriod;
    }

    private List<String> dataValueList;

    public List<String> getDataValueList()
    {
        return dataValueList;
    }

    private List<String> services;

    public List<String> getServices()
    {
        return services;
    }

    private List<String> slNos;

    public List<String> getSlNos()
    {
        return slNos;
    }

    private SimpleDateFormat simpleDateFormat;

    public SimpleDateFormat getSimpleDateFormat()
    {
        return simpleDateFormat;
    }

    private SimpleDateFormat monthFormat;

    public SimpleDateFormat getMonthFormat()
    {
        return monthFormat;
    }

    private SimpleDateFormat yearFormat;

    public SimpleDateFormat getYearFormat()
    {
        return yearFormat;
    }

    private List<String> deCodeType;

    private List<String> serviceType;

    private String reportFileNameTB;
    
    private String reportModelTB;
    
    private String reportList;

    public void setReportList( String reportList )
    {
        this.reportList = reportList;
    }

    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }

    private int availablePeriods;

    public void setAvailablePeriods( int availablePeriods )
    {
        this.availablePeriods = availablePeriods;
    }


    private List<Integer> sheetList;

    private List<Integer> rowList;

    private List<Integer> colList;

    private List<Integer> rowMergeList;

    private List<Integer> colMergeList;
    
    private List<String> dataTypeList;

    private Date sDate;

    private Date eDate;

    private OrganisationUnit currentOrgUnit;

    private Map<String, String> resMap;

    private String raFolderName;

    
    
    
    
    
    
    
    
    private List<OrganisationUnitGroup> orgUnitGroupList;
   
    private String startDate;
    private String endDate;
   
    private String reportListTB;
    private String ouNameTB;
    
    private int selectedOrgUnitGroup;
    private String selectedOrgUnitGroupTB;
  
    private boolean capturedDataTB;
    public Set<OrganisationUnit> orgUnitListAssociatedWithCurrentUser;
    
    private List<Period> selectedPeriodList = new ArrayList();
    
    private String tmpS;
    private String tmpE;
    private User currentUser = new User();
    
    
    
    
    
    
    
    
    
    

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        raFolderName = reportService.getRAFolderName();

        mathTool = new MathTool();
        services = new ArrayList<String>();
        slNos = new ArrayList<String>();
        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();

        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        monthFormat = new SimpleDateFormat( "MMMM" );
        yearFormat = new SimpleDateFormat( "yyyy" );
        
        initializeResultMap();

        List<Integer> llrecordNos = new ArrayList<Integer>();


        String deCodesXMLFileName = "";

        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        deCodesXMLFileName = selReportObj.getXmlTemplateName();
        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        
        System.out.println( selReportObj.getName() + " Report Generation Start Time is : " + new Date() );

        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();
        rowMergeList = new ArrayList<Integer>();
        colMergeList = new ArrayList<Integer>();
        dataTypeList = new ArrayList<String>();

        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
            + "template" + File.separator + reportFileNameTB;
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator  + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
       
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );

        // Period Info
        selectedPeriod = periodService.getPeriod( availablePeriods );
        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );
        eDate = format.parseDate( String.valueOf( selectedPeriod.getEndDate() ) );
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );

        // OrgUnit Info
        currentOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        llrecordNos = reportService.getLinelistingRecordNos( currentOrgUnit, selectedPeriod, deCodesXMLFileName );

        // Getting DataValues
        dataValueList = new ArrayList<String>();
        List<String> deCodesList = getDECodes( deCodesXMLFileName );
        
        
        
        return SUCCESS;
    }
    
    public void initializeResultMap()
    {
        resMap = new HashMap<String, String>();

        resMap.put( "NONE", "---" );
        resMap.put( "M", "Male" );
        resMap.put( "F", "Female" );
        resMap.put( "Y", "YES" );
        resMap.put( "N", "NO" );
        resMap.put( "NK", "NOT KNOWN" );
        resMap.put( "B1DAY", "BELOW 1 DAY" );
        resMap.put( "B1WEEK", "1 DAY - 1 WEEK" );
        resMap.put( "B1MONTH", "1 WEEK - 1 MONTH" );
        resMap.put( "B1YEAR", "1 MONTH - 1 YEAR" );
        resMap.put( "B5YEAR", "1 YEAR - 5 YEARS" );
        resMap.put( "O5YEAR", "6 YEARS - 14 YEARS" );

        resMap.put( "O15YEAR", "15 YEARS - 55 YEARS" );
        resMap.put( "O55YEAR", "OVER 55 YEARS" );

        resMap.put( "ASPHYXIA", "ASPHYXIA" );
        resMap.put( "SEPSIS", "SEPSIS" );
        resMap.put( "LOWBIRTHWEIGH", "LOWBIRTHWEIGH" );
        resMap.put( "IMMREAC", "Immunization reactions" );
        resMap.put( "PNEUMONIA", "Pneumonia" );
        resMap.put( "DIADIS", "Diarrhoeal Disease" );
        resMap.put( "MEASLES", "Measles" );
        resMap.put( "TUBER", "Tuberculosis" );
        resMap.put( "MALARIA", "Malaria" );
        resMap.put( "HIVAIDS", "HIV/AIDS" );
        resMap.put( "OFR", "Other Fever related" );
        resMap.put( "PRD", "Pregnancy Related Death( maternal mortality)" );
        resMap.put( "SRD", "Sterilisation related deaths" );
        resMap.put( "AI", "Accidents or Injuries" );
        resMap.put( "SUICIDES", "Suicides" );
        resMap.put( "ABS", "Animal Bites or Stings" );
        resMap.put( "RID", "Respiratory Infections and Disease" );
        resMap.put( "HDH", "Heart Disease and hypertension" );
        resMap.put( "SND", "Stroke and Neurological Disease" );
        resMap.put( "OKAD", "Other Known Acute Disease" );
        resMap.put( "OKCD", "Other Known Chronic Disease" );
        resMap.put( "OTHERS", "Others" );
        resMap.put( "FTP", "FIRST TRIMESTER PREGNANCY" );
        resMap.put( "STP", "SECOND TRIMESTER PREGNANCY" );
        resMap.put( "TTP", "THIRD TRIMESTER PREGNANCY" );
        resMap.put( "DELIVERY", "DELIVERY" );
        resMap.put( "ADW42D", "AFTER DELIVERY WITHIN 42 DAYS" );
        resMap.put( "HOME", "HOME" );
        resMap.put( "SC", "SUBCENTER" );
        resMap.put( "PHC", "PHC" );
        resMap.put( "CHC", "CHC" );
        resMap.put( "MC", "MEDICAL COLLEGE" );
        resMap.put( "UNTRAINED", "UNTRAINED" );
        resMap.put( "TRAINED", "TRAINED" );
        resMap.put( "ANM", "ANM" );
        resMap.put( "NURSE", "NURSE" );
        resMap.put( "DOCTOR", "DOCTOR" );
        resMap.put( "ABORTION", "ABORTION" );
        resMap.put( "OPL", "OBSTRUCTED/PROLONGED LABOUR" );
        resMap.put( "FITS", "FITS" );
        resMap.put( "SH", "SEVERE HYPERTENSION" );
        resMap.put( "BBCD", "BLEEDING BEFORE CHILD DELIVERY" );
        resMap.put( "BACD", "BLEEDING AFTER CHILD DELIVERY" );
        resMap.put( "HFBD", "HIGH FEVER BEFORE DELIVERY" );
        resMap.put( "HFAD", "HIGH FEVER AFTER DELIVERY" );

    }
    
    public List<String> getDECodes( String fileName )
    {
        List<String> deCodes = new ArrayList<String>();
        String path = System.getProperty( "user.home" ) + File.separator + "dhis" + File.separator + raFolderName
            + File.separator + fileName;
        try
        {
            String newpath = System.getenv( "DHIS2_HOME" );
            if ( newpath != null )
            {
                path = newpath + File.separator + raFolderName + File.separator + fileName;
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
                rowMergeList.add( new Integer( deCodeElement.getAttribute( "rowmerge" ) ) );
                colMergeList.add( new Integer( deCodeElement.getAttribute( "colmerge" ) ) );
                try
                {
                    dataTypeList.add( deCodeElement.getAttribute( "datatype" ) );
                }
                catch( Exception e )
                {
                    dataTypeList.add( "text" );
                }

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

}
