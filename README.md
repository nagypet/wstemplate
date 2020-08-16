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

## KNOW-HOW tags:
Find the tags in the source code to see how it was made.

| Tag | Description |
| ------ | ------ |
| #know-how:access-spring-managed-beans-from-outside | How can we access beans managed within the application context from outside of the context |
| #know-how:custom-authentication-provider |  |
| #know-how:simple-httpsecurity-builder | The WebSecurityConfigurerAdapter is a real pain in the ... We have SimpleHttpSecurityBuilder to simplify the security configuration. |
| #know-how:custom-rest-error-response | In case of any exception in the server side we want to provide a useful HTTP body in Json form. This can be converted back to an Exception on the client side. |
| #know-how:hibernate-configuration | How to configure hibernate in the most flexible way? |
| #know-how:jpa-auditing | How to configure custom auditing features to track creation/modification of entities? |
