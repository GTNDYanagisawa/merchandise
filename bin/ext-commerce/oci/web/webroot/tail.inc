<br><br>
<font size="1">
<% out.println("logged in as: " + JaloSession.getCurrentSession().getUser().getUID()); %>
</font>
<hr>
<font size="1">
<%=Config.getParameter("build.description")%> - <%=Config.getParameter("build.version")%> - 
<%=Config.getParameter("build.builddate")%> - 
Copyright (c) 2000-2013 hybris AG - <a href="http://www.hybris.de">www.hybris.com</a></font><p>
<%
JaloSession.deactivate();
%>

</body>
</html>


