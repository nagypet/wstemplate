KEY_FILE=./key.txt
CERT_FILE=./cert.txt
CNAME=wstemplate.k8s-test.perit.hu
ORG_NAME=perit.hu
HOST1=auth-service.wstemplate.k8s-test.perit.hu
HOST2=scalable-service.wstemplate.k8s-test.perit.hu
openssl req -x509 -nodes -days 3650 -newkey rsa:2048 -keyout ${KEY_FILE} -out ${CERT_FILE} -subj "/CN=${CNAME}/O=${ORG_NAME}" -addext "subjectAltName = DNS:${HOST1},DNS:${HOST2}"
