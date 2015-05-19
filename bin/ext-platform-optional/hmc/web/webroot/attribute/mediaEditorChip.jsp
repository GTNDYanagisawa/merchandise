<%@include file="../head.inc"%>
<%
	MediaEditorChip theChip = (MediaEditorChip)request.getAttribute(AbstractChip.CHIP_KEY);

	final String contextMenu = theChip.hasVisibleContextMenuEntries()
									? "(new Menu(" + theChip.createMenuEntriesForJS(theChip.getMenuEntries()) + ", event, null, null, { uniqueName: '" + theChip.getUniqueName() +"'} )).show(); return false;"
									: "return false;";

	String link;
	if( !theChip.isElementReadable() )
	{
		out.println( localized("elementnotreadable") );
	}
	else
	{
		if( !theChip.canReadURL() )
		{
			link = localized("elementnotreadable");
		}
		else if( theChip.hasItem() )
		{
			if( theChip.isImage() )
			{
				link = getLink( theChip.getEventID(ReferenceAttributeEditorChip.OPEN_EDITOR), "<img src=\"" + theChip.getURL() + "\"/>", "imagelink");
			}
			else
			{
				link = getLink( theChip.getEventID(ReferenceAttributeEditorChip.OPEN_EDITOR), theChip.getSelectedItemAsString(), "");
			}
		}
		else
		{
			link = localized("notdefined");
		}
%>
		<table class="mediaEditorChip" oncontextmenu="<%= contextMenu %>" cellspacing="0" cellpadding="0">
			<tr>
				<td class="link">
					<%= link %>
				</td>
				<td style="padding-left:3px; width:100%">
<%
					if( theChip.isEditable() && !theChip.isPartOf() && theChip.isAttributeTypeReadable() )
					{
%>
						<div class="button-on-white chip-event" style="width:23px;">
							<a href="#" style="width:23px" title="<%= localized("clicktoopensearch") %>" name="<%= theChip.getEventID( ReferenceAttributeEditorChip.OPEN_SEARCH ) %>" hidefocus="true">
								<span>
									<img class="icon" src="images/icons/valueeditor_search.gif" id="<%= theChip.getUniqueName() %>_img"/>
								</span>
							</a>
						</div>
<%
					}
%>
				</td>
			</tr>
<%
					if( theChip.hasItem() && theChip.isElementReadable() )
					{
%>
			<tr>
				<td colspan="2">
					<table class="readable" >
						<tr>
							<td class="label"><%= localized( "url" )%>:&nbsp;</td>
							<td><%= (theChip.getURL() == null ? localized("notdefined") : theChip.getURL()) %></td>
						<tr>
						<tr>
							<td class="label"><%= localized( "realfilename" )%>:&nbsp;</td>
							<td><%= (theChip.getRealFileName() == null ? localized("notdefined") : theChip.getRealFileName()) %></td>
						<tr>
						<tr>
							<td class="label"><%= localized( "mimetype" )%>:&nbsp;</td>
							<td><%= (theChip.getMimeType() == null ? localized("notdefined") : theChip.getMimeType()) %></td>
						<tr>
						<tr>
							<td class="label"><%= theChip.getMediaCodeTitle() %>:&nbsp;</td>
							<td><%= (theChip.getMediaCode() == null ? localized("notdefined") : theChip.getMediaCode()) %></td>
						<tr>
					</table>
				</td>
			</tr>
<%
					}
%>
		</table>
<%
		}
%>
