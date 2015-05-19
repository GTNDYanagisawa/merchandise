<%@page import="de.hybris.platform.hmc.webchips.*"%>
<%@page import="de.hybris.platform.hmc.*"%>
<html>
<head><title>hybris hMC</title>
<%
	Frame frame = (Frame)request.getAttribute(AbstractChip.CHIP_KEY);
	FrameSet mainChip = (FrameSet)frame.getMainChip();
	mainChip.render(pageContext);
%>
</head>
<body>
	Your browser does not support frames.
</body>
</html>
