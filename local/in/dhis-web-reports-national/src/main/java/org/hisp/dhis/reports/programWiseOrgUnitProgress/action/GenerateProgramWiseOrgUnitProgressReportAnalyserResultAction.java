package org.hisp.dhis.reports.programWiseOrgUnitProgress.action;


import static org.hisp.dhis.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.util.TextUtils.getCommaDelimitedString;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;

import com.opensymphony.xwork2.Action;

public class GenerateProgramWiseOrgUnitProgressReportAnalyserResultAction
implements Action
{
    /*
    private final String GENERATEAGGDATA = "generateaggdata";

    private final String USEEXISTINGAGGDATA = "useexistingaggdata";

    private final String USECAPTUREDDATA = "usecaptureddata";
    */
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    

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
    
    private OrganisationUnitGroupService organisationUnitGroupService;

    public void setOrganisationUnitGroupService( OrganisationUnitGroupService organisationUnitGroupService )
    {
        this.organisationUnitGroupService = organisationUnitGroupService;
    }
    
    
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }

    
    // -------------------------------------------------------------------------
    // Getter & Setter
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
    
    /*
    private String aggData;
    
    public void setAggData( String aggData )
    {
        this.aggData = aggData;
    }
    */
    
    private OrganisationUnit selectedOrgUnit;

    private List<OrganisationUnit> orgUnitList;

    private SimpleDateFormat simpleDateFormat;

    private String reportFileNameTB;

    private String reportModelTB;

    private Date sDate;

    private Date eDate;

    private String raFolderName;
    
    
   // private PeriodType selPeriodType;
    
    private Period selectedPeriod;
    
    private Integer monthCount;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        // Initialization
        
        raFolderName = reportService.getRAFolderName();
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        
        //SimpleDateFormat dayFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        
        
        // Getting Report Details       
        String deCodesXMLFileName = "";

        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );

        deCodesXMLFileName = selReportObj.getXmlTemplateName();
        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        
        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        int selectedOrgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( ouIDTB );
        
        System.out.println( selectedOrgUnit.getName()+ " : " + selReportObj.getName()+" Report Generation Start Time is : " + new Date() );
        
        
        if ( reportModelTB.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
        {            
            orgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren() );
            Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
            
            if( selectedOrgUnitLevel != 1 )
            {
                orgUnitList.add( selectedOrgUnit );
            }
            
        }
        
        
        // Period Related Info
        selectedPeriod = periodService.getPeriod( availablePeriods );

        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );
        eDate = format.parseDate( String.valueOf( selectedPeriod.getEndDate() ) );
        
        Calendar monthStart = Calendar.getInstance();
        Calendar monthEnd = Calendar.getInstance();
        
        monthStart.setTime( sDate );
        monthEnd.setTime( eDate );

        //monthStart.set( Calendar.MONTH, Calendar.APRIL );
        
        //int startMonth = monthStart.get( Calendar.MONTH );
       
        //int endMonth = monthEnd.get( Calendar.MONTH );
        
        
        // for January,February,March,April,May,June,July,August,September,October,November,December
        int financialMonthOrder[] = { 10, 11, 12, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        
        monthCount = financialMonthOrder[ monthEnd.get( Calendar.MONTH ) ];
        
        //monthCount = ( endMonth - startMonth ) + 1 ;

       // tempStr = monthCount.toString();
        
        //System.out.println( " Month count is  -- "  + monthCount );
        
        /*
        Calendar tStartDate = Calendar.getInstance();
        Calendar tEndDate = Calendar.getInstance();
        
        tStartDate.setTime( selectedPeriod.getStartDate() );
        
        tStartDate.get( Calendar.YEAR );
        
        tStartDate.roll( Calendar.YEAR, -1 );
        
        
        Date startDate = format.parseDate( tStartDate.get( Calendar.YEAR ) + "-01-01" );
        Date endDate = format.parseDate( tStartDate.get( Calendar.YEAR ) + "-12-31" );
        
        tStartDate.setTime( startDate );
        tEndDate.setTime( endDate );
        
        System.out.println( tStartDate.getTime().toString() + " -- "  + tEndDate.getTime().toString() );
        */
        
        
        
        //PeriodType periodType = periodService.getPeriodTypeByName( periodTypeId );
        
        // Collecting periods
        /*
        selPeriodType = selReportObj.getPeriodType();
        List<Period> periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( selPeriodType, sDate, eDate ) );
        Collections.sort( periodList, new PeriodStartDateComparator() );
        
        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );        
        String periodIdsByComma = getCommaDelimitedString( periodIds );
        
        String dataElmentIdsByComma = reportService.getDataelementIds( reportDesignList );
        */
        
        // Getting DataValues
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );

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
        
        WritableCellFormat wCellformat = new WritableCellFormat();
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );
        
        int slno = 1;
        int orgUnitCount = 0;
        Iterator<OrganisationUnit> it = orgUnitList.iterator();
        while ( it.hasNext() )
        {
            OrganisationUnit currentOrgUnit = (OrganisationUnit) it.next();
            
            // for Aggregate Data OrgUnit Wise to all Children
            
            /*
            Map<String, String> aggDeMap = new HashMap<String, String>();
            
            List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( currentOrgUnit.getId() ) );
            List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, childOrgUnitTree ) );
            
            String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );

            aggDeMap.putAll( reportService.getAggDataFromDataValueTable( childOrgUnitsByComma, dataElmentIdsByComma, periodIdsByComma ) );
            */
            
            
            int count1 = 0;
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
                List<Calendar> calendarList = new ArrayList<Calendar>( reportService.getStartingEndingPeriods( deType, selectedPeriod ) );
                
                //System.out.println( "Size of Calender List is : "  + calendarList.size() );
                
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
                
                //System.out.println( tempStartDate.getTime().toString() + " -- "  + tempEndDate.getTime().toString() );
                
                if ( deCodeString.equalsIgnoreCase( "FACILITY" ) )                    
                {
                    tempStr = selectedOrgUnit.getName();
                }
                
                else if( deCodeString.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
                {
                    tempStr = currentOrgUnit.getName();
                }
                
                else if ( deCodeString.equalsIgnoreCase( "MONTH-YEAR" ) )
                {
                    tempStr = simpleDateFormat.format( sDate );
                }
                
                else if ( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    tempStr = " ";
                }
               
                else if( deCodeString.equalsIgnoreCase( "SLNo" ) )
                {
                    tempStr = "" + slno;
                } 
                
                else
                {
                    if ( sType.equalsIgnoreCase( "dataelement" ) )
                    {
                        //tempStr = getAggVal( deCodeString, aggDeMap );
                        tempStr = reportService.getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );
                    }
                    
                    else if ( sType.equalsIgnoreCase( "orgunitgroupdata" ) )
                    {
                        //tempStr = getAggVal( deCodeString, aggDeMap );
                        String orgunitGroups = deCodeString.split( "--" )[0];                        
                        String deExp = deCodeString.split( "--" )[1];
                        
                        //System.out.println( orgunitGroups + " : " + deExp +" Report Generation End Time is : " + orgunitGroups.split( "," )[0]  );
                        
                        List<OrganisationUnit> orgUnitGroupMemberList = new ArrayList<OrganisationUnit>();
                        //List<OrganisationUnit> orgUnitGroupMembers = new ArrayList<OrganisationUnit>();
                        for( int i = 0; i < orgunitGroups.split( "," ).length; i++ )
                        {
                            OrganisationUnitGroup orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( Integer.parseInt ( orgunitGroups.split( "," )[i] )  );
                            List<OrganisationUnit> orgUnitGroupMembers  = new ArrayList<OrganisationUnit>( orgUnitGroup.getMembers() );
                            
                            orgUnitGroupMemberList.addAll( orgUnitGroupMembers );
                        }
                        
                        List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( currentOrgUnit.getId() ) );
                        
                        childOrgUnitTree.retainAll( orgUnitGroupMemberList );
                        
                        List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, childOrgUnitTree ) );
                        String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );
                       
                        tempStr = reportService.getResultDataValueForOrgUnitGroupMember( deExp, childOrgUnitsByComma, tempStartDate.getTime(), tempEndDate.getTime(), reportModelTB );
                    }
                    
                    else if ( sType.equalsIgnoreCase( "proportionate" ) )
                    {
                        //tempStr = getAggVal( deCodeString, aggDeMap );
                        tempStr = reportService.getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );
                    
                        if( tempStr != null && !tempStr.trim().equalsIgnoreCase( "" ) )
                        {
                            Double proportionateValue = ( Double.parseDouble( tempStr ) / 12 ) * monthCount;
                            
                            proportionateValue = Math.round( proportionateValue * Math.pow( 10, 0 ) ) / Math.pow( 10, 0 );
                            
                            tempStr = proportionateValue.toString();
                        }
                        else
                        {
                            tempStr = "";
                        }
                    }
                    else if ( sType.equalsIgnoreCase( "formula" ) )
                    {
                        tempStr = deCodeString;
                    }
                    
                }
                
                int tempRowNo = report_inDesign.getRowno();
                int tempColNo = report_inDesign.getColno();
                int sheetNo = report_inDesign.getSheetno();
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                
                if ( reportModelTB.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
                {
                    if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "MONTH-YEAR" ) )
                    {
                    }
                    else
                    {
                        tempRowNo += orgUnitCount;
                    }

                    try
                    {
                       
                        if( sType.equalsIgnoreCase( "formula" ) )
                        {
                            tempStr = tempStr.replace( "?", "" + ( tempRowNo + 1 ) );
                            if( orgUnitCount == orgUnitList.size()-1 && selectedOrgUnitLevel != 1 )
                            {
                                sheet0.addCell( new Formula( tempColNo, tempRowNo, tempStr, getCellFormat1() ) );
                            }
                            else
                            {
                                sheet0.addCell( new Formula( tempColNo, tempRowNo, tempStr, wCellformat ) );
                            }
                        }
                        else
                       {
                            if( orgUnitCount == orgUnitList.size()-1 && selectedOrgUnitLevel != 1 )
                            {
                                //sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStrForSelectedFacility ), getCellFormat2() ) );
                                if(deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "MONTH-YEAR" ) )
                                {
                                    continue;
                                }
                                else
                                {
                                    sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), getCellFormat1() ) );
                                }
                                
                                
                            }
                            else
                            {
                                sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), wCellformat ) );
                            }
                       }
                    }
                    catch( Exception e )
                    {
                        if( orgUnitCount == orgUnitList.size()-1 && selectedOrgUnitLevel != 1 )
                        {
                            if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "MONTH-YEAR" ) )
                            {
                                continue;
                            }
                            else
                            {
                                sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, getCellFormat1() ) );
                            }
                            
                        }
                        else
                        {
                            sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                        }
                    }
                    //System.out.println( "SType : " + sType + " DECode : " + deCodeString + "   TempStr : " + tempStr + " -- Row No " + tempRowNo  + " -- Col No " + tempColNo  );
                }
                count1++;
            }
            slno++;
            orgUnitCount++;  
        }
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + selectedOrgUnit.getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( sDate ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        System.out.println( selectedOrgUnit.getName()+ " : " + selReportObj.getName()+" Report Generation End Time is : " + new Date() );

        outputReportFile.deleteOnExit();

        //statementManager.destroy();        
        
        return SUCCESS;
    }
    
    // for getting aggregate value from map
    /*
    private String getAggVal( String expression, Map<String, String> aggDeMap )
    {
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

            return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }
        
 */   
    // for format the cell
    public WritableCellFormat getCellFormat1() throws Exception
    {
        WritableFont arialBold = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD );
        WritableCellFormat wCellformat = new WritableCellFormat( arialBold );
        
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }
    
}
