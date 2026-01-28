# Etapa 1: Build (Compilação)
FROM maven:3.9.6-amazoncorretto-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Etapa 2: Runtime (Execução)
FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Porta do Faturamento Service
EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]