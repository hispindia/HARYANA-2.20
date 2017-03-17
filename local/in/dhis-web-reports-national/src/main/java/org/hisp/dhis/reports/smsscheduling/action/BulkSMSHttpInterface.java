package org.hisp.dhis.reports.smsscheduling.action;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.hisp.dhis.constant.Constant;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserGroup;
import org.hisp.dhis.user.UserGroupService;


/**
 * @author Mithilesh Kumar Thakur
 */
public class BulkSMSHttpInterface
{
    public static final String SMS_USER_GROUP_ID = "SMS_USER_GROUP_ID";//
    
    private String username, password, phoneNo, senderName;

    private URL url;

    private String url_string, data, response = "";

    Properties properties;
    
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private OrganisationUnitService organisationUnitService;

    public void setOrganisationUnitService( OrganisationUnitService organisationUnitService )
    {
        this.organisationUnitService = organisationUnitService;
    }
    
    private ConstantService constantService;

    public void setConstantService( ConstantService constantService )
    {
        this.constantService = constantService;
    }
    
    private UserGroupService userGroupService;

    public void setUserGroupService( UserGroupService userGroupService )
    {
        this.userGroupService = userGroupService;
    }
    
    
    /*****this constructor takes the username , password and sendername from a file a file**********/
    public BulkSMSHttpInterface() throws FileNotFoundException, IOException
    {
        properties = new Properties();

        properties.load( new FileReader( System.getenv( "DHIS2_HOME" ) + File.separator + "hibernate.properties" ) );
        username = getUsername();
        password = getPassword();
        senderName = getSenderName();
    }
    
    
    public BulkSMSHttpInterface( String username, String password, String senderName )
    {
        this.username = username;
        this.password = password;
        this.senderName = senderName;
    }    
    
    // getter
    public String getUsername()
    {
        return properties.getProperty( "bsms.username" );
    }

    public String getPassword()
    {
        return properties.getProperty( "bsms.password" );
    }

    public String getSenderName()
    {
        return properties.getProperty( "bsms.sender" );
    }
    
    // sending message to single mobile no
    public String sendMessage( String message, String phoneNo ) throws MalformedURLException, IOException
    {
        if (message==null || phoneNo==null)
        {
            return "either message or phone no null";
        }
        
        else if (message.equalsIgnoreCase( "") || phoneNo.equalsIgnoreCase( "") )
        {
            return "either message or phone no empty";
        }
        //Populating the data according to the api link
        //data = "username=" + username + "&password=" + password + "&sendername=" + senderName + "&mobileno=" + phoneNo + "&message=" + message;
        
        data = "username=" + username + "&password=" + password +  "&to=" + phoneNo + "&from=" + senderName + "&text=" + message;
        
        //http://myvaluefirst.com/smpp/sendsms?username=nrhmhttp&password=nrhm1234&to=9643208387&from=NRHMHR&text=hi

        //this link is used for sending sms(there are different links for different functions.refer to the api for more details)
        //url_string = "http://bulksms.mysmsmantra.com:8080/WebSMS/SMSAPI.jsp?";
        
        //url_string = "https://myvaluefirst.com/smpp/sendsms?";// for Instance SMS
        url_string = "http://myvaluefirst.com/smpp/sendsms?";// for Schedule SMS
        
        //System.out.println(" Mobile No -------------------->"+ phoneNo );
        
        //System.out.println(" URL -------------------->"+ url_string );
        
        //System.out.println(" Data -------------------->"+ data );
        
        //System.out.println(" Data -------------------->"+ message );
        
        url = new URL( url_string );
        URLConnection conn = url.openConnection();
        conn.setDoOutput( true );
        
        //sending data:
        OutputStreamWriter out = new OutputStreamWriter( conn.getOutputStream() );
        out.write( data );
        out.flush();

        //recieving response:
        InputStreamReader in = new InputStreamReader( conn.getInputStream() );
        BufferedReader buff_in = new BufferedReader( in );
        while ( buff_in.ready() )
        {
            response += buff_in.readLine() + "   ";
            //System.out.println( response + " " + data );
        }

        buff_in.close();
        out.close();

        return response;
    }
    
    // sending message to multiple mobile no
    public String sendMessages( String message, List<String> phonenos ) throws MalformedURLException, IOException
    {

        Iterator<String> it = phonenos.iterator();

        while ( it.hasNext() )
        {
            if ( phoneNo == null )
            {
                phoneNo = (String) it.next();
            } 
            
            else
            {
                phoneNo += "," + it.next();
            }
        }
        
        //System.out.println(" Mobile No -------------------->"+ phoneNo );

        data = "username=" + username + "&password=" + password + "&sendername=" + senderName + "&mobileno=" + phoneNo + "&message=" + message;

        //for sending multiple sms (same as single sms)
        //url_string = "http://bulksms.mysmsmantra.com:8080/WebSMS/SMSAPI.jsp?";
        
        url_string = "http://myvaluefirst.com/smpp/sendsms?";
        
        url = new URL( url_string );
        URLConnection conn = url.openConnection();
        conn.setDoOutput( true );
        
        //System.out.println(" URL -------------------->"+ url_string );
        
        //System.out.println(" Data -------------------->"+ data );
        
        OutputStreamWriter out = new OutputStreamWriter( conn.getOutputStream() );
        out.write( data );
        out.flush();

        InputStreamReader in = new InputStreamReader( conn.getInputStream() );
        BufferedReader buff_in = new BufferedReader( in );

        while ( buff_in.ready() )
        {
            response += buff_in.readLine() + "   ";
            //System.out.println( response + " " + data );

        }

        buff_in.close();
        out.close();

        return response;

    }   
    
    // get phoneNo of users
    public List<String> getUsersMobileNumber( Integer organisationUnitId )
    {
        List<String> mobileNumbers = new ArrayList<String>();
        
        System.out.println(" OrgUnit Id in SMS Service " + organisationUnitId );
        
        System.out.println(" Organisation Unit Service " + organisationUnitService );
        
        List<OrganisationUnit> orgUnitList = new ArrayList<OrganisationUnit>( organisationUnitService.getOrganisationUnitBranch( organisationUnitId ) );
        
        List<User> orgUnitUserList = new ArrayList<User>();
        for( OrganisationUnit orgUnit : orgUnitList )
        {
            if( orgUnit.getUsers() != null && orgUnit.getUsers().size() > 0 )
            {
                orgUnitUserList.addAll( orgUnit.getUsers() );
            }
        }
        
        // SMS user Details
        Constant smsUserGroupConstant = constantService.getConstantByName( SMS_USER_GROUP_ID );
        
        UserGroup userGroup = userGroupService.getUserGroup( (int) smsUserGroupConstant.getValue() );
        List<User> smsUsers = new ArrayList<User>( userGroup.getMembers() );
        
        smsUsers.retainAll( orgUnitUserList );
        
        try
        {
            for( User user : smsUsers )
            {
                if( user.getPhoneNumber() != null && user.getPhoneNumber().equalsIgnoreCase( "" ) )
                {
                    mobileNumbers.add( user.getPhoneNumber()  );
                }
            }
            
            System.out.println("-------------------- > " + mobileNumbers );
            
            return mobileNumbers;
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal OrganisationUnit id", e );
        }
        
    }        
    
}

