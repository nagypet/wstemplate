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

import feign.auth.BasicAuthRequestInterceptor;
import hu.perit.performancetester.config.TesterProperties;
import hu.perit.spvitamin.core.StackTracer;
import hu.perit.spvitamin.core.batchprocessing.BatchJob;
import hu.perit.spvitamin.core.batchprocessing.BatchProcessor;
import hu.perit.spvitamin.spring.auth.AuthorizationToken;
import hu.perit.spvitamin.spring.feignclients.SimpleFeignClientBuilder;
import hu.perit.spvitamin.spring.security.authservice.restclient.AuthClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Peter Nagy
 */

@Slf4j
@Component
public class Runner extends BatchProcessor implements CommandLineRunner
{

    private final TesterProperties testerProperties;

    public Runner(TesterProperties testerProperties)
    {
        super(testerProperties.getThreadCount());
        this.testerProperties = testerProperties;
    }

    @Override
    public void run(String... args) throws Exception
    {
        log.debug("Started!");

        long startMillis = System.currentTimeMillis();

        while (!Thread.currentThread().isInterrupted()
            && ((System.currentTimeMillis() - startMillis) / 60000 < this.testerProperties.getDurationMins()))
        {
            this.runOneBatch();

            log.info(String.format("Waiting %d seconds...", this.testerProperties.getPauseSeconds()));
            TimeUnit.SECONDS.sleep(this.testerProperties.getPauseSeconds());
        }
    }


    private void runOneBatch()
    {
        log.debug("--------------------------------------------------------");
        log.debug("runOneBatch()");

        MeasurementStats stats = new MeasurementStats("SERVICE", "");
        try
        {
            AuthorizationToken token = this.getAuthorizationToken();
            int count = this.testerProperties.getBatchSize();
            stats.setDocumentCount(count);
            List<BatchJob> jobList = new ArrayList<>();
            for (int i = 0; i < count; i++)
            {
                jobList.add(new ScalableServiceJob(token, stats));
            }

            this.process(jobList);
        }
        catch (Exception ex)
        {
            log.error(StackTracer.toString(ex));
        }
        finally
        {
            double duration = (double) stats.getDuration();
            log.info(String.format("Performance test took: %.2f seconds.", duration / 1000.0));
        }
    }


    private AuthorizationToken getAuthorizationToken()
    {
        AuthClient authClient = SimpleFeignClientBuilder.newInstance().requestInterceptor(
            new BasicAuthRequestInterceptor("admin", "admin")).build(AuthClient.class, testerProperties.getAuthServiceUrl());

        return authClient.authenticate(null);
    }
}
