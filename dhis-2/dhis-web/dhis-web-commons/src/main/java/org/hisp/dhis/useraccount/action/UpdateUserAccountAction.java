package org.hisp.dhis.useraccount.action;

/*
 * Copyright (c) 2004-2015, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import org.apache.commons.lang3.StringUtils;
import org.hisp.dhis.i18n.I18n;
import org.hisp.dhis.security.migration.MigrationPasswordManager;
import org.hisp.dhis.user.CurrentUserService;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import com.opensymphony.xwork2.Action;

/**
 * @author Torgeir Lorange Ostby
 */
public class UpdateUserAccountAction
    implements Action
{
    // -------------------------------------------------------------------------
    // Dependencies
    // -------------------------------------------------------------------------

    private UserService userService;

    private MigrationPasswordManager passwordManager;

    @Autowired
    private CurrentUserService currentUserService;

    // -------------------------------------------------------------------------
    // Input
    // -------------------------------------------------------------------------

    private I18n i18n;

    private Integer id;

    private String oldPassword;

    private String rawPassword;
    
    private String retypePassword;
    
    private String surname;

    private String firstName;

    private String email;

    private String phoneNumber;

    private String message;

    // -------------------------------------------------------------------------
    // Getters && Setters
    // -------------------------------------------------------------------------

    public void setPasswordManager( MigrationPasswordManager passwordManager )
    {
        this.passwordManager = passwordManager;
    }

    public void setUserService( UserService userService )
    {
        this.userService = userService;
    }

    public void setOldPassword( String oldPassword )
    {
        this.oldPassword = oldPassword;
    }

    public void setRawPassword( String rawPassword )
    {
        this.rawPassword = rawPassword;
    }

    public void setRetypePassword( String retypePassword )
    {
        this.retypePassword = retypePassword;
    }

    public void setId( Integer id )
    {
        this.id = id;
    }

    public String getMessage()
    {
        return message;
    }

    public void setI18n( I18n i18n )
    {
        this.i18n = i18n;
    }

    public void setPhoneNumber( String phoneNumber )
    {
        this.phoneNumber = phoneNumber;
    }

    public void setSurname( String surname )
    {
        this.surname = surname;
    }

    public void setEmail( String email )
    {
        this.email = email;
    }

    public void setFirstName( String firstName )
    {
        this.firstName = firstName;
    }

    // -------------------------------------------------------------------------
    // Action implementation
    // -------------------------------------------------------------------------

    @Override
    public String execute()
        throws Exception
    {
        // ---------------------------------------------------------------------
        // Prepare values
        // ---------------------------------------------------------------------

        email = StringUtils.trimToNull( email );
        rawPassword = StringUtils.trimToNull( rawPassword );
        
        retypePassword = StringUtils.trimToNull( retypePassword );
   
        User user = userService.getUser( id );
        String currentPassword = userService.getUserCredentials( user ).getPassword();
        
        User currentUser = currentUserService.getCurrentUser();

        if ( currentUser.getId() != user.getId() )
        {
            message = i18n.getString( "access_denied" );

            return INPUT;
        }
        
        if ( !passwordManager.legacyOrCurrentMatches( oldPassword, currentPassword, user.getUsername() ) )
        {
            message = i18n.getString( "wrong_password" );
            return INPUT;
        }

       

        // ---------------------------------------------------------------------
        // Update userCredentials and user
        // ---------------------------------------------------------------------

        user.setSurname( surname );
        user.setFirstName( firstName );
        user.setEmail( email );
        user.setPhoneNumber( phoneNumber );

        if ( rawPassword != null && retypePassword != null )
        {
            //System.out.println( rawPassword + " 1 -- " + retypePassword );
            
            if( !rawPassword.equalsIgnoreCase( retypePassword ))
            {
                //System.out.println( rawPassword + " 2 -- " + retypePassword );
                
                message = i18n.getString( "The entered both password do not match. Please re-enter the password" );

                return INPUT;
            }
            
            String passwordMessage = isValidPassword( rawPassword );
            
            if( !passwordMessage.equalsIgnoreCase( "OK" ) )
            {
                message = i18n.getString( passwordMessage );

                return INPUT;
            }
            
            userService.encodeAndSetPassword( user, rawPassword );
        }

        userService.updateUserCredentials( user.getUserCredentials() );
        userService.updateUser( user );

        message = i18n.getString( "update_user_success" );

        return SUCCESS;
    }
    
    // supportive method
    public String isValidPassword( String password )
    {
        boolean valid = true;
        String validMessage = "";
        
        if ( password.length() > 35 || password.length() < 8 )
        {
            //System.out.println( "Password should be less than 15 and more than 8 characters in length." );
            validMessage = "Password should be less than 35 and more than 8 characters in length.";
            valid = false;
        }

        String upperCaseChars = "(.*[A-Z].*)";
        if ( !password.matches( upperCaseChars ) )
        {
            //System.out.println( "Password should contain atleast one upper case alphabet" );
            validMessage = "Password should contain atleast one upper case alphabet";
            valid = false;
        }
        String lowerCaseChars = "(.*[a-z].*)";
        if ( !password.matches( lowerCaseChars ) )
        {
            //System.out.println( "Password should contain atleast one lower case alphabet" );
            validMessage = "Password should contain atleast one lower case alphabet";
            valid = false;
        }
        String numbers = "(.*[0-9].*)";
        if ( !password.matches( numbers ) )
        {
            //System.out.println( "Password should contain atleast one number." );
            validMessage = "Password should contain atleast one number.";
            valid = false;
        }
        String specialChars = "(.*[,~,!,@,#,$,%,^,&,*,(,),-,_,=,+,[,{,],},|,;,:,<,>,/,?].*$)";
        if ( !password.matches( specialChars ) )
        {
            //System.out.println( "Password should contain atleast one special character" );
            validMessage = "Password should contain atleast one special character";
            valid = false;
        }

        if (valid)
        {
            validMessage = "OK";
            //System.out.println("Password is valid.");
        }

        return validMessage;
    }

}
