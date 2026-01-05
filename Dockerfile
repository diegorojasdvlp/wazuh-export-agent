# Stage 1: Build con JDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar pom y recuperar dependencias
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar source y build
# Saltar tests, ya que se realizan antes en el pipeline (**confirmar)
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime con JRE 17 (más ligero y seguro que el JDK completo)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Correr como usuario no raiz (llamado spring)
# Ayuda en seguridad
# No se si realizarlo aqui (**confirmar)
RUN groupadd -r spring && useradd -r -g spring spring
USER spring:spring

# Copiar el jar de la etapa de build
COPY --from=build --chown=spring:spring /app/target/*.jar app.jar

# Cambiar a variable ENV para el puerto si es necesario
EXPOSE 8081

# Flags para la maquina virtual (lenguaje, contenedor, uso de ram, nombres)
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]