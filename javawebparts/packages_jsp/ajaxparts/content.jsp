<%@ taglib prefix="ajax" uri="javawebparts/ajaxparts/taglib" %>

  <script>
    function testPre(ajCall) {
      alert(ajCall.evtDef.ajaxRef + " -- Pre-processor");
    }
    function testPost(ajCall) {
      alert(ajCall.evtDef.ajaxRef + " -- Post-processor");
    }
    function alertErrorHandlerElement(ajCall) {
      alert("alertErrorHandlerElement: " + ajCall.xhr.status + " - " +
        ajCall.xhr.statusText);
    }
    function alertErrorHandlerEvent(ajCall) {
      alert("alertErrorHandlerEvent: " + ajCall.xhr.status + " - " +
        ajCall.xhr.statusText);
    }
  </script>

  <!-- AjaxParts Taglib Test -->
  <table width="100%" border="0" cellpadding="0" cellspacing="0">
    <tr class="sectionHeader">
      <td>
        <a name="AjaxPartsTaglibTest">AjaxParts Taglib Test</a>
      </td>
      <td align="right">
        <a href="<%=request.getContextPath()%>/javadocs/javawebparts/ajaxparts/taglib/package-summary.html">Javadocs</a>
      </td>
    </tr>
  </table>
  <div class="sectionContent"><br/>
  This test demonstrates the use of AjaxParts Taglib.  Various permutations of
  usage are shown below, using a variety of handlers.  It is recommended that
  you view the source of this page (both the source JSP and what is returned to
  the browser) to get a feel for how it all fits together.
  <br/><br/>
  <center>
  <input type="button" value="Click for AJAX" onclick="alert('Hello JWP user!\n\nYou know, Feyenoord, and not Ajax, is my preferred Dutch soccer team!\n\n- Herman\n\n(This is the onClick handler that is present before AjaxParts Taglib attaches its handler.\nThe next popup you see is a result of the handler AjaxParts Taglib attaches.')"><ajax:event ajaxRef="TestPage/Button2" />
  <br/><br/>
  <form name="TestForm">
    <input type="text" name="text1" value="Britney Spears">
    <br>
    <input type="text" name="text2" value="Is Nuts">
    <br>
    This select is meant to be empty to test JWPGetElementValue:&nbsp;
    <select multiple="true" name="select1"></select>
  </form>
  <input type="button" value="Submit above form via POST"><ajax:event ajaxRef="TestForm/Button" />
  <br/><br/>
  <span id="firstName">Frank</span>&nbsp;
  <input type="text" id="lastName" value="Zammetti" size="10">
  <br/><br/>
  <form name="AjaxPartsTaglibTestForm" method="post" action="#" id="SomeID">
    <table border="0" cellpadding="0" cellspacing="0">
      <tr><td align="center">
        <input type="button" value="Hover over and click this button for AJAX"><ajax:event ajaxRef="AjaxPartsTaglibTestForm/Button1" />
        <br/><br/>
        Select one: <select name="selbox1">
 		      <option value="">Zero</option>
		      <option value="1">One</option>
		      <option value="2">Two</option>
		      <option value="3">Three</option>
		      <option value="4">Four</option>
	      </select><ajax:event ajaxRef="AjaxPartsTaglibTestForm/selectbox1" />
        &nbsp;&nbsp;
	      <select name="selbox2"></select>
	      <br/><br/>
        <input type="checkbox">Check for AJAX<ajax:event ajaxRef="AjaxPartsTaglibTestForm/Checkbox1" />
        &nbsp;&nbsp;
        <input type="radio" name="r1">Click for AJAX<ajax:event ajaxRef="AjaxPartsTaglibTestForm/Radio1" />
        </td></tr>
    </table>
    <br/>
    Form Manipulation:<br/>
	<input name="field1" id="field1" type="text" style="width: 200px;">
	<input type="button" value="Do Form Manipulation" name="button1" id="button1"><ajax:event ajaxRef="AjaxPartsTaglibTestForm/manipulator" />
    <input type="button" value="show properties" name="button2" id="button2" onclick="showProps();">
  </form>
  <script>
  function showProps() {
  	alert("form method: " + document.getElementsByName("AjaxPartsTaglibTestForm")[0].method +
  		"\nform action: " + document.getElementsByName("AjaxPartsTaglibTestForm")[0].action +
  		"\nfield value: " + document.getElementById("field1").value +
  		"\nfield width: " + document.getElementById("field1")["style"]["width"] +
  		"\nbutton text: " + document.getElementById("button1").value +
  		"\nbutton disabled: " + document.getElementById("button1").disabled);
  }
  </script>
  <br/><br/>
  <div style="background-color:#ffe0e0;width:25%;">Click this text for AJAX</div><ajax:event ajaxRef="TestPage/Link2" />
  <br/>
  <a href="#">Hover over this link for AJAX</a><ajax:event ajaxRef="TestPage/Link1" />
  <br/><br/>
  <span id="ajaxPartsResults">-- Quote will appear here in response to user events --</span>
  <br/>
  <span id="ajaxPartsResults2">-- Quote will also appear here when link is hovered over --</span>
  <br/><br/>
  <input type="button" value="AJAX In IFrame (JSP renders response)"><ajax:event ajaxRef="TestPage/iFrame1" />
  <br/>
  <iframe name="ajaxIFrame" width="600" height="100" scrolling="yes" align="center" hspace="0" vspace="0" frameborder="1" marginwidth="0"></iframe>
  <br/><br/>
  <input type="button" value="Click to test absolute pathing (text will appear below)"><ajax:event ajaxRef="TestPage/AbsLink" />
  <br/><br/>
  <div id="divAbsLink" style="background-color:#ffe0e0;height:40px;">&nbsp;</div>
  <br/>
  <input type="button" value="Test using tags in an AJAX response"><ajax:event ajaxRef="TestGroup1/Button1"/>
  <br/><br/>
  <input type="button" value="Test a SYNCHRONOUS AJAX request"><ajax:event ajaxRef="TestGroup1/Button2"/>
  <br/><br/>
  <input type="button" value="Test a manual AJAX event" onClick="MyManualEvent();">
  <ajax:manual ajaxRef="TestGroup3/Manual1" function="MyManualEvent"/>
  <br/><br/>
  <input type="button" value="Test a manual AJAX event that uses a form" onClick="MyManualEvent2();">
  <ajax:manual ajaxRef="TestGroup3/Manual2" function="MyManualEvent2"/>
  <br/><br/>
  <input type="button" value="Test a timed AJAX event" onClick="startTestGroup3_Timer1();">
  <input type="button" value="Stop the timer" onClick="stopTestGroup3_Timer1();">
  <ajax:timer ajaxRef="TestGroup3/Timer1" startOnLoad="true" frequency="3000"/>
  <br/><br/>
  <input type="button" value="Test a group-level error handler"><ajax:event ajaxRef="TestGroup2/Button1"/>
  <br/><br/>
  <input type="button" value="Test an element-level error handler"><ajax:event ajaxRef="TestGroup2/Button2"/>
  <br/><br/>
  <input type="button" value="Test an event-level error handler"><ajax:event ajaxRef="TestGroup2/Button3"/>
  <br/><br/>
  <hr>
  <br/>
  <div id="ajaxPartsTaglibResults1">-- Responses will appear here (1) --</div>
  <br/>
  <div id="ajaxPartsTaglibResults2">-- Responses will appear here (2) --</div>
  <br/><br/>
  <input type="button" value="Redirect dynamic (will send you to start page)"><ajax:event ajaxRef="TestGroup2/RedirecterButton1"/>
  <br/><br/>
  <input type="button" value="Redirect hardcoded (will send you to cnn.com)"><ajax:event ajaxRef="TestGroup2/RedirecterButton2"/>
  <br/><br/>
  <div>Click this text for a popup window</div><ajax:event ajaxRef="TestGroup2/Popup"/>
  </center>
  Fill in the name and details for a person you find attractive... and be
  honest with that last part!  Or you can just use the defaults if your lazy,
  this person works just fine for me!
  <br/><br/>
  <form name="aptFormSenderForm">
    <input type="hidden" id="testCodeField" name="testCode">
    First Name: <input type="text" name="firstName" value="Amanda" size="20" /><br/>
    Last Name: <input type="text" name="lastName" value="Tapping" size="20" /><br/>
    Attractiveness:
    <select name="attractiveness">
      <option value="Hot as hell" selected>Hot as hell</option>
      <option value="Very fine">Very fine</option>
      <option value="Cute" selected>Cute</option>
      <option value="So-So">So-So</option>
      <option value="Ugly Betty-ish">Ugly Betty-ish</option>
    </select><br/>
    Marrying Material?
    <input type="radio" name="marryingMaterial" value="maybe" checked />Unknown
    <input type="radio" name="marryingMaterial" value="is" />Yes
    <input type="radio" name="marryingMaterial" value="is not" />No
    <br/>
    Check here if you know it'll never happen: <input type="checkbox" checked name="neverHappen" />
    <br/>
    <br/>
    Pick one of the following ways to send the contents of this form:
    <br/><br/>
    <div onClick="document.getElementById('testCodeField').value='A';" onMouseOver="this.style.cursor='pointer';">Click here to send this form as plain old request parameters (GET)</div><ajax:event ajaxRef="aptFormSender/parametersGET"/>
    <div onClick="document.getElementById('testCodeField').value='B';" onMouseOver="this.style.cursor='pointer';">Click here to send this form as plain old request parameters (POST)</div><ajax:event ajaxRef="aptFormSender/parametersPOST"/>
    <div onClick="document.getElementById('testCodeField').value='C';" onMouseOver="this.style.cursor='pointer';">Click here to send this form as JSON</div><ajax:event ajaxRef="aptFormSender/json"/>
    <div onClick="document.getElementById('testCodeField').value='D';" onMouseOver="this.style.cursor='pointer';">Click here to send this form as XML</div><ajax:event ajaxRef="aptFormSender/xml"/>
    <br/>
    In all cases you'll get an alert with the values from the form inserted into a sentence.
    <br><br>
    <ajax:event ajaxRef="TestPage/a2buttonElem" attachTo="a2button" />
    The following event was attached to the button using the attachTo attribute.
    <br>
    <input type="button" id="a2button" value="Click me for AJAX">
    <br>
    <div id="a2buttondiv"></div>

    <!-- ***************************************************************** -->
    <!-- ************************** JSON-P TEST ************************** -->
    <!-- ***************************************************************** -->
    <br>
    Test cross-domain JSON-P request... <br>
    First, enter a U.S. Zip Code here: <input type="text" size="6"
      maxlength="5" id="location" value="11706"><br>
    Then, click <input type="button" id="jsonpButton" value="ME"> to
    initiate a call to the Yahoo! Maps Map Image Service.<br>
    The resultant map should appear below:
    <br><br>
    <img id="jsonpResult" style="display:none;">
    <ajax:event ajaxRef="jsonp/btnJSONP" attachTo="jsonpButton" />
    <script>
      function jsonpCallback(obj) {
        var imgTag = document.getElementById("jsonpResult");
        imgTag.src = obj.ResultSet.Result.replace(/\\/, "");
        imgTag.style.display = "block";
      }
    </script>
    <!-- ***************************************************************** -->
    <!-- ***************************************************************** -->

  <br/></div>

<ajax:enable debug="error" />
