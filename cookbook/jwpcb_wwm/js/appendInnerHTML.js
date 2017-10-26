function AppendInnerHTML(ajCall, resParam) { 
  var a = resParam.split(","); 
  for (var i = 0; i < a.length; i++) { 
    document.getElementById(a[i]).innerHTML += ajCall.xhr.responseText; 
  } 
} 