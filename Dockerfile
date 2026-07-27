# Utilisation d'une image légère avec Java 17
# Remplace la version 17 par la version 21
FROM eclipse-temurin:21-jre-alpine

  # Création du répertoire de travail dans le conteneur
WORKDIR /app

#RUN mvn clean package

  # Copie du fichier JAR exécutable
  # Remarque : si tu utilises Gradle, le chemin sera plutôt COPY build/libs/*.jar app.jar
COPY target/*.jar app.jar

  # Exposition du port par défaut de Spring Boot
EXPOSE 8080

  # Commande pour démarrer l'application
ENTRYPOINT ["java", "-jar", "app.jar"]