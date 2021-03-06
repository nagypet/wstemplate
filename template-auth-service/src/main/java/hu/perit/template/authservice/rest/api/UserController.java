/*
 * Copyright 2020-2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hu.perit.template.authservice.rest.api;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.RestController;

import com.google.common.reflect.AbstractInvocationHandler;

import hu.perit.spvitamin.core.took.Took;
import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import hu.perit.spvitamin.spring.logging.AbstractInterfaceLogger;
import hu.perit.spvitamin.spring.security.AuthenticatedUser;
import hu.perit.spvitamin.spring.security.auth.AuthorizationService;
import hu.perit.template.authservice.config.Constants;
import hu.perit.template.authservice.rest.model.CreateUserParams;
import hu.perit.template.authservice.rest.model.ResponseUri;
import hu.perit.template.authservice.rest.model.RoleSet;
import hu.perit.template.authservice.rest.model.UpdateUserParams;
import hu.perit.template.authservice.rest.model.UserDTO;
import hu.perit.template.authservice.rest.model.UserDTOFiltered;
import hu.perit.template.authservice.rest.session.UserSession;
import hu.perit.template.authservice.services.UserService;

/**
 * @author Peter Nagy
 */

@RestController
public class UserController implements UserApi
{

    private final UserApi proxy;

    public UserController(UserService userService, HttpServletRequest httpRequest, AuthorizationService authorizationService)
    {
        this.proxy = (UserApi) Proxy.newProxyInstance(UserApi.class.getClassLoader(), new Class[]{UserApi.class},
            new UserController.ProxyImpl(userService, authorizationService, httpRequest));
    }

    /*
     * ============== getAllUsers ======================================================================================
     */
    @Override
    public List<UserDTOFiltered> getAllUsersUsingGET(String processID)
    {
        return this.proxy.getAllUsersUsingGET(processID);
    }


    /*
     * ============== getUserById ======================================================================================
     */
    @Override
    public UserDTO getUserByIdUsingGET(String processID, Long id) throws ResourceNotFoundException
    {
        return this.proxy.getUserByIdUsingGET(processID, id);
    }


    /*
     * ============== createUser =======================================================================================
     */
    @Override
    public ResponseUri createUserUsingPOST(String processID, @Valid CreateUserParams createUserParams)
    {
        return this.proxy.createUserUsingPOST(processID, createUserParams);
    }


    /*
     * ============== updateUser =======================================================================================
     */
    @Override
    public void updateUserUsingPUT(String processID, Long id, @Valid UpdateUserParams updateUserParams) throws ResourceNotFoundException
    {
        this.proxy.updateUserUsingPUT(processID, id, updateUserParams);
    }


    /*
     * ============== deleteUser =======================================================================================
     */
    @Override
    public void deleteUserUsingDELETE(String processID, Long id) throws ResourceNotFoundException
    {
        this.proxy.deleteUserUsingDELETE(processID, id);
    }


    /*
     * ============== addRole ==========================================================================================
     */
    @Override
    public void addRoleUsingPOST(String processID, Long id, @Valid RoleSet roleSet) throws ResourceNotFoundException
    {
        this.proxy.addRoleUsingPOST(processID, id, roleSet);
    }


    /*
     * ============== deleteRole =======================================================================================
     */
    @Override
    public void deleteRoleUsingDELETE(String processID, Long id, @Valid RoleSet roleSet) throws ResourceNotFoundException
    {
        this.proxy.deleteRoleUsingDELETE(processID, id, roleSet);
    }


    /*
     * ============== PROXY Implementation =============================================================================
     */

    private static class ProxyImpl extends AbstractInvocationHandler
    {
        private final UserService userService;
        private final AuthorizationService authorizationService;
        private final ProxyImpl.Logger logger;

        public ProxyImpl(UserService userService, AuthorizationService authorizationService, HttpServletRequest httpRequest)
        {
            this.userService = userService;
            this.authorizationService = authorizationService;
            this.logger = new ProxyImpl.Logger(httpRequest);
        }


        @Override
        protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable
        {
            return this.invokeWithExtras(method, args);
        }


        private Object invokeWithExtras(Method method, Object[] args) throws Throwable
        {
            AuthenticatedUser authenticatedUser = this.authorizationService.getAuthenticatedUser();
            try (Took took = new Took(method))
            {
                this.logger.traceIn(null, authenticatedUser.getUsername(), method, args);
                UserSession session = new UserSession(this.userService);
                Object retval = method.invoke(session, args);
                this.logger.traceOut(null, authenticatedUser.getUsername(), method);
                return retval;
            }
            catch (IllegalAccessException ex)
            {
                this.logger.traceOut(null, authenticatedUser.getUsername(), method, ex);
                throw ex;
            }
            catch (InvocationTargetException ex)
            {
                this.logger.traceOut(null, authenticatedUser.getUsername(), method, ex.getTargetException());
                throw ex.getTargetException();
            }
        }

        private static class Logger extends AbstractInterfaceLogger
        {

            Logger(HttpServletRequest httpRequest)
            {
                super(httpRequest);
            }

            @Override
            protected String getSubsystemName()
            {
                return Constants.SUBSYSTEM_NAME;
            }
        }
    }
}
