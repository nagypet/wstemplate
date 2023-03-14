#!/bin/bash

#
# Copyright 2020-2023 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

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
