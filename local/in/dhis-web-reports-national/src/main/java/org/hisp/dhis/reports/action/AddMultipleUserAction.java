package org.hisp.dhis.reports.action;

import com.opensymphony.xwork2.Action;
import jxl.Sheet;
import jxl.Workbook;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.reports.ReportService;
import org.hisp.dhis.security.PasswordManager;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserAuthorityGroup;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.user.UserService;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddMultipleUserAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private ReportService reportService;

    public void setReportService( ReportService reportService )
    {
        this.reportService = reportService;
    }
    /*
    private UserStore userStore;

    public void setUserStore( UserStore userStore )
    {
        this.userStore = userStore;
    }
    */
    private PasswordManager passwordManager;

    public void setPasswordManager( PasswordManager passwordManager )
    {
        this.passwordManager = passwordManager;
    }
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private UserService userService;
    
    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    // -------------------------------------------------------------------------
    // Input/Output
    // -------------------------------------------------------------------------

    private String message;

    public String getMessage()
    {
        return message;
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

    private List<String> importStatusMsgList = new ArrayList<String>();

    public List<String> getImportStatusMsgList()
    {
        return importStatusMsgList;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------
    public String execute()
        throws Exception
    {
        message = "";
        importStatusMsgList = new ArrayList<String>();
        /*
        String raFolderName = reportService.getRAFolderName();
        
        String fileName = "user.xls";
        String excelImportFolderName = "excelimport";
        String excelFilePath = System.getenv( "DHIS2_HOME" ) + File.separator + raFolderName + File.separator + excelImportFolderName + File.separator  + fileName;
        
        Workbook templateWorkbook = Workbook.getWorkbook( new File( excelFilePath ) );
        WritableWorkbook outputReportWorkbook = Workbook.createWorkbook( new File( excelFilePath ), templateWorkbook );
        */

        //Workbook  workbook = Workbook.getWorkbook( new File( fileName ) );


        //Sheet sheet = excelImportFile.getSheet( sheetNo );

        System.out.println( "File name : " + fileName  );
        String fileType = fileName.substring( fileName.indexOf( '.' ) + 1, fileName.length() );

        if ( !fileType.equalsIgnoreCase( "xls" ) )
        {
            message = "The file you are trying to import is not an excel file";

            return SUCCESS;
        }

        //WritableSheet sheet0 = outputReportWorkbook.getSheet( sheetNo );
        Workbook excelImportFile = Workbook.getWorkbook( file );
        int sheetNo = 0 ;
        Sheet sheet0 = excelImportFile.getSheet( sheetNo );
        Integer rowStart = Integer.parseInt( sheet0.getCell( 6, 0 ).getContents() );
        Integer rowEnd = Integer.parseInt( sheet0.getCell( 6, 1 ).getContents() );
        System.out.println( "User  Creation Start Time is : " + new Date() );
        System.out.println( "Row Start : " + rowStart + " ,Row End : "  + rowEnd );
        
        int orgunitcount = 0;
        for( int i = rowStart ; i <= rowEnd ; i++ )
        {
            Integer orgUnitId = Integer.parseInt( sheet0.getCell( 0, i ).getContents() );
            //String orgUnitname = sheet0.getCell( 1, i ).getContents();
            //String orgUnitCode = sheet0.getCell( 2, i ).getContents();
            String userName = sheet0.getCell( 1, i ).getContents();
            String passWord = sheet0.getCell( 2, i ).getContents();
            Integer userRoleId = Integer.parseInt( sheet0.getCell( 3, i ).getContents() );
            
            OrganisationUnit orgUId = organisationUnitService.getOrganisationUnit( orgUnitId );
            Set<OrganisationUnit> orgUnits = new HashSet<OrganisationUnit>();
            orgUnits.add( orgUId );
            
            Collection<User> tempUserList = orgUId.getUsers();
            int flag = 0;
            if ( tempUserList != null )
            {
                for ( User u : tempUserList )
                {
                    //UserCredentials uc = userStore.getUserCredentials( u );
                    UserCredentials uc = userService.getUserCredentials( u );
                    if ( uc != null && uc.getUsername().equalsIgnoreCase( userName ) )
                    {
                        System.out.println( uc.getUsername() + " ALREADY EXITS -- " + userName );
                        importStatusMsgList.add( "User Name -- " + userName + " ALREADY EXITS" );
                        flag = 1;
                    }
                }
            }
            
            if ( flag == 1 )
            {
                //System.out.println( userName + " ALREADY EXITS" );
                //message += "<font color=red><strong>"+ userName +  " ALREADY EXITS .<br></font></strong>";
                //importStatusMsgList.add( userName + " ALREADY EXITS" );
                continue;
            }
            
            User user = new User();
            user.setSurname( userName );
            //user.setFirstName( orgUnitCode );
            user.setFirstName( userName );
            user.setOrganisationUnits( orgUnits );
            user.setDataViewOrganisationUnits( orgUnits );


            UserCredentials userCredentials = new UserCredentials();
            userCredentials.setUser( user );
            userCredentials.setUsername( userName );
            //userCredentials.setPassword( passwordManager.encodePassword( userName, passWord ) );
            userCredentials.setPassword( passwordManager.encode( passWord ) );
            
            UserAuthorityGroup group = userService.getUserAuthorityGroup( userRoleId );
            
            //UserAuthorityGroup group = userStore.getUserAuthorityGroup( userRoleId );
            userCredentials.getUserAuthorityGroups().add( group );

            //userStore.addUser( user );
            //userStore.addUserCredentials( userCredentials );
            
            userService.addUser( user );
            userService.addUserCredentials( userCredentials );
            System.out.println( userName + " Created" );
            orgunitcount++;

            importStatusMsgList.add( "User Name -- " + userName + " Created" );
            /*
            if( flag != 1 )
            {
                //message += "<font color=red><strong>"+ userName +  " Created .<br></font></strong>";

                //importStatusMsgList.add( userName + " Created" );
            }
            */
        }

        excelImportFile.close();
        
        System.out.println( "**********************************************" );
        System.out.println( "MULTIPLE USER CREATION IS FINISHED" );
        System.out.println( "Total No of User Created : -- " + orgunitcount );
        System.out.println( "**********************************************" );
        System.out.println( "User  Creation End Time is : " + new Date() );

        //message += "<font color=red><strong>" + orgunitcount +  " : User Created .<br></font></strong>";

        importStatusMsgList.add( "Total No of User Created -- " + orgunitcount );

        return SUCCESS;
    }
}
