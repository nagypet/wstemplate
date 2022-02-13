package hu.perit.template.gateway.exceptionhandler;

import hu.perit.spvitamin.core.StackTracer;
import hu.perit.spvitamin.core.exception.ServerExceptionProperties;
import hu.perit.spvitamin.spring.exceptionhandler.RestExceptionResponse;
import hu.perit.spvitamin.spring.exceptionhandler.RestExceptionResponseFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Slf4j
public class JsonErrorWebExceptionHandler extends DefaultErrorWebExceptionHandler
{

    public JsonErrorWebExceptionHandler(ErrorAttributes errorAttributes,
                                        WebProperties.Resources resources,
                                        ErrorProperties errorProperties,
                                        ApplicationContext applicationContext)
    {
        super(errorAttributes, resources, errorProperties, applicationContext);
    }


    @Override
    protected Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options)
    {
        Throwable ex = super.getError(request);
        Optional<RestExceptionResponse> optExceptionResponse = RestExceptionResponseFactory.of(ex, request.path());
        Map<String, Object> errorAttributes = new LinkedHashMap<>();
        if (optExceptionResponse.isPresent())
        {
            RestExceptionResponse exceptionResponse = optExceptionResponse.get();

            errorAttributes.put("timestamp", exceptionResponse.getTimestamp());
            errorAttributes.put("status", exceptionResponse.getStatus());
            errorAttributes.put("error", exceptionResponse.getError());
            errorAttributes.put("path", exceptionResponse.getPath());
            if (options.isIncluded(ErrorAttributeOptions.Include.EXCEPTION))
            {
                errorAttributes.put("exception", exceptionResponse.getException());
            }
            else if (options.isIncluded(ErrorAttributeOptions.Include.MESSAGE))
            {
                errorAttributes.put("message", exceptionResponse.getMessage());
            }
            errorAttributes.put("type", exceptionResponse.getType());
            return errorAttributes;
        }

        log.warn(StackTracer.toString(ex));
        MergedAnnotation<ResponseStatus> responseStatusAnnotation = MergedAnnotations
                .from(ex.getClass(), MergedAnnotations.SearchStrategy.TYPE_HIERARCHY).get(ResponseStatus.class);
        HttpStatus errorStatus = determineHttpStatus(ex, responseStatusAnnotation);

        errorAttributes.put("timestamp", new Date());
        errorAttributes.put("status", errorStatus.value());
        errorAttributes.put("error", errorStatus.getReasonPhrase());
        errorAttributes.put("path", request.path());
        if (options.isIncluded(ErrorAttributeOptions.Include.EXCEPTION))
        {
            errorAttributes.put("exception", new ServerExceptionProperties(ex));
        }
        else if (options.isIncluded(ErrorAttributeOptions.Include.MESSAGE))
        {
            errorAttributes.put("message", determineMessage(ex, responseStatusAnnotation));
        }
        return errorAttributes;
    }


    private HttpStatus determineHttpStatus(Throwable error, MergedAnnotation<ResponseStatus> responseStatusAnnotation) {
        if (error instanceof ResponseStatusException) {
            return ((ResponseStatusException) error).getStatus();
        }
        return responseStatusAnnotation.getValue("code", HttpStatus.class).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    private String determineMessage(Throwable error, MergedAnnotation<ResponseStatus> responseStatusAnnotation) {
        if (error instanceof BindingResult) {
            return error.getMessage();
        }
        if (error instanceof ResponseStatusException) {
            return ((ResponseStatusException) error).getReason();
        }
        String reason = responseStatusAnnotation.getValue("reason", String.class).orElse("");
        if (StringUtils.hasText(reason)) {
            return reason;
        }
        return (error.getMessage() != null) ? error.getMessage() : "";
    }


    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes)
    {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }


    @Override
    protected int getHttpStatus(Map<String, Object> errorAttributes)
    {
        return (int) errorAttributes.get("status");
    }

    @Override
    protected void logError(ServerRequest request, ServerResponse response, Throwable throwable)
    {
        //log.warn(StackTracer.toString(throwable));
    }
}
