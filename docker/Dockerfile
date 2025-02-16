FROM bellsoft/liberica-runtime-container:jre-21-slim-glibc

WORKDIR /app

COPY target/*.jar /app/application.jar

ENTRYPOINT ["java","-jar","/app/application.jar"]

