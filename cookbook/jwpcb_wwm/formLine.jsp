<%@ taglib uri="javawebparts/ajaxparts/taglib" prefix="ajax" %>
<div> 
<span> 
<select name="TypeList" id="TypeList<%= request.getParameter("uniqIdx") %>" onchange="goAndSelect('<%= request.getParameter("uniqIdx") %>')"> 
<option>[Type]</option> 
<option>Type 1</option> 
<option>Type 2</option> 
<option>Type 3</option> 
</select>
</span> 
<span> 
<select name="ValueList" id="ValueList<%= request.getParameter("uniqIdx") %>"> 
<option>[Value]</option> 
</select> 
</span> 
<span> 
<input type="button" value="More" id="MoreButton" name="MoreButton" onclick="moreButtonClicked()"/> 
</span> 
</div> 