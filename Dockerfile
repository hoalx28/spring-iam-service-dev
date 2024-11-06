FROM maven:3.9.9-amazoncorretto-17-alpine AS BUILD
WORKDIR /iam
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM amazoncorretto:17-alpine
WORKDIR /iam
COPY --from=BUILD /iam/target/*.jar iam.jar
COPY .env .env
ENTRYPOINT ["java", "-jar", "iam.jar"]