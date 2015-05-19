<%@include file="head.inc"%>
<%
	ImageToolbarActionChip theChip = (ImageToolbarActionChip) request.getAttribute(AbstractChip.CHIP_KEY);
	
	final String label = localized(theChip.getLabel());
	final String tooltip = theChip.getTooltip() != null ? localized( theChip.getTooltip() ) : label;
	final String event = theChip.getCommandID(theChip.getEvent());
	final String buttonCSS = theChip.getButtonCSS();
	final String confirm = theChip.getConfirmationMessage();
%>
	<td>
		<div class="<%= buttonCSS %> <%= theChip.isEnabled() ? "chip-event" : "" %>">
			<a href="#" title="<%= tooltip %>" name="<%= event %>" id="<%= theChip.getUniqueName() %>_a" hidefocus="true"><%
				if( confirm != null && confirm.length() > 0 )
				{
					%><span class="confirm-message"><%= confirm %></span><%
				}
%>
				<span>
					<table border="0" cellpadding="0" cellspacing="0">
						<tr>
<%
							if( theChip.hasIcon() )
							{
%>
								<td>
									<img class="icon" src="<%= theChip.getImageURL() %>" id="<%= theChip.getUniqueName() %>_img"/>
								</td>
<%
							}
							if( theChip.showLabel() )
							{
%>
								<td>
									<div class="label" id="<%= theChip.getUniqueName() %>_label"><%= theChip.showLabel() ? label : "" %></div>
								</td>
<%
							}
%>
						</tr>
					</table>
				</span>
			</a>
		</div>
	</td>
<%
	if( theChip.isEnabled() && theChip.getAccessKey() != 0 )
	{
%>
		<script language="JavaScript1.2">
			addKeyEvent("<%= theChip.getAccessKey() %>", "<%= event %>");
		</script>
		
<%
	}
%>
