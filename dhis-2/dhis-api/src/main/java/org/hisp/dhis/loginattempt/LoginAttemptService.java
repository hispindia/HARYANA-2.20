package org.hisp.dhis.loginattempt;

import java.util.Collection;

import org.hisp.dhis.user.User;

/**
 * @author Mithilesh Kumar Thakur
 */
public interface LoginAttemptService
{
    String ID = LoginAttemptService.class.getName();
    
    // -------------------------------------------------------------------------
    // LoginAttempt
    // -------------------------------------------------------------------------
    
    void addLoginAttempt( LoginAttempt loginAttempt );

    void updateLoginAttempt( LoginAttempt loginAttempt );

    void deleteLoginAttempt( LoginAttempt loginAttempt );
    
    LoginAttempt getLoginAttempt( int id );

    LoginAttempt getLoginAttemptByUser( User user );
        
    Collection<LoginAttempt> getAllLoginAttempts();
        
}
