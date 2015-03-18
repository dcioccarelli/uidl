The latest releases are available from the UIDL page on Google code, at http://code.google.com/p/uidl/downloads/list. The SVN repository for the UIDL source code is hosted by Google code at http://code.google.com/p/uidl/source.
Installation

The UIDL distribution comes with pre-built UIDL JARs and all supporting JARs. Most of the files in the distribution are not for the UIDL browser itself but rather for the supporting examples (which require things like Spring and Hibernate).

You should unzip the distribution into a local directory.

The source files for the UIDL browser are located in the `/src` directory whilst the source files for the example scenarios are are located in the `/examples` directory. Ant build scripts are provided for both the browser and the examples and are located in the `/build` directory. A pre-populated Derby database is provided in the `/db` directory for the "CarSales" example application. The `/lib` directory contains all the Java libraries required to build either the browser or the example applications. Run time JARs to support the example applications are located in `/webapp/WEB-INF/lib`.

The /webapp directory contains all the server side code required for the example applications and is also used to server the initial files for the UIDL browser and the subsequent UIDL pages.

## Running the Example Applications ##

### Statically Hosted Files ###

To illustrate how simple it is to run a very simple (static) UIDL application, you can simply copy the contents of the `/webapp` directory to a normal web server. From there you can navigate to the index page (`index.html`) and run either the Java WebStart or Applet version of the UIDL browser. Once running the browser, you can run any of the static UIDL examples (i.e. those which don't use remoting). These include `simple.uidl` and `converter.uidl`.

### Using the embedded Jetty Application Server ###

Obviously, more complex applications will require some interaction with business logic running on the server. As a result, we need to run the server side code for the example applications within a web application container.

You can simply run "`webserver.bat`" or "`webserver.sh`" from the root directory. This will used an embedded version of the Jetty web application server to publish the contents of the "webapp" sub-directory.

After running Jetty, point your browser to http://localhost:8080/uidl/ and run either the applet or Java WebStart UIDL browser from there.

### Using a Web Application Server ###

  * The `/webapp` subdirectory is already in the correct format (exploded WAR) for most web application servers (e.g. Tomcat). You can configure your application server to point directly to the `/webapp` directory which was part of the UIDL distribution that you unzipped previously. In Tomcat, this would be done using a context descriptor (e.g. `uidl.xml`) similar to the following:

```
<Context docBase="C:/cep/uidl/webapp" debug="0" privileged="false" reloadable="true">
</Context>	
```

  * You can simply build the UIDL WAR file and deploy it to your web server as you would any other WAR file. Building the WAR file is described in the next section.

### Configuring the "CarSales" Data Source ###

Irrespective of how you run the UIDL web application, you should look at the "`applicationContext.xml`" file (in the `/WEB-INF directory`) to see how the CarSales Derby database is configured. The location of the database is defined by the JDBC URI, which is located in the Spring "`applicationContext.xml`", i.e.:

```
<!-- Configuration for an application managed JDBC datasource -->
<bean id="carsalesDataSource" class="org.apache.commons.dbcp.BasicDataSource" 
destroy-method="close">
<property name="driverClassName" value="org.apache.derby.jdbc.EmbeddedDriver"/>
<property name="url" value="jdbc:derby:carsales;create=true"/>
<property name="username" value=""/>
<property name="password" value=""/>
</bean>
```

Alternatively you can configure the data source in your web application server and reference it from the Spring configuration file as follows:

```
<bean id="carsalesDataSource" class="org.springframework.jndi.JndiObjectFactoryBean">
<property name="jndiName" value="java:comp/env/jdbc/carsales"/>
</bean>
```

The default URI (`jdbc:derby:carsales;create=true`) will create a database in the root directory where you run the application from. Alternatively you can specify a fixed path as follows: `jdbc:derby:C:/cep/uidl/db/carsales;create=true`.

The default configuration will create and export a new database schema each time the application server is started (or the web application is restarted). This is defined in the following configuration:

```
<property name="hibernateProperties">
    <props>
	<prop key="hibernate.dialect">org.hibernate.dialect.DerbyDialect</prop>
	<prop key="hibernate.hbm2ddl.auto">create</prop>
    </props>
</property>
```

The application will automatically populate the database with some default data if it finds that it is empty. Typically though, after your first run (when the Derby database is created) you should comment out the schema export code as follows:

```
<property name="hibernateProperties">
    <props>
	<prop key="hibernate.dialect">org.hibernate.dialect.DerbyDialect</prop>
	<!-- <prop key="hibernate.hbm2ddl.auto">create</prop> -->
    </props>
</property>
```

Failure to complete this configuration will mean that whenever the web application is restarted it will be recreated and repopulated with the default content.