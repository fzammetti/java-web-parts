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

package javawebparts.cookbook.wwm;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simple servlet that handles two ajax requests.
 * 1. It returns some xml for a selectbox handler (in fact the the same  
 *    as it would do for the standard selectbox handler) 
 * 2. It returns a jsp on the second request.    
 * 
 * @author <a href="mailto:herros@gmail.com">Herman van Rosmalen</a>.
 */
public class WeWantMoreServlet extends HttpServlet {
	
	private int count = 3;

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
		if ("selectValues".equals(method)) {
			fillSelectBox(request, response);
		} else {
			returnNextRow(request, response);
		}

	} // End doPost().

	/**
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	private void fillSelectBox(HttpServletRequest request,
			HttpServletResponse response) throws IOException {

		response.setContentType("text/xml");
		response.setHeader("Cache-Control", "no-cache");
		PrintWriter out = response.getWriter();

		out.print("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
		out.print("<list>");
		for (int i = 0; i < getCount(); i++) {
			out.print("<option value='");
			out.print(getCount());
			out.print("-");
			out.print(i);
			out.print("'>Value ");
			out.print(i);
			out.print("</option>");
		}
		out.print("</list>");
		setCount(getCount()+1);

	}

	/**
	 * @param request
	 * @param response
	 * @throws IOException
	 * @throws ServletException 
	 */
	private void returnNextRow(HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException {
		
		request.getRequestDispatcher("formLine.jsp").forward(request, response);

	}
    
	/**
	 * @return the counter.
	 */
	public int getCount() {
		return count;
	}

	/**
	 * @param count.
	 */
	public void setCount(int count) {
		this.count = count;
	}
	
} // End class.

