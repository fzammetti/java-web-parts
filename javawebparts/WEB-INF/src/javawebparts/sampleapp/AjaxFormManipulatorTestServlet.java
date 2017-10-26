/*
 * Copyright 2006 Herman van Rosmalen
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

package javawebparts.sampleapp;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This servlet returns a XML string with form manipulation values. Each
 * invocation of the servlet returns the same XML. <br>
 * This is used to demonstrate the stdFormManipulator for AjaxTag. <br>
 * <br>
 * Example configuration in web.xml: <br>
 * <br>
 * &lt;servlet&gt; <br>
 * &nbsp;&nbsp;&lt;servlet-name&gt;AjaxFormManipulatorTestServlet&lt;/servlet-name&gt;
 * <br>
 * &nbsp;&nbsp;&lt;servlet-class&gt;javawebparts.sampleapp.
 * AjaxFormManipulatorTestServlet&lt;/servlet-class&gt; <br>
 * &lt;/servlet&gt; <br>
 * &lt;servlet-mapping&gt; <br>
 * &nbsp;&nbsp;&lt;servlet-name&gt;AjaxFormManipulatorTestServlet&lt;/servlet-name&gt;
 * <br>
 * &nbsp;&nbsp;&lt;url-pattern&gt;/ajaxFormManipulatorTest&lt;/url-pattern&gt;
 * <br>
 * &lt;/servlet-mapping&gt; <br>
 * 
 * @author <a href="mailto:herros@gmail.com">Herman van Rosmalen </a>
 */
public class AjaxFormManipulatorTestServlet extends HttpServlet {

	/**
	 * Log instance.
	 */
	private static Log log = LogFactory.getLog(AjaxSelectTestServlet.class);

	/**
	 * doGet. Calls doPost() to do real work.
	 * 
	 * @param request
	 *            HTTPServletRequest.
	 * @param response
	 *            HTTPServletResponse.
	 * @throws ServletException
	 *             ServletException.
	 * @throws IOException
	 *             IOException.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);

	} // End doGet().

	/**
	 * doPost.
	 * 
	 * @param request
	 *            HTTPServletRequest.
	 * @param response
	 *            HTTPServletResponse.
	 * @throws ServletException
	 *             ServletException.
	 * @throws IOException
	 *             IOException.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String ajaxRef = request.getParameter("ajaxRef");
		// Parse the ajaxRef into the form and element parts
		StringTokenizer st = new StringTokenizer(ajaxRef, "/");
		String formRef = st.nextToken();
		String elementRef = st.nextToken();
		log.info("ajaxRef of parent form = " + formRef);
		log.info("ajaxRef of element = " + elementRef);

		StringBuffer result = new StringBuffer();

		result
			.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n")
			.append("<form name='")
			.append(formRef)
			.append("'>\n")
			.append("<formproperty name='action' value='##'/>\n")
			.append("<formproperty name='method' value='get'/>\n")
			.append("<element name='field1' value='field1value'>\n")
			.append("<property name='style.width' value='400px'/>\n")
			.append("<property name='style.color' value='red'/>\n")
			.append("</element>\n")
			.append("<element name='button1' value='can\"t do that anymore'>\n")
			.append("<property name='disabled' value='true'/>\n")
			.append("</element>\n")
			.append("</form>");

		PrintWriter out = response.getWriter();
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		out.println(result.toString());

	} // End doPost().

} // End class.
