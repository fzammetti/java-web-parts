<script>
  function jumpToAnchor() {
    <% if (request.getParameter("hash") != null) { %>
      location.hash = "<%=request.getParameter("hash")%>";
    <% } %>
  }
</script>