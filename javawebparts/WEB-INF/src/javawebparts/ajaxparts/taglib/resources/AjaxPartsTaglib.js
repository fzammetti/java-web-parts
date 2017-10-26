function AjaxPartsTaglib() {

  // This is the array of currently processing calls.
  this.calls = new Array();
  // This is the value used to help ensure a unique URI for GETs.
  this.uniqIdx = 0;
  // The array of registered request handler functions.
  this.reqHandlers = new Array();
  // The array of registered response handler functions.
  this.resHandlers = new Array();
  // The array of events enabled on all elements of the page.
  this.events = new Array();
  // Context path of application.
  this.ctxtPath = "__CONTEXT__PATH__";
  // This is an array of error handlers defined for this page.
  this.errHandlers = new Array();
  // This is the instance of the log object we're going to use and the minimum
  // message level that will be logged.
  this.log = new __LOGGER__();
  this.log.setMinLevel(this.log.LEVEL___DEBUG_LEVEL__);

  // Register a request handler with this object for later use.
  this.regReqHandler = function(handName, func) {
    if (!this.reqHandlers[handName]) {
      this.reqHandlers[handName] = func;
    }
  }

  // Register a request handler with this object for later use.
  this.regResHandler = function(handName, func) {
    if (!this.resHandlers[handName]) {
      this.resHandlers[handName] = func;
    }
  }

  // Register an error handler with this object for later use.
  this.regErrHandler = function(errCode, groupAR, elemAR, evtType, func) {
    var key = errCode + groupAR +
      (groupAR != "" && elemAR != "" ? "/" : "") +
      elemAR + evtType;
    if (!this.errHandlers[key]) {
      this.errHandlers[key] = func;
    }
  }

  // Instantiate a new XMLHttpRequest object.
  this.getXHR = function() {
    if (window.XMLHttpRequest) {
      return new XMLHttpRequest();
    } else if (window.ActiveXObject) {
      return new ActiveXObject("Microsoft.XMLHTTP");
    }
  }

  // Initiate an AJAX request.
  this.ajaxRequestSender = function(evtDef, pb, qs, xhr, headers) {
    // This will be the key the call is stored under.
    var callKey = "" + this.calls.length;
    var xhr = null;
    // Only do the following when not doing a JSON-P request.
    if (!evtDef.jsonp) {
      // Instantiate XMLHttpRequest object for this call, if none passed in.
      if (xhr == null) {
        xhr = this.getXHR();
      }
      // Add callback handler.
      var f = function() {
        ajaxPartsTaglib.onResponseStateChange(callKey);
      }
      xhr.onreadystatechange = f;
    }
    // Construct query string in proper format.
    var targURI = (evtDef.targURI.indexOf('?') > 0);
    if (!qs) {
      qs = "";
    }
    if (targURI && qs) {
      qs = qs.replace("?", "&");
    }
    if (qs == "" && !targURI) {
      qs = "?";
    } else {
      qs += "&";
    }
    // Insert paramter to ensure GET uniqueness and add to query string.
    qs  += "uniqIdx=" + new Date().getTime() + this.uniqIdx++;
    // Add ajaxRef to query string.
    qs += "&ajaxRef=" + evtDef.ajaxRef;
    // Create ajCall and add to collection.
    var ajCall = {
      xhr : xhr,
      evtDef : evtDef,
      pb : pb,
      qs : qs
    };
    this.calls[callKey] = ajCall;
    // Log ajCall (evtDef must be done separately).
    this.log.debug("ajaxRequestSender() called...\n\n" +
      "ajCall:\n" +
      "pb: " + ajCall.pb + "\n" +
      "qs: " + ajCall.qs + "\n\n" +
      "evtDef: (\n" +
      "resHandParams: " + evtDef.resHandParams + "\n" +
      "theForm: " + evtDef.theForm + "\n" +
      "targURI: " + evtDef.targURI + "\n" +
      "reqHandler: " + evtDef.reqHandler + "\n" +
      "reqParam: " + evtDef.reqParam + "\n" +
      "httpMeth: " + evtDef.httpMeth + "\n" +
      "timerObj: " + evtDef.timerObj + "\n" +
      "ajaxRef: " + evtDef.ajaxRef + "\n" +
      "async: " + evtDef.async + "\n" +
      "jsonp: " + evtDef.jsonp + "\n" +
      "evtType: " + evtDef.evtType + "\n" +
      "preProc: " + evtDef.preProc + "\n" +
      "postProc: " + evtDef.postProc + "\n)"
    );
    // If there is a preprocessor defined for this event, call it.
    if (evtDef.preProc && evtDef.preProc != "") {
      var ppResult = eval(evtDef.preProc)(ajCall);
      // Abort AJAX request at the behest of the preprocessor.
      if (ppResult) {
        return;
      }
    }
    // Send request.
    if (!evtDef.jsonp)  {
      xhr.open(evtDef.httpMeth, evtDef.targURI + qs, evtDef.async);
      if (headers != null) {
        for (var hdr in headers) {
          xhr.setRequestHeader(hdr, headers[hdr]);
        }
      }
      xhr.send(pb);
    } else {
      // Doing a JSON-P request.
      var scriptTag = document.createElement("script");
      scriptTag.setAttribute("src", evtDef.targURI + qs);
      scriptTag.setAttribute("type", "text/javascript");
      var headTag = document.getElementsByTagName("head").item(0);
      headTag.appendChild(scriptTag);
    }
    // When async is false, the callback isn't used by XMLHttpRequest...
    // however, we still need it to "do its thing", so we call it here
    // manually.
    if (!evtDef.async) {
      this.onResponseStateChange(callKey);
    }
  }

  // AJAX callback.
  this.onResponseStateChange = function(callKey) {
    var ajCall = this.calls[callKey];
    // IE seems to call this callback one more time than FF does when the
    // requst is sent synchronously.  Unfortunately, the call would have been
    // deleted from the collection at this point, resulting in an NPE basically.
    // So, if either of these values is undefined (and only ajCall should ever
    // be, but I'm anal, so I check both), then we do nothing here.
    if (!callKey || !ajCall) {
      return;
    }
    var xhr = ajCall.xhr;
    var evtDef = ajCall.evtDef;
    if (xhr.readyState < 4) {
      return;
    }
    if (xhr.status == 200) {
      this.log.debug("Call: '" + callKey + "' to: '" + evtDef.targURI +
        "' has returned...\n\nresponseText=\n" + ajCall.xhr.responseText +
        "\n\nAbout to call response handler chain: '" + evtDef.resHandParams);
      var resHandParams = evtDef.resHandParams;
      // Cycle through all response handlers for this event, fire each one.
      for (var i = 0; i < resHandParams.length; i = i + 3) {
        var resHandler = this.resHandlers[resHandParams[i]];
        if (!resHandler) {
          var f = function() {
            ajaxPartsTaglib.onResponseStateChange(callKey);
          }
          setTimeout(f, 100);
        } else {
          if (resHandParams[i + 2] == null ||
            ajCall.xhr.responseText.match(resHandParams[i + 2])) {
            var rhResult = resHandler(ajCall, resHandParams[i + 1]);
            // Stop the handler chain if true is returned.  False, or no
            // explicit return, should result in the chain continuing.
            if (rhResult) {
              break;
            }
          }
        }
      }
      this.execScripts(xhr.responseText);
      // If there is a postprocessor defined for this event, call it.
      if (evtDef.postProc && evtDef.postProc != "") {
        eval(evtDef.postProc)(ajCall);
      }
      // If timer event, start timer.
      if (evtDef.evtType == "timer") {
        eval("start" + evtDef.ajaxRef.replace("/", "_") + "();");
      }
    } else {
      // Log the error
      this.log.debug(xhr.status);
      // Look up event-level error handler.
      var eh = this.errHandlers[xhr.status + evtDef.ajaxRef +
        evtDef.evtType];
      // No element-level error handler found, look up element-level.
      if (!eh) {
        eh = this.errHandlers[xhr.status + evtDef.ajaxRef];
      }
      // No event or element-level error handler found, look up group-level.
      if (!eh) {
        var a = evtDef.ajaxRef.split("/");
        eh = this.errHandlers[xhr.status + a[0]];
      }
      // If an error handler was found, call it.  If none was found in any
      // scope, this error will go unhandled.
      if (eh) {
        eh(ajCall);
      }
    }
    // Regardless of what happens, delete the call.
    ajCall = null;
    delete this.calls[callKey];
  }

  // This is called to attach an AJAX event to an element.  This is called by
  // the code rendered by the <ajax:event> tag.
  this.attach = function(ajaxRef, targURI, reqHandler, reqParam,
    resHandParams, httpMeth, theForm, evtType, preProc, postProc,
    async, jsonp) {

    var targetObject = null;
    var theFormOrigVal = theForm;
    // Get reference to target object, if not a manual or timer event.
    if (evtType != "manual" && evtType != "timer") {
      // Get reference to artificial target element.
      targetObject = document.getElementById(ajaxRef.replace('/', '_'));
      // Attach to target element, either explicitly by ID...
      if (targetObject.getAttribute("title")) {
      	targetObject = document.getElementById(
      	  targetObject.getAttribute("title"));
      } else {
        // ...or backtrack DOM to find previous element, which is target.
        while ((targetObject = targetObject.previousSibling).nodeType != 1);
      }
      // Get reference to form, either the parent of the element, or the one
      // directly defined, if theForm had a value (i.e., some handler don't
      // need a form).
      if (theForm == "parent") {
        try {
          theForm = targetObject.form;
        } catch (error) {
        }
      } else {
        // Get named form.  Try to get it both ways.
        theForm = document.forms[theForm];
        if (!theForm) {
          theForm = document.getElementById(theForm);
        }
      }
    // For manual and timer events, we still need to get a reference to
    // the form, so long as the name of the form isn't "parent".
    } else {
      if (theForm != null && theForm != "" && theForm != "parent") {
        // Get named form.  Try to get it both ways.
        theForm = document.forms[theForm];
        if (!theForm) {
          theForm = document.getElementById(theForm);
        }
      }
    }
    // Log error if form was not found.
    if (theFormOrigVal != null && theFormOrigVal != "" && !theForm) {
      this.log.debug("Unable to find form " + theFormOrigVal +
        " for ajaxRef " + ajaxRef);
    }
    // Modify target URI to deal with relative vs. absolute.
    if (targURI.charAt(0) == "/") {
      targURI = this.ctxtPath + targURI;
    }
    // Construct the descriptor of the event.
    var evtDef = {
      resHandParams : resHandParams,
      theForm : theForm,
      targURI : targURI,
      reqHandler : reqHandler,
      reqParam : reqParam,
      httpMeth : httpMeth,
      timerObj : null,
      ajaxRef : ajaxRef,
      async : async,
      jsonp : jsonp,
      evtType : evtType,
      preProc : preProc,
      postProc : postProc
    };
    // Key to add is the ajaxRef + the event.
    this.events[ajaxRef + evtType] = evtDef;
    // Attach event handler, if not a manual or timer event.
    if (evtType != "manual" && evtType != "timer") {
      // Construct event handler code to attach to target element.
      var newHandler =
        function () { ajaxPartsTaglib.execute(ajaxRef + evtType); };
      // Get reference to existing event handler of target element, if any.
      var targFunc = targetObject[evtType];
      // Add event reference ID to target element, we'll need it later.
      targetObject.evtRef = ajaxRef + evtType;
      // If a handler already exists, append the new handler.  If none already
      // existed, just set it.
      if (targFunc) {
        var oldHandler = targFunc;
        targFunc = function() {
          oldHandler();
          newHandler();
        };
      } else {
        targFunc = newHandler;
      }
      targetObject[evtType] = targFunc;
    }
  }

  // This is called by any event handler that was generated by AjaxParts
  // Taglib
  this.execute = function(evtRef) {
    var evtDef = this.events[evtRef];
    var reqHandler = this.reqHandlers[evtDef.reqHandler];
    reqHandler(evtDef);
  }

  // This function is used to execute all the script blocks found in a
  // returned response.  This should usually be called by all response
  // handlers as their last task.
  this.execScripts = function (resText) {
    var si = 0;
    while (true) {
      var ss = resText.indexOf("<" + "script", si);
      if (ss == -1) {
        return;
      }
      var se = resText.indexOf("<" + "/" + "script" + ">", ss);
      if (se == -1) {
        return;
      }
      si = se + 9;
      var sc = resText.substring(resText.indexOf(">", ss)+1, se);
      eval(sc);
    }
  }

}


// The one and only instance of AjaxPartsTaglib.
var ajaxPartsTaglib = new AjaxPartsTaglib();
