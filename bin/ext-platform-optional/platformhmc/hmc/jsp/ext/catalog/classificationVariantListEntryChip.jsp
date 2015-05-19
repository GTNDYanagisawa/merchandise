<%@include file="../../head.inc"%>

<%@page import="de.hybris.platform.catalog.hmc.ClassificationVariantListEntryChip,
					 de.hybris.platform.catalog.hmc.ClassificationAttributeEntry" %>

<%
	final ClassificationVariantListEntryChip theChip = (ClassificationVariantListEntryChip) request.getAttribute(AbstractChip.CHIP_KEY);
	boolean isEditable = true;
	if( theChip.getParent() instanceof GenericItemListChip )
	{
		isEditable = ((GenericItemListChip) theChip.getParent()).isEditable();
	}
	
	if( !theChip.isElementReadable() )
	{
%>		
		<td colspan="<%= theChip.getAttributeCount() + 2 %>" <%= !isEditable ? "style=\"border-left:1px solid #bbbbbb;\"" : "" %>>
			<%= localized("elementnotreadable") %>
		</td>
<%
	}
	else
	{
%>		
		<td style="<%= !isEditable ? "border-left:1px solid #bbbbbb;" : "" %>">
			<table class="cvlec" cellpadding="0" cellspacing="0">
				<tr>
					<td class="cvlcIsChangedSign">
						<div class="cvlcIsChangedSign">
							<%= theChip.isChanged() ? "*" : "" %>
						</div>
					</td>
					<td class="cvlcIcon">
						<div class="cvlcIcon">
							<%= getIconButton(theChip.getEventID(GenericItemListEntryChip.OPEN_EDITOR), 
														localized("open_editor"), 
														theChip.getIcon(), 
														null, 
														false,
														true) %>
						</div>
					</td>											
				</tr>
			</table>
		</td>
<%
		// all "normal" product attributes (which have been configured to be shown in this list)
		for (final Iterator it = theChip.getAttributeNodes().iterator(); it.hasNext(); )
		{
			AttributeNode an = (AttributeNode) it.next();
			String width = (an.getActualColumnWidth() == 0 || !it.hasNext()) ? "" : " style=\"width: " + an.getColumnWidth() + "px; overflow: hidden;\" ";
%>
			<td <%= width %>>
				<div class="cvlcEntry" <%= width %>>
<%
				if( theChip.getAttributeEditors().containsKey(an.getAttributeQualifier()) )
				{
					theChip.getEditorForCurrentLanguage(an.getAttributeQualifier()).render(pageContext);
				}
%>			
				</div>
			</td>
<%
		}
		
		// all classification attributes, dynamically derived for the current product
		for( final Iterator categoryIter = theChip.getClassificationMap().keySet().iterator(); categoryIter.hasNext(); )
		{
			for( final Iterator entryIter = ((Collection) theChip.getClassificationMap().get( categoryIter.next() )).iterator(); entryIter.hasNext(); )
			{
				final ClassificationAttributeEntry caEntry = (ClassificationAttributeEntry) entryIter.next();
%>
				<td>
					<div class="cvlcEntry">
<%
					caEntry.getValueEditor(theDisplayState.getJaloSession().getSessionContext().getLanguage()).render(pageContext);
%>
					</div>
				</td>
<%
			}
		}
	}
%>
