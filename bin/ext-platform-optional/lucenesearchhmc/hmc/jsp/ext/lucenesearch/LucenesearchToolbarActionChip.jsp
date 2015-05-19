<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.lucenesearch.hmc.*"%>

<%
	LucenesearchToolbarActionChip theChip = (LucenesearchToolbarActionChip) request.getAttribute(AbstractChip.CHIP_KEY);
	final String patternEvent = theChip.getEventID(LucenesearchToolbarActionChip.PATTERN);
	final String startEvent = theChip.getCommandID(LucenesearchToolbarActionChip.START_SEARCH);
	final String refreshIndexEvent = theChip.getCommandID(LucenesearchToolbarActionChip.REFRESH_INDEX);
	final boolean isEnabled = theChip.isEnabled();
	final String label = theChip.getLabel()==null?"":localized(theChip.getLabel());
	final String tooltip = theChip.getTooltip() != null ? localized(theChip.getTooltip()) : label;

	if( theChip.showLabel() )
	{
%>
		<td style="padding-right:5px; vertical-align:middle;" title="<%= tooltip %>">
			<%= label.length()==0?label:label+":" %>
		</td>
<%
	}
%>
<td style="vertical-align: middle;" title="<%= tooltip %>">
	<input name="<%= patternEvent %>" 
			type="text" class="editorform" size="<%=theChip.getSize()%>" 
			value="<%= HMCHelper.getXSSFilter("img").filter( theChip.getValue() )%>" <%= (isEnabled ? "" : "readonly=\"readonly\"") %>
			onkeypress="if(isEnter(event)) { document.editorForm.elements['<%= startEvent %>'].value = <%= AbstractChip.TRUE %>; setScrollAndSubmit(); return false;}"/>
</td>
<%
		String button = getMainToolbarButton(startEvent,
												theChip.getSearchLabel(), 
												tooltip,
												theChip.getSearchImageURL(), 
												null, 
												false, 
												false,
												isEnabled);
%>
<td style="vertical-align:middle;">
	<%= button %>
</td>
<%
		 button = getMainToolbarButton(refreshIndexEvent,
										theChip.getIndexLabel(), 
										tooltip,
										theChip.getIndexImageURL(), 
										null, 
										false, 
										false,
										theChip.isRefreshIndexEnabled());
%>
<!--
<td style="vertical-align:middle; padding-right:5px;">
	<%= button %>
</td>
-->
