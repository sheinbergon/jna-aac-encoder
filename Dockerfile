FROM openjdk:9-jdk-slim-sid

ENV FDK_AAC_VERSION=0.1.6 \
    APPLICATION_DIRECTORY=/opt/jna-aac-encoder \
    SOURCE_ARCHIVE=/tmp/fdk-aac.tar.gz \
    SOURCE_DIRECTORY=/tmp/fdk-aac \
    LIBRARY_DIRECTORY=/opt/fdk-aac

# Add non-free support to apt
RUN sed -i -r 1s/"^(.+)$"/"\1 non-free"/g /etc/apt/sources.list

# Update ubuntu sources
RUN apt-get update

# Download fdk-aac source code
ADD https://github.com/mstorsjo/fdk-aac/archive/v${FDK_AAC_VERSION}.tar.gz ${SOURCE_ARCHIVE}

# Unpack archive
RUN mkdir ${SOURCE_DIRECTORY} && \
    tar --strip-components=1 -xvzf ${SOURCE_ARCHIVE} -C ${SOURCE_DIRECTORY}/

# Install compilation dependencies
RUN apt-get -y install build-essential autoconf libtool bash vim

# Install test essentials
RUN apt-get -y install libmediainfo0v5

# Build library locally
RUN cd ${SOURCE_DIRECTORY} && \
    ls -l && \
    ./autogen.sh && \
    ./configure --prefix=${LIBRARY_DIRECTORY} && \
    make -j 8 && \
    make install

# Add local repository content
ADD . ${APPLICATION_DIRECTORY}

# Set the working directory to allow proper gradlew execution
WORKDIR ${APPLICATION_DIRECTORY}

# Set the Gradle wrapper as the entry point
ENTRYPOINT ./gradlew -PfdkAACLibraryPath=${LIBRARY_DIRECTORY}/lib $@