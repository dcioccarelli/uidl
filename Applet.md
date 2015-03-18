## Embedding the UIDL Browser in an Applet ##
To embed the UIDL browser in an applet, you will need to include the following code in an HTML page:

```
<applet name="uidlApplet" codebase="."
        archive="uidl.jar,js.jar"
        code="net.uidl.BrowserApplet.class" width="100%" height="95%" alt="No applet">
```

If you would like the applet to automatically load a UIDL page (even from a different server) and not display a navigation bar, then simply include the address of the page as a parameter to the applet as follows:

```
<applet name="uidlApplet" codebase="."
        archive="uidl.jar,js.jar"
        code="net.uidl.BrowserApplet.class" width="100%" height="95%" alt="No applet">
    <param name="url" value="http://www.uidl.net/example/carsales.uidl">
    <param name="addressBar" value="false">
</applet>
```

Finally, if you would like to load some extra (client side) Java libraries which the UIDL application will make use of, then these can be added to the applet classpath as follows:

```
<applet name="uidlApplet" codebase="."
        archive="uidl.jar,js.jar,myclientlib.jar"
        code="net.uidl.BrowserApplet.class" width="100%" height="95%" alt="No applet">
</applet>
```

Note that external libraries can also be loaded dynamically from within UIDL scripts as follows:

```
browser.loadRemoteLibrary("myclientlib.jar");
```

Loading libraries from within applet code has the advantage of caching the JAR file and eliminates some security issues when using signed JAR files, although it is advised to load libraries from within scripts wherever possible to place the maximum amount of logic within the script itself and reduce external dependencies.