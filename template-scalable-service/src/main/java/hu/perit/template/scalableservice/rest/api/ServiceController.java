/*
 * Copyright 2020-2020 the original author or authors.
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

package hu.perit.template.scalableservice.rest.api;

import com.google.common.reflect.AbstractInvocationHandler;
import hu.perit.spvitamin.core.connectablecontext.ThreadContextKey;
import hu.perit.spvitamin.core.took.Took;
import hu.perit.spvitamin.spring.logging.AbstractInterfaceLogger;
import hu.perit.spvitamin.spring.metrics.TookWithMetric;
import hu.perit.spvitamin.spring.security.auth.AuthorizationService;
import hu.perit.template.scalableservice.config.Constants;
import hu.perit.template.scalableservice.metrics.MetricsService;
import hu.perit.template.scalableservice.rest.session.ServiceSession;
import hu.perit.template.scalableservice.rest.session.ServiceSessionHolder;
import lombok.extern.log4j.Log4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author Peter Nagy
 */

@RestController
@Log4j
public class ServiceController implements ServiceApi {

    private final ServiceApi proxy;
    private final MetricsService metricsService;

    public ServiceController(
            ServiceSessionHolder serviceSessionHolder,
            AuthorizationService authorizationService,
            MetricsService metricsService,
            HttpServletRequest httpRequest) {
        proxy = (ServiceApi) Proxy.newProxyInstance(
                ServiceApi.class.getClassLoader(),
                new Class[]{ServiceApi.class},
                new ServiceController.ProxyImpl(httpRequest, serviceSessionHolder, authorizationService, metricsService));

        this.metricsService = metricsService;
    }

    /*
     * ============== makeSomeLongCalculation ==========================================================================
     */
    @Override
    public Integer makeSomeLongCalculationUsingGET(String processID) throws InterruptedException {
        try (TookWithMetric took = new TookWithMetric(this.metricsService.getMetricService(), processID, false)) {
            return this.proxy.makeSomeLongCalculationUsingGET(processID);
        }
    }


    @Log4j
    private static class ProxyImpl extends AbstractInvocationHandler {
        private final ServiceSessionHolder threadContextHolder;
        private final AuthorizationService authorizationService;
        private final MetricsService metricsService;
        private final ProxyImpl.Logger logger;

        public ProxyImpl(HttpServletRequest httpRequest, ServiceSessionHolder threadContextHolder, AuthorizationService authorizationService, MetricsService metricsService) {
            this.threadContextHolder = threadContextHolder;
            this.authorizationService = authorizationService;
            this.metricsService = metricsService;
            this.logger = new ProxyImpl.Logger(httpRequest);
        }


        @Override
        protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
            return this.invokeInContext(method, args);
        }


        private Object invokeInContext(Method method, Object[] args) throws Throwable {
            UserDetails user = this.authorizationService.getAuthenticatedUser();
            try (Took took = new Took(method)) {
                this.metricsService.incrementWsCall();
                this.logger.traceIn(null, user.getUsername(), method, args);

                ServiceSession serviceSession = this.threadContextHolder.getContext(new ThreadContextKey());

                Object retval = method.invoke(serviceSession, args);

                this.logger.traceOut(null, user.getUsername(), method);
                return retval;
            }
            catch (IllegalAccessException ex) {
                this.logger.traceOut(null, user.getUsername(), method, ex);
                throw ex;
            }
            catch (InvocationTargetException ex) {
                this.logger.traceOut(null, user.getUsername(), method, ex.getTargetException());
                throw ex.getTargetException();
            }
        }

        @Log4j
        private static class Logger extends AbstractInterfaceLogger {

            protected Logger(HttpServletRequest httpRequest) {
                super(httpRequest);
            }

            @Override
            protected String getSubsystemName() {
                return Constants.SUBSYSTEM_NAME;
            }
        }
    }
}
