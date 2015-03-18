This project aims to create a scriptable, JavaScript based language for expressing complex user interfaces. The aim is to develop a universal client which will display UIDL pages with the same ease with which HTML pages are displayed.

The main features of a UIDL page are the ability to create complex user interfaces (using Swing based widgets) and to communicate asynchronously with server based objects (using the JSON-RPC protocol).

Whilst this may sound similar to AJAX, the implementation is much cleaner thanks to the ability to natively instantiate complex UI components within the browser and the ability to transparently present server based objects via their JavaScript proxies.

The motivation for this project comes from the realisation that the (HTML) browser model was developed for document presentation rather than as a GUI for complex applications. Most web application developers resort to hacks and are required to have knowledge of many different technologies (JavaScript, HTML, ASP, SQL, etc). Web applications are generally less user friendly than a comparable client-server application of the 1980’s and the code is much more difficult to maintain (although web applications are easier to deploy).

On the other hand, the browser (or universal client) model is vastly superior in that it makes it easy to deploy application upgrades in a central location without needing to worry about upgrading clients. The proposal is therefore to extend the browser model to include support for a rich library of UI components and to asynchronously and transparently update client data structures from objects located on the server.

**http://www.uidl.net/**

Also visit our sister project (Applino) at: http://www.applino.com/

## Try it Out ##

  * [Java WebStart Version](http://dcioccarelli.free.fr/uidl/browser/uidl.jnlp)
  * [Java Applet Version](http://www.uidl.net/browser/applet.html)

## Documentation ##

  * [Reference Manual](http://www.uidl.net/doc/) (single page HTML format)
  * [Reference Manual](http://www.uidl.net/doc/UidlReferenceDocumentation.pdf) (PDF format)
  * [WIKI style documentation](http://code.google.com/p/uidl/wiki/Index)
  * [Javadoc API reference](http://www.uidl.net/doc/api/)

## Download ##

All downloads are available from [the download section](http://code.google.com/p/uidl/downloads/list).

Also refer to the ChangeLog Wiki for information about the various releases.

## Participate! ##

  * [UIDL @ Google Groups](http://groups.google.com/group/uidl-user)
