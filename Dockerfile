FROM openjdk:17-jdk

WORKDIR /app

COPY ./build/libs/creativeyoutuber-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]

