FROM amazoncorretto:17
COPY ./*.jar app.jar
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=${QUIZIT_PROFILE}", "app.jar"]
