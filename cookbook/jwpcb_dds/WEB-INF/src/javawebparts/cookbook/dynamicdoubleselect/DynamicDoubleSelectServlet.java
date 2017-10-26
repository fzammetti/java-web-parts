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

package javawebparts.cookbook.dynamicdoubleselect;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;

/**
 * Simple servlet that returns the HTML for a select element containing a list
 * of characters from the requested TV show, or an empty select if the request
 * value is not known.
 * 
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti </a>.
 */
public class DynamicDoubleSelectServlet extends HttpServlet {

	private static String[] Babylon5 = { "Delenn", "G'Kar", "Jeffrey Sinclair",
			"John Sheridan", "Lennier", "Londo Mollari", "Marcus Cole",
			"Michael Garibaldi", "Vir Cotto" };

	private static String[] BattlestarGalactica = { "Bill Adama",
			"Gaius Baltar", "Kara Thrace", "Laura Roslin", "Lee Adama",
			"Number Six", "Saul Tigh", "Sharon Valerii" };

	private static String[] StarTrekTheNextGeneration = { "Beverly Crusher",
			"Data", "Deanna Troi", "Geordi LaForge", "Jean Luc Picard",
			"Wesley Crusher", "William Riker", "Worf" };

	private static String[] StarTrekTheOriginalSeries = { "James Kirk",
			"Leonard McCoy", "Scottie", "Spock", "Uhura",
			"Unnamed Redshirt Guy" };

	private static String[] StargateAtlantis = { "Aiden Ford",
			"Carson Beckett", "Elizabeth Weir", "John Sheppard",
			"Rodney Mckay", "Teyla Emmagan" };

	private static String[] StargateSG1 = { "Cameron Mitchell",
			"Daniel Jackson", "George Hammond", "Hank Landry", "Jack O'Neill",
			"Samantha Carter", "Teal'c" };

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
	 *             IOExcpetion.
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
	 *             IOExcpetion.
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String method = (String) request.getParameter("method");
		if ("first".equals(method)) {
			method1(request, response);
		} else {
			method2(request, response);
		}

	} // End doPost().

	/**
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void method2(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		String showTitle = (String) request.getParameter("showTitle");
		String[] characters = getCharacters(showTitle);
		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out = response.getWriter();

		out.print("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		out.print("<list>");
		for (int i = 0; i < characters.length; i++) {
			out.print("<option value='");
			out.print(showTitle);
			out.print("-");
			out.print(i);
			out.print("'>");
			out.print(characters[i]);
			out.print("</option>");
		}
		out.print("</list>");

	}

	/**
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void method1(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		String showTitle = (String) request.getParameter("showTitle");
		PrintWriter out = response.getWriter();
		out.println("<select>\n");
		String[] characters = getCharacters(showTitle);
		if (characters != null) {
			for (int i = 0; i < characters.length; i++) {
				out.println("  <option>" + characters[i] + "</option>\n");
			}
		}
		out.println("</select>\n");

	}

	/**
	 * @param showTitle
	 * @response String[]
	 */
	private String[] getCharacters(String showTitle) {
		String[] characters = null;

		if (showTitle == null) {
			showTitle = "";
		}
		System.out.println("showTitle = " + showTitle);
		if (showTitle.equalsIgnoreCase("B5")) {
			characters = Babylon5;
		}
		if (showTitle.equalsIgnoreCase("BSG")) {
			characters = BattlestarGalactica;
		}
		if (showTitle.equalsIgnoreCase("STTNG")) {
			characters = StarTrekTheNextGeneration;
		}
		if (showTitle.equalsIgnoreCase("STTOS")) {
			characters = StarTrekTheOriginalSeries;
		}
		if (showTitle.equalsIgnoreCase("SGA")) {
			characters = StargateAtlantis;
		}
		if (showTitle.equalsIgnoreCase("SG1")) {
			characters = StargateSG1;
		}
		return characters;
	}
} // End class.
