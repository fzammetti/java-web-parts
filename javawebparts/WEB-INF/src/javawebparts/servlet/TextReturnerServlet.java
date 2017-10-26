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
import java.io.PrintWriter;
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
 * This servlet returns a string from an XML file.  Each invocation of the
 * servlet returns one of the items, either randomly (with no repeats until
 * all items have been shown) or in forward or reverse order.
 * <br><br>
 * Example configuration in web.xml:
 * <br><br>
 * &lt;servlet&gt;<br>
 * &nbsp;&nbsp;&lt;servlet-name&gt;TextReturnerServlet&lt;/servlet-name&gt;<br>
 * &nbsp;&nbsp;&lt;servlet-class&gt;javawebparts.servlet.
 * TextReturnerServlet&lt;/servlet-class&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;order&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;random&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;itemsFile&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;/WEB-INF/text_returner_items.xml
 * &lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &lt;/servlet&gt;
 * <br><br>
 * &lt;servlet-mapping&gt;<br>
 * &nbsp;&nbsp;&lt;servlet-name&gt;TextReturnerServlet&lt;/servlet-name&gt;<br>
 * &nbsp;&nbsp;&lt;url-pattern&gt;/textReturner&lt;/url-pattern&gt;<br>
 * &lt;/servlet-mapping&gt;
 * <br><br>
 * Example items configuration file:
 * <br><br>
 * &lt;TextReturnerServletItems&gt;
 * &nbsp;&nbsp;&lt;item&gt;0 - It's always darkest before the dawn&lt;/item&gt;
 * &nbsp;&nbsp;&lt;item&gt;1 - A journey of a thousand miles begins with a
 * single step&lt;/item&gt;
 * &nbsp;&nbsp;&lt;item&gt;2 - The universe is like stupidity: both are
 * infinite... and actually, I'm not sure about the universe&lt;/item&gt;
 * &nbsp;&nbsp;&lt;item&gt;3 - I believe there is intelligent life in the
 * universe, except for some parts of New Jersey&lt;/item&gt;
 * &nbsp;&nbsp;&lt;item&gt;4 - If your falling of a cliff, you might as well
 * try and fly&lt;/item&gt;
 * &nbsp;&nbsp;&lt;item&gt;5 - The cost of life is to chance the consequence
 * &lt;/item&gt;
 * &lt;/TextReturnerServletItems&gt;
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class TextReturnerServlet extends HttpServlet {


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
      System.err.println("TextReturnerServlet" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(TextReturnerServlet.class);


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
      digester.addObjectCreate("TextReturnerServletItems",
        "javawebparts.servlet.Items");
      digester.addBeanPropertySetter("TextReturnerServletItems/item", "item");
      // Get a stream on the config file and have Digester do its thing.
      ServletContext servletContext = config.getServletContext();
      InputStream isItemsFile = servletContext.getResourceAsStream(itemsFile);
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

    PrintWriter out = response.getWriter();
    out.println(items.getItem());

  } // End doPost()


} // End class.
