package org.hisp.dhis.reports.linelisting.action;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.velocity.tools.generic.MathTool;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.system.util.MathUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
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
public class GenerateLinelistingReportAnalyserResultAction implements Action
{
    //private static final String NULL_REPLACEMENT = "0";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
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
    
    private DataSetService dataSetService;

    public void setDataSetService( DataSetService dataSetService )
    {
        this.dataSetService = dataSetService;
    }

    private DataElementService dataElementService;

    public void setDataElementService( DataElementService dataElementService )
    {
        this.dataElementService = dataElementService;
    }
    
    private DataElementCategoryService dataElementCategoryOptionComboService;
    
    public void setDataElementCategoryOptionComboService( DataElementCategoryService dataElementCategoryOptionComboService )
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
    
    private Period selectedPeriod;

    public Period getSelectedPeriod()
    {
        return selectedPeriod;
    }
    
    private String organisationUnitGroupId;

    public void setOrganisationUnitGroupId( String organisationUnitGroupId )
    {
        this.organisationUnitGroupId = organisationUnitGroupId;
    }
    
    private List<Integer> sheetList;

    private List<Integer> rowList;

    private List<Integer> colList;

    private List<Integer> rowMergeList;

    private List<Integer> colMergeList;
    
    private List<String> dataTypeList;

    private Date sDate;

    //private Date eDate;

    private OrganisationUnit selectedOrgUnit;

    private Map<String, String> resMap;

    private String raFolderName;
    
    private List<String> deCodeType;

    private List<String> serviceType;

    private String reportFileNameTB;
    
    private String reportModelTB;
    
    private String deCodesXMLFileName;
    
    private DataSet dataSet;
    
    private OrganisationUnitGroup orgUnitGroup;
    
    private List<OrganisationUnit> orgUnitList;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {
        
        // Initialization
        raFolderName = reportService.getRAFolderName();

        mathTool = new MathTool();
        services = new ArrayList<String>();
        slNos = new ArrayList<String>();
        deCodeType = new ArrayList<String>();
        serviceType = new ArrayList<String>();
        
        orgUnitList = new ArrayList<OrganisationUnit>();
        
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        monthFormat = new SimpleDateFormat( "MMMM" );
        yearFormat = new SimpleDateFormat( "yyyy" );
        
        sheetList = new ArrayList<Integer>();
        rowList = new ArrayList<Integer>();
        colList = new ArrayList<Integer>();
        rowMergeList = new ArrayList<Integer>();
        colMergeList = new ArrayList<Integer>();
        dataTypeList = new ArrayList<String>();
        
        initializeResultMap();
        
        // Getting Report Details       
        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        deCodesXMLFileName = selReportObj.getXmlTemplateName();
        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        
        System.out.println( selReportObj.getName() + " Report Generation Start Time is : " + new Date() );
        
        // Period Information
        selectedPeriod = periodService.getPeriod( availablePeriods );
        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );
        //eDate = format.parseDate( String.valueOf( selectedPeriod.getEndDate() ) );
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        // dataSet Related Information
        if( selReportObj.getDataSetIds() != null )
        {
            dataSet = dataSetService.getDataSet( Integer.parseInt( selReportObj.getDataSetIds() ) );
        }
        
        // OrgUnit Info
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        
        List<OrganisationUnit> orgGroupMembers = new ArrayList<OrganisationUnit>();
        List<OrganisationUnit> dataSetSources = new ArrayList<OrganisationUnit>( dataSet.getSources() );
        if ( organisationUnitGroupId.equalsIgnoreCase( "ALL" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );
            //System.out.println(  " Size of Org Unit 1 : " + orgUnitList.size()  );
            orgUnitList.retainAll( dataSetSources );
            //System.out.println(  " Size of Org Unit 2 : " + orgUnitList.size()  );
            Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
        }
        else
        {
            orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( Integer.parseInt( organisationUnitGroupId ) );
            orgGroupMembers = new ArrayList<OrganisationUnit>( orgUnitGroup.getMembers() );
            //System.out.println(  " Size of Org Unit 1 : " + orgGroupMembers.size()  );
            
            orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( selectedOrgUnit.getId() ) );
            //System.out.println(  " Size of Org Unit 2 : " + orgUnitList.size()  );
            orgUnitList.retainAll( orgGroupMembers );
            //System.out.println(  " Size of Org Unit 3 : " + orgUnitList.size()  );
            orgUnitList.retainAll( dataSetSources );
            //System.out.println(  " Size of Org Unit 4 : " + orgUnitList.size()  );
            Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
        }
        
        //System.out.println(  " Final Size of Org Unit : " + orgUnitList.size()  );
        
        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );

        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );
        
        List<String> deCodesList = getDECodes( deCodesXMLFileName );
        
        int recordCount = 0;
        Iterator<OrganisationUnit> organisationUnitItr = orgUnitList.iterator();

        while( organisationUnitItr.hasNext() )
        {
            OrganisationUnit currentOrgUnit = (OrganisationUnit) organisationUnitItr.next();
            List<Integer> llrecordNos = new ArrayList<Integer>();
            
            llrecordNos = reportService.getLinelistingRecordNos( currentOrgUnit, selectedPeriod, deCodesXMLFileName );
            
            //System.out.println(  " Record No : " + llrecordNos.size() + " OrgUnit : " + currentOrgUnit.getName() );
            
            int flag = 0;
            if ( llrecordNos.size() == 0 )
            {
                flag = 1;
            }
               
            Iterator<Integer> recordIt = llrecordNos.iterator();
            while ( recordIt.hasNext() )
            {
                Integer recordNo = -1;
                if ( flag == 0 )
                {
                    recordNo = (Integer) recordIt.next();
                }
                
                flag = 0;

                Iterator<String> it1 = deCodesList.iterator();
                int count1 = 0;
                while ( it1.hasNext() )
                {
                    String deCodeString = (String) it1.next();

                    //String deType = (String) deCodeType.get( count1 );
                    String sType = (String) serviceType.get( count1 );
                    String dataType = dataTypeList.get( count1 );
                    String tempStr = "";

                    if ( deCodeString.equalsIgnoreCase( "SELECTEDFACILITY" ) )
                    {
                        tempStr = selectedOrgUnit.getName();
                    }
                   
                    else if ( deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) )
                    {
                        tempStr = monthFormat.format( sDate );
                    }
                   
                    else if ( deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) )
                    {
                        tempStr = yearFormat.format( sDate );
                    }
                    
                    else if ( deCodeString.equalsIgnoreCase( "ORGHARARCHY" ) )
                    {
                        tempStr = getHierarchyOrgunit( currentOrgUnit );
                    }
                    
                    else if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                    {
                        tempStr = currentOrgUnit.getName();
                    }
                    
                    else if ( deCodeString.equalsIgnoreCase( "SLNO" ) )
                    {
                        tempStr = "" + (recordCount + 1);
                    }
                    else if ( deCodeString.equalsIgnoreCase( "NA" ) )
                    {
                        tempStr = " ";
                    }
                    else
                    {
                        if ( sType.equalsIgnoreCase( "lldataelement" ) )
                        {
                            tempStr = getLLDataValue( deCodeString, selectedPeriod, currentOrgUnit, recordNo, dataType );
                        }
                    }
                    
                    int tempRowNo = rowList.get( count1 );
                    int tempRowNo1 = tempRowNo;
                    int tempColNo = colList.get( count1 );
                    int sheetNo = sheetList.get( count1 );
                    int tempMergeCol = colMergeList.get( count1 );
                    int tempMergeRow = rowMergeList.get( count1 );

                    WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                    if ( tempStr == null || tempStr.trim().equals( "" ) )
                    {
                        
                    }
                    else
                    {
                        //System.out.println( deCodeString + " : " + tempStr );

                        String tstr = resMap.get( tempStr.trim() );
                        if ( tstr != null )
                            tempStr = tstr;

                        if ( reportModelTB.equalsIgnoreCase( "DYNAMIC-DATAELEMENT" ) )
                        {
                            if ( deCodeString.equalsIgnoreCase( "SELECTEDFACILITY" ) || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) )
                            {

                            }
                           
                            else
                            {
                                tempRowNo += recordCount;
                            }

                            WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo1 );

                            CellFormat cellFormat = cell.getCellFormat();

                            WritableCellFormat wCellformat = new WritableCellFormat();

                            wCellformat.setBorder( Border.ALL, BorderLineStyle.THICK );
                            wCellformat.setWrap( true );
                            wCellformat.setAlignment( Alignment.CENTRE );
                            wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );

                            if ( cellFormat != null )
                            {
                                if ( tempMergeCol > 0 || tempMergeRow > 0 )
                                {
                                    sheet0.mergeCells( tempColNo, tempRowNo, tempColNo + tempMergeCol, tempRowNo + tempMergeRow );
                                }
                                
                                if ( deCodeString.equalsIgnoreCase( "ORGHARARCHY" ) )
                                {
                                    try
                                    {
                                        sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), getCellFormat1() ) );
                                    }
                                    catch( Exception e )
                                    {
                                        sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, getCellFormat1() ) );
                                    }
                                }
                                else
                                {
                                    try
                                    {
                                        sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), getCellFormat2() ) );
                                    }
                                    catch( Exception e )
                                    {
                                        sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, getCellFormat2() ) );
                                    }
                                }
                            }
                            else
                            {
                                if ( deCodeString.equalsIgnoreCase( "ORGHARARCHY" ) )
                                {
                                    try
                                    {
                                        sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), getCellFormat1() ) );
                                    }
                                    catch( Exception e )
                                    {
                                        sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, getCellFormat1() ) );
                                    }
                                }
                                else
                                {
                                    try
                                    {
                                        sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), getCellFormat2() ) );
                                    }
                                    catch( Exception e )
                                    {
                                        sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, getCellFormat2() ) );
                                    }
                                }
                            }
                            
                            //System.out.println( " Row No --" + tempRowNo + " : Col No --" + tempColNo   + " : " + recordCount + " : " + currentOrgUnit.getName() + " : " + deCodeString + " : " + tempStr );
                        }
                    }
                    count1++;
                }// inner while loop end
                
                
                recordCount++;
            }// outer while loop end
            
        }
        
        //System.out.println( "noOfRecords:" + recordCount );

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        System.out.println( selReportObj.getName() + " Report Generation End Time is : " + new Date() );

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + selectedOrgUnit.getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( selectedPeriod.getStartDate() ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        outputReportFile.deleteOnExit();
        
        return SUCCESS;
    }
    
    // initialize Result Map
    public void initializeResultMap()
    {
        resMap = new HashMap<String, String>();

        // Line Listing Death Age Type
        resMap.put( "YEAR", "Years" );
        resMap.put( "MONTH", "Months" );
        resMap.put( "WEEK", "Weeks" );
        resMap.put( "HOUR", "Hrs" );
        resMap.put( "DAY", "Days" );
        
        // Line Listing Death Age Category
        resMap.put( "NONE", "---" );
        resMap.put( "M", "Male" );
        resMap.put( "F", "Female" );
        resMap.put( "Y", "YES" );
        resMap.put( "N", "NO" );
        resMap.put( "B1WEEK", "1 DAY - 1 WEEK" );
        resMap.put( "B1MONTH", "1 WEEK - 1 MONTH" );
        resMap.put( "B1YEAR", "1 MONTH - 1 YEAR" );
        resMap.put( "B5YEAR", "1 YEAR - 5 YEARS" );
        resMap.put( "O5YEAR", "6 YEARS - 14 YEARS" );
        resMap.put( "O15YEAR", "15 YEARS - 55 YEARS" );
        resMap.put( "O55YEAR", "OVER 55 YEARS" );
        resMap.put( "IMMREAC", "Immunization reactions" );
        resMap.put( "PRD", "Pregnancy Related Death( maternal mortality)" );
        resMap.put( "SRD", "Sterilisation related deaths" );
        
        //infant Death( Up to 1 Year of age )
        resMap.put( "WITHIN24HOURSOFBIRTH", "C01-Within 24 hrs of birth" );
        resMap.put( "B1DAY", "C01-WITHIN 24 HOURS OF BIRTH" );
        resMap.put( "SEPSIS", "C02-Sepsis" );
        resMap.put( "ASPHYXIA", "C03-Asphyxia" );
        resMap.put( "LOWBIRTHWEIGH", "C04-Low Birth Wight(LBW) for Children upto 4 weeks of age only" );
        resMap.put( "PNEUMONIA", "C05-Pneumonia" );
        resMap.put( "MEASLES", "C08-Measles" );
        resMap.put( "OTHERS", "C09-Others" );
        
        
        //Adolescents and Adults
        resMap.put( "DIADIS", "A01-Diarrhoeal diseases" );
        resMap.put( "TUBER", "A02-Tuberculosis" );
        resMap.put( "RID", "A03-Respiratory disease including infections(other than TB)" );
        resMap.put( "MALARIA", "A04-Malaria" );
        resMap.put( "OFR", "A05-Other Fever related" );
        resMap.put( "HIVAIDS", "A06-HIV/AIDS" );
        resMap.put( "HDH", "A07-Heart disease/Hypertension related" );
        resMap.put( "SND", "A08-Neurological disease including Strokes" );
        resMap.put( "AI", "A09-Trauma/Accidents/Burn cases" );
        resMap.put( "SUICIDES", "A10-Suicides" );
        resMap.put( "ABS", "A11-Animal Bites or Stings" );
        resMap.put( "OKAD", "A12-Known Acute Disease" );
        resMap.put( "OKCD", "A13-Known Chronic Disease" );
        resMap.put( "NK", "A14-Causes not known" );
        
        // Maternal death cause
        resMap.put( "ABORTION", "M01-Abortion" );
        resMap.put( "OPL", "M02-Obstructed/prolonged labour" );
        resMap.put( "SH", "M03-Severe hypertension/fits" );
        resMap.put( "FITS", "M03-Severe hypertension/fits" );
        resMap.put( "BBCD", "M04-Bleeding" );
        resMap.put( "BACD", "M04-Bleeding" );
        resMap.put( "HFBD", "M05-High fever" );
        resMap.put( "HFAD", "M05-High fever" );
        resMap.put( "MDNK", "M06-Other Causes (including cause not known)" );
        
        
        
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
        resMap.put( "OTHER", "OTHERS" );
        
        
        resMap.put( "NOTKNOWN", "NOT KNOWN" );
        
        
        
 
    }
    
    public WritableCellFormat getCellFormat1() throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.LEFT );
        wCellformat.setVerticalAlignment( VerticalAlignment.TOP );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getCellFormat2() throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setVerticalAlignment( VerticalAlignment.TOP );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    // get DECodes
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
    
    // get Line Listing DataValue
    public String getLLDataValue( String formula, Period period, OrganisationUnit organisationUnit, Integer recordNo, String dataType )
    {
        Statement st1 = null;
        ResultSet rs1 = null;

        String query = "";
        try
        {
            //int deFlag1 = 0;
            //int deFlag2 = 0;
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( formula );
            StringBuffer buffer = new StringBuffer();

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                String optionComboIdStr = replaceString.substring( replaceString.indexOf( '.' ) + 1, replaceString.length() );

                replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                int dataElementId = Integer.parseInt( replaceString );
                int optionComboId = Integer.parseInt( optionComboIdStr );

                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optionComboId );

                if ( dataElement == null || optionCombo == null )
                {
                    replaceString = "";
                    matcher.appendReplacement( buffer, replaceString );
                    continue;
                }

                query = "SELECT value FROM lldatavalue WHERE sourceid = " + organisationUnit.getId()
                    + " AND periodid = " + period.getId() + " AND dataelementid = " + dataElement.getId()
                    + " AND recordno = " + recordNo;
                // rs1 = st1.executeQuery( query );

                SqlRowSet sqlResultSet = jdbcTemplate.queryForRowSet( query );

                String tempStr = "";

                if ( sqlResultSet.next() )
                {
                    tempStr = sqlResultSet.getString( 1 );
                }

                replaceString = tempStr;

                matcher.appendReplacement( buffer, replaceString );
            }

            matcher.appendTail( buffer );

            String resultValue = "";
            
            if( dataType.equalsIgnoreCase( "number" ) )
            {
                double d = 0.0; 
                try 
                { 
                    d = MathUtils.calculateExpression( buffer.toString() ); 
                } 
                catch ( Exception e ) 
                { 
                    d = 0.0; 
                }
                
                if ( d == -1 ) d = 0.0; 
                else 
                { 
                    d = Math.round( d * Math.pow( 10, 1 ) ) / Math.pow( 10, 1 );
                    resultValue = "" + d; 
                }
            }
            else
            {
                resultValue = buffer.toString();
            }
            
            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
        catch ( Exception e )
        {
            System.out.println( "SQL Exception : " + e.getMessage() );
            return null;
        }
        finally
        {
            try
            {
                if ( st1 != null )
                    st1.close();

                if ( rs1 != null )
                    rs1.close();
            }
            catch ( Exception e )
            {
                System.out.println( "SQL Exception : " + e.getMessage() );
                return null;
            }
        }// finally block end
    }
    
    //get Hierarchy Orgunit
    private String getHierarchyOrgunit( OrganisationUnit orgunit )
    {
        //String hierarchyOrgunit = orgunit.getName();
        String hierarchyOrgunit = "";
       
        while ( orgunit.getParent() != null )
        {
            /*
            if( organisationUnitService.getLevelOfOrganisationUnit( orgunit.getId() ) == -1 )
            {
                break;
            }
            */
            
            hierarchyOrgunit = orgunit.getParent().getName() + "/" + hierarchyOrgunit;

            orgunit = orgunit.getParent();
        }
        
        hierarchyOrgunit = hierarchyOrgunit.substring( hierarchyOrgunit.indexOf( "/" ) + 1 );
        
        return hierarchyOrgunit;
    }    
    
    
    
}
