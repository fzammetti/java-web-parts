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
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import javawebparts.core.org.apache.commons.digester.Digester;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;


/**
 * The ResourceServerServlet can be used to serve resources from various
 * sources.  It uses an implementation of the ResourceStream interface,
 * each of which is specific to a given source.  There is an implementation
 * for serving resources from a JAR file in the classpath, from the file
 * system, from a URL and from a database.  Multiple instance of this servlet
 * can be used, under different names of course, each with its own set of
 * init parameters.
 * <br><br>
 * This servlet recognizes the following init parameters:
 * <br><br>
 * <ul>
 * <li><b>requestParameter</b> - This is the request parameter that will have
 * the value used to determine the resource to serve.  For instance, if you set
 * this to "res", and use the ResourceStreamFromURL ResourceStream
 * implementation, then the servlet expects to receive a request parameter
 * named "res" that is a URL, the contents from which will be returned.
 * Default: res.  Required: No.</li>
 * <li><b>resourceStreamClass</b> - This is the fully-qualified name of the
 * class which implements the ResourceStream interface.  This class is
 * responsible for returning to this servlet an InputStream to the resource to
 * be served.  Default: None.  Required: Yes.</li>
 * <li><b>serveUnbounded</b> - The value of this is either "true" or "false".
 * When set to true, it means that ANY requested resource that can be resolved
 * by the servlet (and the associated ResourceStream class) will be served.
 * In other words, if this servlet receives a request for a resource in a JAR
 * that it can find, it will be served, whether you intend for it to be
 * servable or not.  When set to false, only resources listed in the named
 * mapping file will be served.  Setting this to true can be a security
 * issue obviously, so you need to be very careful when doing so, and you
 * will probably want to use some sort of security mechanism in front of this
 * servlet in that case.  Required: No.  Default: false (not that in this case
 * the servlet may not initialize because a mapping file would be required,
 * which may not be present).</li>
 * <li><b>resourceMappingFile</b> - This is a context-relative full path to a
 * configuration file that does two things.  First, it lists all the resources
 * that can be served, and no resource that is not listed can be served.
 * Second, it defines the content type for each resource.  Required: no (unless
 * serveUnbound is false, in which case it IS required).  Default: none.</li>
 * <li><b>defaultContentType</b> - This is simply the content type that will
 * be used if the content type is not specified otherwise.  The ResourceStream
 * implementation class is first asked for the contentType.  If it does not
 * supply it, the mapping file, if present, is checked,  If it is still not
 * determined, then the default value here is used.  Required: yes.
 * Default: none.</li>
 * <li><b>streamParam1</b> - This is an arbitrary piece of data that will be
 * passed to the ResourceStream implementation class for every requested
 * resource.  This can be whatever you want.  Required: no.  Default: none.</li>
 * <li><b>streamParam2</b> - This is another arbitrary piece of data that will
 * be passed to the ResourceStream implementation class for every requested
 * resource.  This can be whatever you want.  Required: no.  Default: none.</li>
 * </ul>
 * <br><br>
 * Here is an example configuration:
 * <br><br>
 * &lt;servlet&gt;<br>
 * &nbsp;&nbsp;&lt;servlet-name&gt;ServeResourceFromFileSystem
 * &lt;/servlet-name&gt;<br>
 * &nbsp;&nbsp;&lt;servlet-class&gt;javawebparts.servlet.ResourceServerServlet
 * &lt;/servlet-class&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;requestParameter
 * &lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;fsres&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;resourceStreamClass
 * &lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;javawebparts.servlet.
 * ResourceStreamFromFileSystem&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;serveUnbounded&lt;/param-name
 * &gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;false&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;resourceMappingFile
 * &lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;/WEB-INF/resource-mappings.xml
 * &lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;defaultContentType
 * &lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;dummy&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &lt;/servlet&gt;
 * <br><br>
 * You can write your own implementation of ResourceStream to serve resources
 * from other sources.  One logical implementation would be to serve from a
 * database.  In this case, you may decide that the request parameter will
 * be giving a key in a table.  You may also use the streamParams to name
 * the connection string and userID/password.  The getStream() method simply
 * needs to return an InputStream on the BLOB from the database.  It should
 * return null if the resource cannot be found (in which case nothing will be
 * returned by the servlet).  Also, if applicable, implement the
 * getContentType() method.  In the case of serving from a database, one
 * can envision having a field in the table that stores the content type.
 * You can of course just use the defaultContentType, or a mapping file,
 * whichever is most appropriate (it is likely such a database implementation
 * will be supplied with JWP in the future).
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class ResourceServerServlet extends HttpServlet {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javawebparts.core.org.apache.commons.digester.Digester");
      Class.forName("javax.servlet.http.HttpServlet");
      Class.forName("javax.servlet.http.HttpServletRequest");
      Class.forName("javax.servlet.http.HttpServletResponse");
      Class.forName("javax.servlet.ServletConfig");
      Class.forName("javax.servlet.ServletContext");
      Class.forName("javax.servlet.ServletException");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("ResourceServerServlet" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(ResourceServerServlet.class);


  /**
   * requestParameter is the name of the request parameter that will be
   * examined to determine what the requested resource is.
   */
  private String requestParameter;


  /**
   * resourceStreamClass is the class that implements the ResourceStream
   * interface and knows how to provide a stream to a requested resource,
   * and optionally the contentType for the resource.
   */
  private String resourceStreamClass;


  /**
   * resourceMappingFile is the context-relative path to a file which maps
   * requested resources to contentTypes.  This file is optional in many
   * cases, but not always, depending on how other init parameters are set.
   */
  private String resourceMappingFile;


  /**
   * serveUnbounded, when set to true, means that any resource that the servlet,
   * and by extension the applicable ResourceStream class, can find.  You need
   * to be careful when setting this to true because it potentially opens up
   * security holes (i.e., resources you don't intend might be reachable).
   */
  private boolean serveUnbounded;


  /**
   * If the ResourceStream implementation class doesn't provide the contentType,
   * and if no mapping file is used, then this default contentType will be set
   * for each resource served.
   */
  private String defaultContentType;


  /**
   * mappings is a collection of ResourceMapping objects (which correspond to
   * <mapping> elements in a mapping file) for each resource.
   */
  private HashMap mappings = new HashMap();


  /**
   * streamParam1 is an arbitrary piece of information passed to the
   * ResourceStream implementation class.
   */
  private String streamParam1;


  /**
   * streamParam2 is an arbitrary piece of information passed to the
   * ResourceStream implementation class.
   */
  private String streamParam2;


 /**
   * init.
   *
   * @param  config           ServletConfig.
   * @throws ServletException ServletException.
   */
  public void init(ServletConfig config) throws ServletException {

    super.init(config);

    // streamParam1 and streamParam2.
    streamParam1 = config.getInitParameter("streamParam1");
    streamParam2 = config.getInitParameter("streamParam2");

    // requestParameter.
    String rp = config.getInitParameter("requestParameter");
    if (rp == null || rp.equalsIgnoreCase("")) {
      log.info("init parameter requestParameter not found, " +
        "defaulting to 'res'");
      requestParameter = "res";
    } else {
      requestParameter = rp;
    }

    // resourceStreamClass.
    resourceStreamClass = config.getInitParameter("resourceStreamClass");
    if (resourceStreamClass == null ||
      resourceStreamClass.equalsIgnoreCase("")) {
      String e = "Required init parameter resourceStreamClass not present";
      log.error(e);
      throw new ServletException(e);
    }

    // defaultContentType.
    defaultContentType = config.getInitParameter("defaultContentType");
    if (defaultContentType == null || defaultContentType.equalsIgnoreCase("")) {
      String e = "Required init parameter defaultContentType not present";
      log.error(e);
      throw new ServletException(e);
    }

    // serveUnbounded.
    String su = config.getInitParameter("serveUnbounded");
    if (su == null || su.equalsIgnoreCase("")) {
      log.info("init parameter serveUnbounded not found, " +
        "defaulting to false");
      serveUnbounded = false;
    } else {
      if (su.equalsIgnoreCase("true")) {
        serveUnbounded = true;
      } else if (su.equalsIgnoreCase("false")) {
        serveUnbounded = false;
      } else {
        String e = "init parameter serveUnbounded value should be 'true' " +
          "or 'false'";
        log.error(e);
        throw new ServletException(e);
      }
    }

    // resourceMappingFile.
    String rmf = config.getInitParameter("resourceMappingFile");
    if (rmf == null || rmf.equalsIgnoreCase("")) {
      resourceMappingFile = null;
    } else {
      resourceMappingFile = rmf;
    }
    if (!serveUnbounded && resourceMappingFile == null) {
      String e = "When serveUnbounded is false, resourceMappingFile is " +
        "required";
      log.error(e);
      throw new ServletException(e);
    }

    // Read in mapping file, if applicable.
    if (resourceMappingFile != null) {
      try {
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.push(this);
        digester.addObjectCreate("resourceMappings/mapping",
          "javawebparts.servlet.ResourceServerServlet$ResourceMapping");
        digester.addSetProperties("resourceMappings/mapping");
        digester.addSetNext("resourceMappings/mapping", "addMapping");
        // Get a stream on the config file and have Digester do its thing.
        ServletContext servletContext = config.getServletContext();
        InputStream isMappingsFile =
          servletContext.getResourceAsStream(resourceMappingFile);
        digester.parse(isMappingsFile);
      } catch (IOException ioe) {
        log.error("IOException reading mappings file.");
      } catch (SAXException saxe) {
        log.error("SAXException reading mappings file.  XML valid?");
      }
    }

    log.info(this);
    log.info("init() completed");

  } // End init().


   /**
   * Add a ResourceMapping to the mappings collection.
   *
   * @param rm The ResourceMapping object to add to the collection.
   */
  public void addMapping(ResourceMapping rm) {

    mappings.put(rm.getResource(), rm);

  } // End addMapping().


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

    // Get the requested resource.
    String requestedResource = request.getParameter(requestParameter);
    if (requestedResource == null) {
      log.info("No resource requested");
      return;
    }

    // Get the ResourceMapping from the mappings collection, if present.
    ResourceMapping rm = (ResourceMapping)mappings.get(requestedResource);

    // If serveUnbounded is false, then we have to be sure we have a match in
    // the mapping file, and if not, abort.
    if (!serveUnbounded && rm == null) {
      return;
    }

    InputStream    is = null;
    ResourceStream rs = null;
    // Instantiate the ResourceStream implementation class and get a stream
    // on the resource.
    try {
      try {
        Class clazz = Class.forName(resourceStreamClass);
        rs = (ResourceStream)clazz.newInstance();
        is = rs.getStream(requestedResource, streamParam1, streamParam2,
          getServletContext());
      } catch (ClassNotFoundException cnfe) {
        throw new ServletException(cnfe);
      } catch (InstantiationException ie) {
        throw new ServletException(ie);
      } catch (IllegalAccessException iae) {
        throw new ServletException(iae);
      }
      if (is != null) {
        // First, give the ResourceStream class the chance to define the
        // content type.
        String contentType = rs.getContentType(requestedResource,
          streamParam1, streamParam2, getServletContext());
        // If the ResourceStream class didn't define the content type, see if
        // the ResourceMapping, if present, has it.
        if (rm != null) {
          contentType = rm.getContentType();
        }
        // If the content type still isn't set, use the default.
        if (contentType == null) {
          contentType = defaultContentType;
        }
        // Set the content type.
        response.setContentType(contentType);
        // Stream the resource back to the client.
        OutputStream out = response.getOutputStream();
        byte[] buffer = new byte[4 * 1024];
        int totalBytesRead = 0;
        int bytesRead;
        while ((bytesRead = is.read(buffer)) != -1) {
          out.write(buffer, 0, bytesRead);
          totalBytesRead += bytesRead;
        }
        // Set the response size.
        response.setContentLength(totalBytesRead);
      }
    } finally {
      if (is != null) {
        is.close();
      }
    }

  } // End doPost().


  /**
   * Overriden toString method.
   *
   * @return A reflexively-built string representation of this bean.
   */
  public String toString() {

    String str = null;
    StringBuffer sb = new StringBuffer(1000);
    sb.append("[" + super.toString() + "]={");
    boolean firstPropertyDisplayed = false;
    try {
      Field[] fields = this.getClass().getDeclaredFields();
      for (int i = 0; i < fields.length; i++) {
        if (firstPropertyDisplayed) {
          sb.append(", ");
        } else {
          firstPropertyDisplayed = true;
        }
        sb.append(fields[i].getName() + "=" + fields[i].get(this));
      }
      sb.append("}");
      str = sb.toString().trim();
    } catch (IllegalAccessException iae) {
      iae.printStackTrace();
    }
    return str;

  } // End toString().


  /**
   * ResourceMapping is an inner class which holds the information for each
   * <mapping> element in the mapping file, if present.
   */
  public static class ResourceMapping {


    /**
     * The value of the request parameter that points to the resource.
     */
    private String resource;


    /**
     * The contentType of the resource.
     */
    private String contentType;


    /**
     * Accessor for resource field.
     *
     * @return String Current value of resource Field.
     */
    public String getResource() {

      return resource;

    } // End getResource().


    /**
     * Mutator for resource field.
     *
     * @param inResource New value of resource field.
     */
    public void setResource(String inResource) {

      resource = inResource;

    } // End setResource().


    /**
     * Accessor for contentType field.
     *
     * @return String Current value of contentType Field.
     */
    public String getContentType() {

      return contentType;

    } // End getContentType().


    /**
     * Mutator for contentType field.
     *
     * @param inContentType New value of contentType field.
     */
    public void setContentType(String inContentType) {

      contentType = inContentType;

    } // End setContentType().


    /**
     * Overriden toString method.
     *
     * @return A reflexively-built string representation of this bean.
     */
    public String toString() {

      String str = null;
      StringBuffer sb = new StringBuffer(1000);
      sb.append("[" + super.toString() + "]={");
      boolean firstPropertyDisplayed = false;
      try {
        Field[] fields = this.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
          if (firstPropertyDisplayed) {
            sb.append(", ");
          } else {
            firstPropertyDisplayed = true;
          }
          sb.append(fields[i].getName() + "=" + fields[i].get(this));
        }
        sb.append("}");
        str = sb.toString().trim();
      } catch (IllegalAccessException iae) {
        iae.printStackTrace();
      }
      return str;

    } // End toString().


  } // End inner class.


} // End class.
