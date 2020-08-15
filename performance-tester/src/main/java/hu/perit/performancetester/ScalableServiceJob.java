package hu.perit.performancetester;

import hu.perit.performancetester.config.TesterProperties;
import hu.perit.spvitamin.core.batchprocessing.BatchJob;
import hu.perit.spvitamin.core.exception.ExceptionWrapper;
import hu.perit.spvitamin.core.took.Took;
import hu.perit.spvitamin.spring.config.SpringContext;
import hu.perit.spvitamin.spring.feignclients.JwtAuthRequestInterceptor;
import hu.perit.spvitamin.spring.feignclients.SimpleFeignClientBuilder;
import hu.perit.spvitamin.spring.rest.model.AuthorizationToken;

import java.util.UUID;

public class ScalableServiceJob extends BatchJob {

    private TemplateScalableServiceClient serviceClient;

    private final AuthorizationToken token;
    private final MeasurementStats stats;

    public ScalableServiceJob(AuthorizationToken token, MeasurementStats stats) {
        this.token = token;
        this.stats = stats;

        TesterProperties testerProperties = SpringContext.getBean(TesterProperties.class);

        this.serviceClient = SimpleFeignClientBuilder.newInstance()
                .requestInterceptor(new JwtAuthRequestInterceptor(token.getJwt()))
                .build(TemplateScalableServiceClient.class, testerProperties.getScalableServiceUrl());
    }

    @Override
    protected Boolean execute() throws Exception {
        try (Took took = new Took(false))
        {
            String processID = UUID.randomUUID().toString();

            Integer retval = this.serviceClient.makeSomeLongCalculation(processID);

            this.stats.incrementSuccessCount();
            this.stats.pushExecTimeMillis(took.getDuration());
            this.stats.logIt();
            return null; // NOSONAR
        }
        catch (Exception ex)
        {
            this.stats.incrementFailureCount();
            this.stats.logIt();
            throw ex;
        }
    }

    @Override
    public boolean isFatalException(Throwable ex) {
        ExceptionWrapper exception = ExceptionWrapper.of(ex);

        if (exception.causedBy("org.apache.http.conn.ConnectTimeoutException")
                || exception.causedBy("org.apache.http.NoHttpResponseException")
                || exception.causedBy("org.apache.http.conn.HttpHostConnectException")
        ) {
            return false;
        }

        return true;
    }
}
