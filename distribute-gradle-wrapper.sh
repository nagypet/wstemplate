#!/bin/bash

copyTo() {
  cp -r ./gradle/wrapper/ ./$1/gradle
  cp ./gradlew.bat ./$1
  cp ./gradlew ./$1
}

copyTo performance-tester
copyTo prometheus
copyTo grafana
copyTo template-auth-api
copyTo template-auth-service
copyTo template-eureka
copyTo template-gateway
copyTo template-scalable-service
