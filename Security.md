## UIDL Security ##

Most enterprise class applications will require some form of security, including the possibility to recognize and authenticate users and assign appropriate roles. Rather than forcing application developers to implement custom solutions to satisfy these requirements, the UIDL environment aims to make it as easy as possible to implement security in UIDL applications.

Given that the UIDL browser uses HTTP to transport UIDL scripts and HTTP for all remoting operations (unless a custom remote object mechanism is used), one can make use of existing HTTP security standards such as HTTPS and realm based authentication.

Specifically, the following code within the web descriptor (web.xml) of the UIDL web application is sufficient to protect not only the downloading of the UIDL scripts but also any method calls which might be issued from a script. Finer grained security can obviously be implemented if required.

The UIDL browser (running in either applet or Java Web Start mode) will use a dialog box to prompt the user for credentials the first time he accesses a protected resource. Note that the realm will need to be configured on the web server / web application server in order for this type of security to function. Typically a realm is linked to an LDAP directory containing user information.

```
    <security-constraint>
       <display-name>Admin Security Constraint</display-name>

       <!-- protect method calls -->
       <web-resource-collection>
          <web-resource-name>Spring Remoting</web-resource-name>
          <url-pattern>/remote/*</url-pattern>
       </web-resource-collection>

       <!-- protect UIDL script download -->
       <web-resource-collection>
          <web-resource-name>UIDL Scripts</web-resource-name>
          <url-pattern>/scripts/*.uidl</url-pattern>
       </web-resource-collection>

       <auth-constraint>
          <role-name>Admin</role-name>
       </auth-constraint>
    </security-constraint>

    <!-- Define the Login Configuration for this Application -->
    <login-config>
      <auth-method>BASIC</auth-method>
      <realm-name>Admin Security Constraint</realm-name>
    </login-config>

    <security-role>
       <role-name>Admin</role-name>
    </security-role>
```