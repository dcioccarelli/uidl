## Release 1.3 24th March 2008 ##

  * Simplified JSON-RPC remoting by enhancing client functionality. Remote interfaces are no longer required as proxy objects are automatically constructed from exposed meta-data (by calling "browser.createProxyObjects")

  * Due to the simplified native JSON-RPC support, support for Spring RPC and JavaScript based JSON-RPC is now depreciated.

  * Reorganized build files so that common libraries don't need to be packaged twice (for compile time and runtime). This reduces the overall distribution size.

## Release 1.2 12th January 2008 ##

  * Added embedded Jetty web application server for running examples. Removed requirement for users to have an existing web application such as Tomcat installed (although this is still supported).

  * Fixed bug where hostPort is not always derived correctly when the server is running on port 80.

  * Switched to new version of the jabsorb library (for JSON-RPC remoting) which combines the JSON-RPC client and server code.

  * Simplified JSON-RPC remoting by eliminating dependency on Apache commons HTTP library.

  * Removal of Apache commons HTTP library improves suppot for JSON-RPC when running UIDL applications through a web proxy (as Sun HTTP library as better integration with the browser proxy settings).

  * Updated documentation.

## Release 1.1 December 30 2007 ##

  * Included missing commons-logging and jta libraries from runtime (/WEB-INF/lib)

## Release 1.0 December 28 2007 ##

  * Initial public release.