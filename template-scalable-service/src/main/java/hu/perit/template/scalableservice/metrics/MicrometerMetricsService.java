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

package hu.perit.template.scalableservice.metrics;

import hu.perit.spvitamin.core.exception.UnexpectedConditionException;
import hu.perit.spvitamin.spring.metrics.DualMetric;
import hu.perit.template.scalableservice.config.Constants;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.health.contributor.CompositeHealthContributor;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthContributor;
import org.springframework.boot.health.contributor.HealthContributors;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.boot.health.contributor.Status;
import org.springframework.boot.health.registry.HealthContributorRegistry;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author nagy_peter
 */
@Service
@Getter
@Slf4j
public class MicrometerMetricsService
{
    private static final String METRIC_CALL = Constants.SUBSYSTEM_NAME.toLowerCase() + ".call";
    private static final String METRIC_HEALTH = Constants.SUBSYSTEM_NAME.toLowerCase() + ".health";

    private final List<HealthIndicator> healthIndicators;

    private final Counter generalWSCallCounter;
    private final DualMetric metricService;


    public MicrometerMetricsService(MeterRegistry registry, HealthContributorRegistry healthContributorRegistry)
    {
        this.generalWSCallCounter = registry.counter(METRIC_CALL);
        this.metricService = new DualMetric(registry, Constants.SUBSYSTEM_NAME.toLowerCase(), "service");

        // Health indicators
        this.healthIndicators = healthContributorRegistry.stream()
                .map(this::getIndicatorFromContributor)
                .toList();
        Gauge.builder(METRIC_HEALTH, healthIndicators, MicrometerMetricsService::healthToCode)
                .description("The current value of the composite health endpoint").register(registry);
    }


    public void incrementWsCall()
    {
        this.generalWSCallCounter.increment();
    }


    private HealthIndicator getIndicatorFromContributor(HealthContributors.Entry entry)
    {
        log.debug(String.format("Using health contributor: '%s'", entry.name()));

        HealthContributor contributor = entry.contributor();
        if (contributor instanceof HealthIndicator healthIndicator)
        {
            return healthIndicator;
        }

        if (contributor instanceof CompositeHealthContributor compositeHealthContributor)
        {
            for (HealthContributors.Entry elementOfComposite : compositeHealthContributor)
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
            Health health = indicator.health();
            if (health == null || health.getStatus() == Status.DOWN)
            {
                return 0;
            }
        }

        return 1;
    }
}
