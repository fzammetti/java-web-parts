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


package javawebparts.misc;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import javawebparts.core.JWPHelpers;


/**
 * This class is a simple Java application that accepts on the command line
 * the name of a file that was created by the RequestRecorderFilter.  It
 * plays back the requests in that file the specified number of times, or
 * continuously.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public final class RecordedRequestPlayer {


  /**
   * Constructor to avoid static analysis error.
   */
  private RecordedRequestPlayer() { }


  /**
   * Standard Java app entry point.
   *
   * @param args Command line arguments.
   */
  public static void main(String[] args) {

    System.out.println("\nJava Web Parts - RecordedRequestPlayer\n");

    // Get the source filename and the number of times to run through it and
    // the number of threads to spawn.  If any of the parameters are not valid
    // for any reason, display usage information and exit.
    if (args.length < 3) {
      displayUsageInfo();
      System.exit(-1);
    }
    String fileName    = args[0];
    String numReps     = args[1];
    String numThreads  = args[2];
    int    iNumReps    = 0;
    int    iNumThreads = 0;
    try {
      iNumReps = Integer.parseInt(numReps);
    } catch (NumberFormatException nfe) {
      displayUsageInfo();
      System.exit(-1);
    }
    if (iNumReps == 0) {
      // I know the usage info says 0 is continuous, but I figure 2 BILLION
      // is as close to running forever as one could expect...  If anyone ever
      // runs a series of requests 2 billion times, they deserve to be able
      // to call me a liar I figure! :)
      iNumReps = 2000000000;
    }
    try {
      iNumThreads = Integer.parseInt(numThreads);
    } catch (NumberFormatException nfe) {
      displayUsageInfo();
      System.exit(-1);
    }
    if (iNumThreads <= 0) {
      displayUsageInfo();
      System.exit(-1);
    }

    // Read in the file into an array, one element per line (per request),
    // dealing with any problems that occur.
    String[] requests = null;
    try {
      requests = JWPHelpers.readFromFile(fileName);
    } catch (FileNotFoundException fnfe) {
      System.out.println("The file " + fileName + " could not be found, " +
                         "exiting.");
      System.exit(-1);
    } catch (IOException ioe) {
      System.out.println("IOException reading file: " + ioe);
      System.exit(-1);
    }
    if (requests == null || requests.length == 0) {
      System.out.println("No requests found in file, exiting.");
      System.exit(0);
    }

    // Now, transfer each request into a HashMap and add it to an ArrayList.
    // This is done basically so that we'll know about any problems with the
    // file here, rather than in the threads.  Also, no need to do this work
    // for each request in each thread, it's much simpler and more efficient
    // this way.
    ArrayList requestsAL = new ArrayList();
    for (int i = 0; i < requests.length; i++) {
      HashMap hm = new HashMap();
      StringTokenizer st = new StringTokenizer(requests[i], ",");
      hm.put("method", (String)st.nextToken());
      hm.put("theURL", (String)st.nextToken());
      HashMap params = new HashMap();
      // Now handle each parameter, if any.
      while (st.hasMoreTokens()) {
        String          param = (String)st.nextToken();
        StringTokenizer st1   = new StringTokenizer(param, "=");
        String          pName = (String)st1.nextToken();
        String          pVal  = (String)st1.nextToken();
        params.put(pName, pVal);
      }
      hm.put("parameters", params);
      requestsAL.add(hm);
    }

    // Spawn the requested number of threads and start each.
    System.out.println("Running...\n");
    RunnerThread[] threads = new RunnerThread[iNumThreads];
    for (int i = 0; i < iNumThreads; i++) {
      threads[i] = new RunnerThread();
      threads[i].setReps(iNumReps);
      threads[i].setID(i + 1);
      threads[i].setRequests(requestsAL);
      threads[i].setNumThreads(iNumThreads);
      threads[i].start();
    }

  } // End main().


  /**
   * When any problems are detected when parsing the command line parameters,
   * display usage information.  This will also happen when no command line
   * parameters are used at all, which is how usage information will be
   * displayed when a user doesn't know how to use the program and wants to
   * see usage information.
   */
  public static void displayUsageInfo() {

    System.out.println("This is a java application that will \"play  back\" " +
                       "a series of requests");
    System.out.println("recorded with the RequestRecorderFilter.  It accepts " +
                       "two required command");
    System.out.println("line parameters:\n");
    System.out.println("1. The filename containing the recorded requests " +
                       "(full path to it)");
    System.out.println("2. The number of times to run through the file, or " +
                       "0 for continuous");
    System.out.println("3. The number of concurrent threads to spawn to run " +
                       "through the parameters");
    System.out.println("\nHere is a usage example:\n");
    System.out.println("RecordedRequestPlayer c:\\temp\\saveFile.csv 5 3\n");
    System.out.println("This will run through all the requests in the file " +
                       "saveFile.csv in c:\\temp");
    System.out.println("5 times and then stop, and will spawn 3 threads to " +
                       "do so, essentially");
    System.out.println("simulating 3 simultaneous users.\n");

  } // End displayUsageInfo().


  /**
   * This is an inner class that is the thread which is spawned to run through
   * the recorded requests.
   *
   * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
   */
  public static class RunnerThread extends Thread {


    /**
     * Number of repetitions this thread should perform.
     */
    private int reps;


    /**
     * Count of the number of repetitions this thread has performed so far.
     */
    private int repsCount;


    /**
     * The ID of this thread.
     */
    private int id;


    /**
     * The collection of requests this thread will run through.
     */
    private ArrayList requests;


    /**
     * The response code retrieved from the last request made.
     */
    private int responseCode;


    /**
     * The response message retrieved from the last request made.
     */
    private String responseMessage;


    /**
     * The total number of threads running, for display purposss only.
     */
    private int numThreads;


    /**
     * Mutator for number of repetitions this thread will perform.
     *
     * @param inReps The new value to set.
     */
    public void setReps(int inReps) {

      reps = inReps;

    } // End setReps().


    /**
     * Mutator for the ID of this thread.
     *
     * @param inID The new value to set.
     */
    public void setID(int inID) {

      id = inID;

    } // End setID().


    /**
     * Mutator for the array of requests this thread will run through.
     *
     * @param inRequests The new value to set.
     */
    public void setRequests(ArrayList inRequests) {

      requests = inRequests;

    } // End setRequests().


    /**
     * Mutator for the total number of threads running.
     *
     * @param inNumThreads The new value to set.
     */
    public void setNumThreads(int inNumThreads) {

      numThreads = inNumThreads;

    } // End setNumThreads().


    /**
     * Main thread work method.
     */
    public void run() {

      repsCount = 0;

      // Run through the file the requested number of times.
      while (repsCount < reps) {

        // For each request, send the request and record the result.
        int i = 0;
        for (Iterator it = requests.iterator(); it.hasNext();) {

          i = i + 1;

          // Get all the info on the next request.
          HashMap reqData = (HashMap)it.next();
          String  method  = (String)reqData.get("method");
          String  theURL  = (String)reqData.get("theURL");
          HashMap params  = (HashMap)reqData.get("parameters");

          // Display some logging info.
          StringBuffer sb = new StringBuffer(1024);
          sb.append("* Thread:     " + id + "/" + numThreads + "\n");
          sb.append("* Repetition: " + (repsCount + 1) + "/" + reps + "\n");
          sb.append("* Request:    " + i + "/" + requests.size() + "\n");
          sb.append("* URL:        " + theURL + "\n");
          sb.append("* Parameters: ");
          if (params.isEmpty()) {
            sb.append("None");
          } else {
            // Display the parameters, removing the set notation that the
            // HashMap's toString() method puts in.
            String p = params.toString();
            sb.append(p.substring(1, p.length() - 1));
          }

          // Send the request and log the response.
          sendRequest(theURL, method, params);
          sb.append("\n* Response:   " + responseCode + " " + responseMessage +
                    "\n");
          log(sb);

        } // End for.

        // Move on to the next repitition.
        repsCount++;

      } // End while.

    } // End run().

    /**
     * This method is called to send a given request.  It sets the responseCode
     * and responseMethod class members upon completion.  No exceptiosn are
     * thrown, any that might be encountered are registered as a responseCode
     * of -1 with the exception message as the responseMessage.  It accepts
     * the URL to call, the method used (GET or POST only) and a map of
     * the parameters of the request.  Note that at this point, only form
     * encoding is supported, multipart, or anything else that might exist,
     * is not explicitly supported.
     *
     * @param theURL The URL the request is made to.
     * @param method The method used (GET or POST only).
     * @param params The map of parameters of the request..
     */
    private void sendRequest(String theURL, String method, HashMap params) {

      // If the method is GET, first consutruct a query string.
      StringBuffer queryString = new StringBuffer(1024);
      if (method.equalsIgnoreCase("get")) {
        boolean didOne = false;
        for (Iterator it = params.entrySet().iterator(); it.hasNext();) {
          if (didOne) {
            queryString.append(",");
          } else {
            didOne = true;
          }
          Map.Entry e     = (Map.Entry)it.next();
          String    pName = (String)e.getKey();
          queryString.append(pName);
          queryString.append("=");
          queryString.append((String)e.getValue());
        }
      }

      // Try the request.  The queryString is appended regardless, and for
      // POSTs it will just be empty, no harm done.
      try {

        URL               u   = new URL(theURL + "?" + queryString.toString());
        URLConnection     uc  = u.openConnection();
        HttpURLConnection huc = (HttpURLConnection)uc;
        huc.setUseCaches(false);
        huc.setDoOutput(true);
        huc.setRequestMethod(method);

        // Some extra work needed if the method is post...
        if (method.equalsIgnoreCase("post")) {
          // The method is POST, so we have to basically take our query string
          // and put it in the body of the request.
          OutputStreamWriter wr = new OutputStreamWriter(huc.getOutputStream());
          wr.write(queryString.toString());
          wr.flush();
          wr.close();
        }

        // Get the resposne code and message and set the class members so we can
        // get at them from the calling method.
        responseCode    = huc.getResponseCode();
        responseMessage = huc.getResponseMessage();

      // Any exceptions that occur are handled similarly and simplistically.
      } catch (IOException ioe) {
        responseCode    = -1;
        responseMessage = ioe.toString();
     }

    } // End sendRequest().


    /**
     * For every request that is made, a message is constructed with the
     * results and some status information.  This information is logged when
     * the request has completed (regardless of outcome).  This method is
     * called to log that message.
     *
     * @param sb The message to be logged.
     */
    private void log(StringBuffer sb) {

      System.out.println(sb);

    } // End log().


  } // End inner class.


} // End class.
