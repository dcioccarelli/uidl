If you intend to modify or recompile the UIDL browser code, you will need to be able to build the `uidl.jar` file. Note that it is very important to sign all the JAR files which will be loaded when the browser is run as an applet or as a Java Web Start application. This is typically just `uidl.jar` and `js.jar` (Rhino).

If you wish to distribute the UIDL browser (or a UIDL application) in a production environment then it is advisable to sign all the required JAR files using a proper digital certificate issued from a trusted certificate authority. The build script (`build.xml`) can be modified with the appropriate certificate information if required.

To generate your own certificate (for local testing) you should use the `keytool` utility which is included with the Java JDK. This can be accomplished as follows:

`keytool -genkey -alias myalias`

You will then need to answer a feq questions. When you are finished, make sure you modify the build.xml with the alias and password you used in the previous step:

```
<target name="signedjar" depends="jar" description="Create a .jar file from UIDL browser classes">
   <signjar alias="myalias" storepass="mypassword">
        <fileset dir="${outputDir}/" includes="**/*.jar"/>
   </signjar>
</target>
```

The targets available for building the UIDL browser are as follows:

```
ant -projecthelp
Buildfile: build.xml
Main targets:

 classpathInfo  Display current application classpath
 clean          Delete built files
 compile        Compile core classes
 copyResources  Copies .properties files to classDir
 jar            Create a .jar file from core classes
 signedjar      Create a .jar file from UIDL browser classes

Default target: signedjar
```

Typically you will compile a new browser by using `ant signedjar`. This will produce a new UIDL browser library (`uidl.jar`) in the `/webapp/browser` directory. In this way, the browser (either running as a Java WebStart application or as an applet) will pick up the new JAR file the next time it runs.

In order to compile the example applications, a second build script is used. This time the options are as follows:

```
ant -buildfile examples.xml -projecthelp
Buildfile: examples.xml
Main targets:

 classpathInfo   Display current application classpath
 clean           Delete built files
 compile         Compile core classes
 copyResources   Copies resources files to classDir
 jar             Create a .jar file from core classes
 remoteClassJar  Create a .jar file for the remote classes
 war             Create a WAR file to deploy the application

Default target: jar
```

The default target (jar) will produce a new JAR file with the class files for the examples in the `/webapp/WEB-INF/lib` directory. Depending on how you have configured your web application server, you may need to use the "war" target and redeploy the WAR archive (containing the new examples JAR) to your application server.

For some of the remoting examples, the UIDL code needs to have access to both the Java interfaces for the remote object factories / facades and the class files for the actual objects which are transmitted (in serialized form) to the UIDL application (and subsequently translated into JavaScript objects). Rather than passing the entire "`examples.jar`" file to the client, we produce a much lighter "`remoteClasses.jar`" which is made available in the `/webapp/jar` directory. The `remoteClassJar` target is responsible for producing this file and the classes which it contains are specified in `/build/remoteInterface.txt`.