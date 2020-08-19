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

package hu.perit.template.authservice.integration;

import feign.auth.BasicAuthRequestInterceptor;
import hu.perit.spvitamin.core.StackTracer;
import hu.perit.spvitamin.core.crypto.CryptoUtil;
import hu.perit.spvitamin.spring.config.SecurityProperties;
import hu.perit.spvitamin.spring.config.SysConfig;
import hu.perit.spvitamin.spring.exception.CannotProcessException;
import hu.perit.spvitamin.spring.feignclients.SimpleFeignClientBuilder;
import hu.perit.template.authservice.rest.client.TemplateAuthClient;
import hu.perit.template.authservice.rest.model.*;
import lombok.extern.log4j.Log4j;
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

/**
 * This integration test is only intended to test if endpoints can be called and the returned values can be read.
 * Functionality of lower layers should be covered by unit tests.
 *
 * @author Peter Nagy
 */


@ActiveProfiles({"default", "integtest"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Log4j
public class UserApiIntegrationTest {

    @Autowired
    private InputUserList inputUserListFromCSV;

    private TemplateAuthClient templateAuthClient;

    @BeforeEach
    void setUp() {
        try {
            SecurityProperties securityProperties = SysConfig.getSecurityProperties();
            CryptoUtil crypto = new CryptoUtil();

            this.templateAuthClient = SimpleFeignClientBuilder.newInstance()
                    .requestInterceptor(new BasicAuthRequestInterceptor(
                            securityProperties.getAdminUserName(),
                            crypto.decrypt(SysConfig.getCryptoProperties().getSecret(), securityProperties.getAdminUserEncryptedPassword())))
                    .build(TemplateAuthClient.class, SysConfig.getServerProperties().getServiceUrl());
        }
        catch (RuntimeException e) {
            log.error(StackTracer.toString(e));
            Assertions.fail(e.toString());
        }
    }


    @Test
    void testUserMgmtEndpoints() throws IOException {
        log.debug("-----------------------------------------------------------------------------------------------------");
        log.debug("testUserMgmtEndpoints()");

        try {
            this.inputUserListFromCSV.loadFromFile();

            // Creating users
            Map<String, CreateUserParams> inputUsers = new HashMap<>();
            for (Map<String, String> item : this.inputUserListFromCSV) {

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
                ResponseUri newUser = templateAuthClient.createUser("123", createUserParams);
                log.debug(newUser.getLocation());
            }

            // Retrieving the list of users
            List<UserDTOFiltered> allUsers = this.templateAuthClient.getAllUsers("123");
            log.debug(allUsers.toString());
            Assertions.assertTrue(allUsers.size() >= 3 && allUsers.size() <= 4);

            // Retrieving user details
            for (UserDTOFiltered item : allUsers) {
                UserDTO userById = this.templateAuthClient.getUserById("123", item.getUserId());
                log.debug(userById.toString());

                CreateUserParams inputUser = inputUsers.get(userById.getUserName());
                if (inputUser != null) {
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
            for (UserDTOFiltered item : allUsers) {
                // get original user
                UserDTO originalUser = this.templateAuthClient.getUserById("123", item.getUserId());
                log.debug("original: " + originalUser.toString());

                // We only update the displayName
                UpdateUserParams updateUserParams = UpdateUserParams.builder()
                        .displayName("alma")
                        .build();
                if (!item.getExternal()) {
                    this.templateAuthClient.updateUser("123", item.getUserId(), updateUserParams);

                    UserDTO updatedUser = this.templateAuthClient.getUserById("123", item.getUserId());
                    log.debug("updated: " + updatedUser.toString());
                    // Check if this has been changed
                    Assertions.assertEquals("alma", updatedUser.getDisplayName());

                    // Check if other properties are unchanged
                    Assertions.assertEquals(originalUser.getAddress(), updatedUser.getAddress());
                }
                else {
                    Assertions.assertThrows(CannotProcessException.class, () -> this.templateAuthClient.updateUser("123", item.getUserId(), updateUserParams));
                }
            }

            // Removing users
            for (UserDTOFiltered item : allUsers) {
                if (!item.getExternal()) {
                    this.templateAuthClient.deleteUser("123", item.getUserId());
                }
                else {
                    Assertions.assertThrows(CannotProcessException.class, () -> this.templateAuthClient.deleteUser("123", item.getUserId()));
                }
            }

            // Checking the remaining count
            allUsers = this.templateAuthClient.getAllUsers("123");
            Assertions.assertEquals(1, allUsers.size());
        }
        catch (RuntimeException e) {
            log.error(StackTracer.toString(e));
            Assertions.fail(e.toString());
        }
    }

}
