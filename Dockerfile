FROM openjdk:23-jdk-oracle AS builder

ARG COMPILED_DIR=/compiledir

WORKDIR ${COMPILED_DIR}

COPY src src
COPY .mvn .mvn
COPY pom.xml .
COPY mvnw .
COPY src/main/resources/movies_post_2010.zip /data/movies_post_2010.zip

RUN chmod +x mvnw
RUN ./mvnw package -Dmaven.test.skip=true

ENV SERVER_PORT=8080
ENV SPRING_DATA_MONGODB_URI=""
ENV SPRING_DATA_MONGODB_DATABASE=bedandbreakfast
ENV SPRING_DATASOURCE_USERNAME=local
ENV SPRING_DATASOURCE_PASSWORD=password
ENV SPRING_DATASOURCE_URL=url
ENV MOVIES_POST_ZIP=movies_post_2010.zip

EXPOSE ${SERVER_PORT}

#second stage
FROM openjdk:23-jdk-oracle

ARG WORK_DIR=/app

WORKDIR ${WORK_DIR}

COPY --from=builder /compiledir/target/movies-0.0.1-SNAPSHOT.jar app.jar

ENV SERVER_PORT=8080
ENV SPRING_DATA_MONGODB_URI=""
ENV SPRING_DATA_MONGODB_DATABASE=bedandbreakfast
ENV SPRING_DATASOURCE_USERNAME=local
ENV SPRING_DATASOURCE_PASSWORD=password
ENV SPRING_DATASOURCE_URL=url
ENV MOVIES_POST_ZIP=movies_post_2010.zip

EXPOSE ${SERVER_PORT}

ENTRYPOINT java -jar app.jar