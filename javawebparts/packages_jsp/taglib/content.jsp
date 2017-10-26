<%@ taglib prefix="basicstr" uri="javawebparts/taglib/basicstr" %>
<%@ taglib prefix="jstags" uri="javawebparts/taglib/jstags" %>
<%@ taglib prefix="uiwidgets" uri="javawebparts/taglib/uiwidgets" %>

  <!-- BasicString Taglib Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="BasicStringTaglibTest">BasicString Taglib Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/taglib/basicstr/package-summary.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  This demonstrates various functions of the BasicString Taglib, a taglib
  which, as the name implies, is used to perform some basic, simple string
  manipulations on the text it includes (the text can be the output of a
  scriplet or other custom tag).
  <br/>
  <pre>
Trim (Both): -<basicstr:trim type="both">     This has been fully trimmed     </basicstr:trim>-
Trim (Left): -<basicstr:trim type="left">     This has been left-trimmed     </basicstr:trim>-
Trim (Right, specified chars): <basicstr:trim type="right" chars="xyz">xyz has been trimmed from endxyz</basicstr:trim>
LCase: <basicstr:lcase>This Has Been Lower-Cased</basicstr:lcase>
UCase: <basicstr:ucase>This Has Been Upper-Cased</basicstr:ucase>
ICase: <basicstr:icase>This Has Been Case-Inverted</basicstr:icase>
Reverse: <basicstr:reverse><%="This Has Been Reversed"%></basicstr:reverse>
EntityEncode: <basicstr:entityEncode>View source to see entities: © ä Ä ª æ</basicstr:entityEncode>
Replace: <basicstr:replace match="Larry Bird" replaceWith="Michael Jordan">Larry Bird was the best basketball player ever</basicstr:replace>
Abbreviate: <basicstr:abbreviate leftEdge="6" maxSize="10">abcdefghijklmno</basicstr:abbreviate>
Substr (left): <basicstr:substr type="left" count="6">abcdefghijklmnopqrstuvwxyz</basicstr:substr>
Substr (right): <basicstr:substr type="right" count="4">abcdefghijklmnopqrstuvwxyz</basicstr:substr>
Substr (mid): <basicstr:substr type="mid" start="8" end="14">abcdefghijklmnopqrstuvwxyz</basicstr:substr>
Scramble: <basicstr:scramble>abcdefghijklmnopqrstuvwxyz</basicstr:scramble>
  </pre>
  </div><br/>


  <!-- JSTags Taglib Test -->
<script>
  // Custom types used by JSDigester test.
  function Actor() { this.gender = null; this.name   = null; }
  Actor.prototype.setGender = function(inGender) { this.gender = inGender; }
  Actor.prototype.getGender = function() { return this.gender; }
  Actor.prototype.setName = function(inName) { this.name = inName; }
  Actor.prototype.getName = function() { return this.name; }
  Actor.prototype.toString = function() { return "Actor=[name=" + this.name + ",gender=" + this.gender + "]"; }
  function Movie() { this.title  = null; this.actors = new Array(); }
  Movie.prototype.setTitle = function(inTitle) { this.title = inTitle; }
  Movie.prototype.getTitle = function() { return this.title; }
  Movie.prototype.addActor = function(inActor) { this.actors.push(inActor); }
  Movie.prototype.getActors = function() { return this.actors; }
  Movie.prototype.toString = function() { return "Movie=[title=" + this.title + ",actors={" + this.actors + "}]"; }
  function Movies() { this.movieList = new Array(); this.numMovies = null; }
  Movies.prototype.setNumMovies = function(inNumMovies) { this.numMovies = inNumMovies; }
  Movies.prototype.getNumMovies = function() { return this.numMovies; }
  Movies.prototype.addMovie = function(inMovie) { this.movieList.push(inMovie); }
  Movies.prototype.getMovieList = function() { return this.movieList; }
  Movies.prototype.toString = function() { return "Movies=[numMovies=" + this.numMovies + ",movieList={" + this.movieList + "}]"; }
  // Function called to test JSDigester.
  function testJSDigester() {
    sampleXML  = "<movies numMovies=\"2\">\n";
    sampleXML += "  <movie>\n";
    sampleXML += "    <title>Star Wars</title>\n";
    sampleXML += "    <actor gender=\"male\">Harrison Ford</actor>\n";
    sampleXML += "    <actor gender=\"female\">Carrie Fisher</actor>\n";
    sampleXML += "  </movie>\n";
    sampleXML += "  <movie>\n";
    sampleXML += "    <title>Real Genius</title>\n";
    sampleXML += "    <actor gender=\"male\">Val Kilmer</actor>\n";
    sampleXML += "  </movie>\n";
    sampleXML += "</movies>";
    jsDigester = new JSDigester();
    jsDigester.addObjectCreate("movies", "Movies");
    jsDigester.addSetProperties("movies");
    jsDigester.addObjectCreate("movies/movie", "Movie");
    jsDigester.addBeanPropertySetter("movies/movie/title", "setTitle");
    jsDigester.addObjectCreate("movies/movie/actor", "Actor");
    jsDigester.addSetProperties("movies/movie/actor");
    jsDigester.addBeanPropertySetter("movies/movie/actor", "setName");
    jsDigester.addSetNext("movies/movie/actor", "addActor");
    jsDigester.addSetNext("movies/movie", "addMovie");
    myMovies = jsDigester.parse(sampleXML);
    alert("JSDigester processed the following XML:\n\n" + sampleXML +
      "\n\nIt created an object graph consisting of a Movies object, " +
      "with a numMovies property, and containing a collection of " +
      "Movie objects.\n\nEach Movie object has a title property, and " +
      "contains a collection of Actor objects.\n\nEach Actor object has " +
      "two fields, name and gender.\n\n" +
      "Here's the final Movies object JSDigester returned: \n\n" +
      myMovies);
  }
</script>
<jstags:jsDigester renderScriptTags="true" />
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="JSTagsTaglibTest">JSTags Taglib Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/taglib/jstags/package-summary.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  This demonstrates various functions of the JSTags Taglib, a taglib
  which can render various useful general-purpose Javascript functions.  To
  really get the full effect, view the source for this page and then look at
  the JSP source before it was rendered.  The tags all take a single attribute,
  renderScriptTags.  When set to true, the function will be enclosed in a
  &lt;script&gt; &lt;/script&gt;> block.  When false, it is assumed they are
  properly nested in such a group on the page by the developer.
  <br/>
<script>
<jstags:fullTrim renderScriptTags="false" />
<jstags:replaceSubstr renderScriptTags="false" />
<jstags:substrCount renderScriptTags="false" />
<jstags:stripChars renderScriptTags="false" />
<jstags:locateSelectValue renderScriptTags="false" />
<jstags:stringContentValid renderScriptTags="false" />
<jstags:cookieFunctions renderScriptTags="false" />
<jstags:dateDifference renderScriptTags="false" />
</script>
<pre>
fullTrim:           <script>document.write(JWPFullTrim("     Whitespace removed from both ends of this string     "));</script>
replaceSubstr:      <script>document.write(JWPReplaceSubstr("All lower-case A's have been replaced with @ in this string     ", "a", "@"));</script>
substrCount:        <script>theStr = "The character a appears in this string "; document.write(theStr + JWPSubstrCount(theStr, "a") + " times");</script>
stripChars:         <script>document.write(JWPStripChars("A-l-l dash c-haracters have b----een str-i-pped from th--is str-ing", "strip", "-"));</script>
StringContentValid: Does "This is a test" contain ONLY " thisae"? <script>document.write(JWPStringContentValid('this is a test', ' thisae', JWPSCV_FROMLIST));</script>
StringContentValid: Does "This is a test" NOT contain "xyz"? <script>document.write(JWPStringContentValid('this is a test', 'xyz', JWPSCV_NOTFROMLIST));</script>
locateSelectValue:  <select id="testSelect"><option value="123">Dummy1</option><option value="456">Dummy2</option><option value="ValueSelectedByScript">This value was selected via script</option><option value="789">Dummy3</option></select><script>JWPLocateSelectValue(document.getElementById("testSelect"), "ValueSelectedByScript", false, false);</script>
JSDigester:         <input type="button" value="Click here to parse XML with JSDigester" onClick="testJSDigester();">
setCookie:          <script>setCookie("myCookie", "Hello!", new Date("25 Dec 2006 06:00:00 EST"));</script>Done.
getCookie:          <script>document.write(getCookie("myCookie"));</script>
dateDifference:     10/12/2004, 10/14/2004 = <script>document.write(JWPDateDifference(new Date(2007,10,12), new Date(2007, 10, 20)));</script>
</pre>
<center>
<jstags:createBookmark renderScriptTags="true" />
<jstags:disableRightClick renderScriptTags="true" />
<jstags:formToXML renderScriptTags="true" />
<jstags:getParams renderScriptTags="true" />
<jstags:maximizeWindow renderScriptTags="true" />
<jstags:printPage renderScriptTags="true" />
<input type="button" value="Bookmark Page" onclick="JWPCreateBookmark();">
&nbsp;&nbsp;
<input type="button" value="Disable Right Click" onclick="JWPDisableRightClick(true);">
&nbsp;&nbsp;
<input type="button" value="Enable Right Click" onclick="JWPDisableRightClick(false);">
<br/>
<form name="testform" method="post" action="test">
  <input type="text" name="firstName" value="Frank"><br>
  <input type="text" name="middleName" value="William"><br>
  <input type="text" name="lastName" value="Zammetti"><br>
  <input type="button" value="Convert Form To XML" onclick="alert(JWPFormToXML(document.forms['testform'],'Person'));">
</form>
<input type="button" value="Show Parameters" onclick="alert(JWPGetParams());">
&nbsp;&nbsp;
<input type="button" value="Maximize Window" onclick="JWPMaximizeWindow();">
&nbsp;&nbsp;
<input type="button" value="Print Page" onclick="JWPPrintPage();">
<br/><br/>
</center>

  </div><br/><br/>

  <!-- UIWidgets Taglib Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="UIWidgetsTaglibTest">UIWidgets Taglib Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/taglib/uiwidgets/package-summary.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  This is a demonstration of the various user interface elements to be found in
  the UIWidgets taglib.  The first is called a Swapper.
  <br/><br/>
  <uiwidgets:swapper id="swapper1" boxWidth="150" boxStyle="color:#ff0000;">
    <option value="value1">Value 1 Text</option>
    <option value="value2">Value 2 Text</option>
    <option value="value3">Value 3 Text</option>
    <option value="value4">Value 4 Text</option>
    <option value="value5">Value 5 Text</option>
  </uiwidgets:swapper>
  <input type="button" value="Click to retrieve list on right" onClick="alert(JWP_UIWT_getList('swapper1'));"/>
  <br/>
  <br/>
  This is a second Swapper, proving that we can use 2 Swapers on the same screen.
  <br/><br/>
  <uiwidgets:swapper id="swapper2" boxWidth="150" boxStyle="color:#ff0000;">
    <option value="value1">Value 1 Text</option>
    <option value="value2">Value 2 Text</option>
    <option value="value3">Value 3 Text</option>
    <option value="value4">Value 4 Text</option>
    <option value="value5">Value 5 Text</option>
  </uiwidgets:swapper>
  <input type="button" value="Click to retrieve list on right" onClick="alert(JWP_UIWT_getList('swapper2'));"/>

  <br/><br/>
  The next is a Calendar.  This calendar shows how you can set the range of years,
  as well as override the default style attributes.  There are two calendars in
  fact, just to show that is possible, and also to show that only a single calendar tag
  is actually needed on the page, as well as the fact that the year range and
  style overrides apply to all calendars on the page.  Notice that any &lt;
  select&gt; elements on the page are hidden when the calendar is shown to avoid
  layer "bleed-through".  Note that the calendar always appears centered on the
  page.
  <br/><br/>
  <uiwidgets:calendar minYear="1990" maxYear="2010" styleOverrides="cssCalendarButton,cssCalendarDropdown" />
  <style>
    .cssCalendarButton {
      color:#ff0000;
      font-family:arial;
      font-size:11pt;
      font-weight:bold;
      font-style:normal;
      font-variant:normal;
      text-decoration:none;
      cursor:pointer;
    }
    .cssCalendarDropdown {
      color:#0000ff;
      font-family:arial;
      font-size:11pt;
      font-weight:bold;
      font-style:normal;
      font-variant:normal;
      text-decoration:none;
      cursor:pointer;
    }
  </style>
  <form name="calendarForm1" method="post" action="#">
    Date 1: <input type="text" size="12" name="myDate1">
    <input type="button" value="Click for calendar" onClick="showCalendar(calendarForm1.myDate1);">
  </form>
  <form name="calendarForm2" method="post" action="#">
    Date 2: <input type="text" size="12" name="myDate2">
    <input type="button" value="Click for calendar" onClick="showCalendar(calendarForm2.myDate2);">
  </form>
  <script>
    JWPCalendarAddDropdown("testSelect");
    JWPCalendarAddDropdown("JWP_UIWT_swapper1_leftSelect");
    JWPCalendarAddDropdown("JWP_UIWT_swapper1_rightSelect");
  </script>
<br/><br/></div>

