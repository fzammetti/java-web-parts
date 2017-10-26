var fieldSuffix = "null";
function goAndSelect(suffix) {
	fieldSuffix = suffix;
	typeSelected();
}

function CustomSelectBox(ajCall, resParam) {
  var list = ajCall.xhr.responseXML.getElementsByTagName("list").item(0);
  targetNames = resParam.split(",");
  for (var k = targetNames.length - 1; k >= 0; k--) {
    var targetName = targetNames[k] + fieldSuffix;  
    var targetFields = document.getElementsByName(targetName);
    for (var j = 0; j < targetFields.length; j++) {
      var targetField = targetFields[j];
      targetField.options.length = 0;
      if (list != null) {
        targetField.disabled = false;
        var items = list.childNodes;
        for (var i = 0; i < items.length; i++) {
           targetField.options[i] = new Option(items[i].firstChild.nodeValue,
             items[i].getAttribute("value"));
        }
      }
    }
    try {
      targetFields[0].focus();
    } catch(error) {}
  }
}