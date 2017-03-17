package org.hisp.dhis.de.action;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hisp.dhis.dataset.DataSet;
import org.hisp.dhis.dataset.DataSetService;
import org.hisp.dhis.dataset.LockException;
import org.hisp.dhis.organisationunit.OrganisationUnit;
import org.hisp.dhis.organisationunit.OrganisationUnitService;
import org.hisp.dhis.period.Period;
import org.hisp.dhis.period.PeriodService;
import org.hisp.dhis.period.PeriodType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class DeleteLockException implements Action
{
    private static final Log log = LogFactory.getLog( DeleteLockException.class );

    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    @Autowired
    private DataSetService dataSetService;
    
    @Autowired
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

    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate( JdbcTemplate jdbcTemplate )
    {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    
    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    /*
    private String periodId;

    public void setPeriodId( String periodId )
    {
        this.periodId = periodId;
    }

    private Integer dataSetId;

    public void setDataSetId( Integer dataSetId )
    {
        this.dataSetId = dataSetId;
    }

    private Integer organisationUnitId;

    public void setOrganisationUnitId( Integer organisationUnitId )
    {
        this.organisationUnitId = organisationUnitId;
    }
    */
    
    
    private String ds;
    
    public void setDs( String ds )
    {
        this.ds = ds;
    }

    private String ou;
    
    public void setOu( String ou )
    {
        this.ou = ou;
    }
    
    private String pe;
    
    public void setPe( String pe )
    {
        this.pe = pe;
    }

    private boolean multiOu;
    
    public void setMultiOu( boolean multiOu )
    {
        this.multiOu = multiOu;
    }

    private int statusCode;

    public int getStatusCode()
    {
        return statusCode;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    public String execute()
    {
        //System.out.println ( " In side Delete Lock Exception " );
        //System.out.println ( ds + " -- " + pe + "---" + ou + " -- " + multiOu  );
        
        DataSet dataSet = dataSetService.getDataSet( ds );
        Period period = PeriodType.getPeriodFromIsoString( pe );
        OrganisationUnit organisationUnit = organisationUnitService.getOrganisationUnit( ou );
        Set<OrganisationUnit> children = organisationUnit.getChildren();
        
        
        // ---------------------------------------------------------------------
        // Check locked status
        // ---------------------------------------------------------------------
        
        /*
        if ( !multiOu )
        {
            if ( dataSetService.isLocked( dataSet, period, organisationUnit, null, null ) );
            {
                return logError( "Entry locked for combination: " + dataSet + ", " + period + ", " + organisationUnit, 2 );
            }
        }
        else
        {
            for ( OrganisationUnit ou : children )
            {
                if ( ou.getDataSets().contains( dataSet ) && dataSetService.isLocked( dataSet, period, ou, null, null ) )
                {
                    return logError( "Entry locked for combination: " + dataSet + ", " + period + ", " + ou, 2 );
                }
            }
        }
        */
        
        
        // ---------------------------------------------------------------------
        // Delete Lock Exception
        // ---------------------------------------------------------------------

        if ( !multiOu )
        {
            deleteLockException( dataSet, period, organisationUnit );
        }
        else
        {
            for ( OrganisationUnit ou : children )
            {
                if ( ou.getDataSets().contains( dataSet ) )
                {
                    deleteLockException( dataSet, period, organisationUnit );
                }
            }
        }
        
 
        return SUCCESS;
    }

    // -------------------------------------------------------------------------
    // Supportive methods
    // -------------------------------------------------------------------------

    private void deleteLockException( DataSet dataSet, Period period, OrganisationUnit organisationUnit )
    {
        // Delete Lock Exception if it Exist
        Integer lockExceptionId = null;
        
        if( period ==  null || period.getId() == 0 )
        {
            period = periodService.reloadPeriod( period );
        }
        
        lockExceptionId = getLockExceptionByOrgUnitPeriodDataSet( organisationUnit.getId(), period.getId(), dataSet.getId() );
        
        //System.out.println (organisationUnit.getId() +" -- " + period.getId() + "--" + dataSet.getId() + "--" + lockExceptionExist );
        
        if( lockExceptionId != null )
        {
            //System.out.println ( " In side Delete Lock Exception " );
            //deleteLockException( dataSet.getId(), period.getId(), organisationUnit.getId() );
            
            LockException lockException = dataSetService.getLockException( lockExceptionId );

            if ( lockException != null )
            {
                dataSetService.deleteLockException( lockException );
                System.out.println ( " Delete Lock Exception  " + lockException.getName()  );
            }
        }
        else
        {
            
        }
    }    
    

    // get Lock Exception from organisationUnitId,periodId,dataSetId
    public Integer getLockExceptionByOrgUnitPeriodDataSet( Integer organisationUnitId, Integer periodId, Integer dataSetId )
    {
        Integer lockExceptionId = null;
        
        try
        {
            String query = "SELECT lockexceptionid FROM lockexception WHERE organisationunitid = " + organisationUnitId + " AND "
                + " periodid = " + periodId + " AND datasetid = " + dataSetId;

            SqlRowSet rs = jdbcTemplate.queryForRowSet( query );

            if ( rs.next() )
            {
                Integer lockExpId = rs.getInt( 1 );
                
                if( lockExpId != null )
                {
                    lockExceptionId = lockExpId;
                }
                
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        
        return lockExceptionId;
    }   
    
    //delete lock exception
    public void deleteLockException( Integer dataSetId, Integer periodId, Integer orgUnitId )
    {
        try
        {
            String query = "DELETE FROM lockexception WHERE organisationunitid = " + orgUnitId + "  AND periodid  = " + periodId +  " AND datasetid = " + dataSetId +"";
            
            //System.out.println( " query "  + query);
            
            jdbcTemplate.execute( query );

        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }        

    private String logError( String message, int statusCode )
    {
        log.info( message );

        this.statusCode = statusCode;

        return SUCCESS;
    }
    
}
