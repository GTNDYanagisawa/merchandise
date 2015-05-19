<%@include file="../head.inc"%>
<%
	ModalGenericSearchChip theChip = (ModalGenericSearchChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<%theChip.getConditionsChip().render(pageContext);%>
<%
	if (theChip.getListChip() != null)
	{
%>
		<div class="mgscContext">
<%
			theChip.getListChip().render(pageContext);
%>
		</div>
<%
	}
	else
	{
%>
		<div  class="mgscNoContext">
			<div class="xp-button chip-event">
				<a href="#" title="<%= localized("cancel") %>" name="<%= theChip.getCommandID(ModalGenericSearchChip.CANCEL) %>" hidefocus="true">
					<span class="label">
						<%= localized("cancel") %>
					</span>
				</a>
			</div>
		</div>
<%
	}
%>
