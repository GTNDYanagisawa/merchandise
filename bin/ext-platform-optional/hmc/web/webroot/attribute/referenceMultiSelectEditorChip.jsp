<%@include file="../head.inc"%>
<%@page import="de.hybris.platform.jalo.enumeration.*" %>
<%
	ReferenceMultiSelectEditorChip theChip = (ReferenceMultiSelectEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
	<%-- the following PING is needed to recognize empty selections (which create no event) --%>
	<input type="hidden" name="<%= theChip.getEventID(ReferenceMultiSelectEditorChip.PING) %>" value="<%= AbstractChip.TRUE %>"/>
<div class="referenceMultiSelectEditorChip">
	<select
		class="<%= (theChip.isEditable() ? "enabled" : "disabled") %>"
		size="<%= theChip.getSize() %>"
		name="<%= theChip.getEventID(ReferenceMultiSelectEditorChip.SET_VALUE) %>"
		style="width:<%= theChip.getWidth() %>px;"
		<%= (theChip.isEditable() ? "" : "disabled") %>
		multiple="multiple" >
<%	
		for( Iterator iter = theChip.getPossibleEntries().iterator(); iter.hasNext(); )
		{
			final Object item = iter.next();
%>
			<option value="<%= theChip.indexOf(item) %>" <%= (theChip.isItemSelected(item) ? "selected" : "") %>>
				<%= theChip.getDisplayString(item) %>
			</option>
<%
		}
%>
	</select>
</div>
