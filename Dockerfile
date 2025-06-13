FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# Install necessary build tools
RUN apk add --no-cache maven

# Copy the Maven wrapper files
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# Copy the source code
COPY src/ ./src/

# Build the application
RUN ./mvnw clean package -DskipTests

# Run the application
CMD ["java", "-jar", "target/SarankarDeveloperWebsite-0.0.1-SNAPSHOT.jar"] 