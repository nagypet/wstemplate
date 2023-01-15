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

import hu.perit.spvitamin.core.took.Took;
import hu.perit.spvitamin.spring.metrics.TookWithMetric;
import hu.perit.spvitamin.spring.restmethodlogger.LoggedRestMethod;
import hu.perit.template.scalableservice.config.Constants;
import hu.perit.template.scalableservice.metrics.MicrometerMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author Peter Nagy
 */

@RestController
@Log4j
@RequiredArgsConstructor
public class ServiceController implements ServiceApi
{
    private final MicrometerMetricsService micrometerMetricsService;

    /*
     * ============== makeSomeLongCalculation ==========================================================================
     */
    @Override
    @LoggedRestMethod(eventId = 1, subsystem = Constants.SUBSYSTEM_NAME)
    public Integer makeSomeLongCalculationUsingGET(String processID) throws InterruptedException
    {
        this.micrometerMetricsService.incrementWsCall();

        try (Took took = new TookWithMetric(this.micrometerMetricsService.getMetricService(), processID, false))
        {
            TimeUnit.MILLISECONDS.sleep(2000);
            return 12;
        }
    }
}
