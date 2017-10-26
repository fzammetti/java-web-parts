function StdXSLT(ajCall, resParam) {
  var RHPs = resParam.split(",");
  var processor = new XSLTProcessor();
  var xslDoc = Sarissa.getDomDocument();
  xslDoc.async = false;
  xslDoc.load(RHPs[1]);
  if (xslDoc.parseError != 0){
    alert(Sarissa.getParseErrorText(xslDoc));
  }
  processor.importStylesheet(xslDoc);
  var xmlDoc = Sarissa.getDomDocument();
  xmlDoc.async = false;
  xmlDoc.loadXML(ajCall.xhr.responseText);
  var newDocument = processor.transformToDocument(xmlDoc);
  document.getElementById(RHPs[0]).innerHTML = Sarissa.serialize(newDocument);
}
