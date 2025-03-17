FROM bellsoft/liberica-openjdk-alpine:21
VOLUME /tmp

RUN apk add mc --repository=http://dl-cdn.alpinelinux.org/alpine/edge/community
RUN apk add --no-cache tzdata

# setting image timezone to Europe/Budapest
ENV TZ=Europe/Budapest
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app/auth-service

EXPOSE 8410

COPY ./dependencies ./snapshot-dependencies* ./lib/
COPY ./bin ./bin
COPY ./application ./lib

WORKDIR /app/auth-service/bin

CMD ["sh", "./template-auth-service"]
