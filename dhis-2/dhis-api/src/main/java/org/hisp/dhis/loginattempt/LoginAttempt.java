package org.hisp.dhis.loginattempt;

import java.io.Serializable;
import java.util.Date;

import org.hisp.dhis.user.User;

/**
 * @author Mithilesh Kumar Thakur
 */
public class LoginAttempt
    implements Serializable
{

    /**
     * Determines if a de-serialized file is compatible with this class.
     */
    private static final long serialVersionUID = 6269303850789110610L;

    /**
     * The unique identifier
     */
    private int id;

    private User user;

    private Integer count;

    private Date lastLoginAttempt;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public LoginAttempt()
    {

    }

    public LoginAttempt( User user, Integer count )
    {
        this.user = user;
        this.count = count;
    }

    public LoginAttempt( User user, Integer count, Date lastLoginAttempt )
    {
        this.user = user;
        this.count = count;
        this.lastLoginAttempt = lastLoginAttempt;
    }

    // -------------------------------------------------------------------------
    // hashCode and equals
    // -------------------------------------------------------------------------

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( o == null )
        {
            return false;
        }

        if ( !(o instanceof LoginAttempt) )
        {
            return false;
        }

        final LoginAttempt other = (LoginAttempt) o;

        return user.equals( other.getUser() );

    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;

        result = result * prime + user.hashCode();
        return result;
    }

    // -------------------------------------------------------------------------
    // Getters and setters
    // -------------------------------------------------------------------------

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser( User user )
    {
        this.user = user;
    }

    public Integer getCount()
    {
        return count;
    }

    public void setCount( Integer count )
    {
        this.count = count;
    }

    public Date getLastLoginAttempt()
    {
        return lastLoginAttempt;
    }

    public void setLastLoginAttempt( Date lastLoginAttempt )
    {
        this.lastLoginAttempt = lastLoginAttempt;
    }

}
