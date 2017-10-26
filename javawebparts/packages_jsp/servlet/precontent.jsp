  <%
    // Construct the query string to append to our image source, based on what
    // was entered by the user, if applicable.  This is the the
    // TextRendererServlet test.
    String queryString = "";
    String fontName    = request.getParameter("fontName");
    String fontPoint   = request.getParameter("fontPoint");
    String stylePlain  = request.getParameter("stylePlain");
    String styleBold   = request.getParameter("styleBold");
    String styleItalic = request.getParameter("styleItalic");
    String color       = request.getParameter("color");
    String backColor   = request.getParameter("backColor");
    String text        = request.getParameter("text");
    if (fontName != null && !fontName.equalsIgnoreCase("")) {
      if (queryString.equalsIgnoreCase("")) {
        queryString = "?";
      } else {
        queryString += "&";
      }
      queryString += "fontName=" + fontName;
    }
    if (fontPoint != null && !fontPoint.equalsIgnoreCase("")) {
      if (queryString.equalsIgnoreCase("")) {
        queryString = "?";
      } else {
        queryString += "&";
      }
      queryString += "fontPoint=" + fontPoint;
    }
    if (queryString.equalsIgnoreCase("")) {
      queryString = "?";
    } else {
      queryString += "&";
    }
    if (stylePlain != null && !stylePlain.equalsIgnoreCase("")) {
      queryString += "stylePlain=" + stylePlain;
    } else {
      queryString += "stylePlain=false";
    }
    if (queryString.equalsIgnoreCase("")) {
      queryString = "?";
    } else {
      queryString += "&";
    }
    if (styleBold != null && !styleBold.equalsIgnoreCase("")) {
      queryString += "styleBold=" + styleBold;
    } else {
      queryString += "styleBold=false";
    }
    if (queryString.equalsIgnoreCase("")) {
      queryString = "?";
    } else {
      queryString += "&";
    }
    if (styleItalic != null && !styleItalic.equalsIgnoreCase("")) {
      queryString += "styleItalic=" + styleItalic;
    } else {
      queryString += "styleItalic=false";
    }
    if (color != null && !color.equalsIgnoreCase("")) {
      if (queryString.equalsIgnoreCase("")) {
        queryString = "?";
      } else {
        queryString += "&";
      }
      queryString += "color=" + color;
    }
    if (backColor != null && !backColor.equalsIgnoreCase("")) {
      if (queryString.equalsIgnoreCase("")) {
        queryString = "?";
      } else {
        queryString += "&";
      }
      queryString += "backColor=" + backColor;
    }
    if (text != null && !text.equalsIgnoreCase("")) {
      if (queryString.equalsIgnoreCase("")) {
        queryString = "?";
      } else {
        queryString += "&";
      }
      queryString += "text=" + text;
    }

  %>