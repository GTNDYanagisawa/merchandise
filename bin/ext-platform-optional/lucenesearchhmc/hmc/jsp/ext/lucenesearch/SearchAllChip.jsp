<%@include file="../../head.inc"%>
<%@include file="../../xp_button.inc"%>
<%@page import="de.hybris.platform.lucenesearch.hmc.SearchAllChip"%>
<%@page import="de.hybris.platform.lucenesearch.hmc.SearchResultChip"%>
<%@page import="de.hybris.platform.lucenesearch.hmc.SearchGroupChip"%>

<%
	SearchAllChip theChip = (SearchAllChip) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<table width="100%">
	<!--  <tr>
		<td class="sectionheader" colspan="2"> 
			<%= localized("lucenesearch.search.title") %>
		</td>
	</tr>
	<tr>
		<td colspan="2" style="padding:10px;">
			<%= localized("lucenesearch.search.explanation1") %>			
		</td>
	</tr>-->
	<%
		if ( theChip.getFailureMessage()!= null )
		{
			if ( theChip.getFailureMessage().equalsIgnoreCase("org.apache.lucene.search.BooleanQuery$TooManyClauses") )
			{
			%>
			<tr height="15px"><td colspan="2">&nbsp;</td></tr>
			<tr><th><%=localized("lucenesearch.searchmessage.title")%></th><th>&nbsp;</th></tr>
			<tr><td>&nbsp;</td></tr>
			<tr><td colspan="2"><pre><%=localized("lucenesearch.searchmessage.tomanyclauses")%></pre></td></tr>
			<%
			}
			else
			{
			%>
			<tr height="15px"><td colspan="2">&nbsp;</td></tr>
			<tr><th><%=localized("lucenesearch.searchmessage.title")%></th><th>&nbsp;</th></tr>
			<tr><td>&nbsp;</td></tr>
			<tr><td colspan="2"><pre><%=localized("lucenesearch.searchmessage.message")%></pre></td></tr>
			<tr><td colspan="2"><pre><%=theChip.getFailureMessage()%></pre></td></tr>
			<%
			}
		}
	%>
	<%
		for ( Iterator iter = theChip.getSearchResultChips().iterator(); iter.hasNext(); )
		{
			SearchResultChip src = (SearchResultChip)iter.next();
			%>
			<tr>
				<td colspan="2" style="padding-bottom:10px;">
				<% src.render( pageContext ); %>
				</td>
			</tr>
			<%
		}
	%>
</table>
