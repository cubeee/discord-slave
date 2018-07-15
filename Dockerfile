FROM gradle:jdk8-alpine AS BUILD_IMAGE
ENV APP_HOME=/home/gradle/project/
RUN mkdir -p $APP_HOME/src
WORKDIR $APP_HOME
COPY build.gradle gradlew $APP_HOME
COPY gradle $APP_HOME/gradle
COPY src $APP_HOME/src
RUN gradle jar

FROM openjdk:8-jre-alpine
WORKDIR /discord-slave/
COPY --from=BUILD_IMAGE /home/gradle/project/build/libs/discord-slave.jar .
CMD ["java", "-jar", "discord-slave.jar"]