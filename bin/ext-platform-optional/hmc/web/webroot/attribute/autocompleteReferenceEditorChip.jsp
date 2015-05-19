<%@include file="../head.inc"%>
<%
	final AutocompleteReferenceEditorChip theChip = (AutocompleteReferenceEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	
	final String uniqueName = theChip.getUniqueName();

	final boolean isEditable = theChip.isEditable() && theChip.isAttributeTypeReadable();
	
	final String contextMenu = theChip.hasVisibleContextMenuEntries()
								? "(new Menu(" + theChip.createMenuEntriesForJS(theChip.getMenuEntries()) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
								: "return false;";
								
	
	final int widthSearchButt = 23;							
	final int widthEdit = theChip.getWidth() - widthSearchButt;
	final int widthNotEdit = theChip.getWidth();
	final int listWidth = theChip.getListWidth();

%>
<div id="<%= theChip.getUniqueName() %>" style="width:<%= theChip.getWidth() %>px;" oncontextmenu="<%= contextMenu %>">
	<table class="autocompleteReferenceEditorChip" cellpadding="0" cellspacing="0">
		<tr>
			<td style="width:<%= isEditable ? widthEdit : widthNotEdit %>px;">
<%
				if( !theChip.isElementReadable() )
				{
%>
					<%= localized("elementnotreadable") %>
<%
				}
				else
				{
%>
					<input type="text" 
							 class="<%= isEditable ? "enabled" : "disabled" %>"
							 id="<%= theChip.getInputID() %>" 
							 style="width:<%= isEditable ? widthEdit : widthNotEdit %>px;"
							 <%= isEditable ? "" : "readonly=\"readonly\"" %>
 							 value="<%= escapeHTML(theChip.getDisplayValue()) %>"/>
<% 
/*
							if( isEditable )
							{
%>
 							 	<div id="<%= theChip.getMatchesID() %>" class="autocomplete"></div>
<%
							}
*/
				}
%>
			</td>
<%
			if( isEditable )
			{
%>
				<td style="width:<%= widthSearchButt %>px;">
					<div class="button-on-white chip-event" style="width:<%= widthSearchButt %>px;">
						<a href="#" style="width:<%= widthSearchButt %>px" title="<%= localized("clicktoopensearch") %>" name="<%= theChip.getEventID( ReferenceAttributeEditorChip.OPEN_SEARCH ) %>" hidefocus="true">
							<span>
								<img class="icon" src="images/icons/valueeditor_search.gif" id="<%= theChip.getUniqueName() %>_img"/>
							</span>
						</a>
					</div>
				</td>
<%
			}
%>
		</tr>
	</table>
</div>
<%
	if( isEditable )
	{
		final String tenantIDStr = Registry.getCurrentTenant() instanceof SlaveTenant ?	";tenantID="+Registry.getCurrentTenant().getTenantID() : "";
		final String style = (listWidth > 0) ? "update.setStyle({ width: " + listWidth + " });" : "";
			
		final String options = "{ paramName: '" + AutocompleteReferenceEditorChip.AJAX_SEARCH + "',"
							 + "afterUpdateElement: function(inputElement, selectedListItem){ (new AutocompleterToolbarActionCallback('" + theChip.getID() + "', '" + AutocompleteReferenceEditorChip.AJAX_SELECT + "','"+tenantIDStr+"')).callback(inputElement, selectedListItem, $('" +  theChip.getUniqueName() + "'), true); " + (theChip.isRefreshAfterSelect() ? "setScrollAndSubmit();" : "") +" },"
							 +	"onShow:       function(element, update) { "
							 +								style
							 +								"if(!update.style.position || update.style.position=='absolute')"
							 +										"{	update.style.position = 'absolute'; Position.clone(element, update, { setHeight: false, setWidth:false, offsetTop: element.offsetHeight }); }"
							 +								  "Effect.Appear(update,{duration:0.15}); } }";
%>
		<script language="JavaScript1.2">
			
			Behaviour.addLoadEvent(function() { 
				var autocompleteDiv = document.createElement("div");
				autocompleteDiv.className = "autocomplete";
				autocompleteDiv.id = "<%= theChip.getMatchesID() %>";
				document.getElementsByTagName("body")[0].appendChild(autocompleteDiv); 
				new Ajax.Autocompleter("<%= theChip.getInputID() %>", "<%= theChip.getMatchesID() %>", "prototype<%=tenantIDStr%>?<%= PrototypeServlet.CHIPID %>=<%= theChip.getID() %>", <%= options %>);
			 });
		
		if( <%= theChip.getRequestFocus() %> )
		{
			$("<%= theChip.getInputID() %>").focus();
		}
		</script>
<%
	}
%>
