FROM alpine:3.12
VOLUME /tmp

RUN apk add mc --repository=http://dl-cdn.alpinelinux.org/alpine/edge/community
RUN apk add openjdk11 --repository=http://dl-cdn.alpinelinux.org/alpine/edge/community
RUN apk add --no-cache tzdata

# setting image timezone to Europe/Budapest
ENV TZ Europe/Budapest
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /usr/src/gateway

EXPOSE 8500

COPY ./dependencies ./snapshot-dependencies* ./lib
COPY ./bin ./bin
COPY ./application ./lib

WORKDIR /usr/src/gateway/bin

CMD ["sh", "./template-gateway"]
