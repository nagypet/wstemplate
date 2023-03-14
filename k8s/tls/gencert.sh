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

KEY_FILE=./key.txt
CERT_FILE=./cert.txt
CNAME=wstemplate.k8s-test.perit.local
ORG_NAME=perit.hu
HOST1=auth-service.wstemplate.k8s-test.perit.local
HOST2=scalable-service.wstemplate.k8s-test.perit.local
openssl req -x509 -nodes -days 3650 -newkey rsa:2048 -keyout ${KEY_FILE} -out ${CERT_FILE} -subj "/CN=${CNAME}/O=${ORG_NAME}" -addext "subjectAltName = DNS:${HOST1},DNS:${HOST2}"
