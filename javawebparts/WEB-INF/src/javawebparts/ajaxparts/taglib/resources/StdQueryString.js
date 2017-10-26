function StdQueryString(evtDef) {
  var qs = "";
  var addition = "?";
  var nvp = evtDef.reqParam.split(",");
  var i = 0;
  while (i < nvp.length) {
    var nav = nvp[i].split("=");
    if (nav[1].charAt(0) == "\'") {
      var fe = nav[1].substring(1, nav[1].length - 1);
      qs += addition + nav[0] + '=' + encodeURIComponent(fe);
    } else {
      var fe = eval("evtDef.theForm[\"" + nav[1] + "\"]");
      var feValues = JWPGetElementValue(fe);
      var j = 0;
      while (j < feValues.length) {
        qs += addition + nav[0] + '=' + encodeURIComponent(feValues[j]);
        addition = "&";
        j++;
      }
    }
    addition = "&";
    i++;
  }
  ajaxPartsTaglib.ajaxRequestSender(evtDef, null, qs);
}
