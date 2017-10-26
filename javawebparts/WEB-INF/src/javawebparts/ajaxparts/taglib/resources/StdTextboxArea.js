function StdTextboxArea(ajCall, resParam) {
  for (var i = 0; i < ajCall.evtDef.theForm.elements.length; i++) {
    if (ajCall.evtDef.theForm.elements[i].name == resParam) {
      ajCall.evtDef.theForm.elements[i].value = ajCall.xhr.responseText;
    }
  }
}