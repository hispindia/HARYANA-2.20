package org.hisp.dhis.dataanalyser.vr.action;

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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.hisp.dhis.config.Configuration_IN;
import org.hisp.dhis.dataanalyser.util.ValidationRule;
import org.hisp.dhis.i18n.I18nFormat;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitGroup;
import org.hisp.dhis.organisationunit.OrganisationUnitGroupService;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.DailyPeriodType;
import org.hisp.dhis.period.FinancialAprilPeriodType;
import org.hisp.dhis.period.MonthlyPeriodType;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.QuarterlyPeriodType;
import org.hisp.dhis.period.SixMonthlyPeriodType;
import org.hisp.dhis.period.WeeklyPeriodType;
import org.hisp.dhis.period.YearlyPeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.commons.filter.Filter;
import org.hisp.dhis.commons.filter.FilterUtils;
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

public class GenerateValidationRuleResultAction
    implements Action
{
    public static final String RED = "red";

    public static final String ORANGE = "orange";

    public static final String WHITE = "white";

    public static final String GREEN = "green";

    private final String ORGUNITGRP = "orgUnitGroupRadio";

    private final String ORGUNITLEVEL = "orgUnitLevelRadio";

    private final String ORGUNITSELECTED = "orgUnitSelectedRadio";

    private final String GENERATEAGGDATA = "generateaggdata";

    private final String USEEXISTINGAGGDATA = "useexistingaggdata";

    private final String USECAPTUREDDATA = "usecaptureddata";

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

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

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
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

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }

    // -------------------------------------------------------------------------
    // Input & Output
    // -------------------------------------------------------------------------

    private List<OrganisationUnit> selOUList;

    private OrganisationUnit selOrgUnit;

    private List<Date> selStartPeriodList;

    private List<Date> selEndPeriodList;

    String orgUnitIdsByComma;

    String dataElementIdByComma = "-1";

    String optionIdByComma = "-1";

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

    private List<String> orgUnitListCB;

    public void setOrgUnitListCB( List<String> orgUnitListCB )
    {
        this.orgUnitListCB = orgUnitListCB;
    }

    public String orgUnitSelListCB;

    public void setOrgUnitSelListCB( String orgUnitSelListCB )
    {

        this.orgUnitSelListCB = orgUnitSelListCB;
    }

    private String periodTypeLB;

    public void setPeriodTypeLB( String periodTypeLB )
    {
        this.periodTypeLB = periodTypeLB;
    }

    private List<String> yearLB;

    public void setYearLB( List<String> yearLB )
    {
        this.yearLB = yearLB;
    }

    private List<String> periodLB;

    public void setPeriodLB( List<String> periodLB )
    {
        this.periodLB = periodLB;
    }

    private Integer orgUnitLevelCB;

    public void setOrgUnitLevelCB( Integer orgUnitLevelCB )
    {
        this.orgUnitLevelCB = orgUnitLevelCB;
    }

    private String aggData;

    public void setAggData( String aggData )
    {
        this.aggData = aggData;
    }

    Map<Integer, List<Integer>> periodMap;

    List<String> periodNames;

    String dataElementIdsByComma;

    String periodIdsByComma;
    
    private String raFolderName;
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
        throws Exception
    {
        
        raFolderName = reportService.getRAFolderName();
        
        selStartPeriodList = new ArrayList<Date>();
        selEndPeriodList = new ArrayList<Date>();

        periodMap = new HashMap<Integer, List<Integer>>();

        String monthOrder[] = { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" };
        int monthDays[] = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

        // for financial year
        String financialMonthOrder[] = { "04", "05", "06", "07", "08", "09", "10", "11", "12", "01", "02", "03" };
        int financialMonthDays[] = { 30, 31, 30, 31, 31, 30, 31, 30, 31, 31, 28, 31 };

        String startD = "";
        String endD = "";

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );

        periodNames = new ArrayList<String>();

        // for weekly period
        if ( periodTypeLB.equalsIgnoreCase( WeeklyPeriodType.NAME ) )
        {
            Integer pCount = 0;
            for ( String periodStr : periodLB )
            {
                String startWeekDate = periodStr.split( "To" )[0]; // for start
                                                                   // week
                String endWeekDate = periodStr.split( "To" )[1]; // for end week

                startD = startWeekDate.trim();
                endD = endWeekDate.trim();

                Date sDate = format.parseDate( startD );
                Date eDate = format.parseDate( endD );
                selStartPeriodList.add( sDate );
                selEndPeriodList.add( eDate );

                List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );
                List<Integer> periodIds = new ArrayList<Integer>( getIdentifiers( Period.class, periodList ) );
                periodMap.put( pCount, periodIds );

                pCount++;

                periodNames.add( periodStr );
            }
        }
        // for FinancialAprilPeriodType
        else if ( periodTypeLB.equalsIgnoreCase( FinancialAprilPeriodType.NAME ) )
        {
            Integer pCount = 0;
            for ( String year : yearLB )
            {
                int selYear = Integer.parseInt( year.split( "-" )[0] );

                for ( String periodStr : periodLB )
                {
                    int period = Integer.parseInt( periodStr );

                    simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );

                    if ( period >= 9 )
                    {
                        startD = "" + (selYear + 1) + "-" + financialMonthOrder[period] + "-01";
                        endD = "" + (selYear + 1) + "-" + financialMonthOrder[period] + "-"
                            + financialMonthDays[period];

                        if ( (((selYear + 1) % 400 == 0) || (((selYear + 1) % 100 != 0 && (selYear + 1) % 4 == 0)))
                            && period == 10 )
                        {
                            endD = "" + (selYear + 1) + "-" + financialMonthOrder[period] + "-"
                                + (financialMonthDays[period] + 1);
                        }
                    }
                    else
                    {
                        startD = "" + selYear + "-" + financialMonthOrder[period] + "-01";
                        endD = "" + selYear + "-" + financialMonthOrder[period] + "-" + financialMonthDays[period];
                    }

                    Date sDate = format.parseDate( startD );
                    Date eDate = format.parseDate( endD );

                    List<Period> periodList = new ArrayList<Period>(
                        periodService.getIntersectingPeriods( sDate, eDate ) );
                    List<Integer> periodIds = new ArrayList<Integer>( getIdentifiers( Period.class, periodList ) );

                    selStartPeriodList.add( format.parseDate( startD ) );
                    selEndPeriodList.add( format.parseDate( endD ) );

                    periodMap.put( pCount, periodIds );
                    pCount++;

                    periodNames.add( simpleDateFormat.format( format.parseDate( startD ) ) );
                }
            }

        }

        else
        {
            Integer pCount = 0;
            for ( String year : yearLB )
            {
                int selYear = Integer.parseInt( year );

                if ( periodTypeLB.equalsIgnoreCase( YearlyPeriodType.NAME ) )
                {
                    startD = "" + selYear + "-01-01";
                    endD = "" + selYear + "-12-31";

                    Date sDate = format.parseDate( startD );
                    Date eDate = format.parseDate( endD );
                    selStartPeriodList.add( sDate );
                    selEndPeriodList.add( eDate );

                    List<Period> periodList = new ArrayList<Period>(
                        periodService.getIntersectingPeriods( sDate, eDate ) );
                    List<Integer> periodIds = new ArrayList<Integer>( getIdentifiers( Period.class, periodList ) );
                    periodMap.put( pCount, periodIds );

                    pCount++;

                    periodNames.add( "" + selYear );

                    continue;
                }

                for ( String periodStr : periodLB )
                {
                    if ( periodTypeLB.equalsIgnoreCase( MonthlyPeriodType.NAME ) )
                    {
                        int period = Integer.parseInt( periodStr );
                        simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" );

                        startD = "" + selYear + "-" + monthOrder[period] + "-01";
                        endD = "" + selYear + "-" + monthOrder[period] + "-" + monthDays[period];

                        // check for leapYear
                        if ( (((selYear) % 400 == 0) || (((selYear) % 100 != 0 && (selYear) % 4 == 0))) && period == 1 )
                        {
                            endD = "" + selYear + "-" + monthOrder[period] + "-" + (monthDays[period] + 1);
                        }

                        Date sDate = format.parseDate( startD );
                        Date eDate = format.parseDate( endD );
                        selStartPeriodList.add( sDate );
                        selEndPeriodList.add( eDate );

                        List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate,
                            eDate ) );
                        List<Integer> periodIds = new ArrayList<Integer>( getIdentifiers( Period.class, periodList ) );
                        periodMap.put( pCount, periodIds );
                        pCount++;

                        periodNames.add( simpleDateFormat.format( format.parseDate( startD ) ) );
                    }
                    else if ( periodTypeLB.equalsIgnoreCase( QuarterlyPeriodType.NAME ) )
                    {
                        int period = Integer.parseInt( periodStr );
                        if ( period == 0 )
                        {
                            startD = "" + selYear + "-01-01";
                            endD = "" + selYear + "-03-31";
                            periodNames.add( selYear + "-Q1" );
                        }
                        else if ( period == 1 )
                        {
                            startD = "" + selYear + "-04-01";
                            endD = "" + selYear + "-06-30";
                            periodNames.add( selYear + "-Q2" );
                        }
                        else if ( period == 2 )
                        {
                            startD = "" + selYear + "-07-01";
                            endD = "" + selYear + "-09-30";
                            periodNames.add( selYear + "-Q3" );
                        }
                        else
                        {
                            startD = "" + selYear + "-10-01";
                            endD = "" + selYear + "-12-31";
                            periodNames.add( (selYear) + "-Q4" );
                        }

                        Date sDate = format.parseDate( startD );
                        Date eDate = format.parseDate( endD );
                        selStartPeriodList.add( sDate );
                        selEndPeriodList.add( eDate );

                        List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate,
                            eDate ) );
                        List<Integer> periodIds = new ArrayList<Integer>( getIdentifiers( Period.class, periodList ) );
                        periodMap.put( pCount, periodIds );
                        pCount++;

                    }
                    else if ( periodTypeLB.equalsIgnoreCase( SixMonthlyPeriodType.NAME ) )
                    {
                        int period = Integer.parseInt( periodStr );
                        if ( period == 0 )
                        {
                            startD = "" + selYear + "-01-01";
                            endD = "" + selYear + "-06-30";
                            periodNames.add( selYear + "-HY1" );
                        }
                        else
                        {
                            startD = "" + selYear + "-07-01";
                            endD = "" + selYear + "-12-31";
                            periodNames.add( selYear + "-HY2" );
                        }

                        Date sDate = format.parseDate( startD );
                        Date eDate = format.parseDate( endD );
                        selStartPeriodList.add( sDate );
                        selEndPeriodList.add( eDate );

                        List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate,
                            eDate ) );
                        List<Integer> periodIds = new ArrayList<Integer>( getIdentifiers( Period.class, periodList ) );
                        periodMap.put( pCount, periodIds );
                        pCount++;
                    }
                    else if ( periodTypeLB.equalsIgnoreCase( DailyPeriodType.NAME ) )
                    {
                        String month = periodStr.split( "-" )[0];
                        String date = periodStr.split( "-" )[1];

                        startD = selYear + "-" + periodStr;
                        endD = selYear + "-" + periodStr;

                        if ( selYear % 4 != 0 && month.trim().equalsIgnoreCase( "02" )
                            && date.trim().equalsIgnoreCase( "29" ) )
                        {
                            continue;
                        }

                        startD = selYear + "-" + month + "-" + date;
                        endD = selYear + "-" + month + "-" + date;

                        Date sDate = format.parseDate( startD );
                        Date eDate = format.parseDate( endD );
                        selStartPeriodList.add( sDate );
                        selEndPeriodList.add( eDate );

                        List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate,
                            eDate ) );
                        List<Integer> periodIds = new ArrayList<Integer>( getIdentifiers( Period.class, periodList ) );
                        periodMap.put( pCount, periodIds );
                        pCount++;

                        System.out.println( startD + " *** " + endD );
                        periodNames.add( startD );
                    }
                }
            }
        }

        initialize();

        int headerRow = 0;
        int headerCol = 0;
        String outputReportPath = System.getenv( "DHIS2_HOME" ) + File.separator + Configuration_IN.DEFAULT_TEMPFOLDER;
        File newdir = new File( outputReportPath );
        if ( !newdir.exists() )
        {
            newdir.mkdirs();
        }
        outputReportPath += File.separator + UUID.randomUUID().toString() + ".xls";
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( outputReportPath ) );
        WritableSheet sheet0 = outputReportWorkbook.createSheet( "ValidationRule", 0 );

        sheet0.addCell( new Label( headerCol, headerRow, "RHS (DATA)", getPinkCellFormat() ) );
        headerRow++;
        sheet0.addCell( new Label( headerCol, headerRow, "LHS (DATA)", getAquaCellFormat() ) );
        headerRow++;
        headerCol++;

        List<ValidationRule> vaRules = getValidationRuleDesign();

        dataElementIdByComma = getDataelementIdsAsString( vaRules );

        System.out.println( "********** vaRules Size : " + vaRules.size() + "**********************" );
        for ( ValidationRule vr : vaRules )
        {
            headerRow = 0;
            if ( vr.getLhsname().trim().equals( "0" ) )
            {
                // System.out.println(vr.getLhsname());
                sheet0.mergeCells( headerCol, headerRow, headerCol, headerRow + 1 );
                sheet0.addCell( new Label( headerCol, headerRow, vr.getRhsname(), getHeadingCellFormat() ) );
                headerRow++;
            }
            else
            {
                // System.out.println(vr.getLhsname());
                sheet0.addCell( new Label( headerCol, headerRow, vr.getRhsname(), getHeadingCellFormat() ) );
                headerRow++;
                sheet0.addCell( new Label( headerCol, headerRow, vr.getLhsname(), getHeadingCellFormat() ) );
                headerRow++;
            }

            headerCol++;
        }

        if ( orgUnitSelListCB.equalsIgnoreCase( ORGUNITLEVEL ) )
        {
            if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
            {
                selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
                //selOUList = new ArrayList<OrganisationUnit>( organisationUnitService.getLeafOrganisationUnits( selOrgUnit.getId() ) );
                
                selOUList = new ArrayList<OrganisationUnit>( getLeafOrganisationUnits( selOrgUnit.getId() ) );
                
                Map<Integer, Integer> orgunitLevelMap = new HashMap<Integer, Integer>(
                    reportService.getOrgunitLevelMap() );

                Iterator<OrganisationUnit> ouIterator = selOUList.iterator();
                while ( ouIterator.hasNext() )
                {
                    OrganisationUnit orgU = ouIterator.next();

                    Integer level = orgunitLevelMap.get( orgU.getId() );
                    if ( level == null )
                        level = organisationUnitService.getLevelOfOrganisationUnit( orgU.getId() );
                    if ( level > orgUnitLevelCB )
                    {
                        ouIterator.remove();
                    }
                }

            }
            else if ( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
            {

                selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
                selOUList = new ArrayList<OrganisationUnit>(
                    organisationUnitService.getOrganisationUnitWithChildren( selOrgUnit.getId() ) );

                Map<Integer, Integer> orgunitLevelMap = new HashMap<Integer, Integer>(
                    reportService.getOrgunitLevelMap() );

                Iterator<OrganisationUnit> ouIterator = selOUList.iterator();
                while ( ouIterator.hasNext() )
                {
                    OrganisationUnit orgU = ouIterator.next();

                    Integer level = orgunitLevelMap.get( orgU.getId() );
                    if ( level == null )
                        level = organisationUnitService.getLevelOfOrganisationUnit( orgU.getId() );
                    if ( level > orgUnitLevelCB )
                    {
                        ouIterator.remove();
                    }
                }
            }
            else if ( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
            {

                selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
                selOUList = new ArrayList<OrganisationUnit>(
                    organisationUnitService.getOrganisationUnitWithChildren( selOrgUnit.getId() ) );

                Map<Integer, Integer> orgunitLevelMap = new HashMap<Integer, Integer>(
                    reportService.getOrgunitLevelMap() );

                Iterator<OrganisationUnit> ouIterator = selOUList.iterator();
                while ( ouIterator.hasNext() )
                {
                    OrganisationUnit orgU = ouIterator.next();

                    Integer level = orgunitLevelMap.get( orgU.getId() );
                    if ( level == null )
                        level = organisationUnitService.getLevelOfOrganisationUnit( orgU.getId() );
                    if ( level > orgUnitLevelCB )
                    {
                        ouIterator.remove();
                    }
                }
            }
            else
            {
                for ( String ouStr : orgUnitListCB )
                {
                    OrganisationUnit ou = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouStr ) );
                    selOUList.add( ou );
                }
            }
        }
        else if ( orgUnitSelListCB.equalsIgnoreCase( ORGUNITGRP ) )
        {
            if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
            {

                selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
                //selOUList = new ArrayList<OrganisationUnit>(  organisationUnitService.getLeafOrganisationUnits( selOrgUnit.getId() ) );
                selOUList = new ArrayList<OrganisationUnit>( getLeafOrganisationUnits( selOrgUnit.getId() ) );
                
                
                OrganisationUnitGroup selOrgUnitGroup = organisationUnitGroupService
                    .getOrganisationUnitGroup( orgUnitLevelCB );
                List<OrganisationUnit> orgUnitList1 = new ArrayList<OrganisationUnit>( selOrgUnitGroup.getMembers() );
                selOUList.retainAll( orgUnitList1 );
            }
            else if ( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
            {

                selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
                selOUList = new ArrayList<OrganisationUnit>(
                    organisationUnitService.getOrganisationUnitWithChildren( selOrgUnit.getId() ) );

                OrganisationUnitGroup selOrgUnitGroup = organisationUnitGroupService
                    .getOrganisationUnitGroup( orgUnitLevelCB );
                List<OrganisationUnit> orgUnitList1 = new ArrayList<OrganisationUnit>( selOrgUnitGroup.getMembers() );

                selOUList.retainAll( orgUnitList1 );
            }

            else if ( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
            {

                selOrgUnit = organisationUnitService.getOrganisationUnit( Integer.parseInt( orgUnitListCB.get( 0 ) ) );
                selOUList = new ArrayList<OrganisationUnit>(
                    organisationUnitService.getOrganisationUnitWithChildren( selOrgUnit.getId() ) );

                OrganisationUnitGroup selOrgUnitGroup = organisationUnitGroupService
                    .getOrganisationUnitGroup( orgUnitLevelCB );
                List<OrganisationUnit> orgUnitList1 = new ArrayList<OrganisationUnit>( selOrgUnitGroup.getMembers() );

                selOUList.retainAll( orgUnitList1 );
            }
            else
            {
                for ( String ouStr : orgUnitListCB )
                {
                    OrganisationUnit ou = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouStr ) );
                    selOUList.add( ou );
                }
            }
        }
        else if ( orgUnitSelListCB.equalsIgnoreCase( ORGUNITSELECTED ) )
        {
            for ( String ouStr : orgUnitListCB )
            {
                OrganisationUnit ou = organisationUnitService.getOrganisationUnit( Integer.parseInt( ouStr ) );
                selOUList.add( ou );
            }
            if ( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
            {

                for ( String ouStr : orgUnitListCB )
                {
                    System.out.println( "Organisation Unit is : ========" + ouStr );
                    OrganisationUnit ou = organisationUnitService
                        .getOrganisationUnit( Integer.parseInt( ouStr.trim() ) );
                    selOUList.add( ou );
                }
            }

            else if ( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
            {
                for ( String ouStr : orgUnitListCB )
                {
                    System.out.println( "Organisation Unit is : ========" + ouStr );
                    OrganisationUnit ou = organisationUnitService
                        .getOrganisationUnit( Integer.parseInt( ouStr.trim() ) );
                    selOUList.add( ou );
                }
            }

            else if ( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
            {
                for ( String ouStr : orgUnitListCB )
                {
                    System.out.println( "Organisation Unit is : ========" + ouStr );
                    OrganisationUnit ou = organisationUnitService
                        .getOrganisationUnit( Integer.parseInt( ouStr.trim() ) );
                    selOUList.add( ou );
                }
            }
            else
            {
                for ( String ouStr : orgUnitListCB )
                {
                    System.out.println( "Organisation Unit is : ========" + ouStr );
                    OrganisationUnit ou = organisationUnitService
                        .getOrganisationUnit( Integer.parseInt( ouStr.trim() ) );
                    selOUList.add( ou );
                }
            }
        }

        int count = 1;
        headerCol = 0;
        headerRow = 2;
        for ( OrganisationUnit ou : selOUList )
        {
            // System.out.println(ou.getName() + " : " +new Date());
            sheet0.setColumnView( headerCol, ou.getName().length() + 2 );
            sheet0.addCell( new Label( headerCol, headerRow, count + " " + ou.getName(), getWhiteCellFormat() ) );

            List<OrganisationUnit> orgUnitChildTree = new ArrayList<OrganisationUnit>(
                organisationUnitService.getOrganisationUnitWithChildren( ou.getId() ) );
            List<Integer> orgUnitIds = new ArrayList<Integer>(
                getIdentifiers( OrganisationUnit.class, orgUnitChildTree ) );
            orgUnitIdsByComma = getCommaDelimitedString( orgUnitIds );

            Map<String, String> dataValueMap = new HashMap<String, String>( getDataValueCountforDataElements(
                dataElementIdByComma, orgUnitIdsByComma, periodIdsByComma ) );

            int colCount = 1;
            for ( ValidationRule vr : vaRules )
            {
                Double rhsValue = Double.parseDouble( getAggVal( vr.getRhsexp(), dataValueMap ) );
                ;
                Double lhsValue = 0.0;
                if ( !vr.getLhsname().equals( "0" ) )
                {
                    lhsValue = Double.parseDouble( getAggVal( vr.getLhsexp(), dataValueMap ) );
                    ;
                }

                String color1 = vr.getColor().split( "," )[0];
                String color2 = vr.getColor().split( "," )[1];
                String color3 = vr.getColor().split( "," )[2];

                if ( rhsValue != null && lhsValue != null )
                {
                    Double resultValue = rhsValue - lhsValue;

                    WritableCellFormat writableCellFormat = null;

                    if ( resultValue == 0 )
                    {
                        if ( color2.equalsIgnoreCase( RED ) )
                        {
                            writableCellFormat = getColorCellFormat( Colour.RED );
                            sheet0.addCell( new Number( headerCol + colCount, headerRow, resultValue,
                                writableCellFormat ) );
                        }
                        else if ( color2.equalsIgnoreCase( GREEN ) )
                        {
                            writableCellFormat = getColorCellFormat( Colour.GREEN );
                            sheet0.addCell( new Number( headerCol + colCount, headerRow, resultValue,
                                writableCellFormat ) );
                        }
                        else if ( color2.equalsIgnoreCase( ORANGE ) )
                        {
                            writableCellFormat = getColorCellFormat( Colour.ORANGE );
                            sheet0.addCell( new Number( headerCol + colCount, headerRow, resultValue,
                                writableCellFormat ) );
                        }
                        else
                        {
                            writableCellFormat = getColorCellFormat( Colour.WHITE );
                            sheet0.addCell( new Number( headerCol + colCount, headerRow, resultValue,
                                writableCellFormat ) );
                        }

                        // System.out.println("Color2: "+writableCellFormat.getBackgroundColour());
                    }
                    else if ( resultValue < 0 )
                    {
                        if ( color1.equalsIgnoreCase( RED ) )
                        {
                            writableCellFormat = getColorCellFormat( Colour.RED );
                            sheet0.addCell( new Number( headerCol + colCount, headerRow, resultValue,
                                writableCellFormat ) );
                        }
                        else if ( color1.equalsIgnoreCase( GREEN ) )
                        {
                            writableCellFormat = getColorCellFormat( Colour.GREEN );
                            sheet0.addCell( new Number( headerCol + colCount, headerRow, resultValue,
                                writableCellFormat ) );
                        }
                        else if ( color1.equalsIgnoreCase( ORANGE ) )
                        {
                            writableCellFormat = getColorCellFormat( Colour.ORANGE );
                            sheet0.addCell( new Number( headerCol + colCount, headerRow, resultValue,
                                writableCellFormat ) );
                        }
                        else
                        {
                            writableCellFormat = getColorCellFormat( Colour.WHITE );
                            sheet0.addCell( new Number( headerCol + colCount, headerRow, resultValue,
                                writableCellFormat ) );
                        }

                    }
                    else
                    {
                        if ( color3.equalsIgnoreCase( RED ) )
                        {
                            writableCellFormat = getColorCellFormat( Colour.RED );
                            sheet0.addCell( new Number( headerCol + colCount, headerRow, resultValue,
                                writableCellFormat ) );
                        }
                        else if ( color3.equalsIgnoreCase( GREEN ) )
                        {
                            writableCellFormat = getColorCellFormat( Colour.GREEN );
                            sheet0.addCell( new Number( headerCol + colCount, headerRow, resultValue,
                                writableCellFormat ) );
                        }
                        else if ( color3.equalsIgnoreCase( ORANGE ) )
                        {
                            writableCellFormat = getColorCellFormat( Colour.ORANGE );
                            sheet0.addCell( new Number( headerCol + colCount, headerRow, resultValue,
                                writableCellFormat ) );
                        }
                        else
                        {
                            writableCellFormat = getColorCellFormat( Colour.WHITE );
                            sheet0.addCell( new Number( headerCol + colCount, headerRow, resultValue,
                                writableCellFormat ) );
                        }

                    }
                    // System.out.print(color1);
                }
                // System.out.println(ou.getId()+" LHS Exp"+vr.lhsexp
                // +" LHSVal: "+lhsValue +"RHS Exp"+vr.rhsexp
                // +" RHSVal "+rhsValue );

                colCount++;
            }

            headerRow++;
            count++;
        }

        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = "ValidationRule" + new Date() + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );

        return SUCCESS;
    }

    public void initialize()
    {
        dataElementIdsByComma = "-1";

        List<Period> periods = new ArrayList<Period>();
        int periodCount = 0;
        for ( Date sDate : selStartPeriodList )
        {
            Date eDate = selEndPeriodList.get( periodCount );
            List<Period> periodList = new ArrayList<Period>( periodService.getIntersectingPeriods( sDate, eDate ) );

            if ( periodList != null && periodList.size() > 0 )
                periods.addAll( periodList );
            periodCount++;
        }
        Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers( Period.class, periods ) );
        periodIdsByComma = getCommaDelimitedString( periodIds );
    }

    public WritableCellFormat getHeadingCellFormat()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.GRAY_25 );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getPinkCellFormat()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();
        WritableFont fontFormat = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD, false,
            UnderlineStyle.NO_UNDERLINE, Colour.WHITE );
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.PERIWINKLE );
        wCellformat.setFont( fontFormat );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getAquaCellFormat()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();
        WritableFont fontFormat = new WritableFont( WritableFont.ARIAL, 10, WritableFont.BOLD, false,
            UnderlineStyle.NO_UNDERLINE, Colour.WHITE );
        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setFont( fontFormat );
        wCellformat.setBackground( Colour.PALE_BLUE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getWhiteCellFormat()
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        // wCellformat.setAlignment( Alignment.CENTRE );
        wCellformat.setBackground( Colour.WHITE );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public WritableCellFormat getColorCellFormat( Colour color )
        throws Exception
    {
        WritableCellFormat wCellformat = new WritableCellFormat();

        wCellformat.setBorder( Border.ALL, BorderLineStyle.THIN );
        // wCellformat.setAlignment( Alignment.CENTRE );

        wCellformat.setBackground( color );
        wCellformat.setVerticalAlignment( VerticalAlignment.CENTRE );
        wCellformat.setWrap( true );

        return wCellformat;
    }

    public List<ValidationRule> getValidationRuleDesign()
    {
        String path = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + "ValidationRule.xml";
        List<ValidationRule> vaList = new ArrayList<ValidationRule>();
        try
        {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse( new File( path ) );
            if ( doc == null )
            {
                System.out.println( "XML File Not Found at user home" );
                return null;
            }

            NodeList listOfReports = doc.getElementsByTagName( "validationcheck" );
            int totalReports = listOfReports.getLength();
            for ( int s = 0; s < totalReports; s++ )
            {
                Node reportNode = listOfReports.item( s );
                if ( reportNode.getNodeType() == Node.ELEMENT_NODE )
                {
                    Element reportElement = (Element) reportNode;

                    ValidationRule validationRule = new ValidationRule();

                    validationRule.setRhsname( reportElement.getAttribute( "rhsname" ) );
                    validationRule.setRhsexp( reportElement.getAttribute( "rhsexp" ) );
                    validationRule.setLhsname( reportElement.getAttribute( "lhsname" ) );
                    validationRule.setLhsexp( reportElement.getAttribute( "lhsexp" ) );
                    validationRule.setColor( reportElement.getAttribute( "color" ) );

                    vaList.add( validationRule );
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
        return vaList;
    }

    public String getDataelementIdsAsString( List<ValidationRule> validationRuleList )
    {
        String dataElmentIdsByComma = "-1";
        for ( ValidationRule vr : validationRuleList )
        {
            String formula = vr.getLhsexp() + " + " + vr.getRhsexp();
            //String formula = vr.getLeftSide() + " + " + vr.getRightSide();
            try
            {
                Pattern pattern = Pattern.compile( "(\\[\\d+\\.\\d+\\])" );

                Matcher matcher = pattern.matcher( formula );
                StringBuffer buffer = new StringBuffer();

                while ( matcher.find() )
                {
                    String replaceString = matcher.group();

                    replaceString = replaceString.replaceAll( "[\\[\\]]", "" );
                    replaceString = replaceString.substring( 0, replaceString.indexOf( '.' ) );

                    int dataElementId = Integer.parseInt( replaceString );
                    dataElmentIdsByComma += "," + dataElementId;
                    replaceString = "";
                    matcher.appendReplacement( buffer, replaceString );
                }
            }
            catch ( Exception e )
            {

            }
        }

        return dataElmentIdsByComma;
    }

    public Map<String, String> getDataValueCountforDataElements( String dataElementIdsByComma,
        String orgUnitIdsBycomma, String periodIdsBycomma )
    {
        Map<String, String> dataValueCountMap = new HashMap<String, String>();
        try
        {
            String query = "SELECT dataelementid, categoryoptioncomboid, SUM( value ) FROM datavalue " + " WHERE "
                + " dataelementid IN (" + dataElementIdsByComma + ") AND " + " sourceid IN ( " + orgUnitIdsBycomma
                + " ) AND" + " periodid IN (" + periodIdsBycomma + ")"
                + "GROUP BY dataelementid, categoryoptioncomboid";
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            // System.out.println( query );

            while ( rs.next() )
            {
                // String dataElementValue = rs.getString( 3 );
                dataValueCountMap.put( rs.getString( 1 ) + "." + rs.getString( 2 ), "" + rs.getDouble( 3 ) );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Exception: ", e );
        }

        return dataValueCountMap;
    }

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

                if ( replaceString == null )
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
    
    
    // get Leaf Organisation Units
    public Collection<OrganisationUnit> getLeafOrganisationUnits( int id )
    {
        Collection<OrganisationUnit> units = organisationUnitService.getOrganisationUnitWithChildren( id );

        return FilterUtils.filter( units, new Filter<OrganisationUnit>()
        {
            public boolean retain( OrganisationUnit object )
            {
                return object != null && object.getChildren().isEmpty();
            }
        } );
    }    
}
