function StdFormSender(evtDef) {
  var qs = "";
  var json = "";
  var xml = "";
  var sendMethod = evtDef.reqParam.toLowerCase();
  var theForm = eval("evtDef.theForm");
  for (var i = 0; i < theForm.elements.length; i++) {
    var elem = theForm.elements[i];
    var en = elem.name;
    var et = elem.type;
    var val = new Array();
    if (et == "checkbox") {
      if (elem.checked) {
        val.push("true");
      } else {
        val.push("false");
      }
    } else if (et == "radio") {
      if (elem.length == undefined) {
		    if (elem.checked) {
			    val.push(elem.value);
			  }
		  } else {
        for (var l = 0; l < elem.length; l++) {
          if (elem[l].checked) {
            val.push(elem[i].value);
          }
        }
      }
    } else if (et == "text" || et == "hidden" || et == "password"
      || et == "textarea") {
      val.push(elem.value);
    } else if (et == "select-one" || et == "select-multiple") {
      for (var j = 0; j < elem.options.length; j++) {
        if (elem.options[j].selected) {
          val.push(elem.options[j].value);
        }
      }
    }
    for (var k = 0; k < val.length; k++) {
      if (sendMethod == "parameters") {
        if (qs != "") {
          qs += "&";
        }
        qs = qs + en + "=" + encodeURIComponent(val[k]);
      } else if (sendMethod == "json") {
        if (json == "") {
          json = "\"" + theForm.name + "\" : { ";
        } else {
          json += ", ";
        }
        json = json + "\"" + en + "\" : \"" + val[k] + "\"";
      } else if (sendMethod == "xml") {
        if (xml == "") {
          xml = "<" + theForm.name + ">";
        }
        xml = xml + "<" + en + ">" + val[k] + "</" + en + ">";
      }
    }
  }
  var pb = null;
  var hdrs = null;
  if (sendMethod == "parameters") {
    if (evtDef.httpMeth == "post") {
      hdrs = { "Content-Type" : "application/x-www-form-urlencoded" };
      pb = qs;
      qs = "";
    } else if (evtDef.httpMeth == "get") {
      qs = "?" + qs;
    }
  } else if (sendMethod == "json") {
    json += " }";
    pb = json;
  } else if (sendMethod == "xml") {
    xml += "</" + theForm.name + ">";
    pb = xml;
  }
  ajaxPartsTaglib.ajaxRequestSender(evtDef, pb, qs, null, hdrs);
}
