@echo off
set KEY_FILE=key.txt
set CERT_FILE=cert.txt
set HOST1=auth-service.wstemplate.k8s-test.perit.hu
set HOST2=scalable-service.wstemplate.k8s-test.perit.hu
openssl req -x509 -nodes -days 3650 -newkey rsa:2048 -keyout %KEY_FILE% -out %CERT_FILE% -subj "/CN=%HOST1%/O=%HOST1%" -addext "subjectAltName=DNS:%HOST1%,DNS:%HOST2%"