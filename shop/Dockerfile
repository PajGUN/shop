FROM openjdk:latest
EXPOSE 8080
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY shop-0.0.1-SNAPSHOT.jar /usr/src/app
ENTRYPOINT ["java","-jar","shop-0.0.1-SNAPSHOT.jar"]