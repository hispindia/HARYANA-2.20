package org.hisp.dhis.user.action.unlock;

import java.util.ArrayList;
import java.util.List;

import org.hisp.dhis.loginattempt.LoginAttempt;
import org.hisp.dhis.loginattempt.LoginAttemptService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Mithilesh Kumar Thakur
 */
public class UnlockUsersFornAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
    
    @Autowired
    private LoginAttemptService loginAttemptService;
    
    // -------------------------------------------------------------------------
    // Input and Output Parameters
    // -------------------------------------------------------------------------
    
    private List<LoginAttempt> lockdUsers = new ArrayList<LoginAttempt>();
    
    public List<LoginAttempt> getLockdUsers()
    {
        return lockdUsers;
    }

    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {        
        lockdUsers = new ArrayList<LoginAttempt>( loginAttemptService.getAllLoginAttempts() );
        /*
        for( LoginAttempt loginAttempt : lockdUsers)
        {
            System.out.println( loginAttempt.getId() + " -- " + loginAttempt.getUser().getName() );
        }
       */
        
        return SUCCESS;
    }
}

