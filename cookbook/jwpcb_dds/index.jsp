<%@ taglib prefix="ajax" uri="javawebparts/ajaxparts/taglib" %>

<html>
<head>
  <title>Java Web Parts Cookbook: Dynamic Double Select</title>
</head>
<body>
  Java Web Parts Cookbook
  <br/>
  Dynamic Double Select
  <br/><br/>
  This shows how a selection in one &lt;select&gt; element can change the
  contents of another based on a request to the server. This can be done 
  in more than one way, we show you the most common one's from APT.
  <br/><br/>
  <form name="DynamicDoubleSelectForm">
  <table cellpadding="20" width="90%">
   <tr>
     <td width="50%">
	    Select one of my favorite TV shows to see a list of characters:<br/>
	    <select name="showTitleSelect">
	      <option></option>
	      <option value="B5">Babylon 5</option>
	      <option value="BSG">Battlestar Galactica</option>
	      <option value="STTNG">Star Trek The Next Generation</option>
	      <option value="STTOS">Star Trek The Original Series</option>
	      <option value="SGA">Stargate Atlantis</option>
	      <option value="SG1">Stargate SG-1</option>
	    </select><ajax:event ajaxRef="DynamicDoubleSelectForm/showTitleChange"/>
     </td>
     <td>
	    Select one of my favorite TV shows to see a list of characters:<br/>
	    <select name="showTitleSelect2">
	      <option></option>
	      <option value="B5">Babylon 5</option>
	      <option value="BSG">Battlestar Galactica</option>
	      <option value="STTNG">Star Trek The Next Generation</option>
	      <option value="STTOS">Star Trek The Original Series</option>
	      <option value="SGA">Stargate Atlantis</option>
	      <option value="SG1">Stargate SG-1</option>
	    </select><ajax:event ajaxRef="DynamicDoubleSelectForm/showTitleChange2"/>
     </td>
    </tr>
   </table>  
  </form>
  <table cellpadding="20" width="90%">
   <tr>
     <td width="50%">
	  Here's the list of characters:
	  <div id="characters">
	    <select>
	    </select>
	  </div>
     </td>
     <td>
	  Here's the list of characters:
	   <div>
	    <select id="characters2" name="characters2">
	    </select>
	   </div> 
     </td>
    </tr>
   </table>  
</body>
</html>
<ajax:enable debug="debug" logger="JWPAlertLogger"/>