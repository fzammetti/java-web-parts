function JWPGetElementValue(field) {
  if (field.length != null) {
    var type = field[0].type;
  }
  if ((typeof(type) == 'undefined') || (type == 0)) {
    var type = field.type;
  }
  var result = new Array();
  switch(type) {
    case 'undefined': break;
    case 'radio':
      for (var x=0; x < field.length; x++) {
        if (field[x].checked == true) {
           result[0] = field[x].value;
        }
      }
      break;
    case 'select-multiple':
      for(var x=0; x < field.length; x++) {
        if (field[x].selected == true) {
          result[result.length] = field[x].value;
        }
      }
      break;
    case 'checkbox':
      result[0] = field.checked;
      break;
    default:
      result[0] = field.value;
  }
  return result;
}
