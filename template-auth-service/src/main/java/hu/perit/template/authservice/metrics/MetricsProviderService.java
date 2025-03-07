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

package hu.perit.template.authservice.metrics;

import hu.perit.spvitamin.core.StackTracer;
import hu.perit.spvitamin.core.timeoutlatch.TimeoutLatch;
import hu.perit.spvitamin.spring.config.SysConfig;
import hu.perit.spvitamin.spring.metrics.AsyncExecutor;
import hu.perit.template.authservice.db.demodb.repo.UserRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsProviderService
{
    private final UserRepo userRepo;

    private TimeoutLatch timeoutLatch;

    @PostConstruct
    void postConstruct()
    {
        this.timeoutLatch = new TimeoutLatch(SysConfig.getMetricsProperties().getMetricsGatheringHysteresisMillis());
    }

    public double getUserCountMetric()
    {
        if (this.timeoutLatch.isClosed())
        {
            log.info("Getting total user count failed! The Database server was down, waiting some time before checking it again.");
            return 0.0;
        }

        try
        {
            return AsyncExecutor.invoke(this::getTotalUserCount, 0L);
        }
        catch (RuntimeException ex)
        {
            this.timeoutLatch.setClosed();
            log.error(StackTracer.toString(ex));
        }
        catch (TimeoutException ex)
        {
            this.timeoutLatch.setClosed();
            log.error(String.format("getTotalUserCount() did not complete within %d ms! The database is not reachable or slow!",
                SysConfig.getMetricsProperties().getTimeoutMillis()));
        }

        return 0.0;
    }


    private long getTotalUserCount()
    {
        return userRepo.count();
    }
}
