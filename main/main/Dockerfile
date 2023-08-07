FROM amazoncorretto:11
#ENV TZ=Europe/Samara
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]