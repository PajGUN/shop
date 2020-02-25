FROM openjdk:latest
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY shop-email-informer-0.0.1-SNAPSHOT.jar /usr/src/app
ENTRYPOINT ["java","-jar","shop-email-informer-0.0.1-SNAPSHOT.jar"]