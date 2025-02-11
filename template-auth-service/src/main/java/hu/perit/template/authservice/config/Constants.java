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

package hu.perit.template.authservice.config;

/**
 * @author Peter Nagy
 */

public class Constants {

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    public static final String INDEXNAME_USERNAME = "user_ix_username";

    public static final int USER_API_GET_ALL = 1;
    public static final int USER_API_GET_BY_ID = 2;
    public static final int USER_API_CREATE = 3;
    public static final int USER_API_UPDATE = 4;
    public static final int USER_API_DELETE = 5;
    public static final int USER_API_ADD_ROLE = 6;
    public static final int USER_API_DELETE_ROLE = 7;

    public static final String SUBSYSTEM_NAME = "template-auth-service";
    public static final String SCHEMA = "dbo";
}
