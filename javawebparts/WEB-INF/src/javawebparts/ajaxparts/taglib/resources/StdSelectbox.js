function StdSelectbox(ajCall, resParam) {
  var list = ajCall.xhr.responseXML.getElementsByTagName(
    "list").item(0).getElementsByTagName("option");
  var targetNames = resParam.split(",");
  for (var k = targetNames.length - 1; k >= 0; k--) {
    var targetName = targetNames[k];
    var targetFields = document.getElementsByName(targetName);
    for (var j = 0; j < targetFields.length; j++) {
      var targetField = targetFields[j];
      targetField.options.length = 0;
      if (list != null) {
        targetField.disabled = false;
        for (var i = 0; i < list.length; i++) {
          if (list[i].firstChild) {
            var temp = list[i].firstChild.nodeValue;
          }
          targetField.options[i] = new Option(temp, list[i].getAttribute("value"));
        }
      }
    }
    try {
      targetFields[0].focus();
    } catch(error) {}
  }
}
