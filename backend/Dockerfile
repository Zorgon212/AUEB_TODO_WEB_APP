# ---- build stage ----
FROM eclipse-temurin:26-jdk AS build
WORKDIR /app

# copy wrapper + pom first so dependency downloads are cached as their own layer
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

COPY src ./src
RUN ./mvnw -B package -DskipTests

# ---- run stage ----
FROM eclipse-temurin:26-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
