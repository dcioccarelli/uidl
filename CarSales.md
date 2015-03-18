The CarSales example application is intended as a showcase for UIDL technology. It uses a combination of Spring and Hibernate on the server side to implement an application which manages customer's automotive preferences. The database schema for the example is presented below:

http://java.uidl.net/doc/images/dbSchema.GIF

There is a one to one correspondence between the Java objects and their respective tables. All relationships are managed using EJB 3 persistence annotations and the entire database schema can be regenerated at any time directly from the object model. The association tables (e.g. customer\_make) are generated automatically and are required for N:N mappings. Obviously these tables don't directly map to any Java objects.

To run the CarSales application, first ensure that your [database is configured correctly](http://code.google.com/p/uidl/wiki/Running) and then simply point your UIDL browser at the CarSales example URL (e.g. `http://localhost:8080/uidl/carsales/carsales.uidl`).

The CarSales examples allows of use of either SpringRemoting or JsonRpc as a transport layer. The example also reveals how transparently the remoting implementation can be switched.

The UIDL source code for the application is located under `/webapp/carsales`. The Java code for the server side is located under `/examples/net/uidl/carsales/`.

http://java.uidl.net/doc/images/CarSales.GIF

Basically, the CarSales application allows you to add as many makes as you like (e.g. Alfa, Fiat, Ferrari). You can then add models to these makes (MAKE\_MODEL association) In a similar way, you can create customers and associate make preferences with each customer. You can also create options (heated seats, metallic paint, etc.) and associate these with wither the make or the model. All data is persisted in real time in the Derby embedded database on the server.