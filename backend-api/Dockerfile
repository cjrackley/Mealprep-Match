# Build stage
FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

COPY . .

RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar Mealprep_Match.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "Mealprep_Match.jar"]
