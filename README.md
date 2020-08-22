# wstemplate

This project implements a small, but _complete_ solution using the Spring framework and a microservice architecture for demonstration purposes. What makes this solution complete?
- scalability and high availability with redundant services
- https communication
- secured webservice endpoints
- unit tests
- integration tests
- Swagger online documentation and test UI
- sonarqube
- Eureka, Zuul, Ribbon, Hystrix with fault tolerance
- components running in docker containers
- monitoring with Prometheus/Grafana using cAdvisor and Node-exporter

The project can be used as a basis for POC projects as a fully dockerized microservice environment.

## Architecture
![architecture](https://github.com/nagypet/wstemplate/blob/master/docs/images/wstemplate_architecture.jpg)

There are 4 main components of the system:
- template-auth-service: an authorization and user management service with Jwt authentication
- template-eureka: service discovery, API gateway and load balancer
- template-scalable-service: 3 instances of the service is installed to achieve scalability and high availability. The services are running within a single private docker network. In a real-world scenario we would use separated docker hosts with an overlay network, but this is only a small change in the docker-compose.yml file.
- performance-tester: to generate a simulated load for the system

## Build and run
### Prerequisits
Install AdoptOpenJDK 11
```
wget -qO - https://adoptopenjdk.jfrog.io/adoptopenjdk/api/gpg/key/public | sudo apt-key add -
sudo add-apt-repository --yes https://adoptopenjdk.jfrog.io/adoptopenjdk/deb/
sudo apt install adoptopenjdk-11-hotspot
```
- Get the sources. Please note that the spvitamin library is stored in a git submodule, so you have to update this in a separate step:
```
git clone https://github.com/nagypet/wstemplate.git
cd wstemplate
git submodule update --init
```

### Frontend
- Install Node.js
```
npm install -g @angular/cli
npm update
build.bat
```
Maybe you will need to update your local ng CLI:
```
npm install --save-dev @angular/cli@latest
```

### Backends
- Configure your AD parameter in template-auth-service\src\main\dist\bin\config\application-dev.properties or set ldaps.ad1.enabled=false if you do not have access to an AD.
- cd to the root folder and
```
gradlew dist dockerImage --parallel
```
- then cd to docker-compose\dev\ and
```
coU postgres pgadmin
```
When the containers are up and running, go to http://localhost:5400 for pgadmin. 
- login with postgres/sa
- create a new connection. Parameters: host: postgres, port: 5432, username: postgres, password: sa
- create a new database with the name 'testdb'. 
- run the script in db\scripts.sql
- start the remaining services with `coU --all`

Start the performance-tester in a command-line. The executables are located in performance-tester\build\install\performance-tester\bin\.

## Monitoring
Go to http://localhost:3000 for Grafana. Login with admin/admin.
![image](https://github.com/nagypet/wstemplate/blob/master/docs/images/grafana.jpg)

## Know-how tags:
Find the tags in the source code to see how it was made.

| Tag | Description |
| ------ | ------ |
| #know-how:access-spring-managed-beans-from-outside | How can we access beans managed within the application context from outside of the context |
| #know-how:custom-authentication-provider |  |
| #know-how:simple-httpsecurity-builder | The WebSecurityConfigurerAdapter is a real pain in the ... We have SimpleHttpSecurityBuilder to simplify the security configuration. |
| [#know-how:custom-rest-error-response](#custom-rest-error-response) | In case of any exception in the server side we want to provide a useful HTTP body in Json form. This can be converted back to an Exception on the client side. |
| #know-how:hibernate-configuration | How to configure hibernate in the most flexible way? |
| #know-how:jpa-auditing | How to configure custom auditing features to track creation/modification of entities? |
| [#know-how:disable-ssl-certificate-validation](#disable-ssl-certificate-validation) | How to completely disable SSL certificate validation in the development environment? |
| [#know-how:custom-zuul-error-filter](#custom-zuul-error-filter) | Custom Zuul error filter |
| [#know-how:gc-timer](#gc-timer) | Forced garbage collection |

### <a name="custom-rest-error-response"></a> #know-how:custom-rest-error-response
I am a big fan of propagating as much information as possible in an exception thrown on the server side. It would be great if we could catch exceptions on the client side in the same way as on the server side. There are two major problems with is:
- Exceptions cannot be deserialized from Json
- On the client side exception classes might not be known. As an example, imagine, the server throws a SqlServerException, but we do not include any Sql-Server dependency on the client side, still we want to be able to catch SqlServerException in the client.

To overcome those two issues I have implemented `ServerExceptionProperties`. This peace of information will be sent over the webservice boundaries; it contains each information of the original exception and therefore can be turned back to the proper exception on the client side. If the original exception class is not available on the client side, an instance of `ServerException` will be thrown. If you only want to log the exception, our `ServerException` behaves like the original exception. The method `toString()` provides the very same output as the original exception.

```java
public interface ServerExceptionInterface {
    String getClassName();
    boolean instanceOf(Class anExceptionClass);
    boolean instanceOf(String anExceptionClassName);
    List<String> getSuperClassNames();
    Annotation[] getAnnotations();
}
```

There is a wrapper class `ExceptionWrapper` which can be created out of a `Throwable`. No matter if the `Throwable` was a `ServerException` or a regular exception, the wrapper handles both types identically.
```java
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
```
The method `causedBy` is more advanced than `instanceof` because the first returns true if the exception itself is an instance of the parameter, or the parameter is anywhere in the cause chain.

### <a name="disable-ssl-certificate-validation"></a> #know-how:disable-ssl-certificate-validation
In a development environment often we do not have a signed certificate. We generate a self-signed one, just for encryption and do not want a validation. As long as only Feign or RestTemplate is in use for building a HTTP request, we can easily customize both of them to ignore certificate validation. But if many spring cloud services are in use, like Eureka, Ribbon, Zuul, etc., each and every of them have an own way of creating the request. We simply do not have as many time to switch off validation at all the components we have. So we need a general solution which grabs the problem at a lower layer: at the layer of `java.security.Provider`. We can easiliy implement a `NullSecurityProvider`, which points to our own factory methods to instantiate a `NullTrustManager`.
```java
public class NullSecurityProvider extends java.security.Provider {

    public NullSecurityProvider(String name, String versionStr, String info) {
        super(name, versionStr, info);

        put("TrustManagerFactory.PKIX", "hu.perit.spvitamin.spring.security.NullTrustManagerFactory$SimpleFactory");
        put("TrustManagerFactory.SunX509", "hu.perit.spvitamin.spring.security.NullTrustManagerFactory$SimpleFactory");
    }
}
```
For convenience we can implement `NullSecurityProviderConfigurer` and have a config key in our application.properties to easiliy allow or disable certificate validation.
```java
@Component
@Log4j
public class NullSecurityProviderConfigurer {

    private final ServerProperties serverProperties;

    public NullSecurityProviderConfigurer(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @PostConstruct
    void init() {
        if (this.serverProperties.getSsl() != null && this.serverProperties.getSsl().isIgnoreCertificateValidation()) {
            Provider nullSecurityProvider = new NullSecurityProvider("NullSecurityProvider", "1.0", "Skipping SSL certificate validation");
            Security.insertProviderAt(nullSecurityProvider, 1);

            log.warn("NullSecurityProvider installed!");
        }
    }
}
```
### <a name="custom-zuul-error-filter"></a> #know-how:custom-zuul-error-filter
Having Zuul in our classpath we can easily implement an API gateway, to have a single access point for our microservice components. Without any further configuration we only receive the following response from the gateway, in case of an internal failure, which might be completely correct, but a little more information would be nice.
```
{
    "timestamp": "2020-08-16 09:39:58",
    "status": 500,
    "error": "Internal Server Error",
    "message": ""
}
```
Implementing our `CustomZuulErrorFilter` we will have a better response, like this:
```
{
    "timestamp": "2020-08-16 09:32:12.273",
    "status": 500,
    "error": "Internal Server Error",
    "path": "/authenticate",
    "exception": {
        "message": "Filter threw Exception",
        "exceptionClass": "com.netflix.zuul.exception.ZuulException",
        "superClasses": [
            "com.netflix.zuul.exception.ZuulException",
            "java.lang.Exception",
            "java.lang.Throwable",
            "java.lang.Object"
        ],
        "stackTrace": [
            {
                "classLoaderName": "app",
                "moduleName": null,
                "moduleVersion": null,
                "methodName": "processZuulFilter",
                "fileName": "FilterProcessor.java",
                "lineNumber": 227,
                "className": "com.netflix.zuul.FilterProcessor",
                "nativeMethod": false
            }
        ],
        "cause": {
            "message": "com.netflix.zuul.exception.ZuulException: Forwarding error",
            "exceptionClass": "org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException",
            "superClasses": [
                "org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException",
                "java.lang.RuntimeException",
                "java.lang.Exception",
                "java.lang.Throwable",
                "java.lang.Object"
            ],
            "stackTrace": [
                {
                    "classLoaderName": "app",
                    "moduleName": null,
                    "moduleVersion": null,
                    "methodName": "run",
                    "fileName": "RibbonRoutingFilter.java",
                    "lineNumber": 124,
                    "className": "org.springframework.cloud.netflix.zuul.filters.route.RibbonRoutingFilter",
                    "nativeMethod": false
                }
            ],
            "cause": {
                "message": "Forwarding error",
                "exceptionClass": "com.netflix.zuul.exception.ZuulException",
                "superClasses": [
                    "com.netflix.zuul.exception.ZuulException",
                    "java.lang.Exception",
                    "java.lang.Throwable",
                    "java.lang.Object"
                ],
                "stackTrace": [
                    {
                        "classLoaderName": "app",
                        "moduleName": null,
                        "moduleVersion": null,
                        "methodName": "handleException",
                        "fileName": "RibbonRoutingFilter.java",
                        "lineNumber": 198,
                        "className": "org.springframework.cloud.netflix.zuul.filters.route.RibbonRoutingFilter",
                        "nativeMethod": false
                    }
                ],
                "cause": {
                    "message": "Load balancer does not have available server for client: template-auth-service",
                    "exceptionClass": "com.netflix.client.ClientException",
                    "superClasses": [
                        "com.netflix.client.ClientException",
                        "java.lang.Exception",
                        "java.lang.Throwable",
                        "java.lang.Object"
                    ],
                    "stackTrace": [
                        {
                            "classLoaderName": "app",
                            "moduleName": null,
                            "moduleVersion": null,
                            "methodName": "getServerFromLoadBalancer",
                            "fileName": "LoadBalancerContext.java",
                            "lineNumber": 483,
                            "className": "com.netflix.loadbalancer.LoadBalancerContext",
                            "nativeMethod": false
                        }
                    ],
                    "cause": null
                }
            }
        }
    }
}
```
### <a name="gc-timer"></a> #know-how:gc-timer
Calling `System.gc()` is generally not recommended. But my applications have smaller memory footprint when calling gc() periodically. I have setup a scheduled job for calling gc() once in a minute, and see what happened:
| JVM Total | G1 Eden Space |
| ------ | ------ |
| ![image](https://github.com/nagypet/wstemplate/blob/master/docs/images/jvm_total_memory.jpg) | ![image](https://github.com/nagypet/wstemplate/blob/master/docs/images/g1_eden_space.jpg) |
