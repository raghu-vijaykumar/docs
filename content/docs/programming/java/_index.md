+++
title= "Java"
tags = [ "java" ]
author = "Me"
showToc = true
TocOpen = false
draft = false
hidemeta = false
comments = false
disableShare = false
disableHLJS = false
hideSummary = false
searchHidden = true
ShowReadingTime = true
ShowBreadCrumbs = true
ShowPostNavLinks = true
ShowWordCount = true
ShowRssButtonInSectionTermList = true
UseHugoToc = true
weight= 1
bookCollapseSection= true
+++

# Java

## Building Executable Jar

### Using Maven

To build a JAR with dependencies in Maven, you can use the maven-assembly-plugin or maven-shade-plugin. Here's how to configure both:

#### Option 1: Using maven-shade-plugin
The shade plugin bundles all dependencies and relocates them to avoid classpath conflicts.

pom.xml Configuration:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-shade-plugin</artifactId>
            <version>3.3.0</version>
            <executions>
                <execution>
                    <phase>package</phase>
                    <goals>
                        <goal>shade</goal>
                    </goals>
                    <configuration>
                        <transformers>
                            <!-- This transformer adds the Main-Class to the manifest -->
                            <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                <mainClass>com.example.Main</mainClass>
                            </transformer>
                        </transformers>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

Commands to Build:

```bash
mvn clean package
```

#### Option 2: Using maven-assembly-plugin
This plugin also allows bundling dependencies into a single JAR, but it doesn't relocate them.

pom.xml Configuration:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-assembly-plugin</artifactId>
            <version>3.3.0</version>
            <executions>
                <execution>
                    <id>make-assembly</id>
                    <phase>package</phase>
                    <goals>
                        <goal>single</goal>
                    </goals>
                    <configuration>
                        <descriptors>
                            <descriptor>src/main/assembly/descriptor.xml</descriptor>
                        </descriptors>
                        <archive>
                            <manifest>
                                <mainClass>com.example.Main</mainClass>
                            </manifest>
                        </archive>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```
Example descriptor.xml:

```xml
<assembly>
    <id>jar-with-dependencies</id>
    <formats>
        <format>jar</format>
    </formats>
    <dependencySets>
        <dependencySet>
            <outputDirectory>/</outputDirectory>
            <useTransitiveDependencies>true</useTransitiveDependencies>
            <unpack>true</unpack>
        </dependencySet>
    </dependencySets>
</assembly>
```

Commands to Build:

```bash
mvn clean package
```

### Using Gradle
To build a JAR with dependencies in Gradle, you can use the shadow plugin or a custom task.

#### Option 1: Using shadow Plugin
The shadow plugin provides an easy way to bundle all dependencies into a single executable JAR.

build.gradle Configuration:

```groovy
plugins {
    id 'java'
    id 'com.github.johnrengelman.shadow' version '7.1.2'
}

group = 'com.example'
version = '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.apache.commons:commons-lang3:3.12.0'
    // Add your other dependencies here
}

tasks.withType(JavaCompile) {
    options.encoding = 'UTF-8'
}

shadowJar {
    archiveClassifier.set('')
    manifest {
        attributes 'Main-Class': 'com.example.Main'
    }
}

jar {
    enabled = false // Disable default JAR task as we are using shadowJar
}
```

Commands to Build:

```bash
./gradlew shadowJar
```
The JAR will be generated in the build/libs/ directory.

#### Option 2: Custom JAR Task
You can also create a custom task to package the dependencies inside the JAR.

build.gradle Configuration:

```groovy
plugins {
    id 'java'
}

group = 'com.example'
version = '1.0-SNAPSHOT'

repositories {
    mavenCentral()
}

dependencies {
    implementation 'org.apache.commons:commons-lang3:3.12.0'
    // Add your other dependencies here
}

jar {
    manifest {
        attributes(
            'Main-Class': 'com.example.Main'
        )
    }
    from {
        configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) }
    }
}
```
Commands to Build:

```bash
./gradlew clean build
```
The JAR will be generated in the build/libs/ directory.
