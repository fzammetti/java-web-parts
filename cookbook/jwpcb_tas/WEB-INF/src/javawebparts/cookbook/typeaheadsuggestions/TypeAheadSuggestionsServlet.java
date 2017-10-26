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


package javawebparts.cookbook.typeaheadsuggestions;


import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;


/**
 * Simple servlet that returns the HTML to render the list of matching
 * suggestions based on the entered text passed in.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public class TypeAheadSuggestionsServlet extends HttpServlet {


  private static String[][] words = {
    { "aardvark", "abbot", "abort", "account", "acorn", "action", "adam",
      "aeon", "agorophobia", "ahab", "ailment", "airway",  "ajar", "ajax", },
    { "bad", "bald", "band", "barrel", "beard", "bed", "beltway", "biff",
      "big", "bird", "bladder", "bones", "buddy", "burp", "butt", "buzz" },
    { "calm", "card", "cargo", "chase", "chasm", "chemical", "christmas",
      "congo", "congress", "consider", "cork", "crash", "creep", "crisp" },
    { "damn", "dastardly", "dean", "dell", "descent", "develop", "diet", "dig",
      "dilbert", "directory", "dog", "douglas", "drug", "druthers" },
    { "ear", "earlier", "ebert", "effort", "egg", "emergency", "enemy",
      "energy", "enterprise", "evening", "every", "exempt", "exhaust", "eye" },
    { "fan", "fantasy", "farm", "fender", "fish", "flew", "fling", "flung",
      "fly", "fog", "foghorn", "frank", "frenzy", "friend", "frog", "fumble" },
    { "gang", "gap", "gas","girl", "gleam", "going", "gone", "gong", "grand",
      "grasp", "great", "guinea", "gulf", "gulp", "gurgle", "guy" },
    { "halo", "ham", "hammer", "hang", "hearing", "heart", "hello", "help",
      "hermit", "hero", "hog", "howdy", "hubert", "hung", "hurry", "hurt" },
    { "ice", "implicate", "imply", "indecent", "indigo", "industry", "inside",
      "internet", "intimate", "intimidate", "into", "irrevocable" },
    { "jack", "jakarta", "jake", "jam", "jargon", "java", "javascript", "jerk",
      "jest", "john", "joke", "josh", "jug", "july", "june", "jury" },
    { "kangaroo", "karl", "kettledrum", "kill", "killer", "killowatt", "kimbo",
      "kmew", "kmow", "knighthood", "knock", "kong", "konquer" },
    { "lamb", "lance", "learning", "lesson", "let", "liberace", "liberate",
      "lifelike", "like", "likes", "long", "lunch", "lunge", "lurch" },
    { "magic", "mammogram", "march", "may", "mercenary", "mercy", "merge",
      "microsoft", "mom", "mommy", "monday", "money", "munch", "munchies" },
    { "nag", "nanny", "narc", "narcotic", "natural", "nature", "nerd", "never",
      "nobody", "nonsense", "noon", "not", "nurture", "nut", "nuts" },
    { "offense", "offensive", "old", "operate", "operation", "operational",
      "opium", "orca", "organ", "orlando", "orphan", "ort", "overture" },
    { "pal", "pearch", "peas", "penny", "people", "perfume", "personal",
      "pod", "power", "prescott", "pretty", "programmer", "proprties" },
    { "quadradic", "quadrinomial", "quadruple", "query", "question", "quibble",
      "quick", "quickest", "quickly", "quinn", "qwerty" },
    { "rambus", "rap", "rat", "really", "rear", "rebate", "refine",
      "regurgitate", "reject", "rejoice", "rock", "roger", "roll", "roster" },
    { "sack", "sad", "sam", "saturday", "say", "seattle", "september", "squash",
      "star", "still", "street", "stuck", "sucks", "sunday", "swish" },
    { "tag", "ted", "the", "thomas", "thursday", "tom", "tomcat","tone",
      "trace", "transformation", "travel", "troll", "tuck", "tuesday", "turd" },
    { "ugly", "unclean", "undercurrent", "underneath", "underverse",
      "unfettered", "universal", "unpleasent", "upside", "useless","utter" },
    { "valium", "vast", "vegas", "vegetable", "vehement", "veign","venue",
      "venus","verify","veterinarian", "virtue", "vogue" },
    { "wack", "wafer", "wag", "war", "water", "whimsical", "wicked", "wild",
      "wise", "wish", "wobble", "wonderful", "world", "wow", "wully" },
    { "xanadu", "xavier", "xenobiologist", "xenogeologist", "xenomorph",
      "xenophobia", "xigua", "xml", "xslt", "xteq", "xylophone" },
    { "yack", "yam", "year", "yell", "yellow", "yes", "yesterday", "yesteryear",
      "yoda", "yodel", "yogurt", "you", "yours", "yummy" },
    { "zammetti", "zap", "zapped", "zen", "zenith", "zero", "zest", "zinc",
      "zitto", "zone", "zoned", "zucchini", "zuckerman", "zygot" }
  };


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
   * @param  request          HTTPServletRequest.
   * @param  response         HTTPServletResponse.
   * @throws ServletException ServletException.
   * @throws IOException      IOExcpetion.
   */
  public void doPost(HttpServletRequest request, HttpServletResponse response)
                     throws ServletException, IOException {

    String enteredText = (String)request.getParameter("enteredText");
    System.out.println("enteredText = " + enteredText);
    StringBuffer sb = new StringBuffer(1024);
    if (enteredText != null && !enteredText.equalsIgnoreCase("")) {
      enteredText = enteredText.toLowerCase();
      int i = ((int)enteredText.charAt(0)) - 97;
      if (i >= 0 && i <= 25) {
        for (int j = 0; j < words[i].length; j++) {
          if (words[i][j].startsWith(enteredText)) {
            sb.append(words[i][j] + "<br>\n");
          }
        }
      }
    }
    PrintWriter out = response.getWriter();
    out.println(sb.toString());


  } // End doPost().


} // End class.
