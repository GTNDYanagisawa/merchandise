<%@include file="head.inc"%>
<%
	CustomChip theChip = ( CustomChip )request.getAttribute( AbstractChip.CHIP_KEY );

	Iterator it = theChip.getAttributes().keySet().iterator();
%>
<div class="customChip">
<%= theChip.getAttributes().size()%> parameters:<br />
<%	
	while( it.hasNext())
	{
		String key;
		key = ( String )it.next();
%>
<%= key %>=<%= theChip.getAttributes().get( key ) %><br />
<%
	}
%>
</div>
