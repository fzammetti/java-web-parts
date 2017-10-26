function StdAlertErrorHandler(ajCall) {
  alert("StdAlertErrorHandler: " + ajCall.xhr.status + " - " +
    ajCall.xhr.statusText);
}
