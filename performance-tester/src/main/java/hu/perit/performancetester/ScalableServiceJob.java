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

package hu.perit.performancetester;

import java.util.UUID;

import hu.perit.performancetester.config.TesterProperties;
import hu.perit.spvitamin.core.batchprocessing.BatchJob;
import hu.perit.spvitamin.core.exception.ExceptionWrapper;
import hu.perit.spvitamin.core.took.Took;
import hu.perit.spvitamin.spring.auth.AuthorizationToken;
import hu.perit.spvitamin.spring.config.SpringContext;
import hu.perit.spvitamin.spring.feignclients.JwtAuthRequestInterceptor;
import hu.perit.spvitamin.spring.feignclients.SimpleFeignClientBuilder;

public class ScalableServiceJob extends BatchJob {

    private TemplateScalableServiceClient serviceClient;

    private final AuthorizationToken token;
    private final MeasurementStats stats;

    public ScalableServiceJob(AuthorizationToken token, MeasurementStats stats) {
        this.token = token;
        this.stats = stats;

        TesterProperties testerProperties = SpringContext.getBean(TesterProperties.class);

        this.serviceClient = SimpleFeignClientBuilder.newInstance()
                .requestInterceptor(new JwtAuthRequestInterceptor(token.getJwt()))
                .build(TemplateScalableServiceClient.class, testerProperties.getScalableServiceUrl());
    }

    @Override
    protected Void execute() throws Exception {
        try (Took took = new Took(false))
        {
            String traceId = UUID.randomUUID().toString();

            Integer retval = this.serviceClient.makeSomeLongCalculation(traceId);

            this.stats.incrementSuccessCount();
            this.stats.pushExecTimeMillis(took.getDuration());
            this.stats.logIt();
            return null; // NOSONAR
        }
        catch (Exception ex)
        {
            this.stats.incrementFailureCount();
            this.stats.logIt();
            throw ex;
        }
    }

    @Override
    public boolean isFatalException(Throwable ex) {
        ExceptionWrapper exception = ExceptionWrapper.of(ex);

//        if (exception.causedBy("org.apache.http.conn.ConnectTimeoutException")
//                || exception.causedBy("org.apache.http.NoHttpResponseException")
//                || exception.causedBy("org.apache.http.conn.HttpHostConnectException")
//                || exception.causedBy("org.springframework.web.server.ResponseStatusException")
//        ) {
//            return false;
//        }

        return false;
    }
}
