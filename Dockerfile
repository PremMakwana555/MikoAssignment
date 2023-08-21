FROM openjdk:17
LABEL authors="premvmakwana"
WORKDIR /home
COPY build/libs/assignment-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]