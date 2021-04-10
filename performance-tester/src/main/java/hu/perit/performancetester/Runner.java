package hu.perit.performancetester;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import feign.auth.BasicAuthRequestInterceptor;
import hu.perit.performancetester.config.TesterProperties;
import hu.perit.spvitamin.core.StackTracer;
import hu.perit.spvitamin.core.batchprocessing.BatchJob;
import hu.perit.spvitamin.core.batchprocessing.BatchProcessor;
import hu.perit.spvitamin.spring.auth.AuthorizationToken;
import hu.perit.spvitamin.spring.feignclients.SimpleFeignClientBuilder;
import hu.perit.spvitamin.spring.rest.client.AuthClient;
import lombok.extern.log4j.Log4j;

/**
 * @author Peter Nagy
 */

@Log4j
@Component
public class Runner extends BatchProcessor implements CommandLineRunner {

    private final TesterProperties testerProperties;

    public Runner(TesterProperties testerProperties) {
        super(testerProperties.getThreadCount());
        this.testerProperties = testerProperties;
    }

    @Override
    public void run(String... args) throws Exception {
        log.debug("Started!");

        long startMillis = System.currentTimeMillis();

        while (!Thread.currentThread().isInterrupted() && ((System.currentTimeMillis() - startMillis) / 60000 < this.testerProperties.getDurationMins()))
        {
            this.runOneBatch();

            log.info(String.format("Waiting %d seconds...", this.testerProperties.getPauseSeconds()));
            TimeUnit.SECONDS.sleep(this.testerProperties.getPauseSeconds());
        }
    }


    private void runOneBatch() {
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
            double duration = (double)stats.getDuration();
            log.info(String.format("Performance test took: %.2f seconds.", duration / 1000.0));
        }
    }


    private AuthorizationToken getAuthorizationToken() {
        AuthClient authClient = SimpleFeignClientBuilder.newInstance()
                .requestInterceptor(new BasicAuthRequestInterceptor("admin", "admin"))
                .build(AuthClient.class, testerProperties.getAuthServiceUrl());

        return authClient.authenticate(null);
    }
}
