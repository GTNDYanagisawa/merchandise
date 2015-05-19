<%@include file="../../head.inc"%>
<%@include file="../../xp_button.inc"%>
<%@page import="de.hybris.platform.lucenesearch.hmc.AttributeConfigurationCreatorChip"%>

<%
	AttributeConfigurationCreatorChip theChip = (AttributeConfigurationCreatorChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>


<table class="attribute" cellspacing="0" cellpadding="0"  title=" Dieses Attribut ist nicht editierbar.">
	<tr>
		<td width="16px">&nbsp;</td>
 		<td width="110px">
 			<span class="disabled">
 				<%= localized( "lucenesearch.attributeconfigurationcreator.name" ) %>
 			</span>
 		</td>
 		<td width="20px">&nbsp;</td>
 		<td width="0px"></td>
 		<td> <% theChip.getAttributeSelectorChip().render( pageContext ); %> </td>
 		<td> 
			<div class="xp-button chip-event">
				<a href="#" title="<%= localized("lucenesearch.attributeconfigurationcreator.tooltip") %>" 
					name="<%= theChip.getCommandID(theChip.CREATE_COMMAND) %>" hidefocus="true" id="<%= theChip.getUniqueName() %>_a">
					<span class="label" id="<%= theChip.getUniqueName() %>_span">
						<%= localized("lucenesearch.attributeconfigurationcreator.button") %>
					</span>
				</a>
			</div>
		</td>
	</tr>
</table>
