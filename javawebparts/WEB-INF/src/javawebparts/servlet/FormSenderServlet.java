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


import java.io.BufferedInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import javawebparts.core.org.apache.commons.lang.text.StrSubstitutor;
import javawebparts.request.RequestHelpers;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**

 * The FormSenderServlet allows you to accept the submission of a form and take
 * values from that form, insert them into a template, and send the resultant
 * text as an eMail message to a specified address or list of addresses.
 *
 * <br><br>
 * Example configuration in web.xml:
 * <br><br>
 * &lt;servlet&gt;<br>
 * &nbsp;&nbsp;&lt;servlet-name&gt;FormSenderServlet&lt;/servlet-name&gt;<br>
 * &nbsp;&nbsp;&lt;servlet-class&gt;javawebparts.servlet.FormSenderServlet<br>
 * &nbsp;&nbsp;&lt;/servlet-class&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;smtpHost&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;????&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;smtpLogonRequired
 * &lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;true&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;smtpUsername&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;????&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;smtpPassword&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;????&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;fromAddress&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt
 * ;FormSenderServlet@javawebparts.sourceforge.net<br>&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;toAddresses&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;????&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;subject&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;This is a test
 * &lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;staticTokens&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;ccList=Bill in Accounting,
 * Dana in Legal~~deskOf=Beelzebub&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;templateFile&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;/WEB-INF/eMailTemplate.txt
 * &lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &nbsp;&nbsp;&lt;init-param&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-name&gt;pageAfter&lt;/param-name&gt;<br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&lt;param-value&gt;
 * /packages_jsp/servlet/mailSent.jsp&lt;/param-value&gt;<br>
 * &nbsp;&nbsp;&lt;/init-param&gt;<br>
 * &lt;/servlet&gt;<br>
 * <br>
 * Init parameters explained:
 * <br>
 * <ul>
 * <li><b>smtpHost</b> - This is the address of the SMTP server that will be
 * used to send the eMail.  Required: Yes.  Default: None.
 * <li><b>smtpLogonRequired</b> - This tells whether the SMTP server requires
 * logon to send messages.  Value is either "true" or "false".
 * Required: No.  Default: "false".
 * <li><b>smtpUsername</b> - This is the username that will be used to log on
 * to the SMTP server, if smtpLogonRequired is "true".
 * Required: No, unless smtpLogonRequired is "true".  Default: None.
 * <li><b>smtpUsername</b> - This is the password that will be used to log on
 * to the SMTP server, if smtpLogonRequired is "true".
 * Required: No, unless smtpLogonRequired is "true".  Default: None.
 * <li><b>fromAddress</b> - This is the eMail address that display as the
 * address the eMail was sent from.  Required: No.
 * Default: "FormSenderServlet@javawebparts.sourceforge.net".
 * <li><b>toAddresses</b> - This is the eMail address to send the message to.
 * This can also be a comma-separated list of addresses, the message will be
 * sent to all of them (note that all of them will be the To address, so if you
 * have privacy concerns where you might want to Bcc someone instead, you can't
 * do it with this servlet).  Required: Yes (at least one).  Default: None.
 * <li><b>subject</b> - This is the subject of the eMail.
 * Required: No.  Default: "Message from FormSenderServlet".
 * <li><b>staticTokens</b> - This is a list of values to statically insert into
 * the eMail template.  This allows you to have some dynamic information that
 * you can update via configuration.  This is a name=value pair list, where
 * each name=value pair is separated with a double-tilde sequence (~~).  So,
 * for example, "companyName=Microsoft~~CEO=Steve Balmer" is a valid value for
 * this parameter..  Required: No.  Default: None.
 * <li><b>templateFile</b> - This is the context-relative file that is the
 * template for the eMail.  Required: Yes.  Default: None.
 * <li><b>pageAfter</b> - This is the name of the JSP to forward to once the
 * message has been sent.  Required: Yes.  Default: None.
 * </ul>
 * <br>
 * Creating an eMail template is simple.  It is just plain text with some
 * replacement tokens present.  The tokens are in the form ${xxx}, where xxx
 * is the name of the token.  The tokens are replaced with values from one of
 * two places: the static tokens defined in the staticTokens parameter, and
 * incoming request parameters.  Every request parameter is captured into a
 * map, and this map is then used to populate the tokens.
 * <br><br>
 * So, for example, if you have an eMail template that contains the token
 * ${firstName}, and you have a form submitted that has a text field named
 * firstName, the token in the eMail template will be replaced with the value
 * of the incoming firstName parameter form the form.  Likewise, if you define
 * a static token "lastName=Hedburg" in web.xml, and in the eMail template
 * there is a ${lastName} token, then it will be replaced with "Hedburg".
 * <br><br>
 * In addition to the staticTokens, there are some built-in static tokens you
 * can use:
 * <br>
 * <ul>
 * <li>currentDate - This will insert the current date, in "MM/dd/yyyy"
 * format.</li>
 * <li>currentTime - This will insert the current date, in "hh:mm:ss a"
 * format.</li>
 * </ul>
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class FormSenderServlet extends HttpServlet {


  /**
   * This static initializer block tries to load all the classes this one
   * depends on (those not from standard Java anyway) and prints an error
   * meesage if any cannot be loaded for any reason.
   */
  static {
    try {
      Class.forName("javax.mail.MessagingException");
      Class.forName("javax.mail.internet.InternetAddress");
      Class.forName("javax.mail.internet.MimeMessage");
      Class.forName("javax.mail.Message");
      Class.forName("javax.mail.Session");
      Class.forName("javax.mail.Transport");
      Class.forName("javax.servlet.http.HttpServlet");
      Class.forName("javax.servlet.http.HttpServletRequest");
      Class.forName("javax.servlet.http.HttpServletResponse");
      Class.forName("javax.servlet.ServletConfig");
      Class.forName("javax.servlet.ServletContext");
      Class.forName("javax.servlet.ServletException");
      Class.forName("org.apache.commons.logging.Log");
      Class.forName("org.apache.commons.logging.LogFactory");
    } catch (ClassNotFoundException e) {
      System.err.println("FormSenderServlet" +
        " could not be loaded by classloader because classes it depends" +
        " on could not be found in the classpath...");
      e.printStackTrace();
    }
  }


  /**
   * Log instance.
   */
  private static Log log = LogFactory.getLog(FormSenderServlet.class);


  /**
   * The SMTP host name to send the message through.
   */
  private String smtpHost;


  /**
   * Username to log on to the SMTP server (if smtpRequiresLogin is "true").
   */
  private String smtpUsername;


  /**
   * Password to log on to the SMTP server (if smtpRequiresLogin is "true").
   */
  private String smtpPassword;


  /**
   * Flag: Does the SMTP server require logon credentials or not?
   */
  private boolean smtpLogonRequired;


  /**
   * The from address used when sending the eMail.
   */
  private String fromAddress = "FormSenderServlet@javawebparts.sourceforge.net";


  /**
   * The list of eMail addresses to send eMail to.
   */
  private ArrayList toAddresses = new ArrayList();


  /**
   * The subject of the eMail to send.
   */
  private String subject = "Message from FormSenderServlet";


  /**
   * The path and name of the context-relative eMail template file.
   */
  private String templateFile;


  /**
   * The actual eMail template.
   */
  private String template;


  /**
   * The JSP to forward to after sending the message.
   */
  private String pageAfter;


  /**
   * The list of static tokens configured.
   */
  private HashMap staticTokens = new HashMap();


  /**
   * init.
   *
   * @param  config           ServletConfig.
   * @throws ServletException ServletException.
   */
  public void init(ServletConfig config) throws ServletException {

    super.init(config);

    // Get init parameters that may be present.
    smtpHost                  = config.getInitParameter("smtpHost");
    smtpUsername              = config.getInitParameter("smtpUsername");
    smtpPassword              = config.getInitParameter("smtpPassword");
    templateFile              = config.getInitParameter("templateFile");
    pageAfter                 = config.getInitParameter("pageAfter");
    String smtpLogonRequiredP = config.getInitParameter("smtpLogonRequired");
    String fromAddressP       = config.getInitParameter("fromAddress");
    String toAddressesP       = config.getInitParameter("toAddresses");
    String subjectP           = config.getInitParameter("subject");
    String staticTokensP      = config.getInitParameter("staticTokens");

    // Validate parameters.
    if (smtpHost == null || smtpHost.equalsIgnoreCase("")) {
      String es = "Could not initialize " +
        "because mandatory smtpHost init parameter was not found";
      log.error(getClass().getName() + ".init(): " + es);
      throw new ServletException(es);
    }
    log.info("smtpHost = " + smtpHost);
    if (smtpLogonRequiredP != null) {
      if (!smtpLogonRequiredP.equalsIgnoreCase("true") &
        !smtpLogonRequiredP.equalsIgnoreCase("false")) {
        String es = "Could not initialize " +
          "because value of smtpLogonRequired was not 'true' or 'false'";
          log.error(getClass().getName() + ".init(): " + es);
          throw new ServletException(es);
      } else {
        smtpLogonRequired = Boolean.getBoolean(smtpLogonRequiredP);
        if (smtpLogonRequired) {
          if (smtpUsername == null || smtpUsername.equalsIgnoreCase("") ||
            smtpPassword == null || smtpPassword.equalsIgnoreCase("")) {
            String es = "Note: smtpUsername and/or " +
              "smtpPassword init parameter was not present or was empty.  If " +
              "your SMTP server does not require authentication, this is " +
              "fine, but if authentication is required, this servlet will " +
              "not operate properly until these parameters are supplied.";
            log.info(getClass().getName() + ".init(): " + es);
          }
        }
        log.info("smtpLogonRequired = " + smtpLogonRequired);
        log.info("smtpUsername = " + smtpUsername);
        log.info("smtpPassword = " + smtpPassword);
      }
    }
    if (toAddressesP == null || toAddressesP.equalsIgnoreCase("")) {
      String es = "Could not initialize " +
        "because mandatory toAddresses init parameter was not found";
      log.error(getClass().getName() + ".init(): " + es);
      throw new ServletException(es);
    }
    log.info("toAddressesP = " + toAddressesP);
    if (templateFile == null || templateFile.equalsIgnoreCase("")) {
      String es = "Could not initialize " +
        "because mandatory templateFile init parameter was not found";
      log.error(getClass().getName() + ".init(): " + es);
      throw new ServletException(es);
    }
    log.info("templateFile = " + templateFile);
    if (pageAfter == null || pageAfter.equalsIgnoreCase("")) {
      String es = "Could not initialize " +
        "because mandatory pageAfter init parameter was not found";
      log.error(getClass().getName() + ".init(): " + es);
      throw new ServletException(es);
    }
    log.info("pageAfter = " + pageAfter);
    if (fromAddressP != null) {
      fromAddress = fromAddressP;
    }
    log.info("fromAddress = " + fromAddress);
    if (subjectP != null) {
      subject = subjectP;
    }
    log.info("subject = " + subject);

    // Tokenize toAddresses value to populate List.
    StringTokenizer st = new StringTokenizer(toAddressesP, ",");
    while (st.hasMoreTokens()) {
      toAddresses.add(st.nextToken());
    }
    log.info("toAddresses = " + toAddresses);

    // Create map of static tokens.  Note that things will break downstream
    // if the value of the staticTokens init param isn't in the right form,
    // i.e., isn't a CSV list of token=value pairs, but we're going to live
    // with that.
    st = new StringTokenizer(staticTokensP, "~~");
    while (st.hasMoreTokens()) {
      String nextToken = st.nextToken();
      StringTokenizer st1 = new StringTokenizer(nextToken, "=");
      staticTokens.put(st1.nextToken(), st1.nextToken());
    }
    log.info("staticTokens = " + staticTokens);

    // Read in the template file.
    BufferedInputStream isTemplateFile = null;
    try {
      ServletContext servletContext = config.getServletContext();
      isTemplateFile = new BufferedInputStream(
        servletContext.getResourceAsStream(templateFile));
      int nextChar;
      StringBuffer sb = new StringBuffer(4096);
      nextChar = isTemplateFile.read();
      while (nextChar != -1) {
        sb.append((char)nextChar);
        nextChar = isTemplateFile.read();
      }
      log.info("template = " + sb);
      template = sb.toString();
    } catch (IOException ioe) {
      String es = "Problem reading eMail template file: " + ioe;
      log.error(getClass().getName() + ".init(): " + es);
      throw new ServletException(es);
    } finally {
      try {
        if (isTemplateFile != null) {
          isTemplateFile.close();
        }
      } catch (IOException e) {
        String es = "Problem closing stream to eMail template file " +
          "(servlet might still work): " + e;
        log.info(getClass().getName() + ".init(): " + es);
      }
    }

    // Add to static tokens any "special" tokens we're going to support.
    staticTokens.put("currentDate",
      new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
    staticTokens.put("currentTime",
      new SimpleDateFormat("hh:mm:ss a").format(new Date()));

    // Do static token replacements so we don't have to do it with every
    // invocation.
    StrSubstitutor substitutor = new StrSubstitutor(staticTokens);
    template = substitutor.replace(template);
    log.info("Final template after static replacements = " + template);

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

    // Get all incoming parameters.
    Map requestParameters = RequestHelpers.getRequestParameters(request);

    // Do all token replacements.
    StrSubstitutor substitutor = new StrSubstitutor(requestParameters);
    String text = substitutor.replace(template);

    // Send the message,
    sendMessage(text);

    // finally, forward to pageAfter.
    request.getRequestDispatcher(pageAfter).forward(request, response);

  } // End doPost()


  /**
   * This method sends a message.
   *
   * @param  inText    The text of the message.
   */
  public void sendMessage(String inText) {

    Transport transport = null;

    try {

      // Construct Properties JavaMail needs.
      Properties props = new Properties();
      props.setProperty("mail.transport.protocol", "smtp");
      props.setProperty("mail.host", smtpHost);
      if (smtpLogonRequired) {
        props.setProperty("mail.user",     smtpUsername);
        props.setProperty("mail.password", smtpPassword);
      }
      log.debug("props = " + props + "\n\n");
      // Create a JavaMail message.
      Session session = Session.getDefaultInstance(props, null);
      log.debug("session = " + session + "\n\n");
      transport = session.getTransport();
      log.debug("transport = " + transport + "\n\n");
      MimeMessage message = new MimeMessage(session);
      // Populate the data for the message.
      for (Iterator it = toAddresses.iterator(); it.hasNext();) {
        message.addRecipient(Message.RecipientType.TO,
          new InternetAddress((String)it.next()));
      }
      message.setFrom(new InternetAddress(fromAddress));
      message.setSubject(subject);
      message.setContent(inText, "text/plain");
      // Send it!
      transport.connect();
      transport.sendMessage(message,
        message.getRecipients(Message.RecipientType.TO));
    } catch (MessagingException me) {
      me.printStackTrace();
      log.error("Error sending message: " + me);
    } finally {
      try {
        if (transport != null) {
          transport.close();
        }
      } catch (MessagingException me) {
        log.error("Exception closing transport: " + me);
      }
    }

  } // End sendMessage().


} // End class.
