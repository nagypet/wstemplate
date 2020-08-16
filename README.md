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

## Architecture

There are 4 main components of the system:
- template-auth-service: an authorization and user management service with Jwt authentication
- template-eureka: service discovery, API gateway and load balancer
- template-scalable-service: 3 instances of the service is installed to achieve scalability and high availability. The services are running within a single private docker network. In a real-world scenario we would use separated docker hosts with an overlay network, but this is only a small change in the docker-compose.yml file.
- performance-tester: to generate a simulated load for the system

## Build and run
- get the sources

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

### <a name="custom-rest-error-response"></a> #know-how:custom-rest-error-response
I am a big fan of propagating as many information in an exception thrown on the server side as possible. It would be great if we could catch exceptions on the client side in the same way as on the server side. There are two major problems with is:
- Exceptions cannot be deserialized from Json
- On the client side exception classes might not be known. As an example, imagine, the server throws a SqlServerException, but we do not include any Sql-Server dependency on the client side, still we want to ba able to catch SqlServerException in the client.

To overcome those two issues I have implemented `ServerExceptionProperties`. This pease of information will we sent over webservice boundaries; it contains each information of the original exception and therefore can be turned back to the proper exception on the client side. If the original exception class is not available on the client side, an instance of `ServerException` will be thrown. If you only want to log the exception, our `ServerException` behaves like the original exception. The method `toString()` provides the very same output as the original exception.

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
