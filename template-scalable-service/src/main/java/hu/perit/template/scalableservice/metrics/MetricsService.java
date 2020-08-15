/*
 * Copyright (c) 2020. Innodox Technologies Zrt.
 * All rights reserved.
 */

package hu.perit.template.scalableservice.metrics;

import hu.perit.spvitamin.spring.metrics.DualMetric;
import hu.perit.template.scalableservice.config.Constants;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import org.springframework.stereotype.Service;


@Service
@Getter
public class MetricsService
{
    private final Counter generalWSCallCounter;
    private DualMetric metricService;

    public MetricsService(MeterRegistry registry)
    {
        final String METRIC_CALL = "template.scalable-service.call";

        this.generalWSCallCounter = registry.counter(METRIC_CALL);
        this.metricService = new DualMetric(registry, Constants.SUBSYSTEM_NAME.toLowerCase(),"service");
    }

    public void incrementWsCall()
    {
        this.generalWSCallCounter.increment();
    }
}
