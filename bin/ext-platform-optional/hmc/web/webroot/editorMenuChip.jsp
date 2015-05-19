<%@include file="head.inc"%>
<%
	final AbstractEditorMenuChip theChip = (AbstractEditorMenuChip) request.getAttribute(AbstractChip.CHIP_KEY);
	final String cssClass = theChip.isPartOf() ? "organizereditortab" : "detachededitortab";
%>
<table class="editorMenuChip" cellspacing="0" cellpadding="0">
	<tr>
		<td colspan="2">
			<div class="<%= cssClass %>">
			  <ul>
<%
					for( final Iterator iter = theChip.getAllVisibleEditorTabChips().iterator(); iter.hasNext(); )
					{
						final EditorTabChip tab = (EditorTabChip) iter.next();
						tab.render(pageContext);
					}
%>
			  </ul>
			</div> 
		</td>
	</tr>
<%
	if( theChip.hasEssentials() && !theChip.hideEssentials() )
	{
%>
	<tr>
		<td><%theChip.getEssentials().render(pageContext);%></td>
		<td class="rightPadder"><div class="rightPadder">&nbsp;</div></td>
	</tr>
<%
	}
%>
	<tr>
		<td><% theChip.getDetailEditor().render(pageContext); %></td>
		<td class="rightPadder"><div class="rightPadder">&nbsp;</div></td>
	</tr>
</table>	
