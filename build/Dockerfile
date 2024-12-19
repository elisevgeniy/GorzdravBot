FROM maven:latest AS builder

WORKDIR /build
COPY ../pom.xml ./
COPY ../src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean install

FROM openjdk:21-jdk AS run

WORKDIR /app

COPY --from=builder /build/target/*.jar /app/application.jar

ENTRYPOINT ["java","-jar","/app/application.jar"]
