FROM openjdk:9-jdk-slim-sid

# Add non-free support to apt
RUN sed -i -r 1s/"^(.+)$"/"\1 non-free"/g /etc/apt/sources.list

# Update ubuntu sources
RUN apt-get update

# On Debian "sid", libfdk-aac1 maps to fdk-aac 0.1.5.
RUN apt-get install libfdk-aac1 libmediainfo0v5

# Add local repository content
ADD . /app

# Set the working directory to allow proper gradlew execution
WORKDIR '/app'

# Set the Gradle wrapper as the entry point
ENTRYPOINT ["./gradlew"]
