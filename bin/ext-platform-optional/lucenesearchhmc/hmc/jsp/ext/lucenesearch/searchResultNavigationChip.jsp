<%@include file="../../head.inc"%>
<%@page import="de.hybris.platform.lucenesearch.hmc.SearchResultNavigationChip"%>

<%
SearchResultNavigationChip theChip = (SearchResultNavigationChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<table border="0" cellspacing="0" cellpadding="0" style="height:100%; width:100%;">
	<tr>
		<td>
<%
			theChip.getHeaderToolbar().render( pageContext );
%>
		</td>
	</tr>
	<tr>
		<td style="border-left: 1px solid #e1e1e1; border-right: 1px solid #e1e1e1; background-color: #ffffff;vertical-align: top;">
			<table>
				<tr>
					<td>
<%
						theChip.getRootTreeChip().render(pageContext);
%>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	<tr>
		<td>
<%
			theChip.getFooterToolbar().render( pageContext );
%>
		</td>
	</tr>

</table>
