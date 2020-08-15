FROM grafana/grafana:6.7.3

EXPOSE 3000

ENV GF_SECURITY_ADMIN_USER=admin
ENV GF_SECURITY_ADMIN_PASSWORD=admin
ENV GF_USERS_ALLOW_SIGN_UP=false

ADD ./dashboards /etc/grafana/provisioning/dashboards
ADD ./datasources /etc/grafana/provisioning/datasources