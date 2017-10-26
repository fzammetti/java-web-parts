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


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * This servlet serves a dynamically-rendered image of a string of text with
 * the specified font characteristics.
 * <br><br>
 * Example configuration in web.xml:
 * <br><br>
 * &lt;servlet&gt;<br>
 * &nbsp;&nbsp;&lt;servlet-name&gt;TextRendererServlet&lt;/servlet-name&gt;<br>
 * &nbsp;&nbsp;&lt;servlet-class&gt;javawebparts.servlet.
 * TextRendererServlet&lt;/servlet-class&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;fontName&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;arial&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;fontPoint&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;24&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;color&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;255,0,0
 * &lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;backColor&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;255,255,0
 * &lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;stylePlain&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;false&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;styleBold&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;true&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;styleItalic&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;true&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &lt;/servlet&gt;
 * <br><br>
 * &lt;servlet-mapping&gt;<br>
 * &nbsp;&nbsp;&lt;servlet-name&gt;TextRendererServlet&lt;/servlet-name&gt;<br>
 * &nbsp;&nbsp;&lt;url-pattern&gt;/textRenderer&lt;/url-pattern&gt;<br>
 * &lt;/servlet-mapping&gt;
 * <br><br>
 * Init parameters explained:
 * <br><br>
 * <ul>
 * <li><b>fontName</b> - This can be any font installed on the server that the
 * servlet runs on, and it is what font the text will be rendered with.  For
 * more information, see the Font class in the standard Java SDK, as this
 * value is used in the call to the constructor of that class.
 * Required: No.  Default: Arial.
 * <li><b>fontPoint</b> - This is what point size the text will be rendered in.
 * For more information, see the Font class in the standard Java SDK, as this
 * value is used in the call to the constructor of that class.
 * Required: No.  Default: 12.
 * <li><b>color</b> - This is a comma-separated list of red, green, blue
 * color component values (0-255 each) that will be mixed together and used
 * as the color of the text.  For example, 255,0,0 is pure red, 255,255,0
 * is yellow, 255,0,255 is purple, etc.
 * Required: No.  Default: 0,0,0 (black).
 * <li><b>backColor</b> - Same as the color, except this is the background
 * color of the rendered image.
 * Required: No.  Default: 255,255,255 (white).
 * <li><b>stylePlain</b> - One of the three font style paramters.  If set to
 * true, the text will be rendered in it's plain style.  Note that setting
 * plain=true overrides any setting of styleBold and styleItalics.
 * For more information, see the Font class in the standard Java SDK, as this
 * value is used in the call to the constructor of that class.
 * Required: No.  Default: true.
 * <li><b>styleBold</b> - One of the three font style parameters.  If set to
 * true, the text will be rendered in bold style.
 * For more information, see the Font class in the standard Java SDK, as this
 * value is used in the call to the constructor of that class.
 * Required: No.  Default: false.
 * <li><b>styleItalic</b> - One of the three font style parameters.  If set to
 * true, the text will be rendered in italic style.
 * For more information, see the Font class in the standard Java SDK, as this
 * value is used in the call to the constructor of that class.
 * Required: No.  Default: false.
 * </ul>
 * <br>
 * All of the above parameters can be passed in as request parameters to
 * the servlet as well, and the request parameters will override the servlet
 * init paramter values.  Also note that all of the above init parameters are
 * optional.  However, the font, fontPoint, color and backColor parameters
 * ARE IN FACT REQUIRED FOR THE SERVLET TO FUNCTION!  They <b>MUST</b> be
 * supplied either as init values or as request parameters.  You can mix and
 * match as appropriate, i.e., if you want to only render in Arial, then set
 * the fontName init parameter and do not pass a fontName request parameter in.
 * IF a particular parameter is found as neither an init parameter or a request
 * parameter, the above specified default values are used.  If the text
 * parameter is not found in request, a blank image will be returned, but no
 * error will occur.
 * <br><br>
 * The most common usage of this servlet is usually to use it as the src of
 * an &lt;img&gt; tag with some or all of the required parameters attached as a
 * query string.  For example:
 * <br><br>
 * &lt;img src="/TextRenderer?fontPoint=32&text=testing123"&gt;
 * <br><br>
 * The method that actually renders the text, appropriately named renderText(),
 * is public and stand-alone and available to any outside code, so if you want
 * to use the functionality of the servlet from your own code without actually
 * using it as a servlet, simply instantiate it as any other class and call this
 * method.  It accepts a Map which contains the following elements:
 * <br><br>
 * <ul>
 * <li>Integer colR
 * <li>Integer colG
 * <li>Integer colB
 * <li>Integer backR
 * <li>Integer backG
 * <li>Integer backB
 * <li>Integer fontPoint
 * <li>String fontName
 * <li>String stylePlain
 * <li>String styleBold
 * <li>String styleItalic
 * <li>String text
 * </ul>
 * <br>
 * They should all be familiar.  colR, colG and colB are the RGB components
 * of the foreground color, and backR, backG and backB are the RGB components
 * of the background color.
 * It returns an OutputStream (ByteArrayOutputStream specifically) whichis
 * the rendered image.  You can do whatever you want to with it at that point.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class TextRendererServlet extends HttpServlet {


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
      Class.forName("javax.servlet.ServletException");
      Class.forName("javax.servlet.ServletOutputStream");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("TextRendererServlet" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(TextRendererServlet.class);


  /**
   * font.
   */
  private String initFontName;


  /**
   * fontPoint.
   */
  private Integer initFontPoint;


  /**
   * stylePlain.
   */
  private String initStylePlain;


  /**
   * styleBold.
   */
  private String initStyleBold;


  /**
   * styleItalic.
   */
  private String initStyleItalic;


  /**
   * colR.
   */
  private Integer initColR;


  /**
   * colG.
   */
  private Integer initColG;


  /**
   * colB.
   */
  private Integer initColB;


  /**
   * backR.
   */
  private Integer initBackR;


  /**
   * backG.
   */
  private Integer initBackG;


  /**
   * backB.
   */
  private Integer initBackB;


  /**
   * init.
   *
   * @param  config           ServletConfig.
   * @throws ServletException ServletException.
   */
  public void init(ServletConfig config) throws ServletException {

    super.init(config);

    log.info("init()...");

    // Get init parameters that may be present.
    String inFontName    = config.getInitParameter("fontName");
    String inFontPoint   = config.getInitParameter("fontPoint");
    String inStylePlain  = config.getInitParameter("stylePlain");
    String inStyleBold   = config.getInitParameter("styleBold");
    String inStyleItalic = config.getInitParameter("styleItalic");
    String inColor       = config.getInitParameter("color");
    String inBackColor   = config.getInitParameter("backColor");

    // For each init parameter, set the default value if not present,
    // otherwise set it to the value read in.
    if (inFontName == null) {
      initFontName = "Arial";
    } else {
      initFontName = inFontName;
    }
    if (inFontPoint == null) {
      initFontPoint = new Integer(12);
    } else {
      initFontPoint = new Integer(Integer.parseInt(inFontPoint));
    }
    if (inStylePlain == null) {
      initStylePlain = "true";
    } else {
      initStylePlain = inStylePlain;
    }
    if (inStyleBold == null) {
      initStyleBold = "true";
    } else {
      initStyleBold = inStyleBold;
    }
    if (inStyleItalic == null) {
      initStyleItalic = "true";
    } else {
      initStyleItalic = inStyleItalic;
    }
    if (inColor == null) {
      initColR = new Integer(0);
      initColG = new Integer(0);
      initColB = new Integer(0);
    } else {
      StringTokenizer st = new StringTokenizer(inColor, ",");
      initColR = new Integer(Integer.parseInt(st.nextToken()));
      initColG = new Integer(Integer.parseInt(st.nextToken()));
      initColB = new Integer(Integer.parseInt(st.nextToken()));
    }
    if (inBackColor == null) {
      initBackR = new Integer(0);
      initBackG = new Integer(0);
      initBackB = new Integer(0);
    } else {
      StringTokenizer st = new StringTokenizer(inBackColor, ",");
      initBackR = new Integer(Integer.parseInt(st.nextToken()));
      initBackG = new Integer(Integer.parseInt(st.nextToken()));
      initBackB = new Integer(Integer.parseInt(st.nextToken()));
    }

    log.info("init() completed");

  } // End init().


  /**
   * doGet.  Calls doPost() to do real work.
   *
   * @param  request          HTTPServletRequest.
   * @param  response         HTTPServletResponse.
   * @throws ServletException ServletException.
   * @throws IOException      IOExcpetion.
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
   * @throws IOException      IOExcpetion
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    log.info("TextRendererServlet.doPost()...");

    // Get the request parameters that may be present.
    String inFontName    = request.getParameter("fontName");
    String inFontPoint   = request.getParameter("fontPoint");
    String inStylePlain  = request.getParameter("stylePlain");
    String inStyleBold   = request.getParameter("styleBold");
    String inStyleItalic = request.getParameter("styleItalic");
    String inColor       = request.getParameter("color");
    String inBackColor   = request.getParameter("backColor");
    String text          = request.getParameter("text");

    // For each request parameter, set the default value if not present,
    // otherwise set it to the value read in.
    HashMap m = new HashMap();
    if (inFontName == null) {
      m.put("fontName", initFontName);
    } else {
      m.put("fontName", inFontName);
    }
    if (inFontPoint == null) {
      m.put("fontPoint", initFontPoint);
    } else {
      m.put("fontPoint", new Integer(Integer.parseInt(inFontPoint)));
    }
    if (inStylePlain == null) {
      m.put("stylePlain", initStylePlain);
    } else {
      m.put("stylePlain", inStylePlain);
    }
    if (inStyleBold == null) {
      m.put("styleBold", initStyleBold);
    } else {
      m.put("styleBold", inStyleBold);
    }
    if (inStyleItalic == null) {
      m.put("styleItalic", initStyleItalic);
    } else {
      m.put("styleItalic", inStyleItalic);
    }
    if (inColor == null) {
      m.put("colR", initColR);
      m.put("colG", initColG);
      m.put("colB", initColB);
    } else {
      StringTokenizer st = new StringTokenizer(inColor, ",");
      m.put("colR", new Integer(Integer.parseInt(st.nextToken())));
      m.put("colG", new Integer(Integer.parseInt(st.nextToken())));
      m.put("colB", new Integer(Integer.parseInt(st.nextToken())));
    }
    if (inBackColor == null) {
      m.put("backR", initBackR);
      m.put("backG", initBackG);
      m.put("backB", initBackB);
    } else {
      StringTokenizer st = new StringTokenizer(inBackColor, ",");
      m.put("backR", new Integer(Integer.parseInt(st.nextToken())));
      m.put("backG", new Integer(Integer.parseInt(st.nextToken())));
      m.put("backB", new Integer(Integer.parseInt(st.nextToken())));
    }
    m.put("text", text);

    log.info(m);

    OutputStream os = renderText(m);
    if (response != null) {
      ServletOutputStream out = response.getOutputStream();
      if (out != null) {
        ((ByteArrayOutputStream)os).writeTo(out);
      }
    }

  } // End doPost()


  /**
   * This method is the method which actually renders a given text string
   * using the given font characteristics and returns the image as an
   * OutputStream (ByteArrayOutputStream specifically).
   *
   * @param  m           A Map containing all the required elements.  See the
   *                     javadocs for this class for details.
   * @return             An OutputStream (ByteArrayOutputStream specifically)
   *                     that is the final rendered image.
   * @throws IOException IOException
   */
  public static OutputStream renderText(Map m) throws IOException {

    int    colR        = ((Integer)m.get("colR")).intValue();
    int    colG        = ((Integer)m.get("colG")).intValue();
    int    colB        = ((Integer)m.get("colB")).intValue();
    int    backR       = ((Integer)m.get("backR")).intValue();
    int    backG       = ((Integer)m.get("backG")).intValue();
    int    backB       = ((Integer)m.get("backB")).intValue();
    int    fontPoint   = ((Integer)m.get("fontPoint")).intValue();
    String fontName    = (String)m.get("fontName");
    String stylePlain  = (String)m.get("stylePlain");
    String styleBold   = (String)m.get("styleBold");
    String styleItalic = (String)m.get("styleItalic");
    String text        = (String)m.get("text");
    // Calculate the value for the font style in the Font constructor.  It's
    // either going to be PLAIN, BOLD, ITALIC or BOLD | ITALIC.
    if (stylePlain == null) {
      stylePlain = "false";
    }
    if (styleBold == null) {
      styleBold = "false";
    }
    if (styleItalic == null) {
      styleItalic = "false";
    }
    int fontStyle = 0;
    if (stylePlain.equalsIgnoreCase("true")) {
      fontStyle = Font.PLAIN;
    } else if (styleBold.equalsIgnoreCase("true") &&
      styleItalic.equalsIgnoreCase("false")) {
      fontStyle = Font.BOLD;
    } else if (styleBold.equalsIgnoreCase("false") &&
      styleItalic.equalsIgnoreCase("true")) {
      fontStyle = Font.ITALIC;
    } else if (styleBold.equalsIgnoreCase("true") &&
      styleItalic.equalsIgnoreCase("true")) {
      fontStyle = Font.BOLD | Font.ITALIC;
    }
    if (text == null || text.equalsIgnoreCase("")) {
      text = " ";
    }

    // Render the text.
    Font f = new Font(fontName, fontStyle, fontPoint);
    BufferedImage fmimg =
                  new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
    Graphics2D fmg = fmimg.createGraphics();
    fmg.setFont(f);
    FontMetrics fm = fmg.getFontMetrics();
    int width = fm.stringWidth(text) + 8;
    int height = fm.getHeight();
    BufferedImage img =
      new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = img.createGraphics();
    g.setBackground(new Color(backR, backG, backB));
    g.clearRect(0, 0, width, height);
    g.setColor(new Color(colR, colG, colB));
    g.setFont(f);
    g.drawString(text, 4, fm.getAscent());

    // Get the image as an OutputStream and return it.
    OutputStream baos = new ByteArrayOutputStream();
    ImageIO.write((RenderedImage)img, "jpg", baos);
    return baos;

  } // End renderText().


} // End class.
