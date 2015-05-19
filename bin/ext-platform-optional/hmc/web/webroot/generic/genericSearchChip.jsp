<%@include file="../head.inc"%>
<%
	GenericSearchChip theChip = (GenericSearchChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<%
	if (theChip.getListChip() == null)
	{
		theChip.getConditionsChip().render(pageContext);
	}
	if (theChip.getListChip() != null)
	{
		theChip.getListChip().render(pageContext);
	}
%>
