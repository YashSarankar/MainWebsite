# Use Eclipse Temurin JDK 17 as the base image
FROM eclipse-temurin:17-jdk-alpine AS builder

# Set working directory
WORKDIR /build

# Set JAVA_HOME explicitly
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

# Install necessary build tools
RUN apk add --no-cache maven

# Copy Maven wrapper and pom.xml first (for better layer caching)
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Download dependencies (this layer will be cached if pom.xml doesn't change)
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src/ ./src/

# Build the application
RUN ./mvnw clean package -DskipTests

# Use a smaller runtime image
FROM eclipse-temurin:17-jre-alpine

# Set JAVA_HOME in the runtime image as well
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

WORKDIR /app

# Copy the built artifact from the builder stage
COPY --from=builder /build/target/SarankarDeveloperWebsite-0.0.1-SNAPSHOT.jar ./app.jar

# Create a non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Expose the port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 