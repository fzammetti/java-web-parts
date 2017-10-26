function StdWindowOpener(ajCall, resParam) {
  var a = resParam.split("~");
  var windowHandle = window.open("", a[0], a[1]);
  windowHandle.document.write(ajCall.xhr.responseText);
  windowHandle.document.close();
}
