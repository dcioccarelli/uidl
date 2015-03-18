## Running a UIDL application with Java Web Start ##

Java Web Start is one of the most convenient ways to run a UIDL based application. It allows for automatic application updating and integration with the user's desktop. An example of a Web Start descriptor (JNLP file) for a UIDL application is as follows

```
<?xml version="1.0" encoding="utf-8"?>
<!-- JNLP File for UIDL Browser -->
<jnlp
  spec="1.0+"
  codebase="http://www.uidl.net/browser/"
  href="uidl.jnlp">
  <information>
    <title>UIDL Browser</title>
    <vendor>www.uidl.net</vendor>
    <homepage href="docs/help.html"/>
    <description>UIDL Browser</description>
    <description kind="short">A browser for displaying rich client
    applications written in the UIDL syntax.</description>
    <icon href="images/logo.jpg"/>
    <offline-allowed/>
    <shortcut>
      <desktop/>
      <menu submenu="UIDL"/>
    </shortcut>
  </information>
  <security>
    <all-permissions/>
  </security>
  <resources>
    <j2se version="1.3+"/>
    <jar href="uidl.jar" download="eager"/>
    <jar href="js.jar" download="eager"/>
    <jar href="jabsorb.jar" download="eager"/>
  </resources>
  <application-desc main-class="net.uidl.Browser">
  </application-desc>
</jnlp>
```

As with the applet version of the UIDL browser, if you are going to be using Spring remoting, you will need to include some extra libraries, as follows:

```
<jar href="js.jar" download="eager"/>
<jar href="spring-client.jar" download="lazy"/>
</resources>
```

If you would like the applet to automatically load a UIDL page (even from a different server) and not display a navigation bar, then you will need to include an extra parameter as follows:

```
</resources>
<application-desc main-class="net.uidl.Browser">
  <argument>http://localhost:8080/uidl/admin/admin.uidl</argument>
</application-desc>
```

Finally, if you would like to load some extra (client side) Java libraries which the UIDL application will make use of, then these can be added to the applet classpath as follows:

```
  <j2se version="1.3+"/>
  <jar href="uidl.jar" download="eager"/>
  <jar href="js.jar" download="eager"/>
  <jar href="TableLayout.jar" download="lazy"/>
</resources>
```

Note that external libraries can also be loaded dynamically from within UIDL scripts as follows:

```
browser.loadRemoteLibrary("myclientlib.jar");
```

Loading libraries from within the JNLP descriptor has the advantage of caching the JAR file and eliminates some security issues when using signed JAR files, although it is advised to load libraries from within scripts wherever possible to place the maximum amount of logic within the script itself and reduce external dependencies.