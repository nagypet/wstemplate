package hu.perit.template.scalableservice.metrics;

import hu.perit.spvitamin.core.exception.UnexpectedConditionException;
import hu.perit.spvitamin.spring.metrics.DualMetric;
import hu.perit.template.scalableservice.config.Constants;
import io.micrometer.core.instrument.Counter;
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
    private final String METRIC_CALL = Constants.SUBSYSTEM_NAME.toLowerCase() + ".call";
    private final String METRIC_HEALTH = Constants.SUBSYSTEM_NAME.toLowerCase() + ".health";
    private final String TOTAL_USER_COUNT_METRIC = Constants.SUBSYSTEM_NAME.toLowerCase() + ".total_user_count";

    private final List<HealthIndicator> healthIndicators;

    private final Counter generalWSCallCounter;
    private DualMetric metricService;

    public MicrometerMetricsService(MeterRegistry registry, HealthContributorRegistry healthContributorRegistry)
    {
        this.generalWSCallCounter = registry.counter(METRIC_CALL);
        this.metricService = new DualMetric(registry, Constants.SUBSYSTEM_NAME.toLowerCase(),"service");

        // Health indicators
        this.healthIndicators = healthContributorRegistry.stream() //
                .map(c -> this.getIndicatorFromContributor(c)) //
                .collect(Collectors.toList());
        Gauge.builder(METRIC_HEALTH, healthIndicators, MicrometerMetricsService::healthToCode) //
                .description("The current value of the composite health endpoint").register(registry);
    }

    public void incrementWsCall()
    {
        this.generalWSCallCounter.increment();
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
