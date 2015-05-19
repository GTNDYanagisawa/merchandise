<%@include file="head.inc"%>
<%
	DropDownToolbarActionChip theChip = (DropDownToolbarActionChip) request.getAttribute(AbstractChip.CHIP_KEY);
	
	final String label = theChip.getLabel();
	final String tooltip = theChip.getTooltip() != null ? localized( theChip.getTooltip() ) : label;
	final String buttonCSS = theChip.getButtonCSS();
%>

	<td>
		<div class="<%= buttonCSS %>">
			<a href="#" title="<%= tooltip %>"
<%
				if( theChip.isEnabled() )
				{
%>
					onclick="(new Menu(<%= theChip.createMenuEntriesForJS(theChip.getMenuEntries()) %>, event, this, null, <%= theChip.getOptions() %>)).show(); return false;"
<%
				}
%>
			>
				<span>
					<table border="0" cellpadding="0" cellspacing="0">
						<tr>
<%
							if( theChip.hasIcon() )
							{
%>
								<td>
									<img class="icon" src="<%= theChip.getImageURL() %>"/>
								</td>
<%
							}
%>
							<td>
								<div class="label" id="<%= theChip.getUniqueName() %>_button">
									<%= label %>
								</div>
							</td>
						</tr>
					</table>
				</span>
			</a>
		</div>
	</td>
		
