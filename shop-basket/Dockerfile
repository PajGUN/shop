FROM openjdk:latest
EXPOSE 8090
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY shop-basket-0.0.1-SNAPSHOT.jar /usr/src/app
ENTRYPOINT ["java","-jar","shop-basket-0.0.1-SNAPSHOT.jar"]