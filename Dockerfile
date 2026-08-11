# Étape 1 : Construction (on utilise Maven avec JDK 21)
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
# Copie du fichier de dépendances pour profiter du cache Docker
COPY pom.xml .
RUN mvn dependency:go-offline
# Copie du code source et compilation
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Exécution (image légère)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# On récupère le .jar construit à l'étape précédente
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]