package org.hisp.dhis.reports.linelisting.action;

import static org.hisp.dhis.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.util.TextUtils.getCommaDelimitedString;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.write.WritableCellFormat;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.velocity.tools.generic.MathTool;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataelement.DataElement;
import org.hisp.dhis.dataelement.DataElementCategoryOptionCombo;
import org.hisp.dhis.dataelement.DataElementCategoryService;
import org.hisp.dhis.dataelement.DataElementService;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;
import org.hisp.dhis.system.util.MathUtils;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class GenerateLinelistingWebPortalReportAnalyserResultAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private final String GENERATEAGGDATA = "generateaggdata";

    private final String USEEXISTINGAGGDATA = "useexistingaggdata";

    private final String USECAPTUREDDATA = "usecaptureddata";
    
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

    public OrganisationUnitService getOrganisationUnitService()
    {
        return organisationUnitService;
    }
    
    private DataElementCategoryService dataElementCategoryOptionComboService;

    public void setDataElementCategoryOptionComboService(
        DataElementCategoryService dataElementCategoryOptionComboService )
    {
        this.dataElementCategoryOptionComboService = dataElementCategoryOptionComboService;
    }
  
    /*
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    */
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    // -------------------------------------------------------------------------
    // Properties Getter and Setter
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

    private String reportFileNameTB;

    private String reportModelTB;

    private String reportList;

    public void setReportList( String reportList )
    {
        this.reportList = reportList;
    }

    private String ouIDTB;
    
    public void setOuIDTB( String ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }
    
    private int availablePeriods;

    public void setAvailablePeriods( int availablePeriods )
    {
        this.availablePeriods = availablePeriods;
    }

    private Date sDate;

    private Date eDate;

    private OrganisationUnit currentOrgUnit;

    private Map<String, String> resMap;

    private Map<String, String> resMapForDeath;

    Connection con = null;

    private String raFolderName;

    private SimpleDateFormat simpleMonthFormat;

    
    private String aggData;
    
    public void setAggData( String aggData )
    {
        this.aggData = aggData;
    }
    
    //private Map<String, String> trackedEntityDataValueMap;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute()
        throws Exception
    {
        
        // Initialization
        raFolderName = reportService.getRAFolderName();

        mathTool = new MathTool();
        services = new ArrayList<String>();
        slNos = new ArrayList<String>();
        
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        monthFormat = new SimpleDateFormat( "MMMM" );
        yearFormat = new SimpleDateFormat( "yyyy" );
        simpleMonthFormat = new SimpleDateFormat( "MMM" );
        
        List<Integer> llrecordNos = new ArrayList<Integer>();
        
        initializeResultMap();

        initializeLLDeathResultMap();
        
        System.out.println( "Report Generation Start Time is : \t" + new Date() );
        
        // Getting Report Details
        String deCodesXMLFileName = "";

        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        deCodesXMLFileName = selReportObj.getXmlTemplateName();
        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        String parentUnit = "";

        if ( reportModelTB.equalsIgnoreCase( "DYNAMIC-ORGUNIT" )
            || reportModelTB.equalsIgnoreCase( "DYNAMIC-DATAELEMENT" ) )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
            Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
        }
        else if ( reportModelTB.equalsIgnoreCase( "STATIC" ) || reportModelTB.equalsIgnoreCase( "STATIC-DATAELEMENTS" )
            || reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" ) )
        {
            orgUnitList = new ArrayList<OrganisationUnit>();
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList.add( orgUnit );
        }
        else if ( reportModelTB.equalsIgnoreCase( "dynamicwithrootfacility" ) )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
            Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
            orgUnitList.add( orgUnit );

            parentUnit = orgUnit.getName();
        }
                
        
        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator
            + "template" + File.separator + reportFileNameTB;
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";

        FileInputStream tempFile = new FileInputStream( new File( inputTemplatePath ) );
        HSSFWorkbook apachePOIWorkbook = new HSSFWorkbook( tempFile );

        // Period Info
        selectedPeriod = periodService.getPeriod( availablePeriods );
        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );
        eDate = format.parseDate( String.valueOf( selectedPeriod.getEndDate() ) );
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        // collect periodId by commaSepareted
        List<Period> tempPeriodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
        
        Collection<Integer> tempPeriodIds = new ArrayList<Integer>( getIdentifiers(Period.class, tempPeriodList ) );
        
        String periodIdsByComma = getCommaDelimitedString( tempPeriodIds );
        
        // OrgUnit Info
        currentOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        //Collection<Integer> tempOrgUnitIds = new ArrayList<Integer>( getIdentifiers(OrganisationUnit.class, orgUnitList ) );
        //String tempOrgUnitIdsByComma = getCommaDelimitedString( tempOrgUnitIds );
        
        // Getting DataValues
        dataValueList = new ArrayList<String>();
        // List<String> deCodesList = getDECodes( deCodesXMLFileName );
        
        
        // Getting DataValues
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );
        List<Report_inDesign> reportDesignListLLDeath = reportService.getReportDesign( deCodesXMLFileName );
        
        // collect dataElementIDs by commaSepareted
        String dataElmentIdsByComma = reportService.getDataelementIds( reportDesignList );
        String tempDataElmentIdsByComma = "468,469,470,471,472,473";
        
        Integer programId = 465;
        Integer programStageId = 466;
        
        llrecordNos = new ArrayList<Integer>( reportService.getProgramStageInstanceIds( programId, programStageId, currentOrgUnit.getId(), selectedPeriod ));
        
        Map<String, String> trackedEntityDataValueMap = new HashMap<String, String>();
        //trackedEntityDataValueMap = new HashMap<String, String>( reportService.getTrackedEntityDataValue( programId, programStageId, ""+currentOrgUnit.getId(), tempDataElmentIdsByComma, selectedPeriod ));
        
        trackedEntityDataValueMap.putAll( reportService.getTrackedEntityDataValue( programId, programStageId, ""+currentOrgUnit.getId(), tempDataElmentIdsByComma, selectedPeriod ) );
        
        //System.out.println( " Size of tracked Entity DataValue Map  1 ======  :" + trackedEntityDataValueMap.size() ); 
        
        int orgUnitCount = 0;
        Iterator<OrganisationUnit> it = orgUnitList.iterator();
        
        while ( it.hasNext() )
        {
            OrganisationUnit currentOrgUnit = (OrganisationUnit) it.next();
            
            Map<String, String> aggDeMap = new HashMap<String, String>();
            
            if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
            {
                aggDeMap.putAll( reportService.getResultDataValueFromAggregateTable( currentOrgUnit.getId(), dataElmentIdsByComma, periodIdsByComma ) );
            }
            else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
            {
                List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( currentOrgUnit.getId() ) );
                List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, childOrgUnitTree ) );
                String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );

                aggDeMap.putAll( reportService.getAggDataFromDataValueTable( childOrgUnitsByComma, dataElmentIdsByComma, periodIdsByComma ) );
            }
            else if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
            {
                aggDeMap.putAll( reportService.getAggDataFromDataValueTable( ""+currentOrgUnit.getId(), dataElmentIdsByComma, periodIdsByComma ) );
            }
            
            //int count1 = 0;
            Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
            while ( reportDesignIterator.hasNext() )
            {
                Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator.next();

                String deType = report_inDesign.getPtype();
                String sType = report_inDesign.getStype();
                String deCodeString = report_inDesign.getExpression();
                String tempStr = "";

                Calendar tempStartDate = Calendar.getInstance();
                Calendar tempEndDate = Calendar.getInstance();
                List<Calendar> calendarList = new ArrayList<Calendar>( reportService.getStartingEndingPeriods( deType,
                    selectedPeriod ) );
                if ( calendarList == null || calendarList.isEmpty() )
                {
                    tempStartDate.setTime( selectedPeriod.getStartDate() );
                    tempEndDate.setTime( selectedPeriod.getEndDate() );
                    return SUCCESS;
                }
                else
                {
                    tempStartDate = calendarList.get( 0 );
                    tempEndDate = calendarList.get( 1 );
                }

                if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = currentOrgUnit.getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ) )
                {
                    tempStr = parentUnit;
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getParent().getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getParent().getParent().getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getParent().getParent().getParent().getName();
                }
                else if ( deCodeString.equalsIgnoreCase( "PERIOD" )
                    || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) )
                {
                    tempStr = simpleDateFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) )
                {
                    tempStr = monthFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                {
                    tempStr = yearFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" ) )
                {
                    tempStr = simpleMonthFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" ) )
                {
                    tempStr = simpleMonthFormat.format( eDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTH-START" ) )
                {
                    tempStr = monthFormat.format( sDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "MONTH-END" ) )
                {
                    tempStr = monthFormat.format( eDate );
                }
                else if ( deCodeString.equalsIgnoreCase( "SLNO" ) )
                {
                    tempStr = "" + (orgUnitCount + 1);
                }
                else if ( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    tempStr = " ";
                }
                else
                {
                    if ( sType.equalsIgnoreCase( "dataelement" ) )
                    {
                        if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                        {
                            tempStr = getAggVal( deCodeString, aggDeMap );
                            
                            if ( deCodeString.equalsIgnoreCase( "[1.1]" ) || deCodeString.equalsIgnoreCase( "[2.1]" ) || deCodeString.equalsIgnoreCase( "[153.1]" ) 
                                || deCodeString.equalsIgnoreCase( "[157.1]" ) || deCodeString.equalsIgnoreCase( "[158.1]" )
                                || deCodeString.equalsIgnoreCase( "[160.1]" ) || deCodeString.equalsIgnoreCase( "[5990.1]" ) )
                            {
                                //System.out.println( " USEEXISTINGAGGDATA Before Converting : SType : " + sType + " DECode : " + deCodeString + "   TempStr : " + tempStr );
                                
                                if( tempStr.equalsIgnoreCase( "0.0" ) )
                                {
                                    tempStr = ""+ 1.0;
                                }
                                else if ( tempStr.equalsIgnoreCase( "1.0" ) )
                                {
                                    tempStr = ""+ 0.0;
                                }
                                else
                                {
                                }
                                //System.out.println( "  USEEXISTINGAGGDATA After Converting : SType : " + sType + " DECode : " + deCodeString + "   TempStr : " + tempStr );
                            }
                        }
                        else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                        {
                            tempStr = getAggVal( deCodeString, aggDeMap );
                            
                            if ( deCodeString.equalsIgnoreCase( "[1.1]" ) || deCodeString.equalsIgnoreCase( "[2.1]" ) || deCodeString.equalsIgnoreCase( "[153.1]" ) 
                                || deCodeString.equalsIgnoreCase( "[157.1]" ) || deCodeString.equalsIgnoreCase( "[158.1]" )
                                || deCodeString.equalsIgnoreCase( "[160.1]" ) || deCodeString.equalsIgnoreCase( "[5990.1]" ) )
                            {
                                //System.out.println( " GENERATEAGGDATA Before Converting : SType : " + sType + " DECode : " + deCodeString + "   TempStr : " + tempStr );
                                
                                if( tempStr.equalsIgnoreCase( "0.0" ) )
                                {
                                    tempStr = ""+ 1.0;
                                }
                                else if ( tempStr.equalsIgnoreCase( "1.0" ) )
                                {
                                    tempStr = ""+ 0.0;
                                }
                                else
                                {
                                }
                                //System.out.println( " GENERATEAGGDATA After Converting : SType : " + sType + " DECode : " + deCodeString + "   TempStr : " + tempStr );
                            }
                        }
                        
                        else if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) ) 
                        {
                            tempStr = getAggVal( deCodeString, aggDeMap );
                            
                            //System.out.println( " USECAPTUREDDATA Before Converting : SType : " + sType + " DECode : " + deCodeString + "   TempStr : " + tempStr );
                            
                            if ( deCodeString.equalsIgnoreCase( "[1.1]" ) || deCodeString.equalsIgnoreCase( "[2.1]" ) || deCodeString.equalsIgnoreCase( "[153.1]" ) 
                                || deCodeString.equalsIgnoreCase( "[157.1]" ) || deCodeString.equalsIgnoreCase( "[158.1]" )
                                || deCodeString.equalsIgnoreCase( "[160.1]" ) || deCodeString.equalsIgnoreCase( "[5990.1]" ))
                            {
                                //System.out.println( " USECAPTUREDDATA Before Converting : SType : " + sType + " DECode : " + deCodeString + "   TempStr : " + tempStr );
                                
                                if( tempStr.equalsIgnoreCase( "0.0" ) )
                                {
                                    tempStr = ""+ 1.0;
                                }
                                else if ( tempStr.equalsIgnoreCase( "1.0" ) )
                                {
                                    tempStr = ""+ 0.0;
                                }
                                else
                                {
                                }
                                //System.out.println( " USECAPTUREDDATA After Converting : SType : " + sType + " DECode : " + deCodeString + "   TempStr : " + tempStr );
                            }
                        }
                     
                    }
                    else if ( sType.equalsIgnoreCase( "dataelement_institution" ) )
                    {
                        if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                        {
                            tempStr = getAggVal( deCodeString, aggDeMap );
                            
                            if( tempStr.equalsIgnoreCase( "0.0" ) )
                            {
                                tempStr = ""+ 1.0;
                            }
                            else if ( tempStr.equalsIgnoreCase( "1.0" ) )
                            {
                                tempStr = ""+ 0.0;
                            }
                            else
                            {
                            }
                        }
                        else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                        {
                            tempStr = getAggVal( deCodeString, aggDeMap );
                            
                            if( tempStr.equalsIgnoreCase( "0.0" ) )
                            {
                                tempStr = ""+ 1.0;
                            }
                            else if ( tempStr.equalsIgnoreCase( "1.0" ) )
                            {
                                tempStr = ""+ 0.0;
                            }
                            else
                            {
                            }
                        }
                        
                        else if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) ) 
                        {
                            tempStr = getAggVal( deCodeString, aggDeMap );
                            
                            if( tempStr.equalsIgnoreCase( "0.0" ) )
                            {
                                tempStr = ""+ 1.0;
                            }
                            else if ( tempStr.equalsIgnoreCase( "1.0" ) )
                            {
                                tempStr = ""+ 0.0;
                            }
                            else
                            {
                            }
                        }
                     
                        //tempStr = reportService.getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );
                    }
                    else if ( sType.equalsIgnoreCase( "dataelement-boolean" ) )
                    {
                        tempStr = reportService.getBooleanDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );
                    }
                    else
                    {
                        //System.out.println( " SType : " + sType + " DECode : " + deCodeString  );
                        tempStr = reportService.getResultIndicatorValue( deCodeString, tempStartDate.getTime(),tempEndDate.getTime(), currentOrgUnit );
                    }
                }

                int tempRowNo = report_inDesign.getRowno();
                int tempColNo = report_inDesign.getColno();
                int sheetNo = report_inDesign.getSheetno();
                
                Sheet sheet0 = apachePOIWorkbook.getSheetAt( sheetNo );
                
                if ( sType.equalsIgnoreCase( "lldeathdataelement_name" ) || sType.equalsIgnoreCase( "lldeathdataelement_sex" ) || sType.equalsIgnoreCase( "lldeathdataelement_age_type" ) 
                     || sType.equalsIgnoreCase( "lldeathdataelement_age" )  ||  sType.equalsIgnoreCase( "lldeathdataelement_cause" )  )
                {
                    continue;
                }
                
                if ( tempStr == null || tempStr.equals( " " ) )
                {
                    
                }

                else
                {
                    if ( reportModelTB.equalsIgnoreCase( "DYNAMIC-ORGUNIT" ) )
                    {
                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPP" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPPP" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                        {
                        }
                        else if ( deCodeString.equalsIgnoreCase( "PERIOD" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" )
                            || deCodeString.equalsIgnoreCase( "MONTH-START" )
                            || deCodeString.equalsIgnoreCase( "MONTH-END" )
                            || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" )
                            || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" )
                            || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-START" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-END" )
                            || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" )
                            || deCodeString.equalsIgnoreCase( "YEAR-END" )
                            || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                        {
                        }
                        else
                        {
                            tempColNo += orgUnitCount;
                        }
                    }
                    else if ( reportModelTB.equalsIgnoreCase( "dynamicwithrootfacility" ) )
                    {
                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" )
                            || deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPP" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPPP" )
                            || deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                        {
                        }
                        else if ( deCodeString.equalsIgnoreCase( "PERIOD" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" )
                            || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" )
                            || deCodeString.equalsIgnoreCase( "MONTH-START" )
                            || deCodeString.equalsIgnoreCase( "MONTH-END" )
                            || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" )
                            || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" )
                            || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-START" )
                            || deCodeString.equalsIgnoreCase( "QUARTER-END" )
                            || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" )
                            || deCodeString.equalsIgnoreCase( "YEAR-END" )
                            || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                        {
                        }
                        else
                        {
                            tempRowNo += orgUnitCount;
                        }
                    }

                    try
                    {
                        //sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), wCellformat ) );
                        Row row = sheet0.getRow( tempRowNo );
                        Cell cell = row.getCell( tempColNo );
                        cell.setCellValue( Double.parseDouble( tempStr ) );
                        
                    }
                    catch ( Exception e )
                    {
                        //sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                        Row row = sheet0.getRow( tempRowNo );
                        Cell cell = row.getCell( tempColNo );
                        cell.setCellValue( tempStr );
                        
                    }
                    
                }

                //count1++;
            }// inner while loop end
            orgUnitCount++;
        }// outer while loop end
        
        
        // for LineListing data
        
        //int tempLLDeathRowNo = 0;
        int flag = 0;
        if ( llrecordNos.size() == 0 )
        {
            flag = 1;
        }
            
        Iterator<Integer> itlldeath = llrecordNos.iterator();
        int recordCount = 0;
        int totalLineListingRecordCount = 0;
        if( llrecordNos != null && llrecordNos.size() > 0 )
        {
            //int currentRowNo = 0;
            
            while ( itlldeath.hasNext() )
            {
                if( totalLineListingRecordCount >= 600 )
                {
                    break;
                }
                else
                {
                    totalLineListingRecordCount ++;
                }
                
                Integer programStageInstanceId = -1;
                if ( flag == 0 )
                {
                    programStageInstanceId = (Integer) itlldeath.next();
                }
                
                flag = 0;
                
                // Iterator<String> it1 = deCodesList.iterator();
                Iterator<Report_inDesign> reportDesignIterator = reportDesignListLLDeath.iterator();
                //int count1 = 0;
                while ( reportDesignIterator.hasNext() )
                {
                  
                    Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator.next();
    
                    String deType = report_inDesign.getPtype();
                    String sType = report_inDesign.getStype();
                    String deCodeString = report_inDesign.getExpression();
                    String tempStr = "";
                    //String tempLLDeathValuStr = "";
                    //System.out.println( "SType--" + sType );
                    Calendar tempStartDate = Calendar.getInstance();
                    Calendar tempEndDate = Calendar.getInstance();
                    
                    List<Calendar> calendarList = new ArrayList<Calendar>( reportService.getStartingEndingPeriods( deType, selectedPeriod ) );
                    if ( calendarList == null || calendarList.isEmpty() )
                    {
                        tempStartDate.setTime( selectedPeriod.getStartDate() );
                        tempEndDate.setTime( selectedPeriod.getEndDate() );
                        return SUCCESS;
                    }
                    else
                    {
                        tempStartDate = calendarList.get( 0 );
                        tempEndDate = calendarList.get( 1 );
                    }
    
                    if ( deCodeString.equalsIgnoreCase( "NA" ) )
                    {
                        tempStr = " ";
                        //tempLLDeathValuStr = " ";
                    }
                    else
                    {
                        if ( sType.equalsIgnoreCase( "lldeathdataelement_name" ) || sType.equalsIgnoreCase( "lldeathdataelement_sex" ) || sType.equalsIgnoreCase( "lldeathdataelement_age_type" )
                            || sType.equalsIgnoreCase( "lldeathdataelement_age" ) || sType.equalsIgnoreCase( "lldeathdataelement_cause" ) )
                        {
                            tempStr = getTrackedEntityDataValue( deCodeString, currentOrgUnit, programStageInstanceId, trackedEntityDataValueMap );
                            //System.out.println( "SType--" + sType + " Inside decode string : " + deCodeString + " currentOrgUnit : " + currentOrgUnit.getId() +  " programStageInstanceId : " + programStageInstanceId + "   TempStr : " + tempStr );
                        }
                        
                        else
                        {
                           
                        }
                    }
                    
                    //tempLLDeathRowNo = report_inDesign.getRowno();
                    int tempRowNo = report_inDesign.getRowno();
                   
                    //currentRowNo = tempLLDeathRowNo;
                    int tempColNo = report_inDesign.getColno();
                    int sheetNo = report_inDesign.getSheetno();
                    
                    Sheet sheet0 = apachePOIWorkbook.getSheetAt( sheetNo );
                    
                    if ( tempStr == null || tempStr.equals( " " ) || tempStr.equals( "" ))
                    {
    
                    }
                    else
                    {
                        /*
                        String tstr1 = resMap.get( tempStr.trim() );
                        if ( tstr1 != null )
                        {
                            tempStr = tstr1;
                        }
                        */
                        
                        if ( reportModelTB.equalsIgnoreCase( "DYNAMIC-DATAELEMENT" )
                            || reportModelTB.equalsIgnoreCase( "STATIC-DATAELEMENTS" ) )
                        {
                            if ( deCodeString.equalsIgnoreCase( "FACILITYP" )
                                || deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                            {
    
                            }
                            else if ( deCodeString.equalsIgnoreCase( "FACILITYPPP" )
                                || deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                            {
    
                            }
                            else if ( deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) )
                            {
    
                            }
                            else if ( deCodeString.equalsIgnoreCase( "PERIOD-MONTH" )
                                || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" ) )
                            {
    
                            }
                            else if ( deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) )
                            {
    
                            }
                            else if ( sType.equalsIgnoreCase( "dataelementnorepeat" ) )
                            {
    
                            }
                            else
                            {
                                //tempLLDeathRowNo += recordCount;
                                //currentRowNo += recordCount;
                                tempRowNo += recordCount;
                            }
    
                            if ( sType.equalsIgnoreCase( "lldeathdataelement_name" ) ||  sType.equalsIgnoreCase( "lldeathdataelement_sex" ) || sType.equalsIgnoreCase( "lldeathdataelement_age_type" ) 
                                || sType.equalsIgnoreCase( "lldeathdataelement_age" )|| sType.equalsIgnoreCase( "lldeathdataelement_cause" )
                                )
                            {
                                try
                                {
                                    Row row = sheet0.getRow( tempRowNo );
                                    Cell cell = row.getCell( tempColNo );
                                    cell.setCellValue( Integer.parseInt( tempStr ) );
                                }
                                catch ( Exception e )
                                {
                                    Row row = sheet0.getRow( tempRowNo );
                                    Cell cell = row.getCell( tempColNo );
                                    cell.setCellValue( tempStr );
                                }
                            }
                        }
                        //System.out.println( " deCodeString : " + deCodeString + " currentOrgUnit : " + currentOrgUnit.getId() +  " programStageInstanceId : " + programStageInstanceId + "   TempStr : " + tempStr );
                    }
                    //count1++;
                    //System.out.println( " is Below 1 Day :" + isBelow1Day  + " -- s Type is : " + sType );
                }// inner while loop end
                recordCount++;
                // System.out.println("End Row no for ll Death Death is  : " +  recordCount );
            }// outer while loop end
        }
        
        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + currentOrgUnit.getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( selectedPeriod.getStartDate() ) + ".xls";
        
        tempFile.close(); //Close the InputStream
        
        FileOutputStream output_file = new FileOutputStream( new File(  outputReportPath ) );  //Open FileOutputStream to write updates
        
        apachePOIWorkbook.write( output_file ); //write changes
          
        output_file.close();  //close the stream   
        
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        
        outputReportFile.deleteOnExit();
  
        try
        {

        }
        finally
        {
            if ( con != null )
                con.close();
        }
        
        //System.out.println( "Total LineListing Record Count : \t" + totalLineListingRecordCount );
        //statementManager.destroy();
        System.out.println( "Report Generation End Time is : \t" + new Date() );
        
        
        return SUCCESS;
    }
    
   
    
    // Supportive Methods
    
    public void initializeResultMap()
    {
        
        resMap = new HashMap<String, String>();
        
        resMap.put( "YEAR", "Years" );
        resMap.put( "MONTH", "Months" );
        resMap.put( "WEEK", "Weeks" );
        resMap.put( "HOUR", "Hrs" );
        resMap.put( "DAY", "Days" );
        
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
        //resMap.put( "AI", "Accidents or Injuries" );
        
        //infant Death( Up to 1 Year of age )
        
        resMap.put( "WITHIN24HOURSOFBIRTH", "C01-Within 24 hrs of birth" );
        
        resMap.put( "B1DAY", "C01-Within 24 hrs of birth" );
        
        resMap.put( "SEPSIS", "C02-Sepsis" );
        resMap.put( "ASPHYXIA", "C03-Asphyxia" );
        resMap.put( "LOWBIRTHWEIGH", "C04-Low Birth Weight (LBW) for Children upto 4 weeks of age only" );
        resMap.put( "PNEUMONIA", "C05-Pneumonia" );
        resMap.put( "DIADIS", "C06-Diarrhoea" );
        resMap.put( "OFR", "C07-Fever related" );
        resMap.put( "MEASLES", "C08-Measles" );
        resMap.put( "OTHERS", "C09-Others" );
        
        
        //Adolescents and Adults
        resMap.put( "DIADIS", "A01-Diarrhoeal diseases" );
        resMap.put( "TUBER", "A02-Tuberculosis" );
        resMap.put( "RID", "A03-Respiratory diseases including infections (other than TB)" );
        resMap.put( "MALARIA", "A04-Malaria" );
        resMap.put( "OFR", "A05-Other Fever Related" );
        resMap.put( "HIVAIDS", "A06-HIV/AIDS" );
        resMap.put( "HDH", "A07-Heart disease/Hypertension related" );
        resMap.put( "SND", "A08-Neurological disease including strokes" );
        resMap.put( "AI", "A09-Trauma/Accidents/Burn cases" );
        resMap.put( "SUICIDES", "A10-Suicide" );
        resMap.put( "ABS", "A11-Animal bites and stings" );
        
        //Others Disease
        resMap.put( "OKAD", "A12-Known Acute Disease" );
        resMap.put( "OKCD", "A13-Known Chronic Disease" );
        resMap.put( "NK", "A14-Causes not known" );
        
        
        // Maternal death cause
        
        resMap.put( "ABORTION", "M01-Abortion" );
        resMap.put( "OPL", "M02-Obstructed/prolonged labour" );
        resMap.put( "SH", "M03-Severe hypertension/fits" );
        resMap.put( "FITS", "M03-Severe hypertension/fits" );
        resMap.put( "BBCD", "M04-Bleeding" );
        //resMap.put( "BACD", "M04-Bleeding" );
        resMap.put( "HFBD", "M05-High fever" );
        //resMap.put( "HFAD", "M05-High fever" );
        resMap.put( "MDNK", "M06-Other Causes (including causes not known)" );
        
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
        
    }

    public void initializeLLDeathResultMap()
    {
        resMapForDeath = new HashMap<String, String>();

        resMapForDeath.put( "B1DAY", "Hrs:12" );
        resMapForDeath.put( "B1WEEK", "Weeks:1" );
        resMapForDeath.put( "B1MONTH", "Weeks:3" );
        resMapForDeath.put( "B1YEAR", "Months:6" );
        resMapForDeath.put( "B5YEAR", "Years:3" );
        resMapForDeath.put( "O5YEAR", "Years:10" );
        resMapForDeath.put( "O15YEAR", "Years:40" );
        resMapForDeath.put( "O55YEAR", "Years:60" );
    }

    public WritableCellFormat getCellFormat1()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getCellFormat2()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.LEFT );
        wCellformat.setWrap( true );

        return wCellformat;
    }
    
    // getting data value using Map
    private String getAggVal( String expression, Map<String, String> aggDeMap )
    {
        int flag = 0;
        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( expression );
            StringBuffer buffer = new StringBuffer();

            String resultValue = "";

            while ( matcher.find() )
            {
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );

                replaceString = aggDeMap.get( replaceString );
                
                if( replaceString == null )
                {
                    replaceString = "0";                    
                }
                else
                {
                    flag = 1;
                }
                
                matcher.appendReplacement( buffer, replaceString );

                resultValue = replaceString;
            }

            matcher.appendTail( buffer );
            
            double d = 0.0;
            try
            {
                d = MathUtils.calculateExpression( buffer.toString() );
            }
            catch ( Exception e )
            {
                d = 0.0;
                resultValue = "";
            }
            
            resultValue = "" + (double) d;
            
            if( flag == 0 )
            {
                return "";
            }
                
            else
            {
                return resultValue;
            }
            
            //return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }
    
    
    
    public String getTrackedEntityDataValue( String formula, OrganisationUnit organisationUnit, Integer programStageInstanceId, Map<String, String> trackedEntityDataValueMap )
    {
        //System.out.println( " Size of tracked Entity DataValue Map  inside method is 2 ======  :" + trackedEntityDataValueMap.size() );
        try
        {
            Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

            Matcher matcher = pattern.matcher( formula );
            StringBuffer buffer = new StringBuffer();

            while ( matcher.find() )
            {
                //System.out.println( " Size of tracked Entity DataValue Map  inside service is 3 ======  :" + trackedEntityDataValueMap.size() ); 
                String replaceString = matcher.group();

                replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                String optionComboIdStr = replaceString.substring( replaceString.indexOf( '.' ) + 1, replaceString.length() );

                replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                int dataElementId = Integer.parseInt( replaceString );
                int optionComboId = Integer.parseInt( optionComboIdStr );
                
                //System.out.println("dataElementId : \t" + dataElementId + "optionComboId : \t" + optionComboId );
                
                DataElement dataElement = dataElementService.getDataElement( dataElementId );
                DataElementCategoryOptionCombo optionCombo = dataElementCategoryOptionComboService.getDataElementCategoryOptionCombo( optionComboId );

                if ( dataElement == null || optionCombo == null )
                {
                    replaceString = "";
                    matcher.appendReplacement( buffer, replaceString );
                    continue;
                }
                
                String tempStr = "";
                //System.out.println( " Size of tracked Entity DataValue Map  inside service is 4 ======  :" + trackedEntityDataValueMap.size() ); 
                if( trackedEntityDataValueMap != null && trackedEntityDataValueMap.size() > 0 )
                {
                    String mapKey = organisationUnit.getId() + ":" + programStageInstanceId + ":" + dataElement.getId();
                    tempStr = trackedEntityDataValueMap.get( mapKey );
                    //System.out.println("Map Key : \t" + mapKey + "Map Value : \t" + tempStr );
                    if(tempStr == null)
                    {
                        tempStr = "";
                    }
                }
                
                replaceString = tempStr;

                matcher.appendReplacement( buffer, replaceString );
            }

            matcher.appendTail( buffer );

            String resultValue = "";
           
            resultValue = buffer.toString();

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
    }
}
