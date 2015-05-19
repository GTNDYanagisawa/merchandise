<%@include file="../head.inc"%>
<%@page import="de.hybris.platform.hmc.attribute.DiffTextDisplayChip"%>

<%
	final DiffTextDisplayChip theChip = (DiffTextDisplayChip)request.getAttribute( AbstractChip.CHIP_KEY );
	final String style = "style=\"width:" + theChip.getWidth() + "px;\" ";
	if( theChip.isWysiwygEditor() )
	{
%>

	<div class="diffTextDisplayChip">

		<div>&nbsp;</div>			
		<div class="wysiwyg">
				<%= theChip.getStringWithMarks() %>
			</div>

	</div>
<%
	}
	else
	{
%>
	<div <%= style %>>
		<table class="diffTextDisplayChip" <%= style %> cellspacing="0" cellpadding="0">
		<tr>
				<td>
				<%= theChip.getStringWithMarks() %>
			</td>
		</tr>
		</table>
	</div>
<%
	}
%>
