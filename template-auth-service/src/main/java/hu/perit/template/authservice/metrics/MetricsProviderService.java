package hu.perit.template.authservice.metrics;

import java.util.concurrent.TimeoutException;

import hu.perit.spvitamin.core.timeoutlatch.TimeoutLatch;
import hu.perit.template.authservice.db.demodb.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import hu.perit.spvitamin.spring.config.SysConfig;
import hu.perit.spvitamin.spring.metrics.AsyncExecutor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

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
        try
        {
            long userCount = AsyncExecutor.invoke(this::getTotalUserCount, 0L);
            return (double) userCount;
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
        if (this.timeoutLatch.isClosed())
        {
            return 0;
        }

        return userRepo.count();
    }
}
