<%@include file="../head.inc"%>
<%
	final DownloadMediaEditorChip theChip = (DownloadMediaEditorChip) request.getAttribute(AbstractChip.CHIP_KEY);
	
	final String uniqueName = theChip.getUniqueName();
	final boolean isEditable = theChip.isEditable() && theChip.isAttributeTypeReadable();
	final String contextMenu = theChip.hasVisibleContextMenuEntries()
								? "(new Menu(" + theChip.createMenuEntriesForJS(theChip.getMenuEntries()) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
								: "return false;";									
	final int widthSearchButt = 23;							

%>
<div style="width:<%= theChip.getWidth() %>px;" oncontextmenu="<%= contextMenu %>">
	<table class="autocompleteReferenceEditorChip" cellpadding="0" cellspacing="0">
		<tr>
			<td style="width:100%;">
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
							style="width:100%;"
							 class="<%= isEditable ? "enabled" : "disabled" %>"
							 id="<%= theChip.getInputID() %>" 
							 <%= isEditable ? "" : "readonly=\"readonly\"" %>
 							 value="<%= theChip.getDisplayValue() %>"/>
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
			if( isEditable && !theChip.isPartOf() && theChip.isAttributeTypeReadable() )
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

			if( theChip.getValue() != null )
			{
%>
				<td style="width:<%= widthSearchButt %>px;">
					<div style="width:<%= widthSearchButt %>px;">
						<a href="<%= theChip.getURL() %>" title="<%= localized("download") %>" hidefocus="true">
							<span>
								<img class="icon" src="images/icons/download.gif"/>
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

				final String options = "{ paramName: '" + AutocompleteReferenceEditorChip.AJAX_SEARCH + "',"
			 + "afterUpdateElement: function(inputElement, selectedListItem){(new AutocompleterToolbarActionCallback('" + theChip.getID() + "', '" + AutocompleteReferenceEditorChip.AJAX_SELECT + "','"+tenantIDStr+"')).callback(inputElement, selectedListItem, null,true); " + (theChip.isRefreshAfterSelect() ? "setScrollAndSubmit();" : (theChip.isRefreshWithoutSubmitAfterSelect() ? "location.reload();":"")) +" },"
				 
			 +	"onShow:       function(element, update)"
			 +								"{ if(!update.style.position || update.style.position=='absolute')"
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
		
		</script>
<%
	}
%>
