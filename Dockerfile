FROM openjdk:17-jdk-slim

WORKDIR /app

# Copier le pom.xml et télécharger les dépendances
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Copier le code source
COPY src src

# Construire l'application
RUN ./mvnw clean package -DskipTests

# Exposer le port attendu par Render
EXPOSE 8080

# Variables d'environnement par défaut
ENV SPRING_PROFILES_ACTIVE=prod

# Commande de démarrage : utilise le port Render
CMD ["java", "-jar", "target/myCar-API-1.0-SNAPSHOT.jar", "--server.port=${PORT:-8080}"]
