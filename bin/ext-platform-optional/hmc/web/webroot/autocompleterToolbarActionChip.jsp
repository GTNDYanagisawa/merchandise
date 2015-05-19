<%@include file="head.inc"%>
<%
	final AbstractAutocompleterToolbarActionChip theChip = (AbstractAutocompleterToolbarActionChip) request.getAttribute(AbstractChip.CHIP_KEY);

	final String label = (theChip.getLabel() == null ? "" : localized(theChip.getLabel()));
	final String tooltip = (theChip.getTooltip() == null ? label : localized(theChip.getTooltip()));
	final String width = theChip.getWidth() != null && theChip.getWidth().length() > 0 ? theChip.getWidth() : "200px";
	final String value = theChip.getValue() != null ? "value=\"" + theChip.getValue() + "\"" : "";

	final String contextMenu = theChip.hasVisibleContextMenuEntries()
								? "(new Menu(" + theChip.createMenuEntriesForJS(theChip.getMenuEntries()) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
								: "return false;";
%>

<td title="<%= tooltip %>" class="toolbar-autocomplete" oncontextmenu="<%= contextMenu %>">
	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td class="label"><%= label %>:</td>
			<td><input type="text" 
						 id="<%= theChip.getInputID() %>" 
						 style="width:<%= width %>;"
						 name="<%= theChip.getEventID(AbstractAutocompleterToolbarActionChip.VALUE) %>" 
						 <%= value %>/><div id="<%= theChip.getMatchesID() %>" class="autocomplete"></div></td>
		</tr>
	</table>
</td>
<%
	final String options = "{ paramName: '" + AbstractAutocompleterToolbarActionChip.SEARCH + "',"
								 + "afterUpdateElement: function(inputElement, selectedListItem) "
								 + 							"{ if( selectedListItem.nodeName == \"LI\" )"
								 +										"{ setEvent('" + theChip.getCommandID(AbstractAutocompleterToolbarActionChip.SELECT) + "', domQuery('span.hidden', selectedListItem)[0].firstChild.nodeValue);setScrollAndSubmit(); }"
								 +									"else "
								 +										"{	inputElement.value = \"\"; }},"
								 +	"onShow:       function(element, update)"
								 +								"{ if(!update.style.position || update.style.position=='absolute')"
								 +										"{	update.style.position = 'absolute'; Position.clone(element, update, { setHeight: false, setWidth:false, offsetTop: element.offsetHeight }); }"
								 +								  "Effect.Appear(update,{duration:0.15}); } }";
	final String tenantIDStr = Registry.getCurrentTenant() instanceof SlaveTenant ?
		";tenantID="+Registry.getCurrentTenant().getTenantID() : "";
%>
<script language="JavaScript1.2">
	
	new Ajax.Autocompleter("<%= theChip.getInputID() %>", "<%= theChip.getMatchesID() %>", "prototype<%=tenantIDStr%>?<%= PrototypeServlet.CHIPID %>=<%= theChip.getID() %>", <%= options %>);

</script>
		
