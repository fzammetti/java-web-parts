function StdRedirecter(ajCall, resParam) {
  if (resParam == null || resParam == "" || resParam == "null") {
    window.location.href = ajCall.xhr.responseText;
  } else {
    window.location.href = resParam;
  }
}
