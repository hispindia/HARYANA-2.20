package org.hisp.dhis.reports.periodorgunitserviceprogressreport.action;


import static org.hisp.dhis.util.ConversionUtils.getIdentifiers;
import static org.hisp.dhis.util.TextUtils.getCommaDelimitedString;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.VerticalAlignment;
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
import org.hisp.dhis.period.PeriodType;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.reports.Report_in;
import org.hisp.dhis.reports.Report_inDesign;
import org.hisp.dhis.system.util.MathUtils;
import org.hisp.dhis.util.comparator.PeriodStartDateComparator;

import com.opensymphony.xwork2.Action;



public class GeneratePeriodOrgUnitServiseProgressAnalyserResultAction implements Action
{
    private final String GENERATEAGGDATA = "generateaggdata";

    private final String USEEXISTINGAGGDATA = "useexistingaggdata";

    private final String USECAPTUREDDATA = "usecaptureddata";
    
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
    
    private I18nFormat format;

    public void setFormat( I18nFormat format )
    {
        this.format = format;
    }
    // -------------------------------------------------------------------------
    // Input/output
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
    
    private int availablePeriodsto;
    
    public void setAvailablePeriodsto( int availablePeriodsto )
    {
        this.availablePeriodsto = availablePeriodsto;
    }
    
    private String periodTypeId;
    
    public void setPeriodTypeId( String periodTypeId )
    {
        this.periodTypeId = periodTypeId;
    }
    
    private String aggData;
    
    public void setAggData( String aggData )
    {
        this.aggData = aggData;
    }
    
    private String reportList;

    public void setReportList( String reportList )
    {
        this.reportList = reportList;
    }
    
    private Period selectedPeriod;
    
    private Period selectedEndPeriod;

    private SimpleDateFormat simpleDateFormat;
    
    
    private String raFolderName;
    
    private OrganisationUnit selectedOrgUnit;
    
    private List<OrganisationUnit> orgUnitList;
    
    
    private String organisationUnitGroupId;

    public void setOrganisationUnitGroupId( String organisationUnitGroupId )
    {
        this.organisationUnitGroupId = organisationUnitGroupId;
    }
    
    
    private Date sDate;

    private Date eDate;
    
    private int sheetNo = 0;

    private int tempColNo;

    private int tempRowNo;
    
    
    private String deCodesXMLFileName = "";
    
    private String reportFileNameTB = "";
    
    private List<Period> periodList = new ArrayList<Period>();
    private PeriodType periodType;
    
    /*
    private OrganisationUnitGroup orgUnitGroup =  new  OrganisationUnitGroup();
    
    private List<OrganisationUnit> orgGroupMembers = new ArrayList<OrganisationUnit>();
    */
    
    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    
    public String execute() throws Exception
    {
        
        // Initialization
        
        raFolderName = reportService.getRAFolderName();
        
        // Report Info
        Report_in selReportObj = reportService.getReport( Integer.parseInt( reportList ) );
        
        
        deCodesXMLFileName = selReportObj.getXmlTemplateName();
        //String reportModelTB = selReportObj.getModel();
        reportFileNameTB = selReportObj.getExcelTemplateName();
        
        // Period Info
        selectedPeriod = periodService.getPeriod( availablePeriods );
        selectedEndPeriod = periodService.getPeriod( availablePeriodsto );

        sDate = format.parseDate( String.valueOf( selectedPeriod.getStartDate() ) );
        eDate = format.parseDate( String.valueOf( selectedEndPeriod.getEndDate() ) );

        periodType = periodService.getPeriodTypeByName( periodTypeId );
        periodList = new ArrayList<Period>( periodService.getPeriodsBetweenDates( periodType, sDate, eDate ) );
        Collections.sort( periodList, new PeriodStartDateComparator() );
        
        if( periodTypeId.equalsIgnoreCase( "monthly" ) )
        {
            simpleDateFormat = new SimpleDateFormat( "MMM-yyyy" ); 
        }
        else if( periodTypeId.equalsIgnoreCase( "yearly" ) )
        {
            simpleDateFormat = new SimpleDateFormat( "yyyy" );
        }
        else
        {
            simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        }
        
        
        // OrgUnit Related Info
        selectedOrgUnit = new OrganisationUnit();
        selectedOrgUnit = organisationUnitService.getOrganisationUnit( ouIDTB );
        int selectedOrgUnitLevel = organisationUnitService.getLevelOfOrganisationUnit( ouIDTB );
        
        orgUnitList = new ArrayList<OrganisationUnit>( selectedOrgUnit.getChildren() );
        Collections.sort( orgUnitList, new IdentifiableObjectNameComparator() );
        
        /*
        if( selectedOrgUnitLevel != 1 )
        {
            orgUnitList.add( selectedOrgUnit );
        }
        */
     
        
        System.out.println( selectedOrgUnit.getName()+ " : " + selReportObj.getName()+" : Report Generation Start Time is : " + new Date() );
        
        
        
        if ( organisationUnitGroupId.equalsIgnoreCase( "ALL" ) )
        {
            generateReportWithOutGroupMember();
        }
        else
        {
            generateReportWithGroupMember();
        }
        
            
        
        System.out.println( selectedOrgUnit.getName()+ " : " + selReportObj.getName()+" : Report Generation End Time is : " + new Date() );
        
        return SUCCESS;
        
    }        
        
       
    public void generateReportWithGroupMember() throws Exception
    {

        OrganisationUnitGroup orgUnitGroup = null;
        
        List<OrganisationUnit> orgGroupMembers = null;

        orgUnitGroup = organisationUnitGroupService.getOrganisationUnitGroup( Integer.parseInt( organisationUnitGroupId ) );
        orgGroupMembers = new ArrayList<OrganisationUnit>( orgUnitGroup.getMembers() );
        
        String periodIdsByComma = "-1";
        
        int orgUnitStartCol = 0 ;
        int orgUnitStartRow = 0;
        int periodStartCol = 0;
        int periodStartRow = 0;
        int dataElementStartCol = 0;
        int dataElementStartRow = 0;
        
        // Getting DataValues
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );
        String dataElmentIdsByComma = reportService.getDataelementIds( reportDesignList );
        
        
        //System.out.println( " : Size of de-code is  : " + reportDesignList.size() );
        
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
        
        String[] dataElementName = new String[ reportDesignList.size() ];
        
        int dataElementCount = 0;
        Iterator<Report_inDesign> deCountIterator = reportDesignList.iterator();
        while ( deCountIterator.hasNext() )
        {
            Report_inDesign report_inDesign = (Report_inDesign) deCountIterator.next();
            
            String sType = report_inDesign.getStype();
            
            if ( sType.equalsIgnoreCase( "dataelementname" ) )
            {
                int rowNo = report_inDesign.getRowno();
                int colNo = report_inDesign.getColno();
                int sheetNo = report_inDesign.getSheetno();
                
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                
                String deName = sheet0.getCell( colNo, rowNo ).getContents();
                
                dataElementName[dataElementCount] = deName;
                
                //System.out.println( " : dataElement Name is  : " + dataElementName[dataElementCount] );
                
                dataElementCount ++;
            }
            
            if ( sType.equalsIgnoreCase( "progressiveperiod" ) )
            {
                periodStartCol = report_inDesign.getColno();
                periodStartRow = report_inDesign.getRowno();;
            }
        }
        
        //System.out.println( " : periodStartCol  : " + periodStartCol + " -- periodStartRow : "  + periodStartRow + " -- " + selReportObj.getExcelTemplateName() + " -- " + selReportObj.getXmlTemplateName() );
        int colCount = 1;
        
        
        int tempPeriodCount = 0;
        int tempColumnNo = 0;
        for( Period period : periodList )
        {
            WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
            
            sheet0.mergeCells( periodStartCol, periodStartRow, periodStartCol + dataElementCount-1 , periodStartRow );
            sheet0.addCell( new Label( periodStartCol, periodStartRow, simpleDateFormat.format( period.getStartDate()), getCellFormat1() ) );
            
            for( int i=0 ; i < dataElementCount ; i++ )
            {
                sheet0.addCell( new Label( periodStartCol+i, periodStartRow+1, dataElementName[i], getCellFormat1() ) );
            }
            
            periodStartCol +=  dataElementCount;
            tempPeriodCount++;
            tempColumnNo = periodStartCol;
        }
        
        //System.out.println( " : tempColumnNo  : " + tempColumnNo + " : tempColumnNo  : " + periodStartCol );
        
        WritableSheet tempSheet0 = outputReportWorkbook.getSheet( sheetNo );
        
        tempSheet0.mergeCells( periodStartCol, periodStartRow, periodStartCol + dataElementCount-1 , periodStartRow );
        tempSheet0.addCell( new Label( periodStartCol, periodStartRow, "Total", getCellFormat1() ) );
        
        for( int i=0 ; i < dataElementCount ; i++ )
        {
            tempSheet0.addCell( new Label( periodStartCol+i, periodStartRow+1, dataElementName[i], getCellFormat1() ) );
        }
        
        //System.out.println( " : tempColumnNo  : " + tempColumnNo + " : tempColumnNo  : " + periodStartCol );
        
        colCount = tempColumnNo;
        
        //WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
        
        
        //System.out.println( " : dataElement count is  : " + dataElementCount );
        
        int lastRowNo = 0;
        
        Map<OrganisationUnit, Map<String,Double>> orgUnitwiseRowTotalMap = new HashMap<OrganisationUnit, Map<String,Double>>();
        Map<String, Double> orgUnitwiseColTotalMap = new HashMap<String,Double>();
        int orgUnitCount = 0;
        int orgUnitGroupMemberCount = 0;
        int slno = 1;
        
        Iterator<OrganisationUnit> orgUnit = orgUnitList.iterator();
        while ( orgUnit.hasNext() )
        {
            OrganisationUnit currentOrgUnit = (OrganisationUnit) orgUnit.next();
            
            //System.out.println( " CurrentOrgUnit : " + currentOrgUnit.getName() );
            
            //if( orgUnitGroup != null && orgGroupMembers.size() != 0 )
            if( orgUnitGroup != null )
            {
                List<OrganisationUnit> ouList =  new ArrayList<OrganisationUnit>();
                ouList.addAll( organisationUnitService.getOrganisationUnitWithChildren( currentOrgUnit.getId() ) );
                
                ouList.retainAll( orgGroupMembers );
                
                //System.out.println( " -- Group Name : " + orgUnitGroup.getName() + " Size of Group Member : " + ouList.size() );
                
                
                Iterator<OrganisationUnit> tempOrgUnit = ouList.iterator();
                
                while ( tempOrgUnit.hasNext() )
                {
                    OrganisationUnit groupMemberOrgUnit = (OrganisationUnit) tempOrgUnit.next();
                    
                    //System.out.println( " -- " + slno  + " --Group Member Name :  " + groupMemberOrgUnit.getName() + " -- " +  orgUnitCount + " -- " + orgUnitGroupMemberCount );
                    
                    Map<String,Double> oneRowTotal = new HashMap<String, Double>();
                    int periodCount = 0;
                    int tempPeriodInrc = 0;
                    
                    for( Period period : periodList )
                    {
                        if( periodTypeId.equalsIgnoreCase( "daily" ) )
                        {
                            periodIdsByComma = ""+period.getId();
                        }
                        else
                        {
                            Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodService.getIntersectingPeriods( period.getStartDate(), period.getEndDate() ) ) );
                            periodIdsByComma = getCommaDelimitedString( periodIds );
                        }
                        
                        
                        Map<String, String> aggDeMap = new HashMap<String, String>();
                        if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                        {
                            aggDeMap.putAll( reportService.getResultDataValueFromAggregateTable( groupMemberOrgUnit.getId(), dataElmentIdsByComma, periodIdsByComma ) );
                        }
                        else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                        {
                            List<OrganisationUnit> childOrgUnitTree = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitWithChildren( groupMemberOrgUnit.getId() ) );
                            List<Integer> childOrgUnitTreeIds = new ArrayList<Integer>( getIdentifiers( OrganisationUnit.class, childOrgUnitTree ) );
                            String childOrgUnitsByComma = getCommaDelimitedString( childOrgUnitTreeIds );

                            aggDeMap.putAll( reportService.getAggDataFromDataValueTable( childOrgUnitsByComma, dataElmentIdsByComma, periodIdsByComma ) );
                        }
                        else if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) )
                        {
                            aggDeMap.putAll( reportService.getAggDataFromDataValueTable( ""+groupMemberOrgUnit.getId(), dataElmentIdsByComma, periodIdsByComma ) );
                        }
                        
                        int count = 0;
                        Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
                        while (  reportDesignIterator.hasNext() )
                        {
                            Report_inDesign reportDesign =  reportDesignIterator.next();
                            
                            String deCodeString = reportDesign.getExpression();

                            String sType = reportDesign.getStype();
                            String tempStr = "";

                            //tempRowNo = reportDesign.getRowno();
                            //tempColNo = reportDesign.getColno();
                            //sheetNo = reportDesign.getSheetno();
                            
                            //WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                            
                            if( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                            {
                                tempStr = selectedOrgUnit.getName();
                            } 
                            
                            else if ( deCodeString.equalsIgnoreCase( "PERIOD-FROM" ) )
                            {
                                tempStr = simpleDateFormat.format( selectedPeriod.getStartDate() );
                            }
                            
                            else if ( deCodeString.equalsIgnoreCase( "PERIOD-TO" ) )
                            {
                                tempStr = simpleDateFormat.format( selectedEndPeriod.getEndDate() );
                            }
                            else if( deCodeString.equalsIgnoreCase( "PROGRESSIVE-PERIOD" ) )
                            {
                                tempStr = simpleDateFormat.format( period.getStartDate() );
                            } 
                            
                            else if( deCodeString.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
                            {
                                tempStr = currentOrgUnit.getName();
                            }
                            
                            else if( deCodeString.equalsIgnoreCase( "ORGUNIT-GROUP" ) )
                            {
                                if( ouList.size() == 0)
                                {
                                    tempStr = " ";
                                }
                                else
                                {
                                    tempStr = groupMemberOrgUnit.getName();
                                }
                                //tempStr = groupMemberOrgUnit.getName();
                            }
                            
                            else if( deCodeString.equalsIgnoreCase( "SLNo" ) )
                            {
                                tempStr = "" + slno;
                            } 
                            else
                            {
                                if( sType.equalsIgnoreCase( "dataelement" ) )
                                {
                                    if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) ) 
                                    {
                                        tempStr = getAggVal( deCodeString, aggDeMap );
                                    } 
                                    else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                                    {
                                        tempStr = getAggVal( deCodeString, aggDeMap );
                                    }
                                    else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                                    {
                                        tempStr = getAggVal( deCodeString, aggDeMap );
                                    }
                                    
                                    Double tempD = oneRowTotal.get( deCodeString );
                                    if( tempD == null )
                                    {
                                        try
                                        {
                                             Double tempD1 = Double.parseDouble( tempStr );
                                             oneRowTotal.put( deCodeString, tempD1 );
                                        }
                                        catch( Exception e )
                                        {
                                            oneRowTotal.put( deCodeString, 0.0 );
                                        }
                                    }
                                    else
                                    {
                                        try
                                        {
                                             Double tempD1 = Double.parseDouble( tempStr ) + tempD;
                                             oneRowTotal.put( deCodeString, tempD1 );
                                        }
                                        catch( Exception e )
                                        {
                                            //oneRowTotal.put( deCodeString, 0.0 );
                                        }
                                    }
                                    
                                    
                                    Double tempD2 = orgUnitwiseColTotalMap.get( period.getId()+":"+deCodeString );
                                    if( tempD2 == null )
                                    {
                                        try
                                        {
                                             Double tempD3 = Double.parseDouble( tempStr );
                                             orgUnitwiseColTotalMap.put( period.getId()+":"+deCodeString , tempD3 );
                                        }
                                        catch( Exception e )
                                        {
                                            orgUnitwiseColTotalMap.put( period.getId()+":"+deCodeString , 0.0 );
                                        }
                                    }
                                    else
                                    {
                                        try
                                        {
                                             Double tempD3 = Double.parseDouble( tempStr ) + tempD2;
                                             orgUnitwiseColTotalMap.put( period.getId()+":"+deCodeString , tempD3 );
                                        }
                                        catch( Exception e )
                                        {
                                            //oneRowTotal.put( deCodeString, 0.0 );
                                        }
                                    }
                                }
                                
                                
                            }
                            
                           
                            
                            int tempRowNo = reportDesign.getRowno();
                            int tempColNo = reportDesign.getColno();
                            //tempColNo = tempColNo + tempPeriodInrc;
                            int sheetNo = reportDesign.getSheetno();
                            WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                            
                            
                            
                            if ( sType.equalsIgnoreCase( "dataelement" ) )
                            {
                                if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "DE" ) || deCodeString.equalsIgnoreCase( "PERIOD-FROM" ) || deCodeString.equalsIgnoreCase( "PERIOD-TO" ) )
                                {
                                    //tempRowNo = reportDesign.getRowno();
                                    //tempColNo = reportDesign.getColno();
                                }
                                else
                                {
                                    if( deCodeString.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) || deCodeString.equalsIgnoreCase( "SLNo" ) || deCodeString.equalsIgnoreCase( "ORGUNIT-GROUP" ) )
                                    {
                                        //tempRowNo += orgUnitCount + orgUnitGroupMemberCount;
                                        tempRowNo = tempRowNo + orgUnitGroupMemberCount;
                                        lastRowNo = tempRowNo;
                                        //tempRowNo = tempRowNo + orgUnitCount + orgUnitGroupMemberCount;
                                        //tempColNo = reportDesign.getColno();
                                    }
                                   
                                    else
                                    {
                                        //tempRowNo += orgUnitCount + orgUnitGroupMemberCount;
                                        tempRowNo = tempRowNo + orgUnitGroupMemberCount;
                                        //tempRowNo = tempRowNo + orgUnitCount + orgUnitGroupMemberCount;
                                        tempColNo = tempColNo + tempPeriodInrc;
                                        lastRowNo = tempRowNo;
                                        
                                        //tempColNo += dataElementCount;
                                        //tempRowNo = reportDesign.getRowno();
                                    }
                                    
                                    //tempRowNo += orgUnitCount;
                                    //tempColNo += dataElementCount;
                                }
                                try
                                {
                                    /*
                                    if( orgUnitCount == orgUnitList.size()-1 && selectedOrgUnitLevel != 1 )
                                    {
                                        
                                        if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "DE" ) || deCodeString.equalsIgnoreCase( "PERIOD-FROM" ) || deCodeString.equalsIgnoreCase( "PERIOD-TO" ) )
                                        {
                                            continue;
                                        }
                                        else
                                        {
                                            sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), getCellFormat1() ) );
                                        }
                                    }
                                    */
                                    //else
                                    //{
                                        sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), wCellformat ) );
                                    //}
                                }
                               
                                catch( Exception e )
                                {
                                    /*
                                    if( orgUnitCount == orgUnitList.size()-1 && selectedOrgUnitLevel != 1 )
                                    {
                                        if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "DE" ) || deCodeString.equalsIgnoreCase( "PERIOD-FROM" ) || deCodeString.equalsIgnoreCase( "PERIOD-TO" ) )
                                        {
                                            continue;
                                        }
                                        else
                                        {
                                            sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, getCellFormat1() ) );
                                        }
                                        
                                    }
                                    */
                                    //else
                                   // {
                                        sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                                    //}
                                }
                                                                                                
                                //System.out.println( "--Sl.NO --"+ slno +" -- DECode : " + deCodeString + "   TempStr : " + tempStr + " -- Row No " + tempRowNo  + " -- Col No " + tempColNo  );
                            }
                            
                            count++;
                        }
                        
                        periodStartCol++;
                        periodCount++;
                        tempPeriodInrc += dataElementCount;
                    }
                    
                    orgUnitwiseRowTotalMap.put( groupMemberOrgUnit, oneRowTotal );
                    
                    slno++;
                    orgUnitGroupMemberCount++;
                }
            }
            //slno++;
            orgUnitCount++;
        }
        
        
        //System.out.println( "--Size of org UnitwiseRowTotalMap " + orgUnitwiseRowTotalMap.size() );
        
        /*
        for ( int i = 0 ; i < orgUnitwiseRowTotalMap.size() ; i++ )
        {
            System.out.println( "--Org Unit wis eRow Total Map is : " + orgUnitwiseRowTotalMap.keySet() );
        }
        */
        orgUnitGroupMemberCount = 0;
        orgUnit = orgUnitList.iterator();
        while ( orgUnit.hasNext() )
        {
            OrganisationUnit currentOrgUnit = (OrganisationUnit) orgUnit.next();
            if( orgUnitGroup != null )
            {
                List<OrganisationUnit> ouList =  new ArrayList<OrganisationUnit>();
                ouList.addAll( organisationUnitService.getOrganisationUnitWithChildren( currentOrgUnit.getId() ) );
                ouList.retainAll( orgGroupMembers );

                Iterator<OrganisationUnit> tempOrgUnit = ouList.iterator();
                while ( tempOrgUnit.hasNext() )
                {
                    OrganisationUnit groupMemberOrgUnit = (OrganisationUnit) tempOrgUnit.next();
                    
                    Map<String,Double> oneRowTotal = new HashMap<String, Double>( orgUnitwiseRowTotalMap.get( groupMemberOrgUnit ) );
                    
                    /*
                    System.out.println( "--Size of org UnitwiseRowTotalMap " + oneRowTotal.size() + " -- group Name is : "  + groupMemberOrgUnit.getName() );
                    
                    for ( int i = 0 ; i < oneRowTotal.size() ; i++ )
                    {
                        System.out.println( "-- Org Unit wis eRow Total Map is : " + oneRowTotal.keySet() );
                    }
                    */
                    int count = 0;
                    
                    Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
                    while (  reportDesignIterator.hasNext() )
                    {
                        Report_inDesign reportDesign =  reportDesignIterator.next();
                        String deCodeString = reportDesign.getExpression();
                        String sType = reportDesign.getStype();
                        int tempRowNo = reportDesign.getRowno() + orgUnitGroupMemberCount;
                        
                        int tempColNo = reportDesign.getColno();                            
                        
                        //int tempColNo = reportDesign.getColno() + colCount + count; 
                        
                        int sheetNo = reportDesign.getSheetno();
                        
                        //System.out.println( " -- DECode : " + deCodeString + "    -- Row No " + tempRowNo  + " -- Col No " + colCount++ + " -- Value is  " + oneRowTotal.get( deCodeString ) );
                        
                        WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                        if ( sType.equalsIgnoreCase( "dataelement" ) )
                        {
                            //System.out.println(  " -- S Type : " + sType + " -- DECode : " + deCodeString + "    -- Row No " + tempRowNo  + " -- Col No " + colCount++  );
                            
                            if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "PERIOD-FROM" ) || 
                                deCodeString.equalsIgnoreCase( "PERIOD-TO" ) || deCodeString.equalsIgnoreCase( "SLNo" ) ||
                                deCodeString.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) || deCodeString.equalsIgnoreCase( "ORGUNIT-GROUP" ) )
                            {
                                continue;
                            } 
                            else
                            {
                                sheet0.addCell( new Number( colCount + count, tempRowNo, oneRowTotal.get( deCodeString ), wCellformat ) );
                                count++;
                                
                            }
                            //System.out.println( " --DECode : " + deCodeString + " --colCount " + colCount + "--Row No " + tempRowNo  + "-- Col No " + tempColNo + " -- Value is  " + oneRowTotal.get( deCodeString ) );
                        }
                        
                        
                    }
                    orgUnitGroupMemberCount++;
                }
            }
            //slno++;
            orgUnitCount++;
        }
        
        Map<String,Double> grandTotal = new HashMap<String, Double>();
        int periodCount = 0;
        
        int tempTotalColumn = 0;
        
        int tempPeriodInrc = 0;
        for( Period period : periodList )
        {
            int count = 0;                      
            
            Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
            while (  reportDesignIterator.hasNext() )
            {
                Report_inDesign reportDesign =  reportDesignIterator.next();
                String deCodeString = reportDesign.getExpression();
                
                String sType = reportDesign.getStype();
                
                int tempRowNo = reportDesign.getRowno() + orgUnitGroupMemberCount;
                //int tempColNo = reportDesign.getColno()+ periodCount + count;
                int tempColNo = reportDesign.getColno() + tempPeriodInrc; 
                int sheetNo = reportDesign.getSheetno();
                
                tempTotalColumn = tempColNo;
                
                Double tempGrandTotal = grandTotal.get( deCodeString );
                if( tempGrandTotal == null )
                {
                    try
                    {
                         Double grandTotal1 = orgUnitwiseColTotalMap.get( period.getId()+":"+deCodeString );
                         grandTotal.put( deCodeString , grandTotal1 );
                    }
                    catch( Exception e )
                    {
                        grandTotal.put( deCodeString , 0.0 );
                    }
                }
                else
                {
                    try
                    {
                         Double grandTotal12 = orgUnitwiseColTotalMap.get( period.getId()+":"+deCodeString ) + tempGrandTotal;
                         grandTotal.put( deCodeString , grandTotal12 );
                    }
                    catch( Exception e )
                    {
                        //oneRowTotal.put( deCodeString, 0.0 );
                    }
                }
                
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                
                //int lastRowNo = tempRowNo + 1;
                
                //System.out.println( " -- DECode : " + deCodeString + "    -- Row No " + tempRowNo  + " -- Col No " + colCount++  );
                
                //sheet0.mergeCells( 0, lastRowNo +1, 2 , lastRowNo +1 );
                //sheet0.addCell( new Label( 0, lastRowNo +1, "Total", getCellFormat1() ) );
                
                sheet0.addCell( new Label( 0, lastRowNo +1, " ", getCellFormat1() ) );
                sheet0.addCell( new Label( 1, lastRowNo +1, " ", getCellFormat1() ) );
                sheet0.addCell( new Label( 2, lastRowNo +1, "TOTAL", getCellFormat1() ) );
                
                
                if ( sType.equalsIgnoreCase( "dataelement" ) )
                {
                    if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "PERIOD-FROM" ) || 
                        deCodeString.equalsIgnoreCase( "PERIOD-TO" ) || deCodeString.equalsIgnoreCase( "SLNo" ) ||
                        deCodeString.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) || deCodeString.equalsIgnoreCase( "ORGUNIT-GROUP" ) )
                    {
                        continue;
                    } 
                    else
                    {
                        sheet0.addCell( new Number( tempColNo, lastRowNo +1, orgUnitwiseColTotalMap.get( period.getId()+":"+deCodeString ), wCellformat ) );
                        count++;
                    }
                    
                    
                    /*
                    System.out.println( "-- DECode : " + deCodeString + "-- SL No " + slno++   + "-- Row No " + lastRowNo  + "-- Col No " + tempColNo  + " -- Value is  " + orgUnitwiseColTotalMap.get( period.getId()+":"+deCodeString ) );
                    System.out.println( "\n" );
                    
                    System.out.println( grandTotal.get( deCodeString ) );
                    */
                    
                }
                /*
                sheet0.addCell( new Number( tempColNo, slno++, orgUnitwiseColTotalMap.get( period.getId()+":"+deCodeString ), wCellformat ) );
                count++;
                */
                
            }
            
            tempPeriodInrc += dataElementCount;
            periodCount++;
        }   
        
        int count = 1;                      
        
        Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
        while (  reportDesignIterator.hasNext() )
        {
            Report_inDesign reportDesign =  reportDesignIterator.next();
            String deCodeString = reportDesign.getExpression();
            
            String sType = reportDesign.getStype();
            
            int tempCol = tempTotalColumn + count;
            
            WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
           
            if ( sType.equalsIgnoreCase( "dataelement" ) )
            {
                if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "PERIOD-FROM" ) || 
                    deCodeString.equalsIgnoreCase( "PERIOD-TO" ) || deCodeString.equalsIgnoreCase( "SLNo" ) ||
                    deCodeString.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) || deCodeString.equalsIgnoreCase( "ORGUNIT-GROUP" ) )
                {
                    continue;
                } 
                else
                {
                    sheet0.addCell( new Number( tempCol, lastRowNo +1, grandTotal.get( deCodeString ), wCellformat ) );
                    count++;
                }
                /*
                System.out.println( "-- DECode : " + deCodeString + "-- SL No " + slno   + "-- Row No " + lastRowNo  + "-- Col No " + tempCol  + " -- Value is  " + grandTotal.get( deCodeString ) );
                System.out.println( "\n" );
                
                System.out.println( grandTotal.get( deCodeString ) );
                */
            }
        }
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + selectedOrgUnit.getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( sDate ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        
        //System.out.println( selectedOrgUnit.getName()+ " : " + selReportObj.getName()+" : Report Generation End Time is : " + new Date() );
        
        //return SUCCESS;        
        
        
    }    
    
    public void generateReportWithOutGroupMember() throws Exception
    {
        
        String periodIdsByComma = "-1";
        
        int orgUnitStartCol = 0 ;
        int orgUnitStartRow = 0;
        int periodStartCol = 0;
        int periodStartRow = 0;
        int dataElementStartCol = 0;
        int dataElementStartRow = 0;
        
        // Getting DataValues
        List<Report_inDesign> reportDesignList = reportService.getReportDesign( deCodesXMLFileName );
        String dataElmentIdsByComma = reportService.getDataelementIds( reportDesignList );
        
        
        //System.out.println( " : Size of de-code is  : " + reportDesignList.size() );
        
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
        
        String[] dataElementName = new String[ reportDesignList.size() ];
        
        int dataElementCount = 0;
        Iterator<Report_inDesign> deCountIterator = reportDesignList.iterator();
        while ( deCountIterator.hasNext() )
        {
            Report_inDesign report_inDesign = (Report_inDesign) deCountIterator.next();
            
            String sType = report_inDesign.getStype();
            
            if ( sType.equalsIgnoreCase( "dataelementname" ) )
            {
                int rowNo = report_inDesign.getRowno();
                int colNo = report_inDesign.getColno();
                int sheetNo = report_inDesign.getSheetno();
                
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                
                String deName = sheet0.getCell( colNo, rowNo ).getContents();
                
                dataElementName[dataElementCount] = deName;
                
                //System.out.println( " : dataElement Name is  : " + dataElementName[dataElementCount] );
                
                dataElementCount ++;
            }
            
            if ( sType.equalsIgnoreCase( "progressiveperiod" ) )
            {
                periodStartCol = report_inDesign.getColno();
                periodStartRow = report_inDesign.getRowno();;
            }
        }
        
        //System.out.println( " : periodStartCol  : " + periodStartCol + " -- periodStartRow : "  + periodStartRow + " -- " + selReportObj.getExcelTemplateName() + " -- " + selReportObj.getXmlTemplateName() );
        int colCount = 1;
        
        
        int tempPeriodCount = 0;
        int tempColumnNo = 0;
        for( Period period : periodList )
        {
            WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
            
            sheet0.mergeCells( periodStartCol, periodStartRow, periodStartCol + dataElementCount-1 , periodStartRow );
            sheet0.addCell( new Label( periodStartCol, periodStartRow, simpleDateFormat.format( period.getStartDate()), getCellFormat1() ) );
            
            for( int i=0 ; i < dataElementCount ; i++ )
            {
                sheet0.addCell( new Label( periodStartCol+i, periodStartRow+1, dataElementName[i], getCellFormat1() ) );
            }
            
            periodStartCol +=  dataElementCount;
            tempPeriodCount++;
            tempColumnNo = periodStartCol;
        }
        
        //System.out.println( " : tempColumnNo  : " + tempColumnNo + " : tempColumnNo  : " + periodStartCol );
        
        WritableSheet tempSheet0 = outputReportWorkbook.getSheet( sheetNo );
        
        tempSheet0.mergeCells( periodStartCol, periodStartRow, periodStartCol + dataElementCount-1 , periodStartRow );
        tempSheet0.addCell( new Label( periodStartCol, periodStartRow, "Total", getCellFormat1() ) );
        
        for( int i=0 ; i < dataElementCount ; i++ )
        {
            tempSheet0.addCell( new Label( periodStartCol+i, periodStartRow+1, dataElementName[i], getCellFormat1() ) );
        }
        
        //System.out.println( " : tempColumnNo  : " + tempColumnNo + " : tempColumnNo  : " + periodStartCol );
        
        colCount = tempColumnNo;
        
        int lastRowNo = 0;
        
        Map<OrganisationUnit, Map<String,Double>> orgUnitwiseRowTotalMap = new HashMap<OrganisationUnit, Map<String,Double>>();
        Map<String, Double> orgUnitwiseColTotalMap = new HashMap<String,Double>();
        int orgUnitCount = 0;
        int orgUnitGroupMemberCount = 0;
        int slno = 1;
        
        Iterator<OrganisationUnit> orgUnit = orgUnitList.iterator();
        while ( orgUnit.hasNext() )
        {
            OrganisationUnit currentOrgUnit = (OrganisationUnit) orgUnit.next();
                
                    
                    Map<String,Double> oneRowTotal = new HashMap<String, Double>();
                    int periodCount = 0;
                    int tempPeriodInrc = 0;
                    
                    for( Period period : periodList )
                    {
                        if( periodTypeId.equalsIgnoreCase( "daily" ) )
                        {
                            periodIdsByComma = ""+period.getId();
                        }
                        else
                        {
                            Collection<Integer> periodIds = new ArrayList<Integer>( getIdentifiers(Period.class, periodService.getIntersectingPeriods( period.getStartDate(), period.getEndDate() ) ) );
                            periodIdsByComma = getCommaDelimitedString( periodIds );
                        }
                        
                        
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
                        
                        int count = 0;
                        Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
                        while (  reportDesignIterator.hasNext() )
                        {
                            Report_inDesign reportDesign =  reportDesignIterator.next();
                            
                            String deCodeString = reportDesign.getExpression();

                            String sType = reportDesign.getStype();
                            String tempStr = "";

                            //tempRowNo = reportDesign.getRowno();
                            //tempColNo = reportDesign.getColno();
                            //sheetNo = reportDesign.getSheetno();
                            
                            //WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                            
                            if( deCodeString.equalsIgnoreCase( "FACILITY" ) )
                            {
                                tempStr = selectedOrgUnit.getName();
                            } 
                            
                            else if ( deCodeString.equalsIgnoreCase( "PERIOD-FROM" ) )
                            {
                                tempStr = simpleDateFormat.format( selectedPeriod.getStartDate() );
                            }
                            
                            else if ( deCodeString.equalsIgnoreCase( "PERIOD-TO" ) )
                            {
                                tempStr = simpleDateFormat.format( selectedEndPeriod.getEndDate() );
                            }
                            else if( deCodeString.equalsIgnoreCase( "PROGRESSIVE-PERIOD" ) )
                            {
                                tempStr = simpleDateFormat.format( period.getStartDate() );
                            } 
                            
                            else if( deCodeString.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
                            {
                                tempStr = currentOrgUnit.getName();
                            }
                            
                            else if( deCodeString.equalsIgnoreCase( "SLNo" ) )
                            {
                                tempStr = "" + slno;
                            } 
                            else
                            {
                                if( sType.equalsIgnoreCase( "dataelement" ) )
                                {
                                    if( aggData.equalsIgnoreCase( USECAPTUREDDATA ) ) 
                                    {
                                        tempStr = getAggVal( deCodeString, aggDeMap );
                                    } 
                                    else if( aggData.equalsIgnoreCase( GENERATEAGGDATA ) )
                                    {
                                        tempStr = getAggVal( deCodeString, aggDeMap );
                                    }
                                    else if( aggData.equalsIgnoreCase( USEEXISTINGAGGDATA ) )
                                    {
                                        tempStr = getAggVal( deCodeString, aggDeMap );
                                    }
                                    
                                    Double tempD = oneRowTotal.get( deCodeString );
                                    if( tempD == null )
                                    {
                                        try
                                        {
                                             Double tempD1 = Double.parseDouble( tempStr );
                                             oneRowTotal.put( deCodeString, tempD1 );
                                        }
                                        catch( Exception e )
                                        {
                                            oneRowTotal.put( deCodeString, 0.0 );
                                        }
                                    }
                                    else
                                    {
                                        try
                                        {
                                             Double tempD1 = Double.parseDouble( tempStr ) + tempD;
                                             oneRowTotal.put( deCodeString, tempD1 );
                                        }
                                        catch( Exception e )
                                        {
                                            //oneRowTotal.put( deCodeString, 0.0 );
                                        }
                                    }
                                    
                                    
                                    Double tempD2 = orgUnitwiseColTotalMap.get( period.getId()+":"+deCodeString );
                                    if( tempD2 == null )
                                    {
                                        try
                                        {
                                             Double tempD3 = Double.parseDouble( tempStr );
                                             orgUnitwiseColTotalMap.put( period.getId()+":"+deCodeString , tempD3 );
                                        }
                                        catch( Exception e )
                                        {
                                            orgUnitwiseColTotalMap.put( period.getId()+":"+deCodeString , 0.0 );
                                        }
                                    }
                                    else
                                    {
                                        try
                                        {
                                             Double tempD3 = Double.parseDouble( tempStr ) + tempD2;
                                             orgUnitwiseColTotalMap.put( period.getId()+":"+deCodeString , tempD3 );
                                        }
                                        catch( Exception e )
                                        {
                                            //oneRowTotal.put( deCodeString, 0.0 );
                                        }
                                    }
                                }
                                
                                
                            }
                            
                            int tempRowNo = reportDesign.getRowno();
                            int tempColNo = reportDesign.getColno();
                            //tempColNo = tempColNo + tempPeriodInrc;
                            int sheetNo = reportDesign.getSheetno();
                            WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                            
                            
                            
                            if ( sType.equalsIgnoreCase( "dataelement" ) )
                            {
                                if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "DE" ) || deCodeString.equalsIgnoreCase( "PERIOD-FROM" ) || deCodeString.equalsIgnoreCase( "PERIOD-TO" ) )
                                {
                                    //tempRowNo = reportDesign.getRowno();
                                    //tempColNo = reportDesign.getColno();
                                }
                                else
                                {
                                    if( deCodeString.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) || deCodeString.equalsIgnoreCase( "SLNo" ) )
                                    {
                                        tempRowNo += orgUnitCount;
                                        //tempColNo = reportDesign.getColno();
                                        lastRowNo = tempRowNo;
                                    }
                                   
                                    else
                                    {
                                        tempRowNo += orgUnitCount;
                                        tempColNo = tempColNo + tempPeriodInrc;
                                        lastRowNo = tempRowNo;
                                        //tempColNo += dataElementCount;
                                        //tempRowNo = reportDesign.getRowno();
                                    }
                                    
                                    //tempRowNo += orgUnitCount;
                                    //tempColNo += dataElementCount;
                                }
                                try
                                {
                                    /*
                                    if( orgUnitCount == orgUnitList.size()-1 && selectedOrgUnitLevel != 1 )
                                    {
                                        
                                        if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "DE" ) || deCodeString.equalsIgnoreCase( "PERIOD-FROM" ) || deCodeString.equalsIgnoreCase( "PERIOD-TO" ) )
                                        {
                                            continue;
                                        }
                                        else
                                        {
                                            sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), getCellFormat1() ) );
                                        }
                                    }
                                    */
                                    //else
                                    //{
                                        sheet0.addCell( new Number( tempColNo, tempRowNo, Double.parseDouble( tempStr ), wCellformat ) );
                                    //}
                                }
                               
                                catch( Exception e )
                                {
                                    /*
                                    if( orgUnitCount == orgUnitList.size()-1 && selectedOrgUnitLevel != 1 )
                                    {
                                        if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "DE" ) || deCodeString.equalsIgnoreCase( "PERIOD-FROM" ) || deCodeString.equalsIgnoreCase( "PERIOD-TO" ) )
                                        {
                                            continue;
                                        }
                                        else
                                        {
                                            sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, getCellFormat1() ) );
                                        }
                                        
                                    }
                                    */
                                    //else
                                   // {
                                        sheet0.addCell( new Label( tempColNo, tempRowNo, tempStr, wCellformat ) );
                                    //}
                                }
                                                                                                
                                //System.out.println( "--Sl.NO --"+ slno +" -- DECode : " + deCodeString + "   TempStr : " + tempStr + " -- Row No " + tempRowNo  + " -- Col No " + tempColNo  );
                            }
                            
                            count++;
                        }
                        
                        periodStartCol++;
                        periodCount++;
                        tempPeriodInrc += dataElementCount;
                    }
                    
                    orgUnitwiseRowTotalMap.put( currentOrgUnit, oneRowTotal );
                    
                    slno++;
                    //orgUnitGroupMemberCount++;
                
           
            //slno++;
            orgUnitCount++;
        }
        
        
        //System.out.println( "--Size of org UnitwiseRowTotalMap " + orgUnitwiseRowTotalMap.size() );
        
        /*
        for ( int i = 0 ; i < orgUnitwiseRowTotalMap.size() ; i++ )
        {
            System.out.println( "--Org Unit wis eRow Total Map is : " + orgUnitwiseRowTotalMap.keySet() );
        }
        */
        int currentOrgUnitCount = 0;
        orgUnit = orgUnitList.iterator();
        while ( orgUnit.hasNext() )
        {
            OrganisationUnit currentOrgUnit = (OrganisationUnit) orgUnit.next();
            Map<String,Double> oneRowTotal = new HashMap<String, Double>( orgUnitwiseRowTotalMap.get( currentOrgUnit ) );
            int count = 0;
            
            Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
            while (  reportDesignIterator.hasNext() )
            {
                Report_inDesign reportDesign =  reportDesignIterator.next();
                String deCodeString = reportDesign.getExpression();
                String sType = reportDesign.getStype();
                int tempRowNo = reportDesign.getRowno() + currentOrgUnitCount;
                
                int tempColNo = reportDesign.getColno();                            
                
                //int tempColNo = reportDesign.getColno() + colCount + count; 
                
                int sheetNo = reportDesign.getSheetno();
                
                //System.out.println( " -- DECode : " + deCodeString + "    -- Row No " + tempRowNo  + " -- Col No " + colCount++ + " -- Value is  " + oneRowTotal.get( deCodeString ) );
                
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                if ( sType.equalsIgnoreCase( "dataelement" ) )
                {
                    //System.out.println(  " -- S Type : " + sType + " -- DECode : " + deCodeString + "    -- Row No " + tempRowNo  + " -- Col No " + colCount++  );
                    
                    if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "PERIOD-FROM" ) || 
                        deCodeString.equalsIgnoreCase( "PERIOD-TO" ) || deCodeString.equalsIgnoreCase( "SLNo" ) ||
                        deCodeString.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
                    {
                        continue;
                    } 
                    else
                    {
                        sheet0.addCell( new Number( colCount + count, tempRowNo, oneRowTotal.get( deCodeString ), wCellformat ) );
                        count++;
                        
                    }
                    //System.out.println( " --DECode : " + deCodeString + " --colCount " + colCount + "--Row No " + tempRowNo  + "-- Col No " + tempColNo + " -- Value is  " + oneRowTotal.get( deCodeString ) );
                }
                
                
            }
            currentOrgUnitCount++;
            orgUnitCount++;
        }
        
        
        Map<String,Double> grandTotal = new HashMap<String, Double>();
        int periodCount = 0;
        
        int tempTotalColumn = 0;
        
        int tempPeriodInrc = 0;
        for( Period period : periodList )
        {
            int count = 0;                      
            
            Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
            while (  reportDesignIterator.hasNext() )
            {
                Report_inDesign reportDesign =  reportDesignIterator.next();
                String deCodeString = reportDesign.getExpression();
                
                String sType = reportDesign.getStype();
                
                int tempRowNo = reportDesign.getRowno() + orgUnitGroupMemberCount;
                //int tempColNo = reportDesign.getColno()+ periodCount + count;
                int tempColNo = reportDesign.getColno() + tempPeriodInrc; 
                int sheetNo = reportDesign.getSheetno();
                
                tempTotalColumn = tempColNo;
                
                Double tempGrandTotal = grandTotal.get( deCodeString );
                if( tempGrandTotal == null )
                {
                    try
                    {
                         Double grandTotal1 = orgUnitwiseColTotalMap.get( period.getId()+":"+deCodeString );
                         grandTotal.put( deCodeString , grandTotal1 );
                    }
                    catch( Exception e )
                    {
                        grandTotal.put( deCodeString , 0.0 );
                    }
                }
                else
                {
                    try
                    {
                         Double grandTotal12 = orgUnitwiseColTotalMap.get( period.getId()+":"+deCodeString ) + tempGrandTotal;
                         grandTotal.put( deCodeString , grandTotal12 );
                    }
                    catch( Exception e )
                    {
                        //oneRowTotal.put( deCodeString, 0.0 );
                    }
                }
                
                WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
                
                //int lastRowNo = tempRowNo + 1;
                
                //System.out.println( " -- DECode : " + deCodeString + "    -- Row No " + tempRowNo  + " -- Col No " + colCount++  );
                
                //sheet0.mergeCells( 0, lastRowNo +1, 2 , lastRowNo +1 );
                //sheet0.addCell( new Label( 0, lastRowNo +1, "Total", getCellFormat1() ) );
                
                sheet0.addCell( new Label( 0, lastRowNo +1, " ", getCellFormat1() ) );
                //sheet0.addCell( new Label( 1, lastRowNo +1, " ", getCellFormat1() ) );
                sheet0.addCell( new Label( 1, lastRowNo +1, "TOTAL", getCellFormat1() ) );
                
                
                if ( sType.equalsIgnoreCase( "dataelement" ) )
                {
                    if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "PERIOD-FROM" ) || 
                        deCodeString.equalsIgnoreCase( "PERIOD-TO" ) || deCodeString.equalsIgnoreCase( "SLNo" ) ||
                        deCodeString.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
                    {
                        continue;
                    } 
                    else
                    {
                        sheet0.addCell( new Number( tempColNo, lastRowNo +1, orgUnitwiseColTotalMap.get( period.getId()+":"+deCodeString ), wCellformat ) );
                        count++;
                    }
                    
                    
                    /*
                    System.out.println( "-- DECode : " + deCodeString + "-- SL No " + slno++   + "-- Row No " + lastRowNo  + "-- Col No " + tempColNo  + " -- Value is  " + orgUnitwiseColTotalMap.get( period.getId()+":"+deCodeString ) );
                    System.out.println( "\n" );
                    
                    System.out.println( grandTotal.get( deCodeString ) );
                    */
                    
                }
                /*
                sheet0.addCell( new Number( tempColNo, slno++, orgUnitwiseColTotalMap.get( period.getId()+":"+deCodeString ), wCellformat ) );
                count++;
                */
                
            }
            
            tempPeriodInrc += dataElementCount;
            periodCount++;
        }   
        
        int count = 1;                      
        
        Iterator<Report_inDesign> reportDesignIterator = reportDesignList.iterator();
        while (  reportDesignIterator.hasNext() )
        {
            Report_inDesign reportDesign =  reportDesignIterator.next();
            String deCodeString = reportDesign.getExpression();
            
            String sType = reportDesign.getStype();
            
            int tempCol = tempTotalColumn + count;
            
            WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
           
            if ( sType.equalsIgnoreCase( "dataelement" ) )
            {
                if( deCodeString.equalsIgnoreCase( "FACILITY" ) || deCodeString.equalsIgnoreCase( "PERIOD-FROM" ) || 
                    deCodeString.equalsIgnoreCase( "PERIOD-TO" ) || deCodeString.equalsIgnoreCase( "SLNo" ) ||
                    deCodeString.equalsIgnoreCase( "PROGRESSIVE-ORGUNIT" ) )
                {
                    continue;
                } 
                else
                {
                    sheet0.addCell( new Number( tempCol, lastRowNo +1, grandTotal.get( deCodeString ), wCellformat ) );
                    count++;
                }
                /*
                System.out.println( "-- DECode : " + deCodeString + "-- SL No " + slno   + "-- Row No " + lastRowNo  + "-- Col No " + tempCol  + " -- Value is  " + grandTotal.get( deCodeString ) );
                System.out.println( "\n" );
                
                System.out.println( grandTotal.get( deCodeString ) );
                */
            }
        }
        
        outputReportWorkbook.write();
        outputReportWorkbook.close();

        fileName = reportFileNameTB.replace( ".xls", "" );
        fileName += "_" + selectedOrgUnit.getShortName() + "_";
        fileName += "_" + simpleDateFormat.format( sDate ) + ".xls";
        File outputReportFile = new File( outputReportPath );
        inputStream = new BufferedInputStream( new FileInputStream( outputReportFile ) );
        
        //System.out.println( selectedOrgUnit.getName()+ " : " + selReportObj.getName()+" : Report Generation End Time is : " + new Date() );
        
        //return SUCCESS;        
                
            
    }

    
    // supportive methods
    
    
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

                //System.out.println( replaceString + " : " + aggDeMap.get( replaceString ) );
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
    
}
