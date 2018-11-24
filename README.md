# jna-aac-encoder

[![Build Status](https://travis-ci.org/sheinbergon/jna-aac-encoder.svg?branch=master)](https://travis-ci.org/sheinbergon/jna-aac-encoder) [![Coverage Status](https://coveralls.io/repos/github/sheinbergon/jna-aac-encoder/badge.svg)](https://coveralls.io/github/sheinbergon/jna-aac-encoder) [![License](https://img.shields.io/badge/License-LGPL%20v3-blue.svg)](https://www.gnu.org/licenses/lgpl-3.0) 
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.sheinbergon/jna-aac-encoder/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.sheinbergon/jna-aac-encoder)
![GitHub release](https://img.shields.io/github/release/sheinbergon/jna-aac-encoder.svg)

                                                                                                                                                                                                                                                                                                 
This library provides AAC encoding capabilities for the JVM. 
It utilizes the [FDK AAC](https://github.com/mstorsjo/fdk-aac) library via JNA in order to do so.

## License
**Important!** While this library uses LGPL-3, please see
the [FDK AAC license](NOTICE) for additional information
regarding re/distribution and licensing limitations.

## Usage

### Dependencies
Artifacts are available on maven central:

**_Maven_**
```xml
<dependency>
    <groupId>org.sheinbergon</groupId>
    <artifactId>jna-aac-encoder</artifactId>
    <version>0.1.4</version>
</dependency>
```

**_Gradle_**
```groovy
compile 'org.sheinbergon:jna-aac-encoder:0.1.4'
```
For backwards compatibility, a JDK 9 version is also provided (shared library not included):

```groovy
compile 'org.sheinbergon:jna-aac-encoder:0.1.4:jdk9'
```


#### Notice!!!
The **_libfdk-aac_** shared library so/dll/dylib file is required to be accessible
for dynamic loading upon execution. If using the above depdencey, you
need to make sure the library is installed as part of the runtime OS enviroment
and made accessible to JNA. See [this](https://github.com/java-native-access/jna/blob/master/www/FrequentlyAskedQuestions.md#calling-nativeloadlibrary-causes-an-unsatisfiedlinkerror) link for additional information

To make things easier, cross-compiled artifacts (containing the shared library) are provided through the use of *_classifiers_* (built for JDK 11):

| Platform         | Gradle dependency                                    |
|------------------|------------------------------------------------------|
| Windows (64 bit) | `org.sheinbergon:jna-aac-encoder:0.1.4:win32-x86-64` |
| Windows (32 bit) | `org.sheinbergon:jna-aac-encoder:0.1.4:win32-xi386`  |
| Linux (64 bit)   | `org.sheinbergon:jna-aac-encoder:0.1.4:linux-x86-64` |
| OSX 64 (bit)     | `org.sheinbergon:jna-aac-encoder:0.1.4:osx-x86-64`   |

#### Additional information
* Provided fdk-aac version is 0.1.6
* Both versions 0.1.5 and 0.1.6 were tested and found to comply with this bridge.
 
### Encoding using the JVM AudioSystem
```java
AudioInputStream input = AudioSystem.getAudioInputStream(...);
File output = new File(...);
AudioSystem.write(input, AACFileTypes.AAC_LC, output);
```

## Performance
Performance benchmarks comparing JNA to a BINARY application(`aac-enc`) are available using [JMH](http://openjdk.java.net/projects/code-tools/jmh/) and [JMH Visualizer](https://github.com/jzillmann/jmh-visualizer):

![alt text](perf/jmh-results-23112018.png)

To run the benchmarks locally:
* Clone this repository onto a Linux host
* Ensure that you have `libfdk-aac.so` library installed (either from an external repository or manually compiled)
* Ensure that you have the `aac-enc` binary installed (either from an external repository or manually compiled)
* Run the following command (from within the cloned repository)
```groovy
./gradlew -b perf.gradle jmh jmhReport
```
* If the aac-enc binary is not installed in /usr/bin/aac-enc, you can a custom path path by adding this gradle property:
```groovy
-PaacEncBin=/CUSTOM/PATH/TO/AAC-ENC 
```
* The JMH reports can be viewed by opening `build/reports/perf/index.html` in your browser.

## Limitations
Currently, **_libfdk-aac_ itself** supports only the pcm_s16le WAV input format, meaning:
* Sample size - 16 bit(signed)
* WAV format - (L)PCM
* Byte order - Little Endian

While this form of input formatting seems to be the common raw-audio formatting, it's important
to note that providing input audio with different formatting will cause
the encoding process to fail. 

Additional restrictions:
* A maximum of 6 audio input/output channels
* Only the AAC-LC/HE-AAC/HE-AACv2 encoding profiles are supported  

## Roadmap
* Upgrade to fdk-aac 2.0.0
* Improved lower-level interface (with examples).
* Support for 24 bit WAV input (via conversion).
* M4A encoding.
* AAC decoding ???
