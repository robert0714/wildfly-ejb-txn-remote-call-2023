# BUILD STAGE
FROM ubuntu:14.04  AS builder

WORKDIR /app0

RUN  apt-get update \
  && apt-get install -y wget \
  && wget -O elastic-apm-agent-1.38.0.jar https://repo1.maven.org/maven2/co/elastic/apm/elastic-apm-agent/1.38.0/elastic-apm-agent-1.38.0.jar -P /app0/ \
  && wget -O opentelemetry-javaagent.jar https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.14.0/opentelemetry-javaagent.jar -P /app0/ 


# FINAL STAGE 
# FROM adoptopenjdk/openjdk8:alpine
# FROM adoptopenjdk/openjdk8:x86_64-ubi-jdk8u342-b07
FROM registry.access.redhat.com/ubi8/openjdk-8:latest

COPY ./target/client-bootable.jar /deployments/app.jar
WORKDIR /deployments

COPY --from=builder /app0 .

 
EXPOSE 8081/tcp 8080/tcp 9990/tcp 

ENV TZ=Asia/Taipei
ENV JAVA_OPTS=""

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar  /deployments/app.jar"]