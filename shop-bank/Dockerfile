FROM openjdk:latest
EXPOSE 8100
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY shop-bank-0.0.1-SNAPSHOT.jar /usr/src/app
ENTRYPOINT ["java","-jar","shop-bank-0.0.1-SNAPSHOT.jar"]