FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /workspace
COPY . .
RUN chmod +x ./mvnw && ./mvnw -pl services/identity -am package -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app
COPY --from=build /workspace/services/identity/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
