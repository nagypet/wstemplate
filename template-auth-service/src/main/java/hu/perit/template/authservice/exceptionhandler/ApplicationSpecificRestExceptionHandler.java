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

package hu.perit.template.authservice.exceptionhandler;

import hu.perit.spvitamin.spring.exceptionhandler.DefaultRestExceptionResponseHandler;
import hu.perit.spvitamin.spring.exceptionhandler.RestExceptionResponse;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * @author Peter Nagy
 * #know-how:controller-advice
 */

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class ApplicationSpecificRestExceptionHandler extends DefaultRestExceptionResponseHandler
{
    private final Tracer tracer;

    private String getTraceId()
    {
        final Span span = tracer.currentSpan();
        return span != null
                ? span.context().traceId()
                : null;
    }


    @ExceptionHandler({Exception.class})
    public ResponseEntity<RestExceptionResponse> applicationSpecificExceptionHandler(Exception ex, WebRequest request)
    {
        return super.exceptionHandler(ex, request, getTraceId());
    }

}
