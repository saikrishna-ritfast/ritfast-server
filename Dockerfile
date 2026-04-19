# Stage 1: Build the applications
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Copy all the module folders
COPY common /app/common
COPY security /app/security
COPY processor /app/processor

# First, install the saas-common module as it's a dependency
WORKDIR /app/common
RUN mvn clean install -DskipTests

# Next, install the saas-security module as it's a dependency
WORKDIR /app/security
RUN mvn clean install -DskipTests

# Finally, build the saas-processor module which is the main application
WORKDIR /app/processor
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image using a lighter JRE
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy the built executable JAR from the processor module's target folder
COPY --from=build /app/processor/target/*.jar app.jar

# Set the PORT environment variable to a default, Render will override this
ENV PORT=8080
EXPOSE $PORT

# Command to run the application, binding to the Render-provided PORT
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]
