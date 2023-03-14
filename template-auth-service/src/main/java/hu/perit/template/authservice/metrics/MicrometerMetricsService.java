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

import hu.perit.spvitamin.core.exception.UnexpectedConditionException;
import hu.perit.template.authservice.config.Constants;
import hu.perit.template.authservice.db.demodb.table.UserEntity;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.health.CompositeHealthContributor;
import org.springframework.boot.actuate.health.HealthContributor;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.NamedContributor;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author nagy_peter
 */
@Service
@Getter
@Slf4j
public class MicrometerMetricsService
{
    private final MetricsProviderService metricsProviderService;
    private final List<HealthIndicator> healthIndicators;

    public MicrometerMetricsService(MeterRegistry registry, HealthContributorRegistry healthContributorRegistry, MetricsProviderService metricsProviderService)
    {
        this.metricsProviderService = metricsProviderService;

        final String METRIC_HEALTH = Constants.SUBSYSTEM_NAME.toLowerCase() + ".health";
        final String TOTAL_USER_COUNT_METRIC = Constants.SUBSYSTEM_NAME.toLowerCase() + ".total_user_count";

        // Health indicators
        this.healthIndicators = healthContributorRegistry.stream() //
                .map(c -> this.getIndicatorFromContributor(c)) //
                .collect(Collectors.toList());
        Gauge.builder(METRIC_HEALTH, healthIndicators, MicrometerMetricsService::healthToCode) //
                .description("The current value of the composite health endpoint").register(registry);

        // Total count of users
        Gauge.builder(TOTAL_USER_COUNT_METRIC, this.metricsProviderService, MetricsProviderService::getUserCountMetric)
                .description(String.format("The total count of users in the %s table", UserEntity.TABLE_NAME)).baseUnit("pcs").register(registry);
    }

    private HealthIndicator getIndicatorFromContributor(NamedContributor<HealthContributor> namedContributor)
    {
        log.debug(String.format("Using health contributor: '%s'", namedContributor.getName()));

        HealthContributor contributor = namedContributor.getContributor();
        if (contributor instanceof HealthIndicator)
        {
            return (HealthIndicator) contributor;
        }

        if (contributor instanceof CompositeHealthContributor)
        {
            CompositeHealthContributor compositeHealthContributor = (CompositeHealthContributor) contributor;
            for (NamedContributor<HealthContributor> elementOfComposite : compositeHealthContributor)
            {
                return getIndicatorFromContributor(elementOfComposite); // NOSONAR
            }
        }

        throw new UnexpectedConditionException();
    }

    private static int healthToCode(List<HealthIndicator> indicators)
    {
        for (HealthIndicator indicator : indicators)
        {
            Status status = indicator.health().getStatus();
            if (Status.DOWN.equals(status))
            {
                return 0;
            }
        }

        return 1;
    }
}
