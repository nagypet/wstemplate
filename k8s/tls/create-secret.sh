CERT_NAME=templatekey
KEY_FILE=./key.txt
CERT_FILE=./cert.txt
kubectl create secret tls ${CERT_NAME} --key ${KEY_FILE} --cert ${CERT_FILE} -n wstemplate