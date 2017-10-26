function StdSimpleXML(evtDef) {
  var xml = "";
  var nvp = evtDef.reqParam.split(",");
  var root = nvp[0];
  xml += "<" + root + ">";
  var i = 1;
  while (i < nvp.length) {
    var nav = nvp[i].split("=");
    var fe = eval("evtDef.theForm[\"" + nav[1] + "\"]");
    var feValues = JWPGetElementValue(fe);
    var j = 0;
    while (j < feValues.length) {
      xml += "<" + nav[0] + ">" + encodeURIComponent(feValues[j]) + "</" + nav[0] + ">";
      j++;
    }
    i++;
  }
  xml += "</" + root + ">";
  ajaxPartsTaglib.ajaxRequestSender(evtDef, xml, null);
}
