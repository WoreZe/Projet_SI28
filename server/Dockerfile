FROM openjdk:23-slim as builder
WORKDIR /app
COPY . .
# build app
RUN ./mvnw clean package

FROM openjdk:23-slim as runtime

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

CMD ["java", "-jar", "app.jar", "--spring.profiles.active=docker"]