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
        Object result = this.nativeQueryRepo.getSingleResult("SELECT 1", false);
        return (result instanceof Integer && result.equals(1)) || (result instanceof BigDecimal && result.equals(BigDecimal.ONE));
    }
}
