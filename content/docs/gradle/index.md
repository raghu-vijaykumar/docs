+++
title = "Gradle"
tags = [ "gradle", "build-tool" ]
author = "Me"
date = 2024-07-26T00:01:00+05:30
showToc = true
TocOpen = false
draft = true
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

[cover]
image = "./gradle_logo.png"
alt = "Image"
caption = "Gradle"
relative = false
hidden = false
+++

![Gradle Logo](./gradle_logo.png)

Gradle is an open-source build automation tool that is designed to be flexible enough to build almost any type of software. The following is a high-level overview of some of its most important features:

- Written in Domain Specific Language (Not XML), Groovy or Kotlin DSL

# Quickstart

[Gradle Sample Projects](https://docs.gradle.org/current/samples/index.html)

## Installation

```sh
# Make sure java is installed and JAVA_HOME is set
> java -version
java version "1.8.0_202"
Java(TM) SE Runtime Environment (build 1.8.0_202-b08)
Java HotSpot(TM) 64-Bit Server VM (build 25.202-b08, mixed mode)

# Check Java home using Powershell or echo %JAVA_HOME% in cmd
> $env:JAVA_HOME
C:\Program Files\Java\jdk1.8.0_202
```

[Download](https://gradle.org/releases/) the latest version unpack and add gradle to PATH.

```powershell
$env:Path += ";path-to-gradle/gradle-7.2/bin"

#Verify installation
> gradle -v

------------------------------------------------------------
Gradle 7.1.1
------------------------------------------------------------

Build time:   2021-07-02 12:16:43 UTC
Revision:     774525a055494e0ece39f522ac7ad17498ce032c

Kotlin:       1.4.31
Groovy:       3.0.7
Ant:          Apache Ant(TM) version 1.10.9 compiled on September 27 2020
JVM:          1.8.0_202 (Oracle Corporation 25.202-b08)
OS:           Windows 10 10.0 amd64
```

### Java and Gradle First Support matrix

| Java | Gradle (First Support) |
| ---- | ---------------------- |
| 8    | 2.0                    |
| 9    | 4.3                    |
| 10   | 4.7                    |
| 11   | 5.0                    |
| 12   | 5.4                    |
| 13   | 6.0                    |
| 14   | 6.3                    |
| 15   | 6.7                    |
| 16   | 7.0                    |

## Gradle Build Lifecycle

- Initialization
  - Single project Build: settings.gradle, build executed from project root directory.
  - Multi project Build: settings.gradle is searched within dir or parent dir based on layout. Physical directories should conform to project names.
- Configuration
  - The build script is executed against the project object that was created during the initialization phase.
- Execution
  - depends on task creation, getting the execution graph ready and executing the task.

settings.gradle

```groovy
rootProject.name = 'basic'
println'This is executed during the initialization phase.'
```

build.gradle

```groovy
println'This is executed during the configuration phase.'

tasks.register('configured') {
    println'This is also executed during the configuration phase, because:configured is used in the build.'
}
tasks.register('test') {
    doLast {
        println'This is executed during the execution phase.'
    }
}
tasks.register('testBoth') {
    doFirst {
        println'This is executed first during the execution phase.'
    }
    doLast {
        println'This is executed last during the execution phase.'
    }
    println'This is executed during the configuration phase as well, because:testBoth is used in the build.'
}
```

execution

```sh
> gradle test testBoth
This is executed during the initialization phase.

> Configure project :
This is executed during the configuration phase.
This is executed during the configuration phase as well, because:testBoth is used in the build.

> Task :test
This is executed during the execution phase.

> Task :testBoth
This is executed first during the execution phase.
This is executed last during the execution phase.

BUILD SUCCESSFUL in 7m 17s
2 actionable tasks: 2 executed
```

## Debugging Gradle Builds

Attach a debugger to your build to port 5005.

```sh
gradle help -Dorg.gradle.debug=true
```

## Gradle CLI

Usage: gradle [taskName] [--option-name]

### Tasks

:exclamation: Note: On running a task ignore `project:` if the project only contains one root project. :exclamation:

```sh
# Initialize a gradle project within a directory
> gradle init

# Listing Tasks. Without project ignore --all
> gradle tasks --all

# Executing tasks
> gradle taskName1

## In a multi project build - Note project can be nested you can also go to subdir and just execute the nested project task
> gradle :project1:taskName1 :project2:taskName12

# Excluding a task
> gradle task1 --exclude-task task2

# Force execute all tasks
> gradle task1 --rerun-tasks

# continue a build even when failure occurs, preferably used on multiple tests
> gradle task1 --continue

# Running the application if run task exists and supported
> gradle run

# Running all checks
> gradle check

# Listing out projects
> gradle projects

# Listing dependencies
> gradle project:dependencies

# Listing dependencies of a particular configuration
> gradle project:dependencies --configuration compileOnly

# Listing out build-script dependencies
> gradle project:buildEnvironment

# List all available properties
> gradle project:properties

# List out running gradle instances
> gradle --status

# Stop all running gradle instances
> gradle --stop
```

## Performance Options

- `--build-cache, --no-build-cache`: Toggles the Gradle build cache. Gradle will try to reuse outputs from previous builds. Default is off.
- `--configure-on-demand, --no-configure-on-demand`: Toggles [Configure-on-demand](https://docs.gradle.org/current/userguide/multi_project_configuration_and_execution.html#sec:configuration_on_demand). Only relevant projects are configured in this build run. Default is off.
- `--max-workers`: Sets maximum number of workers that Gradle may use. Default is number of processors.
- `--parallel, --no-parallel`: [Build projects in parallel](https://docs.gradle.org/current/userguide/multi_project_configuration_and_execution.html#sec:parallel_execution). For limitations of this option, Default is off.
- `--priority`: Specifies the scheduling priority for the Gradle daemon and all processes launched by it. Values are normal or low. Default is normal.
- `--profile`: Generates a high-level performance report in the `$buildDir/reports/profile` directory. `--scan` is preferred.

## Build Scans

# Gradle Build Environments

Gradle provides multiple mechanisms for configuring behavior of Gradle itself and specific projects. The following is a reference for using these mechanisms. listed in order of highest to lowest precedence:

- **Command-line** flags such as --build-cache. These have precedence over properties and environment variables. For complete list see `gradle --help`
  - System properties can also be passed using command line using `-DsystemProp.gradle.wrapperUser=myuser`
  - To Specify Gradle properties using cmd line for a project use `-Pprop1=value1,prop2=value2`
- **System properties** such as `systemProp.http.proxyHost=somehost.org` stored in a `gradle.properties` file.
  - In a multi project build, “systemProp.” properties set in any project except the root will be ignored. That is, only the root project’s gradle.properties file will be checked for properties that begin with the “systemProp.” prefix.
- **Gradle properties** such as `org.gradle.caching=true` that are typically stored in a `gradle.properties` file in a project root directory or `GRADLE_USER_HOME` environment variable.
- **Environment variables** such as `GRADLE_OPTS` sourced by the environment that executes Gradle

## Project Properties

## Gradle Properties

:exclamation: Note: Some gradle properties have equivalent command line options need not specify the entire property :exclamation:

> Note: Value in bold is the default value.

| Property                        | Description                                                                                                                                                                                                                                                                             | Values                                                                                |
| ------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------- |
| `org.gradle.caching`            | Gradle will reuse task outputs from any previous build, when possible,resulting is much faster builds                                                                                                                                                                                   | (true,false)                                                                          |
| `org.gradle.caching.debug`      | Individual input property hashes and the build cache key for each task are logged on the console                                                                                                                                                                                        | (true,false)                                                                          |
| `org.gradle.configureondemand`  | Enables incubating configuration on demand, where Gradle will attempt to configure only necessary projects.                                                                                                                                                                             | (true,false)                                                                          |
| `org.gradle.console`            | Customize console output coloring or verbosity. Default depends on how Gradle is invoked.                                                                                                                                                                                               | (**auto**,plain,rich,verbose)                                                         |
| `org.gradle.daemon`             | When set to true the Gradle Daemon is used to run the build.                                                                                                                                                                                                                            | (**true**, false)                                                                     |
| `org.gradle.daemon.idletimeout` | Daemon terminate itself after specified number of milliseconds                                                                                                                                                                                                                          | **10800000**                                                                          |
| `org.gradle.debug`              | Gradle will run the build with remote debugging enabled, listening on port5005. Note that this is the equivalent of adding-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=5005 to the JVM command line and will suspend the virtual machine until a debugger is attached. | (true, **false**)                                                                     |
| `org.gradle.java.home`          | Specifies the Java home for the Gradle build process                                                                                                                                                                                                                                    | Defaults to JAVA_HOME                                                                 |
| `org.gradle.jvmargs`            | Specify JVM Args for Build Server(Daemon)                                                                                                                                                                                                                                               | `mx2g -XX:MaxMetaspaceSize=512m -XX:+HeapDumpOnOutOfMemoryError-Dfile.encoding=UTF-8` |
| `org.gradle.logging.level`      | Various log levels for gradle                                                                                                                                                                                                                                                           | (quiet,warn,**lifecycle**,info,debug)                                                 |
| `org.gradle.parallel`           | Gradle will fork up to `org.gradle.workers.max` JVMs to execute projects in parallel.                                                                                                                                                                                                   | (true,false)                                                                          |
| `org.gradle.priority`           | Specifies the scheduling priority for the Gradle daemon and all processes launched by it                                                                                                                                                                                                | (low, **normal**)                                                                     |
| `org.gradle.vfs.verbose`        | Configures verbose logging when watching the file system.                                                                                                                                                                                                                               | (true, **false**)                                                                     |
| `org.gradle.vfs.watch`          | Toggles watching the file system. When enabled Gradle re-uses information it collects about the file system between builds                                                                                                                                                              | (**true**, false)                                                                     |
| `org.gradle.warning.mode`       | Various warning types.                                                                                                                                                                                                                                                                  | (all,fail,summary,none)                                                               |
| `org.gradle.workers.max`        | When configured, Gradle will use a maximum of the given number of workers                                                                                                                                                                                                               | Number of CPU processors.                                                             |

## System Properties

Setting System Properties

- In `gradle.properties` with prefix `systemProp`
- Using CLI `-D` options

| Property                           | Description                                                                                    | Values                      |
| ---------------------------------- | ---------------------------------------------------------------------------------------------- | --------------------------- |
| `gradle.wrapperUser`               | Specify user name to download Gradle distributions from servers using HTTP BasicAuthentication | username                    |
| `gradle.wrapperPassword`           | Specify password for downloading a Gradle distribution using the Gradle wrapper.               | mypassword                  |
| `gradle.user.home`                 | Specify the Gradle user home directory                                                         | `C:\softwares\gradle-7.1.1` |
| `https.protocols`                  | Specify the supported TLS versions in a comma separated format.                                | TLSv1.2,TLSv1.3             |
| `javax.net.ssl.trustStore`         | Configure JVM Trust Store                                                                      | `path=to-certs/cacerts`     |
| `javax.net.ssl.trustStorePassword` | Configure JVM Trust Store Password                                                             | **`changeit`**              |

[Accessing Web using HTTP Proxy](https://docs.gradle.org/current/userguide/build_environment.html#sec:accessing_the_web_via_a_proxy)

## Environment Variables

Environment variables are available for the gradle command.

> Note: Command-line options and System properties take precedence over environment variables.

| Property         | Description                                                           | Values                                                                              |
| ---------------- | --------------------------------------------------------------------- | ----------------------------------------------------------------------------------- |
| GRADLE_OPTS      | Specifies JVM arguments to use when starting the **Gradle client VM** | `"-Xmx64m -XX:MaxPermSize=64m -XX:+HeapDumpOnOutOfMemoryError-Dfile.encoding=UTF-8` |
| GRADLE_USER_HOME | Specifies the Gradle user home directory                              | `C:\Users\raghu\.gradle`                                                            |
| JAVA_HOME        | Specifies the JDK installation directory to use for the client VM.    | Default to `${org.gradle.java.home}`                                                |

# Dependency Management

## Repositories

Repositories can be of different types like:

- File based repositories
- Local Repositories
- Remote Repositories (Maven or Ivy)

```groovy
repositories {
  // This is a file based repository
  flatDir {
    dirs 'lib'
  }
  // This is a local repository
  mavenLocal()

  // This is a remote maven repository
  mavenCentral()
  jcenter()

  // Maven repo with credentials use ivy for ivy repositories
  maven {
        credentials {
            username "$mavenUser"
            password "$mavenPassword"
        }
        url 'https://maven.yourcorp.net/'
   }
}

// There is no change in how you specify dependencies
dependencies {
  implementation 'log4j:log4j:1.2.8'
  implementation group: 'javax.xml.bind', name: 'jaxb-api', version: '2.3.1'
  testImplementation 'junit:junit:3.8.1'
}
```

## Configuration Scopes

Implementation (Dependency is available both during compile and runtime for src)

- ComplieOnly
- RuntimeOnly

testImplementation (Dependency is available both during compile and runtime for test)

- testCompileOnly
- testRuntimeOnly

## Resolution Strategy

### Gradle Caches

Modules are cached

- File based caching
- Metadata and files are stored separately
- Repository caches are independent

Dependencies can be refreshed (re-downloaded)

- `gradle clean --refresh-dependencies`

Caches can be safely deleted, it will be re downloaded.

# Multi Project Builds

A multi-project build in Gradle consists of one root project, and one or more subprojects.

```
.
├── app
│   ...
│   └── build.gradle
├── lib
│   ...
│   └── build.gradle
└── settings.gradle
```

```groovy
// In seattings.gradle
rootProject.name = 'basic-multiproject'
include 'app','lib'
```

Naming Recommendations:

- Keep default project names for subprojects i.e, directory names
- Use kebab case formatting (kebab-case-formatting) for all project names
- Define the root project name in the settings file

## Dependency Management Across Projects

Shared build logic is extracted into a convention plugin that is applied in the subprojects' build scripts that also define project dependencies.

```
In multi-project/dependencies-java
.
├── buildSrc
│   ...
├── api
│   ├── src
│   │   └──...
│   └── build.gradle
├── services
│   └── person-service
│       ├── src
│       │   └──...
│       └── build.gradle
├── shared
│   ├── src
│   │   └──...
│   └── build.gradle
└── settings.gradle
```

```groovy
// In settings.gradle
rootProject.name = 'dependencies-java'
include 'api', 'shared', 'services:person-service'

// buildSrc/src/main/groovy/myproject.java-conventions.gradle
plugins {
    id 'java'
}

group = 'com.example'
version = '1.0'

repositories {
    mavenCentral()
}

dependencies {
    testImplementation "junit:junit:4.13"
}

// In api/build.gradle
plugins {
    id 'myproject.java-conventions'
}

dependencies {
    implementation project(':shared')
}

// In shared/build.gradle
plugins {
    id 'myproject.java-conventions'
}

// In services/person-service/build.gradle
plugins {
    id 'myproject.java-conventions'
}

dependencies {
    implementation project(':shared')
    implementation project(':api')
}
```

## Common Conventions in Shared build logic

Each of the module belonging to one or more categories below, require different defaults that can be customized.

- public libraries - libraries that are published to some repository
- internal libraries - libraries on which other subprojects depend on internally within the project
- command line applications - applications with specific packaging requirements
- web services - applications with specific packaging requirements that are different from above
- documentation/website - documentation for the project

## Dependency Outgoing Artifacts

Consumer project depending on the file produced by the producer project. which contains version information.

```groovy
// Producer build.gradle generates the artifact as a file
plugins {
    id 'java-library'
}

version = '1.0'

def buildInfo = tasks.register("buildInfo", BuildInfo) {
    version = project.version
    outputFile = layout.buildDirectory.file('generated-resources/build-info.properties')
}

sourceSets {
    main {
        output.dir(buildInfo.map { it.outputFile.asFile.get().parentFile })
    }
}
```

```java
// buildSrc/src/main/java/BuildInfo.java - generates version info in file
public abstract class BuildInfo extends DefaultTask {

    @Input
    public abstract Property<String> getVersion();

    @OutputFile
    public abstract RegularFileProperty getOutputFile();

    @TaskAction
    public void create() throws IOException {
        Properties prop = new Properties();
        prop.setProperty("version", getVersion().get());
        try (OutputStream output = new FileOutputStream(getOutputFile().getAsFile().get())) {
            prop.store(output, null);
        }
    }
}
```

```groovy
// Consumer build.gradle reads the artifact from a file and uses it
dependencies {
    runtimeOnly project(':producer')
}
```

```java
public static Properties readBuildInfo() throws IOException {
  Properties prop = new Properties();
  InputStream inputStream = null;

  try {
    inputStream = Application.class.getClassLoader().getResourceAsStream("build-info.properties");
    prop.load(inputStream);
  } finally {
    if (inputStream != null) {
      try {
        inputStream.close();
      } catch (IOException e) {
      }
    }
  }
  System.out.println(prop); // prints {version=1.0}
  return prop;
}
```

## [Fine Tuning Project Layouts](https://docs.gradle.org/current/userguide/fine_tuning_project_layout.html)

## [Decoupled Projects](https://docs.gradle.org/current/userguide/multi_project_configuration_and_execution.html#sec:decoupled_projects)

# Plugins

Plugins extends the capability of gradle for a specific purpose.

- Extend the Gradle model (e.g. add new DSL elements that can be configured)
- Configure the project according to conventions (e.g. add new tasks or configure sensible defaults)
- Apply specific configuration (e.g. add organizational repositories or enforce standards)

Types of Plugins:

- Binary Plugins
  - Written by using [Plugin](https://docs.gradle.org/current/javadoc/org/gradle/api/Plugin.html) Interface or,
  - Using Gradle DSL Language.
- Script Plugins
  - Unpacked groovy scripts

## Plugin Management

It appears in `settings.gradle` file

```groovy
pluginManagement {
    plugins {
    }
    resolutionStrategy {
    }
    repositories {
      maven {
            url './maven-repo'
        }
        gradlePluginPortal() //default
        ivy {
            url './ivy-repo'
        }
    }
}
rootProject.name = 'plugin-management'
```

## Applying Plugins

### Applying plugin using DSL

```groovy
// Applying core plugin
plugins {
    id 'java'
}

// Applying community plugin - version needs to be specified
plugins {
    id 'com.jfrog.bintray' version '1.8.5'
}

```

### Applying Legacy Plugins

```groovy
// Plugins published as external jars - Add them using build scripts
buildscript {
    repositories {
        gradlePluginPortal()
    }
    dependencies {
        classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.5'
    }
}

apply plugin: 'com.jfrog.bintray'
```

### Applying plugin using Scripts

```groovy
apply from: 'other.gradle'
```

## Commonly used Plugins

List of most commonly using plugins for with java:

- [Java Plugin](https://docs.gradle.org/current/userguide/java_plugin.html)
  - Java compilation, testing and bundling capabilities to a project
- [Spring Boot Gradle Plugin](https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/)
  - Package executable jar or war archives, run Spring Boot applications, and use the dependency management provided by spring-boot-dependencies
- [Java Library Plugin](https://docs.gradle.org/current/userguide/java_library_plugin.html)
- [Java Library Distribution Plugin](https://docs.gradle.org/current/userguide/java_library_distribution_plugin.html)
  - Java library distribution plugin adds support for building a distribution ZIP for a Java library
- [Java Image Builder](https://github.com/GoogleContainerTools/jib/tree/master/jib-gradle-plugin)
  - Jib is for building Docker and OCI images for your Java applications.
- [Maven Publish Plugin](https://docs.gradle.org/current/userguide/publishing_maven.html)
  - Provides the ability to publish build artifacts to an Apache Maven repository
- [PMD Plugin](https://docs.gradle.org/current/userguide/pmd_plugin.html)
  - Performs quality checks on your project’s Java source files using [PMD](https://pmd.github.io/)
- [Signing Plugin](https://docs.gradle.org/current/userguide/signing_plugin.html)
  - Adds the ability to digitally sign built files and artifacts.
- [War Plugin](https://docs.gradle.org/current/userguide/war_plugin.html)
  - Extends the Java plugin to add support for assembling web application WAR files, disable Jar packaging.
- [ShadowJar Plugin](https://imperceptiblethoughts.com/shadow/introduction/#benefits-of-shadow)
  - Bundling and relocating common dependencies in libraries to avoid classpath conflicts
- [CheckStyle Plugin](https://docs.gradle.org/current/userguide/checkstyle_plugin.html)
  - Performs quality checks on your project’s Java source files using [Checkstyle](https://checkstyle.org/index.html) and generates reports from these checks
- [Git Plugin](https://plugins.gradle.org/plugin/org.ajoberstar.grgit)
  - Provides the ability to use Git related tasks in your project.

# Extending Gradle

## [Developing Gradle Plugins](https://docs.gradle.org/current/userguide/custom_plugins.html)

Writing a simple plugin:

```groovy
class MyPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.tasks.create('hello') {
            doLast {
                println 'Hello world!'
            }
        }
    }
}
```

```groovy
plugins {
    id 'my-plugin' version '1.0'
}
```

Making the plugin configurable:

```groovy
class MyPlugin implements Plugin<Project> {
    void apply(Project project) {
        project.tasks.create('hello') {
            doLast {
                println "Hello ${project.myPlugin.greeting}!"
            }
        }
    }
}
```

```groovy
plugins {
    id 'my-plugin' version '1.0'
}

myPlugin {
    greeting = 'world'
}
```

## [Developing Gradle Tasks](https://docs.gradle.org/current/userguide/custom_tasks.html)

# Structuring Organization projects

Look into open source projects and compare.

- Apache Projects

# References

[Gradle Docs](https://docs.gradle.org/current/userguide/userguide.pdf)
