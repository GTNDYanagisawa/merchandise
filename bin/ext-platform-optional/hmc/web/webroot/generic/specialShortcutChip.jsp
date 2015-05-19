<%@include file="../head.inc"%>
<%
	SpecialShortcutChip theChip= (SpecialShortcutChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<%
	if (theChip.isActive())
	{
		%>
			<input type="hidden" name="<%=theChip.getCommandID(GenericShortcutChip.OPEN)%>" value="<%=AbstractChip.FALSE%>">
			<table cellspacing="0" cellpadding="0">
				<tr class="specialShortcutChip">
					<td background="images/icons/button_background.gif" align="left"><img src="images/icons/button_left.gif"></td>
					<td background="images/icons/button_background.gif" align="left"><a href="javascript:document.editorForm.elements['<%=theChip.getCommandID(GenericShortcutChip.OPEN)%>'].value='<%=AbstractChip.TRUE%>';setScrollAndSubmit();"><img src="<%=theChip.getImage()%>"></a></td>
					<td background="images/icons/button_background.gif"><a href="javascript:document.editorForm.elements['<%=theChip.getCommandID(GenericShortcutChip.OPEN)%>'].value='<%=AbstractChip.TRUE%>';setScrollAndSubmit();"><span style="sscActive">&nbsp;<%=theChip.getName()%></span></a></td>
					<td background="images/icons/button_background.gif" align="right"><img src="images/icons/button_right.gif"></td>
				</tr>
			</table>
		<%
	}
	else
	{
		%>
			<table cellspacing="0" cellpadding="0" title="<%=localized("shortcutsnotactive")%>">
				<tr class="specialShortcutChip">
					<td background="images/icons/button_background.gif" align="left"><img src="images/icons/button_left.gif"></td>
					<td background="images/icons/button_background.gif" align="left"><img src="<%=theChip.getImage()%>" style="filter:gray();"></td>
					<td background="images/icons/button_background.gif"><span style="notActive"><%=theChip.getName()%></span></td>
					<td background="images/icons/button_background.gif" align="right"><img src="images/icons/button_right.gif"></td>
				</tr>
			</table>
		<%
	}
%>
