<%@include file="../../head.inc"%>
<%@include file="../../xp_button.inc"%>
<%@page import="de.hybris.platform.lucenesearch.hmc.SearchTab"%>
<%@page import="de.hybris.platform.lucenesearch.hmc.SearchResultChip"%>
<%@page import="de.hybris.platform.lucenesearch.hmc.SearchGroupChip"%>
<%
	SearchTab theChip = (SearchTab) request.getAttribute(AbstractChip.CHIP_KEY);
%>
<script language="JavaScript1.2">
	function checkForEnterLucene(e)
	{
		var keycode;
		if (window.event)
		{
			keycode = window.event.keyCode;
		}
		else if (e)
		{
			keycode = e.which;
		}
		else return true;
		if (keycode == 13)
		{
			if( theLuceneSearchEventElement )
			{
				theLuceneSearchEventElement.value="true";
			}
			document.editorForm.onsubmit();
			document.editorForm.submit();
			return false;
		}
		else return true;
	}	
	var theLuceneSearchEventElement;
</script>

<table width="100%">
	<tr>	
		<td><img width="6" height="1" src="images/transparent.gif" /></td>	
		<td class="sectionheader" colspan="2"> 
			<%= localized("lucenesearch.search.title") %>
		</td>
	</tr>
	<tr>
		<td><img width="6" height="1" src="images/transparent.gif" /></td>
		<td>
			<table style="width:100%">
				
				<tr>
					<td colspan="2" style="padding-top:5px; padding-left:5px;">
						<%= localized("lucenesearch.search.explanation1") %><br>
						<%= localized("lucenesearch.search.explanation2") %>
					</td>
				</tr>
				<tr>
					<td style="padding-left:5px;">
						<%=localized("lucenesearch.search.pattern")%>:
					</td>
					<td>
						<input	class="editorform"  type="text" size="50" 
									name="<%=theChip.getEventID(theChip.SEARCH_PATTERN)%>" value="<%=theChip.getNextSearchPattern()%>"
									onkeypress="return checkForEnterLucene(event);" >
						</input>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<table>
							<tr>
								<td><%= xpButton(
										localized("lucenesearch.searchcommand.button"),  // label
										theChip.getCommandID(theChip.SEARCH_COMMAND),	// event
										localized("lucenesearch.searchcommand.tooltip"), // tooltip
										false  // scrolldown
									) %></td>
								<script type="text/javascript">
										theLuceneSearchEventElement = document.editorForm.elements["<%=theChip.getCommandID(theChip.SEARCH_COMMAND)%>"];
								</script>
							</tr>
						</table>
					</td>
				</tr>
				<%
					if ( theChip.getFailureMessage()!=null )
					{
						%>
						<tr height="15px"><td colspan="2">&nbsp;</td></tr>
						<tr><th colspan="2"><%=localized("lucenesearch.searchmessage.title")%></th></tr>
						<tr><td colspan="2"><pre><%=localized("lucenesearch.searchmessage.message")%></pre></td></tr>
						<tr><td colspan="2"><pre><%=theChip.getFailureMessage()%></pre></td></tr>
						<%
					}
				%>
				<%
					for ( Iterator iter = theChip.getSearchResultChips().iterator(); iter.hasNext(); )
					{
						SearchResultChip src = (SearchResultChip)iter.next();
						%>
						<tr><td colspan="2">
							<% src.render( pageContext ); %>
						</td></tr>
						<%
					}
				%>
			</table>
		</td>
	</tr>
</table>
