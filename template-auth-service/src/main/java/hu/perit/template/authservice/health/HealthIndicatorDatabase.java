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

package hu.perit.template.authservice.health;

import hu.perit.spvitamin.core.timeoutlatch.TimeoutLatch;
import hu.perit.spvitamin.spring.config.Constants;
import hu.perit.spvitamin.spring.config.SysConfig;
import hu.perit.spvitamin.spring.metrics.AsyncExecutor;
import hu.perit.template.authservice.db.demodb.repo.NativeQueryRepo;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeoutException;

/**
 * @author nagy_peter
 */
@Component
@Slf4j
@DependsOn("metricsProperties")
@RequiredArgsConstructor
public class HealthIndicatorDatabase extends AbstractHealthIndicator
{
    private static final String STATUS = "Status";

    private final NativeQueryRepo nativeQueryRepo;

    private TimeoutLatch timeoutLatch;

    @PostConstruct
    void postConstruct()
    {
        this.timeoutLatch = new TimeoutLatch(SysConfig.getMetricsProperties().getMetricsGatheringHysteresisMillis());
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception
    {
        LocalDateTime timestamp = LocalDateTime.now();
        builder.withDetail("Timestamp",
                timestamp.format(DateTimeFormatter.ofPattern(Constants.DEFAULT_JACKSON_TIMESTAMPFORMAT)));

        if (this.timeoutLatch.isClosed())
        {
            log.info("Health check failed: the Database server was down, waiting some time before checking it again.");
            builder.down();
            builder.withDetail(STATUS, "Database server was down, waiting some time before checking it again.");
            return;
        }

        try
        {
            boolean serviceUpAndRunning = AsyncExecutor.invoke(this::checkDbUpAndRunning, false);
            if (serviceUpAndRunning)
            {
                builder.up();
                builder.withDetail(STATUS, "Database server is up and running");
            }
            else
            {
                log.error("Health check failed: the database server is down!");
                builder.down();
                builder.withDetail(STATUS, "Database server is down!");
            }
        }
        catch (RuntimeException ex)
        {
            this.timeoutLatch.setClosed();
            log.error(String.format("Health check failed: %s", ex));
            builder.down();
            builder.withException(ex);
        }
        catch (TimeoutException ex)
        {
            this.timeoutLatch.setClosed();
            log.error("Health check failed: the database server cannot be reached (timeout)!");
            builder.down();
            builder.withDetail(STATUS, "Database server cannot be reached (timeout)!");
        }
    }

    boolean checkDbUpAndRunning()
    {
        // Use SELECT 1 FROM DUAL with ORACLE
        Object result = this.nativeQueryRepo.getSingleResult("SELECT 1", false);
        return (result instanceof Integer && result.equals(1)) || (result instanceof BigDecimal && result.equals(BigDecimal.ONE));
    }
}
