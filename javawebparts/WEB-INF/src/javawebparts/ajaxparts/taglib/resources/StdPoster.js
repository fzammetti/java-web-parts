function StdPoster(evtDef) {
  var pb = "";
  var addition = "";
  var nvp = evtDef.reqParam.split(",");
  var i = 0;
  var fe = null;
  while (i < nvp.length) {
    var nav = nvp[i].split("=");
    if (nav[1].charAt(0) == "\'") {
      fe = nav[1].substring(1, nav[1].length - 1);
      pb += addition + nav[0] + "=" + encodeURIComponent(fe);
    } else {
      fe = eval("evtDef.theForm[\"" + nav[1] + "\"]");
      var feValues = JWPGetElementValue(fe);
      var j = 0;
      while (j < feValues.length) {
        pb += addition + nav[0] + '=' + encodeURIComponent(feValues[j]);
        addition = "&";
        j++;
      }
    }
    addition = "&";
    i++;
  }
  ajaxPartsTaglib.ajaxRequestSender(evtDef, pb, null, null,
    {"Content-Type":"application/x-www-form-urlencoded"});
}
