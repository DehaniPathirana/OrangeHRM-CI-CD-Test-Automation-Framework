FROM maven:3.9.5-openjdk-17-slim

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Install dependencies and compile
RUN mvn clean compile test-compile

# Set environment variables
ENV MAVEN_OPTS="-Xmx1024m"

# Command to run tests
CMD ["mvn", "test", "-Dsurefire.suiteXmlFiles=src/test/resources/testng/testng.xml"]