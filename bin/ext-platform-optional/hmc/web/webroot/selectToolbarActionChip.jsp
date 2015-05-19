<%@include file="head.inc"%>
<%
	final AbstractSelectToolbarActionChip theChip = (AbstractSelectToolbarActionChip) request.getAttribute(AbstractChip.CHIP_KEY);
	final String label = (theChip.getLabel() == null ? "" : localized(theChip.getLabel()));
	final String tooltip = (theChip.getTooltip() == null ? label : localized(theChip.getTooltip()));
	final String color = theChip.isPartOf() ? "#ffffff" : "#ffffff";

	final String contextMenu = theChip.hasVisibleContextMenuEntries()
										? "(new Menu(" + theChip.createMenuEntriesForJS(theChip.getMenuEntries()) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
										: "return false;";
										
	theChip.resetEntries();
%>
<%= (label != "" ? ("<td class=\"selectTollbarActionLabel\" style=\"color:" + color+ ";\" title=\"" + tooltip + "\">" + label + ":</td>") : "") %>
<td class="selectTollbarActionDropDown" <%= theChip.getWidth() != null ? "style=\"width=" + theChip.getWidth() + ";\"" : "" %> title="<%= tooltip %>" oncontextmenu="<%= contextMenu %>">

<script language="JavaScript1.2" type="text/javascript">
  markClipStart(2);
</script>

	<select name="<%= theChip.isCommandAction() ? theChip.getCommandID(theChip.getEvent()) : theChip.getEventID(theChip.getEvent()) %>"
			  oncontextmenu="<%= contextMenu %>"
			  id="<%= theChip.getUniqueName() %>_select"
			  class="componentformelement <%= theChip.isEnabled() ? "" : "disabled" %>"
			  <%= theChip.isEnabled() ? "" : "disabled" %>
			  <%= theChip.getWidth() != null ? "style=\"width:" + theChip.getWidth() + ";\"" : "" %>
			  <%= theChip.getJavaScript() != null ? "onchange=\"" + theChip.getJavaScript() + "\"" : "" %>	>
<%
	for( Iterator it = theChip.getEntries().iterator(); it.hasNext(); )
	{ 
		final AbstractSelectToolbarActionChip.Entry entry = (AbstractSelectToolbarActionChip.Entry) it.next();
		final String style = entry.getStyle() != "" ? "style=\"" + entry.getStyle() + "\"" : "";
%>
		<option value="<%= entry.getDisplayValue() %>" <%= theChip.isSelected(entry) ? " selected" : "" %> <%= style %> >
			<%= entry.getDisplayName() %>
		</option>
<%
	} 
%>
	</select>

<script language="JavaScript1.2" type="text/javascript">
  markClipEnd();
</script>

</td>
