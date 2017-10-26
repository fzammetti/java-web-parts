/*
 * Copyright 2005 Frank W. Zammetti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package javawebparts.servlet;


import java.io.InputStream;
import java.io.IOException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javawebparts.core.org.apache.commons.digester.Digester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;


/**
 * This servlet returns a URL as defined in an XML file.  It builds a
 * collection of URIs from the config file.  Each invocation
 * of the servlet returns one of the items, either randomly (with no repeats)
 * until  all items have been shown) or in forward or reverse order.  The
 * URIs in the config file can be a complete URL (i.e., beginning with
 * http://, https://, ftp://, etc.) or can be context-relative (i.e., beginning
 * with a /).
 * <br><br>
 * Example configuration in web.xml:
 * <br><br>
 * &lt;servlet&gt;<br>
 * &nbsp;&nbsp;&lt;servlet-name&gt;RedirectReturnerServlet&lt;/servlet-name&gt;
 * <br>
 * &nbsp;&nbsp;&lt;servlet-class&gt;javawebparts.servlet.
 * RedirectReturnerServlet&lt;/servlet-class&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;order&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;random&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;itemsFile&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;
 * /WEB-INF/redirect_returner_items.xml&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &lt;/servlet&gt;
 * <br><br>
 * &lt;servlet-mapping&gt;<br>
 * &nbsp;&nbsp;&lt;servlet-name&gt;RedirectReturnerServlet&lt;/servlet-name&gt;
 * <br>
 * &nbsp;&nbsp;&lt;url-pattern&gt;/redirectReturner&lt;/url-pattern&gt;<br>
 * &lt;/servlet-mapping&gt;
 * <br><br>
 * Example items configuration file:
 * <br><br>
 * &lt;RedirectReturnerServletItems&gt;<br>
 * &nbsp;&nbsp;&lt;item&gt;http://www.google.com/intl/en/images/logo.gif
 * &lt;/item&gt;<br>
 * &nbsp;&nbsp;&lt;item&gt;/img/header1.gif&lt;/item&gt;<br>
 * &nbsp;&nbsp;&lt;item&gt;/img/mouse.gif&lt;/item&gt;<br>
 * &nbsp;&nbsp;&lt;item&gt;http://javawebparts.sourceforge.net/logo.jpg
 * &lt;/item&gt;<br>
 * &lt;/RedirectReturnerServletItems&gt;<br>
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class RedirectReturnerServlet extends HttpServlet {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javax.servlet.http.HttpServlet");
      Class.forName("javax.servlet.http.HttpServletRequest");
      Class.forName("javax.servlet.http.HttpServletResponse");
      Class.forName("javax.servlet.ServletConfig");
      Class.forName("javax.servlet.ServletContext");
      Class.forName("javax.servlet.ServletException");
      Class.forName("javawebparts.core.org.apache.commons.digester.Digester");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("RedirectReturnerServlet" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(RedirectReturnerServlet.class);


  /**
   * Bean that holds all our items from the file, and generally does all
   * the work of this servlet.
   */
  private Items items;


  /**
   * init.
   *
   * @param  config           ServletConfig.
   * @throws ServletException ServletException.
   */
  public void init(ServletConfig config) throws ServletException {

    super.init(config);
    String order = config.getInitParameter("order");
    if (order == null) {
      String es = getClass().getName() + " could not initialize " +
        "because mandatory order init parameter was not found";
      log.error(getClass().getName() + ".init(): " + es);
      throw new ServletException(es);
    }
    log.info("order = " + order);
    String itemsFile = config.getInitParameter("itemsFile");
    if (itemsFile == null) {
      String es = getClass().getName() + " could not initialize " +
        "because mandatory itemsFile init parameter was not found";
      log.error(getClass().getName() + ".init(): " + es);
      throw new ServletException(es);
    }
    log.info("itemsFile = " + itemsFile);

    // Now use Digester to populate our Items object named items.
    try {
      Digester digester = new Digester();
      digester.setValidating(false);
      digester.addObjectCreate("RedirectReturnerServletItems",
        "javawebparts.servlet.Items");
      digester.addBeanPropertySetter("RedirectReturnerServletItems/item",
        "item");
      // Get a stream on the config file and have Digester do its thing.
      ServletContext servletContext = config.getServletContext();
      InputStream isItemsFile = servletContext.getResourceAsStream(
        itemsFile);
      items = (Items)digester.parse(isItemsFile);
      items.setOrder(order);
      log.info(items);
    } catch (IOException ioe) {
      log.error("IOException reading items file.");
    } catch (SAXException saxe) {
      log.error("SAXException reading items file.  XML valid?");
    }

    log.info("init() completed");

  } // End init().


  /**
   * doGet.  Calls doPost() to do real work.
   *
   * @param  request          HTTPServletRequest.
   * @param  response         HTTPServletResponse.
   * @throws ServletException ServletException.
   * @throws IOException      IOException.
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    doPost(request, response);

  } // End doGet().


  /**
   * doPost.
   *
   * @param  request          HTTPServletRequest
   * @param  response         HTTPServletResponse
   * @throws ServletException ServletException
   * @throws IOException      IOException
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    String uri = items.getItem();
    if (uri.startsWith("http://") || uri.startsWith("ftp://") ||
      uri.startsWith("https://") || uri.startsWith("gopher://") ||
      uri.startsWith("nntp://") || uri.startsWith("news") ||
      uri.startsWith("file://") || uri.startsWith("telnet://") ||
      uri.startsWith("mailto://") || uri.startsWith("javascript://")) {
      // The URI is already a complete URL, so just redirect to it.
      log.info("RedirectServlet(1) sending redirect to: " + uri);
      ((HttpServletResponse)response).sendRedirect(uri);
    } else {
      // URI points to something local to this webapp, so we need to
      // construct a full URL to it and redirect to that.  We assume its the
      // same protocol, server and port and that the URI is context-relative.
      uri = request.getScheme() + "://" + request.getServerName() + ":" +
        request.getServerPort() + "/" + request.getContextPath() + uri;
      log.info("RedirectServlet sending redirect to: " + uri);
      ((HttpServletResponse)response).sendRedirect(uri);
    }

  } // End doPost()


} // End class.
