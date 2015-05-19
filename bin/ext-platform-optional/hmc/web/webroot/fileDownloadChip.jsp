<%@include file="head.inc"%>
<%
	final FileDownloadChip theChip = (FileDownloadChip)request.getAttribute(AbstractChip.CHIP_KEY);
	final String url = "FileDownloadServlet?" + FileDownloadServlet.CONTENT + "=" + theChip.getContentID();
%>
<%= localized( "download.start" )%>
<div class="filedownloadchip">
<br>
<a href="<%= url %>"><%= localized( "download.url" ) %></a>
<br>
<a href="javascript:window.close()"><%= localized( "download.closewindow" ) %></a>
</div>