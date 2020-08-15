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

package hu.perit.template.authservice.exceptionhandler;

import hu.perit.spvitamin.core.StackTracer;
import hu.perit.spvitamin.core.exception.ExceptionWrapper;
import hu.perit.spvitamin.spring.exceptionhandler.RestExceptionResponse;
import hu.perit.spvitamin.spring.exceptionhandler.RestResponseEntityExceptionHandler;
import lombok.extern.log4j.Log4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;

/**
 * @author Peter Nagy
 */

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
@Log4j
public class ApplicationSpecificRestExceptionHandler extends RestResponseEntityExceptionHandler
{
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> applicationSpecificExceptionHandler(Exception ex, WebRequest request)
    {
        String path = request != null ? request.getDescription(false) : "";

        ExceptionWrapper exception = ExceptionWrapper.of(ex);

        if (exception.instanceOf(IOException.class))
        {
            log.error(StackTracer.toString(ex));
            RestExceptionResponse exceptionResponse = new RestExceptionResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex, path);
            return new ResponseEntity<>(exceptionResponse, HttpStatus.valueOf(exceptionResponse.getStatus()));
        }
        else
        {
            return super.exceptionHandler(ex, request);
        }
    }


}
