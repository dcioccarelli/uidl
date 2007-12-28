//=============================================================================
//Change History:
//Date     UserId      Defect          Description
//----------------------------------------------------------------------------
//07/02/05 ant         ???             Initial version.
//09/02/05 ant                         Support both HTTP GET and POST
//14/02/05 ant                         Fix call backs in functions
//20/02/05 ant                         Support HTTP HEAD
//28/02/05 ant                         HTTP basic authentication support
//
package xmlhttp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.xerces.parsers.DOMParser;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeFunction;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * XMLHttpRequest simulates the Mozilla XMLHttpRequest.
 * 
 * Add this class to the Rhino classpath and then define to Rhino
 * with <code>defineClass('xmlhttp.XMLHttpRequest');</code>
 * 
 * @author <a href="mailto:ant.elder@uk.ibm.com">Ant Elder </a>
 */
public class XMLHttpRequest extends ScriptableObject {

    private String url;
    private String httpMethod;
    private HttpURLConnection urlConnection;

    private int httpStatus;
    private String httpStatusText;

    private Map requestHeaders;

    private String userName;
    private String password;

    private String responseText;
    private Document responseXML;

    private int readyState;
    private NativeFunction readyStateChangeFunction;

    private boolean asyncFlag;
    private Thread asyncThread;

    public XMLHttpRequest() {
    }

    public void jsConstructor() {
    }

    public String getClassName() {
        return "XMLHttpRequest";
    }

    public void jsFunction_setRequestHeader(String headerName, String value) {
        if (readyState > 1) {
            throw new IllegalStateException("request already in progress");
        }

        if (requestHeaders == null) {
            requestHeaders = new HashMap();
        }

        requestHeaders.put(headerName, value);
    }

    public Map jsFunction_getAllResponseHeaders() {
        if (readyState < 3) {
            throw new IllegalStateException(
                    "must call send before getting response headers");
        }
        return urlConnection.getHeaderFields();
    }

    public String jsFunction_getResponseHeader(String headerName) {
        return jsFunction_getAllResponseHeaders().get(headerName).toString();
    }

    public void jsFunction_open(String httpMethod, String url,
            boolean asyncFlag, String userName, String password) {

        if (readyState != 0) {
            throw new IllegalStateException("already open");
        }

        this.httpMethod = httpMethod;

        if (url.startsWith("http")) {
            this.url = url;
        } else {
            throw new IllegalArgumentException("URL protocol must be http: "
                    + url);
        }

        this.asyncFlag = asyncFlag;

        if ("undefined".equals(userName) || "".equals(userName)) {
            this.userName = null;
        } else {
            this.userName = userName;
        }
        if ("undefined".equals(password) || "".equals(password)) {
            this.password = null;
        } else {
            this.password = password;
        }
        if (this.userName != null) {
            setAuthenticator();
        }

        setReadyState(1);
    }

    private void setAuthenticator() {
        Authenticator.setDefault(new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password
                        .toCharArray());
            }
        });
    }

    public void jsFunction_send(Object o) {
        final String content = (o == null) ? "" : o.toString();
        if (asyncFlag) {
            Runnable r = new Runnable() {
                public void run() {
                    doSend(content);
                }
            };
            this.asyncThread = new Thread(r);
            asyncThread.start();
        } else {
            doSend(content);
        }
    }

    public void jsFunction_abort() {
        if (asyncThread != null) {
            asyncThread.interrupt();
        }
    }

    /**
     * @return Returns the readyState.
     */
    public int jsGet_readyState() {
        return readyState;
    }

    /**
     * @return Returns the responseText.
     */
    public String jsGet_responseText() {
        if (readyState < 2) {
            throw new IllegalStateException("request not yet sent");
        }
        return responseText;
    }

    /**
     * @return Returns the responseXML as a DOM Document.
     */
    public Document jsGet_responseXML() {
        if (responseXML == null && responseText != null) {
            convertResponse2DOM();
        }
        return responseXML;
    }

    private void convertResponse2DOM() {
        try {

            DOMParser parser = new DOMParser();
            StringReader sr = new StringReader(jsGet_responseText());
            parser.parse(new InputSource(sr));
            this.responseXML = parser.getDocument();

        } catch (SAXException e) {
            throw new RuntimeException("ex: " + e, e);
        } catch (IOException e) {
            throw new RuntimeException("ex: " + e, e);
        }
    }

    /**
     * @return Returns the htto status.
     */
    public int jsGet_status() {
        return httpStatus;
    }

    /**
     * @return Returns the http status text.
     */
    public String jsGet_statusText() {
        return httpStatusText;
    }

    /**
     * @return Returns the onreadystatechange.
     */
    public Object jsGet_onreadystatechange() {
        return readyStateChangeFunction;
    }

    /**
     * @param function
     *            The onreadystatechange to set.
     */
    public void jsSet_onreadystatechange(NativeFunction function) {
        readyStateChangeFunction = function;
    }

    private void doSend(String content) {

        connect(content);

        setRequestHeaders();

        try {
            urlConnection.connect();
        } catch (IOException e) {
            throw new RuntimeException("ex: " + e, e);
        }

        sendRequest(content);

        if ("POST".equals(this.httpMethod) || "GET".equals(this.httpMethod)) {
            readResponse();
        }

        setReadyState(4);

    }

    private void connect(String content) {
        try {

            URL url = new URL(this.url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod(httpMethod);
            if ("POST".equals(this.httpMethod) || content.length() > 0) {
                urlConnection.setDoOutput(true);
            }
            if ("POST".equals(this.httpMethod) || "GET".equals(this.httpMethod)) {
                urlConnection.setDoInput(true);
            }
            urlConnection.setUseCaches(false);

        } catch (MalformedURLException e) {
            throw new RuntimeException("MalformedURLException: " + e, e);
        } catch (IOException e) {
            throw new RuntimeException("IOException: " + e, e);
        }
    }

    private void setRequestHeaders() {
        if (this.requestHeaders != null) {
            for (Iterator i = requestHeaders.keySet().iterator(); i.hasNext();) {
                String header = (String) i.next();
                String value = (String) requestHeaders.get(header);
                urlConnection.setRequestProperty(header, value);
            }
        }
    }

    private void sendRequest(String content) {
        try {

            if ("POST".equals(this.httpMethod) || content.length() > 0) {
                OutputStreamWriter out = new OutputStreamWriter(urlConnection
                        .getOutputStream(), "ASCII");
                out.write(content);
                out.flush();
                out.close();
            }

            httpStatus = urlConnection.getResponseCode();
            httpStatusText = urlConnection.getResponseMessage();

        } catch (IOException e) {
            throw new RuntimeException("IOException: " + e, e);
        }

        setReadyState(2);
    }

    private void readResponse() {
        try {

            InputStream is = urlConnection.getInputStream();
            StringBuffer sb = new StringBuffer();

            setReadyState(3);

            int i;
            while ((i = is.read()) != -1) {
                sb.append((char) i);
            }
            is.close();

            this.responseText = sb.toString();

        } catch (IOException e) {
            throw new RuntimeException("IOException: " + e, e);
        }
    }

    private void setReadyState(int state) {
        this.readyState = state;
        callOnreadyStateChange();
    }

    private void callOnreadyStateChange() {
        if (readyStateChangeFunction != null) {
            Context cx = Context.enter();
            try {
                Scriptable scope = cx.initStandardObjects();
                readyStateChangeFunction.call(cx, scope, this, new Object[] {});
            } finally {
                Context.exit();
            }
        }
    }

}