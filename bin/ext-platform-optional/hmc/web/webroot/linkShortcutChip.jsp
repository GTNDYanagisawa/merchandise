<%@include file="head.inc"%>

<%
	LinkShortcutChip theChip= (LinkShortcutChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<%	
	StringBuffer parameters = new StringBuffer();
	Map paramMap = theChip.getParameters();
	for( Iterator iter = paramMap.keySet().iterator(); iter.hasNext(); )
	{
		String key = (String) iter.next();
		parameters.append("&" + key + "=" + paramMap.get(key));
	}
%>
	<div class="xp-button">
		<a href="#" style="width:120px;" title="<%= localized(theChip.getName()) %>" onClick="window.location.href=window.location.href+'<%= parameters %>'; return true;">
			<span>
<%
				if( theChip.getIcon() != null && !theChip.getIcon().equals("") )
				{
%>
					<img class="icon" src="<%= theChip.getIcon() %>"/>
<%
				}
%>
				<div class="label">
					<%= localized(theChip.getName()) %>
				</div>
			</span>
		</a>
	</div>
