The JsonRpcExporter is a utility class for use within a Spring container. It allows one to easially expose Spring hosted objects via JSON-RPC. An example configuration is depicted below:

```
    <!-- ================= Expose Services via JSON-RPC ==================== -->
    <bean   id="jsonRpcExporter"
            class="net.uidl.util.JsonRpcExporter"
            init-method="init">
        <property name="jsonObjects">
        <map>
            <entry><key><value>userManager</value></key><ref bean="userManager"/></entry>
            <entry><key><value>hello</value></key><ref bean="hello"/></entry>
            <entry><key><value>delayedHello</value></key><ref bean="delayedHello"/></entry>
            <entry><key><value>customerManager</value></key><ref bean="customerManager"/></entry>
            <entry><key><value>makeManager</value></key><ref bean="makeManager"/></entry>
            <entry><key><value>modelManager</value></key><ref bean="modelManager"/></entry>
            <entry><key><value>optionManager</value></key><ref bean="optionManager"/></entry>
        </map>
        </property>
    </bean>
```

Please refer to the [JavaDoc API reference](http://www.uidl.net/doc/api/net/uidl/util/JsonRpcExporter.html).

Use of this service assumes that the JSON-RPC servlet has been configured in the `web.mxl`, i.e.:

```
  <servlet>
      <servlet-name>JSONRPCServlet</servlet-name>
      <servlet-class>org.jabsorb.JSONRPCServlet</servlet-class>
      <init-param>
        <param-name>gzip_threshold</param-name>
        <param-value>-1</param-value>
      </init-param>
  </servlet>

.
.
.

  <servlet-mapping>
    <servlet-name>JSONRPCServlet</servlet-name>
    <url-pattern>/JSON-RPC</url-pattern>
  </servlet-mapping>
```