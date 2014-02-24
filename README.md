Integration and user docs are located at http://docs.broadleafcommerce.org/rackspace/current

## Running Locally
Assuming that you have Gradle and Groovy installed (see below for instructions) the only tricky part is in running the tests, since you have to pass in credentials. A few ways to do this:

1. If you are a BLC developer, pull down the 'ThirdPartyIntegrationConfig' project and do a local maven install. Run the build with `-Pblc-config`

	```console
	gradle build -Pblc-config
	```

2. Fill in appropriate values within `src/main/resources/config/bc/rackspace/common.properties` but **do not check in**

3. Pass in values via command line properties:

	```console
	gradle build -Pbroadleaf.rackspace.cloudfiles.username=<user> -Pbroadleaf.rackspace.cloudfiles.apikey=<key> -Pbroadleaf.rackspace.cloudfiles.container=<container> -Pbroadleaf.rackspace.cloudfiles.containerSubDirectory=<subdir> -Pbroadleaf.rackspace.cloudfiles.endpoint=<London or US endpoint>
	```

4. Fill out values in `~/.gradle/gradle.properties`

	```properties
	broadleaf.rackspace.cloudfiles.username=<user>
	broadleaf.rackspace.cloudfiles.apikey=<key>
	broadleaf.rackspace.cloudfiles.container=<container>
	broadleaf.rackspace.cloudfiles.containerSubDirectory=<subdir>
	broadleaf.rackspace.cloudfiles.endpoint=<London or US endpoint>
	```

### JRebel
If you are using JRebel, build with the `blc-development` flag either as a command line argument:

```console
gradle build -Pblc-development
```

Or globally in `~/.gradle/gradle.properties`

```properties
blc-development=true
```

### Cobertura
Generate a test coverage report with:

```console
gradle cobertura
```

Open `build/reports/cobertura/index.html` in a browser to see the results (like the Eclipse browser).

> Remember, you still have to fill out the properties appropriately as notated above in order to run the Cobertura report

## Other Considerations
This module declares a hard dependency on Google Guava, in lock-step with the Guava version from JClouds. This is because JClouds :shit:s all over itself when there is a Guava version that it doesn't like. This might actually be a good reason to drop JClouds for this integration completely, but whatever. The hard dependency doesn't seem to break anything else in Broadleaf after some smoke tests and it definitely makes this thing work.

If you REALLY need a different version of Guava than the one that is specified here, then please send a pull request!

## Setting up for development
Install Gradle (1.10 at time of writing)

```console
brew install gradle
```

Install Groovy (2.2.1 at time of writing) and follow the instructions at the end to set GROOVY_HOME

```console
brew install groovy
```

For Eclipse, install the following plugins (look on the Eclipse Marketplace):

- Gradle Integration for Eclipse
- Groovy/Grails Tool Suite

> This is a nice to have for syntax highlighting in the .gradle files as well as content assist for the DSL

After installing these plugins, ensure that Gradle is using the version that you installed in your system at Eclipse -> Preferences -> Gradle and then setting the 'Gradle Distribution Folder' to `/usr/local/Cellar/gradle/1.10/libexec`

> This is kinda weird but at time of writing the Groovy plugin is on 2.0.7 which is different than the Groovy you installed on your system, 2.2.1. This is because the Groovy plugin is also bundled with Grails, and the OOB Groovy that Grails uses is 2.0.7.

From the root of this project, run the Eclipse plugin build to build out all the metadata:

```console
gradle eclipse
```

This will create the .classpath, .project and .settings directories.

You should now be able to import into Eclipse as an existing project (File -> Import -> Existing Projects into Workspace). By right-clicking on the project you can enable DSL support.

> After enabling DSL support, you might need to refresh the project. Right click on the project -> Gradle -> Refresh All

> If your colors are screwed up in your build.gradle and settings.gradle file, if you are using the Eclipse color theme go to Eclipse -> Preferences -> Groovy -> Editor and hit the 'Copy Java Color Preferences' button.

Finally, change the build directory of the project so that the Jrebel plugin will work. Eclipse defaults to outputting .class files in the bin directory, change this to `build/classes/main`. Right-click on the project -> Properties -> Java Build Path -> Source, check the box for 'Allow output folders for source folders'. The output folder for `src/main/java` should be `build/classes/main` and the output folder for `src/test/java` should be `build/classes/test`
