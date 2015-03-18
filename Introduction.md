Traditionally, enterprise applications were built using a client-server approach. As web browsers became more widespread, many application developers preferred to provide a "web interface" as it allowed for transparent deployment of new functionality. This lead to the growth of web applications as we know them today.

The unfortunate side effect of web applications is that web browsers were only ever designed to display static content. This has meant that designing an application to run within a web browser has been a non trivial task and has often lead to applications which are not as responsive or interactive as would have been the case with a rich client version. Thus, whilst web applications provide ease of deployment, they often decrease developer productivity and the overall ease of use of the UI is compromised.

Recognizing the constraints of web browsers, there has been a recent trend towards Rich Internet Applications (RIAs) which aim to harness the ease of deployment of web applications and the rich user experience provided by traditional rich client applications. RIA technology is multi faceted and extends from AJAX applications which "extend" the usefulness of traditional browsers by allowing asynchronous communications (and hence reducing whole page refreshes) to more complex solutions such as Flex / Laszlo which use the Macromedia Flash environment included with most browsers to render the UI.

The UIDL approach is somewhat different in that it uses a Java applet or Java webstart application to act as a pseudo browser environment. This "new" browser is capable of interpreting both HTML and UIDL code, which is basically JavaScript with extensions to allow access to the native windowing system and remote communications.

UIDL hopes to provide the following benefits:

  * A "universal" client which can run anywhere (in a browser, on the desktop) and can be used to render applications written in UIDL. This means that there is no need to worry about deployment or software upgrades.

  * Mechanisms for providing easy access to server based resources. Ideally, applications can be modified to run with a UIDL interface without extensive additions and without modifying the underlying code. In many cases an application API can be exposed to transparently allow "remoting".

  * An environment which provides high developer productivity. As UIDL is based on JavaScript (an interpreted language) UIDL scripts can be written and tested quickly. The use of a scripted language for the UI encourages the separation of concerns and allows less skilled developers to build the UI whilst the more senior developers concentrate on the more complex code running on the server.