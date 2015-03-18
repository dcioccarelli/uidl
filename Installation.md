The UIDL distribution comes with pre-built UIDL JARs and all supporting JARs. Most of the files in the distribution are not for the UIDL browser itself but rather for the supporting examples (which require things like Spring and Hibernate).

You should unzip the distribution into a local directory. From here you will find the directory structure to be as follows:

http://java.uidl.net/doc/images/dirStructure.GIF

The source files for the UIDL browser are located in the `/src` directory whilst the source files for the example scenarios are are located in the `/examples` directory. Ant build scripts are provided for both the browser and the examples and are located in the /build directory. A pre-populated Derby database is provided in the `/db` directory for the CarSales example application. The `/lib` directory contains all the Java libraries required to build either the browser or the example applications. Run time JARs to support the example applications are located in `/webapp/WEB-INF/lib`.

The `/webapp` directory contains all the server side code required for the example applications and is also used to server the initial files for the UIDL browser and the subsequent UIDL pages.