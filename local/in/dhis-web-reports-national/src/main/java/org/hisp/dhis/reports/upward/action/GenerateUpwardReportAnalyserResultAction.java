package org.hisp.dhis.reports.upward.action;


import static org.hisp.dhis.util.ConversionUtils.getIdentifiers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import jxl.CellType;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.CellFormat;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.hisp.dhis.common.comparator.IdentifiableObjectNameComparator;
import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;

import com.opensymphony.xwork2.Action;

public class GenerateUpwardReportAnalyserResultAction
    implements Action
{

    private final String GENERATEAGGDATA = "generateaggdata";

    private final String USEEXISTINGAGGDATA = "useexistingaggdata";

    private final String USECAPTUREDDATA = "usecaptureddata";
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    /*private StatementManager statementManager;

    public void setStatementManager( StatementManager statementManager )
    {
        this.statementManager = statementManager;
    }
        */

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

    private String reportList;

    public void setReportList( String reportList )
    {
        this.reportList = reportList;
    }
    
    /*
    private int ouIDTB;

    public void setOuIDTB( int ouIDTB )
    {
        this.ouIDTB = ouIDTB;
    }
    */
    
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
/*
    private String aggCB;

    public void setAggCB( String aggCB )
    {
        this.aggCB = aggCB;
    }
*/
    private String reportFileNameTB;

    private String reportModelTB;

    private List<OrganisationUnit> orgUnitList;

    private Period selectedPeriod;

    private SimpleDateFormat simpleDateFormat;

    private SimpleDateFormat monthFormat;

    private SimpleDateFormat simpleMonthFormat;

    private Date sDate;

    private Date eDate;

    private String raFolderName;
    
    private String aggData;
    
    public void setAggData( String aggData )
    {
        this.aggData = aggData;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        //statementManager.initialise();

        // Initialization
        raFolderName = reportService.getRAFolderName();
        String deCodesXMLFileName = "";
        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );
        monthFormat = new SimpleDateFormat( "MMMM" );
        simpleMonthFormat = new SimpleDateFormat( "MMM" );
        String parentUnit = "";
        
        Report_in selReportObj =  reportService.getReport( Integer.parseInt( reportList ) );
        
        deCodesXMLFileName = selReportObj.getXmlTemplateName();

        reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        

        String inputTemplatePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "template" + File.separator + reportFileNameTB;
        //String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "output" + File.separator + UUID.randomUUID().toString() + ".xls";
        
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator +  Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";
        
        //FileInputStream tempFile = new FileInputStream( new File( inputTemplatePath ) );
        //HSSFWorkbook apachePOIWorkbook = new HSSFWorkbook( tempFile );
        
        if ( reportModelTB.equalsIgnoreCase( "DYNAMIC-ORGUNIT" ) )
        {
            OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
            orgUnitList = new ArrayList<OrganisationUnit>( orgUnit.getChildren() );
            Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
        }
        else if ( reportModelTB.equalsIgnoreCase( "STATIC" ) || reportModelTB.equalsIgnoreCase( "STATIC-DATAELEMENTS" ) || reportModelTB.equalsIgnoreCase( "STATIC-FINANCIAL" ) )
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

        System.out.println( orgUnitList.get( 0 ).getName()+ " : " + selReportObj.getName()+" : Report Generation Start Time is : " + new Date() );
        
        selectedPeriod = periodService.getPeriod( availablePeriods );

        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );

        eDate = format.parseDate( String.valueOf( selectedPeriod.getEndDate() ) );

        //Workbook templateWorkbook = Workbook.getWorkbook( new File( inputTemplatePath ) );
        //WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ), templateWorkbook );
        
        FileInputStream tempFile = new FileInputStream( new File( inputTemplatePath ) );
        HSSFWorkbook apachePOIWorkbook = new HSSFWorkbook( tempFile );

        // Getting DataValues
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );
        int orgUnitCount = 0;

        Iterator<OrganisationUnit> it = orgUnitList.iterator();
        while ( it.hasNext() )
        {
            OrganisationUnit currentOrgUnit = (OrganisationUnit) it.next();

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
                if( calendarList == null || calendarList.isEmpty() )
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

                if( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                {
                    tempStr = currentOrgUnit.getName();
                } 
                else if( deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ) )
                {
                    tempStr = parentUnit;
                } 
                else if( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getName();
                } 
                else if( deCodeString.equalsIgnoreCase( "FACILITYPP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getParent().getName();
                } 
                else if( deCodeString.equalsIgnoreCase( "FACILITYPPP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getParent().getParent().getName();
                } 
                else if( deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                {
                    tempStr = currentOrgUnit.getParent().getParent().getParent().getParent().getName();
                } 
                else if( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) )
                {
                    tempStr = simpleDateFormat.format( sDate );
                } 
                else if( deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) )
                {
                    tempStr = monthFormat.format( sDate );
                } 
                else if( deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" ) )
                {
                    tempStr = simpleMonthFormat.format( sDate );
                } 
                else if( deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" ) )
                {
                    tempStr = simpleMonthFormat.format( eDate );
                } 
                else if( deCodeString.equalsIgnoreCase( "MONTH-START" ) )
                {
                    tempStr = monthFormat.format( sDate );
                } 
                else if( deCodeString.equalsIgnoreCase( "MONTH-END" ) )
                {
                    tempStr = monthFormat.format( eDate );
                } 
                else if( deCodeString.equalsIgnoreCase( "SLNO" ) )
                {
                    tempStr = "" + ( orgUnitCount + 1 );
                } 
                else if( deCodeString.equalsIgnoreCase( "NA" ) )
                {
                    tempStr = " ";
                } 
                else
                {
                    if( sType.equalsIgnoreCase( "dataelement" ) )
                    {
                        if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                        {
                            tempStr = reportService.getIndividualResultDataValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );
                           
                            if ( deCodeString.equalsIgnoreCase( "[1.1]" ) || deCodeString.equalsIgnoreCase( "[2.1]" ) || deCodeString.equalsIgnoreCase( "[153.1]" ) 
                                || deCodeString.equalsIgnoreCase( "[155.1]" ) || deCodeString.equalsIgnoreCase( "[157.1]" ) || deCodeString.equalsIgnoreCase( "[158.1]" )
                                || deCodeString.equalsIgnoreCase( "[160.1]" ) )
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
                        else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                        {
                            System.out.println( " GENERATEAGGDATA  SType : " + sType + " DECode : " + deCodeString  );
                            
                            tempStr = reportService.getResultDataValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB );
                            
                            if ( deCodeString.equalsIgnoreCase( "[1.1]" ) || deCodeString.equalsIgnoreCase( "[2.1]" ) || deCodeString.equalsIgnoreCase( "[153.1]" ) 
                                || deCodeString.equalsIgnoreCase( "[155.1]" ) || deCodeString.equalsIgnoreCase( "[157.1]" ) || deCodeString.equalsIgnoreCase( "[158.1]" )
                                || deCodeString.equalsIgnoreCase( "[160.1]" ) )
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
                        else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                        {
                            List<Period> periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( tempStartDate.getTime(), tempEndDate.getTime() ) );
                            Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
                            tempStr = reportService.getResultDataValueFromAggregateTable( deCodeString, periodIds, currentOrgUnit, reportModelTB );
                            
                            if ( deCodeString.equalsIgnoreCase( "[1.1]" ) || deCodeString.equalsIgnoreCase( "[2.1]" ) || deCodeString.equalsIgnoreCase( "[153.1]" ) 
                                || deCodeString.equalsIgnoreCase( "[155.1]" ) || deCodeString.equalsIgnoreCase( "[157.1]" ) || deCodeString.equalsIgnoreCase( "[158.1]" )
                                || deCodeString.equalsIgnoreCase( "[160.1]" ) )
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
                    } 
                    else if ( sType.equalsIgnoreCase( "dataelement-boolean" ) )
                    {
                        if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                        {
                            tempStr = reportService.getBooleanDataValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB);
                        } 
                        else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                        {
                            tempStr = reportService.getBooleanDataValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB);
                        }
                        else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                        {
                            tempStr = reportService.getBooleanDataValue(deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit, reportModelTB);
                        }
                        
                    }
                    else
                    {
                        if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                        {
                            tempStr = reportService.getIndividualResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                        } 
                        else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                        {
                            tempStr = reportService.getResultIndicatorValue( deCodeString, tempStartDate.getTime(), tempEndDate.getTime(), currentOrgUnit );
                        }     
                        else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                        {
                            List<Period> periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( tempStartDate.getTime(), tempEndDate.getTime() ) );
                            Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodList ) );
                            tempStr = reportService.getResultDataValueFromAggregateTable( deCodeString, periodIds, currentOrgUnit, reportModelTB );
                        }
                    }
                }
                
                /*
                int tempRowNo = report_inDesign.getRowno();
                int tempColNo = report_inDesign.getColno();
                int sheetNo = report_inDesign.getSheetno();
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                */
                
                int tempRowNo = report_inDesign.getRowno();
                int tempColNo = report_inDesign.getColno();
                int sheetNo = report_inDesign.getSheetno();
                
                Sheet sheet0 = apachePOIWorkbook.getSheetAt( sheetNo );
                
                if ( tempStr == null || tempStr.equals( " " ) )
                {
                    tempColNo += orgUnitCount;
                    /*  
                    WritableCellFormat wCellformat = new WritableCellFormat();
                    wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                    wCellformat.setWrap( true );
                    wCellformat.setAlignment( Alignment.CENTRE );
        
                    sheet0.addCell( new Blank( tempColNo, tempRowNo, wCellformat ) );
                    */
                    
                } 
                else
                {
                    if ( reportModelTB.equalsIgnoreCase( "DYNAMIC-ORGUNIT" ) )
                    {
                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) || deCodeString.equalsIgnoreCase( "FACILITYPP" ) || deCodeString.equalsIgnoreCase( "FACILITYPPP" ) || deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                        {
                        } 
                        else if ( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" ) || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) || deCodeString.equalsIgnoreCase( "MONTH-START" ) || deCodeString.equalsIgnoreCase( "MONTH-END" ) || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" ) || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" ) || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" ) || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-START" ) || deCodeString.equalsIgnoreCase( "QUARTER-END" ) || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" ) || deCodeString.equalsIgnoreCase( "YEAR-END" ) || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                        {
                        }
                        else
                        {
                            tempColNo += orgUnitCount;
                        }
                    }
                    else if ( reportModelTB.equalsIgnoreCase( "dynamicwithrootfacility" ) )
                    {
                        if ( deCodeString.equalsIgnoreCase( "FACILITYP" ) || deCodeString.equalsIgnoreCase( "FACILITY-NOREPEAT" ) ||  deCodeString.equalsIgnoreCase( "FACILITYPP" ) || deCodeString.equalsIgnoreCase( "FACILITYPPP" ) || deCodeString.equalsIgnoreCase( "FACILITYPPPP" ) )
                        {
                        } 
                        else if ( deCodeString.equalsIgnoreCase( "PERIOD" ) || deCodeString.equalsIgnoreCase( "PERIOD-NOREPEAT" ) || deCodeString.equalsIgnoreCase( "PERIOD-WEEK" ) || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) || deCodeString.equalsIgnoreCase( "PERIOD-QUARTER" ) || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) || deCodeString.equalsIgnoreCase( "MONTH-START" ) || deCodeString.equalsIgnoreCase( "MONTH-END" ) || deCodeString.equalsIgnoreCase( "MONTH-START-SHORT" ) || deCodeString.equalsIgnoreCase( "MONTH-END-SHORT" ) || deCodeString.equalsIgnoreCase( "SIMPLE-QUARTER" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-MONTHS" ) || deCodeString.equalsIgnoreCase( "QUARTER-START-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-END-SHORT" ) || deCodeString.equalsIgnoreCase( "QUARTER-START" ) || deCodeString.equalsIgnoreCase( "QUARTER-END" ) || deCodeString.equalsIgnoreCase( "SIMPLE-YEAR" ) || deCodeString.equalsIgnoreCase( "YEAR-END" ) || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                        {
                        } 
                        else
                        {
                            tempRowNo += orgUnitCount;
                        }
                    }
                    
                    /*
                    WritableCell cell = sheet0.getWritableCell( tempColNo, tempRowNo );
    
                    CellFormat cellFormat = cell.getCellFormat();
                    WritableCellFormat wCellformat = new WritableCellFormat();
                    wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                    wCellformat.setWrap( true );
                    wCellformat.setAlignment( Alignment.CENTRE );
    
                    if ( cell.getType() == CellType.LABEL )
                    {
                        Label l = (Label) cell;
                        l.setString( tempStr );
                        l.setCellFormat( cellFormat );
                    } 
                    else
                    {
                        try
                        {
                            sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), wCellformat ) );
                        }
                        catch( Exception e )
                        {
                            sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                        }
                    }
                    */
                    
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
                
                count1++;
            }// inner while loop end
            orgUnitCount++;
        }// outer while loop end
        
        /*
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + orgUnitList.get( 0 ).getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( selectedPeriod.getStartDate() ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        System.out.println( orgUnitList.get( 0 ).getName()+ " : " + selReportObj.getName()+" Report Generation End Time is : " + new Date() );

        outputReportFile.deleteOnExit();
        */
        
        // code for apachePOI Workbook
        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + orgUnitList.get( 0 ).getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( selectedPeriod.getStartDate() ) + ".xls";
        
        tempFile.close(); //Close the InputStream
        
        FileOutputStream output_file = new FileOutputStream( new File(  outputReportPath ) );  //Open FileOutputStream to write updates
        
        apachePOIWorkbook.write( output_file ); //write changes
          
        output_file.close();  //close the stream   
        
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        
        outputReportFile.deleteOnExit();
        
        System.out.println( orgUnitList.get( 0 ).getName()+ " : " + selReportObj.getName()+" Report Generation End Time is : " + new Date() );
        //statementManager.destroy();

        return SUCCESS;
    }
}
