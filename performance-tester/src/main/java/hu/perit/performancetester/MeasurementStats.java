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

package hu.perit.performancetester;

import hu.perit.spvitamin.spring.metrics.SingleMetricAverageOfLastNValueGeneric;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class MeasurementStats {
    private String mode;
    private final AtomicInteger documentCountSuccess = new AtomicInteger();
    private final AtomicInteger documentCountFailure = new AtomicInteger();
    private final AtomicLong sizeMetric = new AtomicLong();
    private final String sizeMetricColumnTitle;
    private final StopWatch timer = new StopWatch();
    private final AtomicLong lastLog = new AtomicLong();
    private final AtomicBoolean headerPrinted = new AtomicBoolean(false);
    private final SingleMetricAverageOfLastNValueGeneric<Long> execTime = new SingleMetricAverageOfLastNValueGeneric<>();
    private int documentCount;

    public MeasurementStats(String mode, String sizeMetricColumnTitle)
    {
        this.mode = mode;
        this.sizeMetricColumnTitle = StringUtils.defaultString(sizeMetricColumnTitle,"N/A");
        this.timer.start();
    }

    public void setDocumentCount(int documentCount)
    {
        this.documentCount = documentCount;
    }

    public int incrementSuccessCount()
    {
        return this.documentCountSuccess.incrementAndGet();
    }


    public int incrementFailureCount()
    {
        return this.documentCountFailure.incrementAndGet();
    }


    public long addToSizeMetric(int val)
    {
        return this.sizeMetric.addAndGet(val);
    }

    public void logIt()
    {
        if (!this.headerPrinted.getAndSet(true))
        {
            log.info(              "+--------------------+--------+--------+--------+----------+--------------------+--------+--------+--------+");
            log.info(String.format("|mode                |elapsed |success |failure | speed    |%-20s|average |max     |min     |", this.sizeMetricColumnTitle));
            log.info(String.format("|                    |        |pcs     |pcs     | call/min |                    |ms      |ms      |ms      |", this.sizeMetricColumnTitle));
            log.info(              "+--------------------+--------+--------+--------+----------+--------------------+--------+--------+--------+");
        }

        long current = timer.getTime(TimeUnit.SECONDS);
        long last = lastLog.get();
        boolean lastDocument = (this.documentCountFailure.get() + this.documentCountSuccess.get()) == this.documentCount;
        if (lastDocument || (current >= last + 5 && lastLog.compareAndSet(last, current)))
        {
            int failure = this.documentCountFailure.get();
            int success = this.documentCountSuccess.get();
            long speed = (long) ((double)success / ((double)current / 60.0));
            log.info(String.format("|%-20s|%s|%,8d|%,8d|%,10d|%,20d|%,8d|%,8d|%,8d|",
                    this.mode,
                    formatInterval(current),
                    success,
                    failure,
                    speed,
                    this.sizeMetric.get(),
                    this.execTime.getAverage().longValue(),
                    this.execTime.getMax().longValue(),
                    this.execTime.getMin().longValue()));
        }
    }


    public void pushExecTimeMillis(long millis)
    {
        this.execTime.push(millis);
    }


    private static String formatInterval(final long l)
    {
        final long hr = TimeUnit.SECONDS.toHours(l);
        final long min = TimeUnit.SECONDS.toMinutes(l - TimeUnit.HOURS.toSeconds(hr));
        final long sec = TimeUnit.SECONDS.toSeconds(l - TimeUnit.HOURS.toSeconds(hr) - TimeUnit.MINUTES.toSeconds(min));
        //final long ms = TimeUnit.MILLISECONDS.toMillis(l - TimeUnit.HOURS.toMillis(hr) - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
        return String.format("%02d:%02d:%02d", hr, min, sec);
    }

    public long getDuration()
    {
        return this.timer.getTime(TimeUnit.MILLISECONDS);
    }
}
