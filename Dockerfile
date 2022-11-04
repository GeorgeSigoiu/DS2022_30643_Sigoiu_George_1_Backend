FROM maven:3.8.6-amazoncorretto-17 AS build
WORKDIR /app
COPY src /app/src
COPY pom.xml /app
RUN mvn clean package

FROM openjdk:17-ea-jdk-oracle
COPY --from=build /app/target/energy-1.0.0-SNAPSHOT.jar /app/app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]