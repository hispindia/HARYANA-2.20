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
public class UnlockUsersResultAction implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------
       
    @Autowired
    private LoginAttemptService loginAttemptService;
    
    // -------------------------------------------------------------------------
    // Input and Output Parameters
    // -------------------------------------------------------------------------
    
    private List<Integer> selectedLockedUsers = new ArrayList<Integer>();
    
    public void setSelectedLockedUsers( List<Integer> selectedLockedUsers )
    {
        this.selectedLockedUsers = selectedLockedUsers;
    }
    // -------------------------------------------------------------------------
    // Action Implementation
    // -------------------------------------------------------------------------

    public String execute() throws Exception
    {        
        if( selectedLockedUsers != null && selectedLockedUsers.size() > 0 )
        {
            for( Integer loginAttemptId :  selectedLockedUsers )
            {
                LoginAttempt loginAttempt = loginAttemptService.getLoginAttempt( loginAttemptId );
                if( loginAttempt != null )
                {
                    loginAttemptService.deleteLoginAttempt( loginAttempt );
                }
            }
        }

        return SUCCESS;
    }
}
