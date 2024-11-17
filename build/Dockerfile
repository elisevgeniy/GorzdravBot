FROM maven:latest AS builder

WORKDIR /build
COPY ../.mvn .mvn
COPY ../pom.xml ./
RUN mvn dependency:go-offline
COPY ../src ./src
RUN mvn clean install -Dmaven.test.skip=true

FROM openjdk:17.0.2-slim-bullseye AS run

WORKDIR /app

COPY --from=builder /build/target/*.jar /app/application.jar

ENTRYPOINT ["java","-jar","/app/application.jar"]
