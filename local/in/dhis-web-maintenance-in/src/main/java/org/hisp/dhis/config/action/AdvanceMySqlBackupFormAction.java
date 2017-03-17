package org.hisp.dhis.config.action;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.system.database.DatabaseInfo;
import org.hisp.dhis.system.database.DatabaseInfoProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

public class AdvanceMySqlBackupFormAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    private DatabaseInfoProvider databaseInfoProvider;

    public void setDatabaseInfoProvider( DatabaseInfoProvider databaseInfoProvider )
    {
        this.databaseInfoProvider = databaseInfoProvider;
    }
    
    // -------------------------------------------------------------------------
    // Input and Output Parameters
    // -------------------------------------------------------------------------
    
    private List<String> availableTables = new ArrayList<String>();
    
    public List<String> getAvailableTables()
    {
        return availableTables;
    }
    
    private List<String> availableViews = new ArrayList<String>();
    
    public List<String> getAvailableViews()
    {
        return availableViews;
    }
    
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {        
       
        //String query =  "SHOW TABLES";
        
        /*
        String query =  "SHOW FULL TABLES";
        
        SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
        
        while ( rs.next() )
        {
           // availableTables.add( rs.getString( 1 ) );
            
            String tableName =  rs.getString( 1 );
            String tableType =  rs.getString( 2 );
            
            if( tableType.equalsIgnoreCase( "VIEW" ) )
            {
                availableViews.add( tableName );
            }
            else
            {
                availableTables.add( tableName );
            }
        }
        */
        
        DatabaseInfo dataBaseInfo = databaseInfoProvider.getDatabaseInfo();
        
        try
        {
            String query = "";
            
            if ( dataBaseInfo.getType().equalsIgnoreCase( "postgresql" ) )
            {
                query =  "SELECT table_name, table_type FROM information_schema.tables WHERE table_schema ='public' AND table_type='BASE TABLE' order by table_name ";
            }
            else if ( dataBaseInfo.getType().equalsIgnoreCase( "mysql" ) )
            {
               query =  "SHOW FULL TABLES";
            }
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            
            while ( rs.next() )
            {
               // availableTables.add( rs.getString( 1 ) );
                
                String tableName =  rs.getString( 1 );
                String tableType =  rs.getString( 2 );
                
                if( tableType.equalsIgnoreCase( "VIEW" ) )
                {
                    availableViews.add( tableName );
                }
                else
                {
                    availableTables.add( tableName );
                }
            }     
            
        }
        
        catch ( Exception e )
        {
            throw new RuntimeException( "Illegal Query for Show all tables", e );
        }
        
        /*
        
        if ( dataBaseInfo.getType().equalsIgnoreCase( "postgresql" ) )
        {
            //SELECT table_name FROM information_schema.tables WHERE table_schema ='public' AND table_type='BASE TABLE';
        }
        
        else if ( dataBaseInfo.getType().equalsIgnoreCase( "mysql" ) )
        {
            String query =  "SHOW FULL TABLES";
            
            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );
            
            while ( rs.next() )
            {
               // availableTables.add( rs.getString( 1 ) );
                
                String tableName =  rs.getString( 1 );
                String tableType =  rs.getString( 2 );
                
                if( tableType.equalsIgnoreCase( "VIEW" ) )
                {
                    availableViews.add( tableName );
                }
                else
                {
                    availableTables.add( tableName );
                }
            }
            
        }
        
        */
        
        //System.out.println(" Total No of Tables is :" + availableTables.size() );
        //System.out.println(" Total No of View is :" + availableViews.size() );
        /*
        int i =1;
        for( String table : availableTables )
        {
            System.out.println(" Table " + i + " is : " + table );
            i++;
        }
        */
        return SUCCESS;
    }
}
