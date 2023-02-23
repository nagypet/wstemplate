#!/bin/bash

copyTo() {
  cp ./gradle.properties ./$1
}

copyTo performance-tester
copyTo prometheus
copyTo grafana
copyTo template-auth-api
copyTo template-auth-service
copyTo template-eureka
copyTo template-gateway
copyTo template-scalable-service
