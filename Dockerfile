# m1 이라면 openjdk:17-jdk-alpine 대신 openjdk:17 사용
FROM openjdk:17
ARG JAR_FILE=build/libs/app.jar
COPY ${JAR_FILE} app.jar
EXPOSE 1234
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -Dspring.profiles.active=prod -jar /app.jar"]