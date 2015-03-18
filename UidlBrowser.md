## The UIDL Browser ##

### UIDL Browser Deployment ###

To make use of a UIDL based application, end users must run a UIDL "browser". This is analogous to a web browser except that it interprets and renders the UIDL language rather than HTML (actually it has limited support for HTML rendering as well). It could also be considered as a "universal client" for rich internet applications.

The size of the browser / universal client is quite small (500Kb - 1Mb depending on remoting options) and it only needs to be downloaded once irrespective of the number of UIDL applications with which it is used. Being Java based, the browser will run on any OS which supports a recent Java Virtual Machine.

In order to simplify the (one time) deployment of the UIDL browser, many options have been made available, including:

  * Stand alone mode. Typically used by developers, where all Java libraries are statically located on the client machine.

  * Java WebStart mode. This mode makes use of the Java Web Start technology introduced in recent versions of the Sun JRE. Users simply need to click once on the JNLP file describing the UIDL browser and it will be installed on their machine. In addition, if updates to the browser are ever required, these will be transparently loaded  by the client each time they run the browser.

  * [Applet](Applet.md) mode. For the most transparent deployment, the UIDL browser can be embedded within a standard web browser as a Java applet. Being a signed applet, it will only need to download the constituent libraries once and will cache them locally on the client machine. This means that the page containing the applet will always load quickly. Updates to the browser code will be delivered transparently to clients running in this mode.

### The UIDL Browser ###

The UIDL Browser looks similar to the following picture when run as a Java Web Start application.

![http://java.uidl.net/doc/images/howitw1.jpg](http://java.uidl.net/doc/images/howitw1.jpg)

When a UIDL page is loaded, the body of the UIDL application is displayed in the browser's content area as follows:

![http://java.uidl.net/doc/images/howitw2.jpg](http://java.uidl.net/doc/images/howitw2.jpg)

When deployed as a Java WebStart application or Java Applet, one can pass the URL of the UIDL application script to the UIDL browser as a parameter and choose to have the address bar concealed. In this way, the user isn't presented with an address bar or navigation buttons and the specified page loads immediately. Compare the application above with that below:

![http://java.uidl.net/doc/images/howitw3.jpg](http://java.uidl.net/doc/images/howitw3.jpg)

This mode of deployment is recommended for environments where the user will only be accessing a single UIDL based application (and therefore doesn't need the navigation capabilities) or when the UIDL browser is run as an applet and the users may confuse the HTML browser's navigation bar with that of the UIDL browser. This mode of deployment can be thought of as "embedding" the UIDL browser into the UIDL application.