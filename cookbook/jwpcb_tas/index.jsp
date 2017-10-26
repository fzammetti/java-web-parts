<%@ taglib prefix="ajax" uri="javawebparts/ajaxparts/taglib" %>
<html>
<head>
  <title>Java Web Parts Cookbook: Type-Ahead Suggestions</title>
</head>
<body>
  Java Web Parts Cookbook
  <br/>
  Type-Ahead Suggestions
  <br/><br/>
  This shows what was made famous by Google Suggest.  Type a word in the
  textbox and you will see a listing below of matching words.  Note that nothing
  will be seen if you start with anything other than a letter.
  <br/><br/>
  <form name="TypeAheadSuggestionsForm">
    <input type="text" name="enteredTextbox" size="30"><ajax:event ajaxRef="TypeAheadSuggestionsForm/enteredTextChange"/>
    <br>
    <div id="suggestions" style="border:1 solid #000000;background-color:#f0f0f0;width:206px;">
  </form>
</body>
</html>
<ajax:enable debug="debug" logger="JWPWindowLogger"/>