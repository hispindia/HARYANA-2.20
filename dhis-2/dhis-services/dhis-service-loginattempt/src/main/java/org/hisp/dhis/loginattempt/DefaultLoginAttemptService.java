package org.hisp.dhis.loginattempt;

import java.util.Collection;

import org.hisp.dhis.user.User;

/**
 * @author Mithilesh Kumar Thakur
 */

public class DefaultLoginAttemptService implements LoginAttemptService
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private LoginAttemptStore loginAttemptStore;

    public void setLoginAttemptStore( LoginAttemptStore loginAttemptStore )
    {
        this.loginAttemptStore = loginAttemptStore;
    }

    // -------------------------------------------------------------------------
    // LoginAttempt Service Methods Imple
    // -------------------------------------------------------------------------
    
    @Override
    public void addLoginAttempt( LoginAttempt loginAttempt )
    {
        loginAttemptStore.addLoginAttempt( loginAttempt );
    }

    @Override
    public void updateLoginAttempt( LoginAttempt loginAttempt )
    {
        loginAttemptStore.updateLoginAttempt( loginAttempt );
    }

    @Override
    public void deleteLoginAttempt( LoginAttempt loginAttempt )
    {
        loginAttemptStore.deleteLoginAttempt( loginAttempt );
    }
    
    @Override
    public LoginAttempt getLoginAttempt( int id )
    {
        return loginAttemptStore.getLoginAttempt( id );
    }
    
    @Override
    public LoginAttempt getLoginAttemptByUser( User user )
    {
        return loginAttemptStore.getLoginAttemptByUser( user );
    }
    
    @Override
    public Collection<LoginAttempt> getAllLoginAttempts()
    {
        return loginAttemptStore.getAllLoginAttempts();
    }
}