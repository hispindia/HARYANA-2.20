package org.hisp.dhis.security;

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

import org.hisp.dhis.constant.Constant;

import org.hisp.dhis.loginattempt.LoginAttempt;
import org.hisp.dhis.loginattempt.LoginAttemptService;
import org.hisp.dhis.security.action.LoginAction;
import org.hisp.dhis.user.User;
import org.hisp.dhis.user.UserCredentials;
import org.hisp.dhis.constant.ConstantService;
import org.hisp.dhis.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;

import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * @author Morten Olav Hansen <mortenoh@gmail.com>
 */
public class CustomExceptionMappingAuthenticationFailureHandler extends ExceptionMappingAuthenticationFailureHandler {
    /*
    @Override
    public void onAuthenticationFailure( HttpServletRequest request, HttpServletResponse response, AuthenticationException exception ) throws IOException, ServletException
    {
        request.getSession().setAttribute( "username", request.getParameter( "j_username" ) );

        super.onAuthenticationFailure( request, response, exception );
    }
    */

    @Autowired
    private UserService userService;

    @Autowired
    private LoginAttemptService loginAttemptService;

    private int MAX_ATTEMPTS = 10;
    private static int count = 0;
    private int logincount = 1;
    public static int diff = -1;
    public static int attempt = -1;

    LoginAction loginaction = new LoginAction();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        super.onAuthenticationFailure(request, response, exception);
        //constantService loginhour=constantService.getConstantByName( "loginhour" );

        request.getSession().setAttribute("username", request.getParameter("j_username"));

        String username = (request.getParameter("j_username"));

        if (username != null && !username.equalsIgnoreCase(""))
        {
            UserCredentials userCredential = userService.getUserCredentialsByUsername(username);

            if (userCredential != null)
            {
                User user = userService.getUser(userCredential.getUser().getUid());

                LoginAttempt loginattempt = loginAttemptService.getLoginAttemptByUser(user);

                if (count == 0)
                {
                    loginaction.failed = true;
                    count++;
                }

                if (loginaction.failed)
                {

                    if (loginattempt != null) {
                        attempt = loginattempt.getCount() + 1;

                        if (loginattempt.getCount() < 10) {
                            Date a = loginattempt.getLastLoginAttempt();

                            Date b = new Date();

                            diff = (differencebetweentimestamp(b, a));
                            loginattempt.setUser(user);
                            loginattempt.setCount(loginattempt.getCount() + 1);
                            loginattempt.setLastLoginAttempt(new Date());

                            loginAttemptService.updateLoginAttempt(loginattempt);

                        }
                        else if (loginattempt.getCount() == MAX_ATTEMPTS)
                        {
                            Date a = loginattempt.getLastLoginAttempt();

                            Date b = new Date();

                            if (differencebetweentimestamp(b, a) < 24)
                            {
                            } else if (differencebetweentimestamp(b, a) > 24)
                            {
                                loginAttemptService.deleteLoginAttempt(loginattempt);
                            }
                        }
                    }
                    else
                    {
                        //new user
                        attempt = 1;
                        loginattempt = new LoginAttempt();
                        loginattempt.setUser(user);
                        loginattempt.setCount(logincount);
                        loginattempt.setLastLoginAttempt(new Date());
                        loginattempt.setId(user.getId());

                        loginAttemptService.addLoginAttempt(loginattempt);

                    }
                }
            }

        }

    }

    // Supportive Methods
    int differencebetweentimestamp(Date date1, Date date2)
    {
        // Get msec from each, and subtract.
        diff = (int) (date1.getTime() - date2.getTime());
        diff = diff / (1000 * 60 * 60);

        Date b = new Date();

        return diff;

    }
}
