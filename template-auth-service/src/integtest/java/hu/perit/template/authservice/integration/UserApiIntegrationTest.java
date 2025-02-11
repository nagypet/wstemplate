/*
 * Copyright 2020-2023 the original author or authors.
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

package hu.perit.template.authservice.integration;

import com.opencsv.exceptions.CsvException;
import feign.auth.BasicAuthRequestInterceptor;
import hu.perit.spvitamin.core.StackTracer;
import hu.perit.spvitamin.spring.auth.AuthorizationToken;
import hu.perit.spvitamin.spring.config.LocalUserProperties;
import hu.perit.spvitamin.spring.config.SpringContext;
import hu.perit.spvitamin.spring.config.SysConfig;
import hu.perit.spvitamin.spring.exception.CannotProcessException;
import hu.perit.spvitamin.spring.exception.ResourceNotFoundException;
import hu.perit.spvitamin.spring.feign.AuthClient;
import hu.perit.spvitamin.spring.feignclients.ForwardingAuthRequestInterceptor;
import hu.perit.spvitamin.spring.feignclients.SimpleFeignClientBuilder;
import hu.perit.template.authservice.api.TemplateAuthServiceClient;
import hu.perit.template.authservice.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * This integration test is only intended to test if endpoints can be called and the returned values can be read.
 * Functionality of lower layers should be covered by unit tests.
 *
 * @author Peter Nagy
 */


@ActiveProfiles({"integtest"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
public class UserApiIntegrationTest
{

    @Autowired
    private InputUserList inputUserListFromCSV;

    private TemplateAuthServiceClient templateAuthServiceClient;

    @BeforeEach
    void setUp()
    {
        try
        {
            LocalUserProperties localUserProperties = SpringContext.getBean(LocalUserProperties.class);

            AuthClient authClient = SimpleFeignClientBuilder.newInstance()
                    .requestInterceptor(new BasicAuthRequestInterceptor(
                            "admin",
                            localUserProperties.getLocaluser().get("admin").getPassword()))
                    .build(AuthClient.class, SysConfig.getServerProperties().getServiceUrl());

            AuthorizationToken authenticate = authClient.authenticate(null);
            String jwt = authenticate.getJwt();
            this.templateAuthServiceClient = SimpleFeignClientBuilder.newInstance()
                    .requestInterceptor(new ForwardingAuthRequestInterceptor("Bearer " + jwt))
                    .build(TemplateAuthServiceClient.class, SysConfig.getServerProperties().getServiceUrl());
        }
        catch (RuntimeException e)
        {
            log.error(StackTracer.toString(e));
            fail(e.toString());
        }
    }


    @Test
    void testUserMgmtEndpoints() throws IOException, CsvException
    {
        log.debug("-----------------------------------------------------------------------------------------------------");
        log.debug("testUserMgmtEndpoints()");

        try
        {
            this.inputUserListFromCSV.loadFromFile();

            // Creating users
            Map<String, CreateUserParams> inputUsers = new HashMap<>();
            for (Map<String, String> item : this.inputUserListFromCSV)
            {

                CreateUserParams createUserParams = CreateUserParams.builder()
                        .userName(item.get("userName"))
                        .password(item.get("password"))
                        .displayName(item.get("displayName"))
                        .active(Boolean.parseBoolean(item.get("active")))
                        .address(item.get("address"))
                        .email(item.get("email"))
                        .nextLoginChangePwd(Boolean.parseBoolean(item.get("nextLoginChangePwd")))
                        .phone(item.get("phone"))
                        .roles(Arrays.stream(item.get("roles").split(",")).map(String::strip).collect(Collectors.toSet()))
                        .build();
                inputUsers.put(createUserParams.getUserName(), createUserParams);

                // Calling the createUser endpoint
                ResponseUri newUser = templateAuthServiceClient.createUser(createUserParams);
                log.debug(newUser.getLocation());
            }

            // Retrieving the list of users
            List<UserDTOFiltered> allUsers = this.templateAuthServiceClient.getAllUsers();
            log.debug(allUsers.toString());
            Assertions.assertTrue(allUsers.size() >= 3 && allUsers.size() <= 4);

            // Retrieving user details
            for (UserDTOFiltered item : allUsers)
            {
                UserDTO userById = this.templateAuthServiceClient.getUserById(item.getUserId());
                log.debug(userById.toString());

                CreateUserParams inputUser = inputUsers.get(userById.getUserName());
                if (inputUser != null)
                {
                    Assertions.assertEquals(inputUser.getDisplayName(), userById.getDisplayName());
                    Assertions.assertEquals(inputUser.getActive(), userById.getActive());
                    Assertions.assertEquals(inputUser.getAddress(), userById.getAddress());
                    Assertions.assertEquals(inputUser.getEmail(), userById.getEmail());
                    Assertions.assertEquals(inputUser.getNextLoginChangePwd(), userById.getNextLoginChangePwd());
                    Assertions.assertEquals(inputUser.getPhone(), userById.getPhone());
                    Assertions.assertEquals(inputUser.getRoles(), userById.getRoles());
                }
            }

            // Updateing users
            for (UserDTOFiltered item : allUsers)
            {
                // get original user
                UserDTO originalUser = this.templateAuthServiceClient.getUserById(item.getUserId());
                log.debug("original: " + originalUser.toString());

                // We only update the displayName
                UpdateUserParams updateUserParams = UpdateUserParams.builder()
                        .displayName("alma")
                        .build();
                if (BooleanUtils.isNotTrue(item.getExternal()))
                {
                    this.templateAuthServiceClient.updateUser(item.getUserId(), updateUserParams);

                    UserDTO updatedUser = this.templateAuthServiceClient.getUserById(item.getUserId());
                    log.debug("updated: " + updatedUser.toString());
                    // Check if this has been changed
                    Assertions.assertEquals("alma", updatedUser.getDisplayName());

                    // Check if other properties are unchanged
                    Assertions.assertEquals(originalUser.getAddress(), updatedUser.getAddress());
                }
                else
                {
                    Assertions.assertThrows(CannotProcessException.class, () -> this.templateAuthServiceClient.updateUser(item.getUserId(), updateUserParams));
                }
            }

            // Removing users
            for (UserDTOFiltered item : allUsers)
            {
                if (BooleanUtils.isNotTrue(item.getExternal()))
                {
                    this.templateAuthServiceClient.deleteUser(item.getUserId());
                }
                else
                {
                    try
                    {
                        this.templateAuthServiceClient.deleteUser(item.getUserId());
                    }
                    catch (RuntimeException e)
                    {
                        log.error(StackTracer.toString(e));
                        if (!(e instanceof CannotProcessException))
                        {
                            fail(e.toString());
                        }
                    }
                }
            }

            // Checking the remaining count
            allUsers = this.templateAuthServiceClient.getAllUsers();
            Assertions.assertEquals(1, allUsers.size());
        }
        catch (RuntimeException | ResourceNotFoundException e)
        {
            log.error(StackTracer.toString(e));
            fail(e.toString());
        }
    }

}
