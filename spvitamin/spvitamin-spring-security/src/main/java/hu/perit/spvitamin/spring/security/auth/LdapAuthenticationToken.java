/*
 * Copyright 2020-2025 the original author or authors.
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

package hu.perit.spvitamin.spring.security.auth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serial;
import java.util.Collection;

@Getter
public class LdapAuthenticationToken extends UsernamePasswordAuthenticationToken
{
    @Serial
    private static final long serialVersionUID = 537004967566037245L;

    @Setter
    private String userCN;
    private final String url;

    public LdapAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities, String url, String userCN)
    {
        super(principal, credentials, authorities);
        this.url = url;
        this.userCN = userCN;
    }
}
