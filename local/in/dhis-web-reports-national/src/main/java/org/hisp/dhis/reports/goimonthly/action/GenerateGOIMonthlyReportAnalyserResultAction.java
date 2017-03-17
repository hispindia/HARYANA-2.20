package org.hisp.dhis.reports.goimonthly.action;


import static org.hisp.dhis.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.util.TextUtils.getCommaDelimitedString;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import org.hisp.dhis.config.Configuration_IN;
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
public class GenerateGOIMonthlyReportAnalyserResultAction implements Action
{
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
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    
    // -------------------------------------------------------------------------
    // Properties
    // -------------------------------------------------------------------------
    
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
    
    private String reportFileNameTB;

    //private String reportModelTB;

    private Period selectedPeriod;

    private SimpleDateFormat simpleMonthYearFormat;
    
    private SimpleDateFormat monthFormat;

    private SimpleDateFormat yearFormat;

    private Date sDate;

    private Date eDate;

    private String raFolderName;
    
    private  String deCodesXMLFileName = "";
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {   
        // Initialization
        raFolderName = reportService.getRAFolderName();
       
        simpleMonthYearFormat = new SimpleDateFormat( "MMM-yyyy" );
        monthFormat = new SimpleDateFormat( "MMMM" );
        yearFormat = new SimpleDateFormat( "yyyy" );
        
        Report_in selReportObj =  reportService.getReport( Integer.parseInt( reportList ) );
        
        deCodesXMLFileName = selReportObj.getXmlTemplateName();

        //reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        
        // OrgUnit Related Information        
        OrganisationUnit orgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        
        // Period Info
        selectedPeriod = periodService.getPeriod( availablePeriods );
        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );
        eDate = format.parseDate( String.valueOf( selectedPeriod.getEndDate() ) );
        
        // collect periodId by CommaSepareted
        List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );        
        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers( Period.class, periodList ) );        
        String periodIdsByComma = getCommaDelimitedString( periodIds );
        
        
        //System.out.println( "periodIdsByComma : "  + periodIdsByComma );
        
        // collect dataElementIDs by commaSepareted
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );
        
        String dataElmentIdsByComma = reportService.getDataelementIds( reportDesignList );
        
        System.out.println( orgUnit.getName()+ " : " + selReportObj.getName()+" : Report Generation Start Time is : " + new Date() );
        
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
                            
        Map<String, String> aggDeMap = new HashMap<String, String>();
        aggDeMap.putAll( reportService.getDataFromDataValueTableForGoiMonthly( ""+orgUnit.getId(), dataElmentIdsByComma, periodIdsByComma ) );
            
        Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
        while ( reportDesignIterator.hasNext() )
        {
            Report_inDesign report_inDesign = (Report_inDesign) reportDesignIterator.next();

            String deCodeString = report_inDesign.getExpression();
            String tempStr = "";
                
            if( deCodeString.equalsIgnoreCase( "FACILITY" ) )
            {
                tempStr = orgUnit.getName();
            }                 
            else if( deCodeString.equalsIgnoreCase( "FACILITYP" ) )
            {
                tempStr = orgUnit.getParent().getName();
            } 
            else if( deCodeString.equalsIgnoreCase( "PERIOD" ) )
            {
                tempStr = simpleMonthYearFormat.format( sDate );
            } 
            
            else if ( deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) )
            {
                tempStr = monthFormat.format( sDate );
            }
            
            else if ( deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) )
            {
                tempStr = yearFormat.format( sDate );
            }
            
            else if ( deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
            {
                tempStr = yearFormat.format( sDate );
            }
            
            else if( deCodeString.equalsIgnoreCase( "NA" ) )
            {
                tempStr = " ";
            } 
            else
            {
                tempStr = getCapturedData( deCodeString, aggDeMap );
                            
                if ( deCodeString.equalsIgnoreCase( "[1.1]" ) || deCodeString.equalsIgnoreCase( "[2.1]" ) || deCodeString.equalsIgnoreCase( "[153.1]" ) 
                    || deCodeString.equalsIgnoreCase( "[155.1]" ) || deCodeString.equalsIgnoreCase( "[157.1]" ) || deCodeString.equalsIgnoreCase( "[158.1]" )
                    || deCodeString.equalsIgnoreCase( "[160.1]" ) )
                {
                                
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
            }
                
            int tempRowNo = report_inDesign.getRowno();
            int tempColNo = report_inDesign.getColno();
            int sheetNo = report_inDesign.getSheetno();
            WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                
            if ( tempStr == null || tempStr.equals( " " ) )
            {
                WritableCellFormat wCellformat = new WritableCellFormat();
                wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
                wCellformat.setWrap( true );
                wCellformat.setAlignment( Alignment.CENTRE );
    
                sheet0.addCell( new Blank( tempColNo, tempRowNo, wCellformat ) );
            } 
            else
            {
                if ( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "FACILITYP" ) 
                    || deCodeString.equalsIgnoreCase( "PERIOD-MONTH" ) || deCodeString.equalsIgnoreCase( "PERIOD-YEAR" ) 
                    || deCodeString.equalsIgnoreCase( "YEAR-FROMTO" ) )
                {
                    
                }
                
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
            }
        }
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + orgUnit.getShortName() + "_";
        fileName += "_" + simpleMonthYearFormat.format( selectedPeriod.getStartDate() ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        System.out.println( orgUnit.getName()+ " : " + selReportObj.getName()+" Report Generation End Time is : " + new Date() );

        outputReportFile.deleteOnExit();

        return SUCCESS;
    }
    
    // Supportive Methods
    // getting captured data value using Map
    private String getCapturedData( String expression, Map<String, String> aggDeMap )
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
                return "";
            else
                return resultValue;
        }
        catch ( NumberFormatException ex )
        {
            throw new RuntimeException( "Illegal DataElement id", ex );
        }
    }   
    
}
