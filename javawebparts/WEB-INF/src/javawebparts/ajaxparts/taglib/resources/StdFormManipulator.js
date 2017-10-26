function StdFormManipulator(ajCall, resParam) {
  var formNode = ajCall.xhr.responseXML.getElementsByTagName("form").item(0);
  if (!(formNode)) {
    ajaxPartsTaglib.log.error("Response from server should be XML in format\\n" +
      "<form name=...>\\n <formproperty name=... value=.../>\\n " +
      "<element name=... value=...>\\n  <property name=... value=.../>\\n " +
      "</element>\\n</form>");
    return;
   }
   var form = document.getElementsByName(formNode.getAttribute("name"))[0];
   if (!(form)) {
     ajaxPartsTaglib.log.error("There is no form named " + formNode.getAttribute("name"));
     return;
   }
   var children = formNode.getElementsByTagName("formproperty");
   for (var i = 0; i < children.length; i++) {
     JWPchangeproperty(form, children[i]);
   }
   var children = formNode.getElementsByTagName("element");
   for (i = 0; i < children.length; i++) {
     JWPchangeelement(form, children[i]);
   }
}

function JWPchangeproperty(target, node) {
  var propName = node.getAttribute("name");
  var propValue = node.getAttribute("value");
  target[propName] = propValue;
}
function JWPchangeelement(target, node) {
  var field = target[node.getAttribute("name")];
  if (!(field)) {
    ajaxPartsTaglib.log.error("There is no element named " + node.getAttribute("name"));
    return;
  }
  field.value = node.getAttribute("value");
  var properties = node.getElementsByTagName("property");
  for (var j = 0; j < properties.length; j++) {
    var propName = properties[j].getAttribute("name");
    var fieldProp = "";
    var fieldProps = propName.split(".");
    for (var k = 0; k < fieldProps.length; k++) {
      fieldProp += "[\"" + fieldProps[k] + "\"]";
    }
    try {
      eval("field" + fieldProp + " = properties[j].getAttribute(\"value\")");
    } catch (error) {
      ajaxPartsTaglib.log.error("Setting field property returns error: " + error.message);
    }
  }
}