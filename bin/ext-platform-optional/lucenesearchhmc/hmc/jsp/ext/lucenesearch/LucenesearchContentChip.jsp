<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.lucenesearch.hmc.LucenesearchContentChip"%>

<%
	LucenesearchContentChip theChip = (LucenesearchContentChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>

	<table cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td>
			<%
				theChip.getSearchAllChip().render( pageContext );
			%>
			</td>
		</tr>
		<%
			if ( theChip.getFailureMessage()!=null )
			{
				%>
				<tr>
					<td>
						<%=localized("lucenesearch.rebuildallindexes.failed")%><br>
						<%=theChip.getFailureMessage()%>
					</td>
				</tr>
				<%
			}
		%>
		
	</table>
