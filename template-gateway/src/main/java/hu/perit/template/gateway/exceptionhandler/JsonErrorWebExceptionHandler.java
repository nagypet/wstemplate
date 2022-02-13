package hu.perit.template.gateway.exceptionhandler;

import hu.perit.spvitamin.core.StackTracer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.*;

import java.util.HashMap;
import java.util.Map;

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
        // Here the logic can actually be customized according to the exception type
        Throwable exception = super.getError(request);
        Map<String, Object> errorAttributes = new HashMap<>();
        errorAttributes.put("message", exception.getMessage());
        errorAttributes.put("status", HttpStatus.SERVICE_UNAVAILABLE.value());
        errorAttributes.put("method", request.methodName());
        errorAttributes.put("path", request.path());
        return errorAttributes;
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
        log.warn(StackTracer.toString(throwable));
    }
}
