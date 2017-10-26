function StdSendByID(evtDef) {
  var a = evtDef.reqParam.split(",");
  var requestType = a[0];
  if (requestType.toLowerCase().indexOf("querystring") != -1) {
    var qs = "";
    var addition = "?";
    for (var i = 1; i < a.length; i++) {
      var nextElement = a[i];
      var b = nextElement.split(".");
      var id = b[0];
      var attr = b[1];
      if (attr == "value") {
        var fe = document.getElementById(id);
        var feValues = JWPGetElementValue(fe);
        var j = 0;
        while (j < feValues.length) {
          qs += addition + id + '=' + encodeURIComponent(feValues[j]);
          addition = "&";
          j++;
        }
      } else {
        qs += addition + id + "=";
        qs += encodeURIComponent(eval("document.getElementById('" + id + "')." + attr));
      }
      addition = "&";
    }
    ajaxPartsTaglib.ajaxRequestSender(evtDef, null, qs);
  }
  if (requestType.toLowerCase().indexOf("xml") != -1) {
    var b = requestType.split(".");
    var rootNode = b[1];
    var xml = "<" + rootNode + ">";
    for (var i = 1; i < a.length; i++) {
      var nextElement = a[i];
      b = nextElement.split(".");
      var id = b[0];
      var attr = b[1];
      if (attr == "value") {
        var fe = document.getElementById(id);
        var feValues = JWPGetElementValue(fe);
        var j = 0;
        while (j < feValues.length) {
          xml += "<" + id + ">";
          xml += encodeURIComponent(feValues[j]);
          xml += "</" + id + ">";
          j++;
        }
      } else {
        xml += "<" + id + ">";
        xml += encodeURIComponent(eval("document.getElementById('" + id + "')." + attr));
        xml += "</" + id + ">";
      }
    }
    xml += "</" + rootNode + ">";
    ajaxPartsTaglib.ajaxRequestSender(evtDef, xml, null);
  }
}
