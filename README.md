Install Gradle (1.10 at time of writing)

```console
brew install gradle
```

Install Groovy (2.2.1 at time of writing) and follow the instructions at the end to set GROOVY_HOME

``console
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
