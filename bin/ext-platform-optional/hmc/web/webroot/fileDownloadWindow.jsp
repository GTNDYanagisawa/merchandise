<%@include file="head.inc"%>
<%
	final Frame theChip = (Frame)request.getAttribute(AbstractChip.CHIP_KEY);
	final FileDownloadChip mainChip = (FileDownloadChip)theChip.getMainChip();
	final String url = "FileDownloadServlet?" + FileDownloadServlet.CONTENT + "=" + mainChip.getContentID();
	final String title = ( (Window)theChip ).getName();
%>
<html>
	<head>
		<title><%=title%> - hybris Management Console (HMC)</title>
		<meta http-equiv="refresh" content="0; URL=<%= url %>">
		<link rel="stylesheet" type="text/css" media="all" href="css/hmc.css"/>
	</head>
	<body>
<%
	mainChip.render( pageContext );
%>
	</body>
</html>
